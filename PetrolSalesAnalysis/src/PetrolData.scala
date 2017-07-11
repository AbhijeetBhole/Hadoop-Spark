import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.sql._
/**
  * Created by abhijeet_bhole on 7/11/2017.
  */
object PetrolData {


  case class pet(
                  DistrictID:String,
                  Distributer_name:String,
                  Buy_rate:Float,
                  Sell_rate:Float,
                  volumeIN:Integer,
                  volumeOUT:Integer,
                  Year:Integer
                )

  def main(args: Array[String]) {
    val salesfile = "Sample_Data/petrol.txt" // Should be some file on your system
    val conf = new SparkConf().setAppName("Simple Application2").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val sqlc = new org.apache.spark.sql.SQLContext(sc);

    import sqlc.implicits._

    val pertolData = sc.textFile(salesfile)
    val pertolDataRdd = pertolData.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter };


    val pertolDataDF = pertolDataRdd.map(rec => { val r = rec.split(",");
      pet(r(0),r(1),r(2).replace('$',' ').trim.toFloat,r(3).replace('$',' ').trim.toFloat,r(4).toInt,r(5).toInt,r(6).toInt)
    }).toDF();


    pertolDataDF.registerTempTable("petrol")


    //1)In real life what is the total amount of petrol in volume sold by every distributor?

    val PetrolSoldByDistributor = sqlc.sql("select Distributer_name,SUM(volumeOUT) as Total_Petrol_Sold FROM petrol GROUP BY Distributer_name");
   println("Total amount of petrol in volume sold by every distributor : ")
    PetrolSoldByDistributor.collect().foreach(println)

    //2)Which are the top 10 distributors ID’s for selling petrol and also display the amount of petrol sold in volume by them individually?

    val topTenDistributors = sqlc.sql("SELECT DistrictID,volumeOUT as Petrol_Sold FROM petrol order by volumeOUT desc limit 10");
    println("Top 10 distributors ID’s for selling petrol : ")
    topTenDistributors.collect().foreach(println)


  }


}
