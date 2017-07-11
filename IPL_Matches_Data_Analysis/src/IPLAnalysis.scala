import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._
import Numeric.Implicits._
/**
  * Created by abhijeet_bhole on 7/11/2017.
  */
object IPLAnalysis {


  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("Simple Application2").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val data = sc.textFile("SampleData/matches.csv")

    //Splitting the data by columns and filtering the bad records
    val filtering_bad_records = data.map(line => line.split(",")).filter(x => x.length < 19)


    //1.Which stadium is best suitable for first batting


    //take out the columns toss_decision, won_by_runs, won_by_wickets, venue
    val extracting_columns = filtering_bad_records.map(x => (x(7), x(11), x(12), x(14)))

    //evaluate that which stadium is most suitable for first batting.
    //won_by_runs value as 0 means team batting first won
    val bat_first_won = extracting_columns.filter(x => x._2 != "0").map(x => (x._4, 1)).reduceByKey(_ + _)


    // total number of matches for which a stadium has been venue.
    val total_matches_per_venue = filtering_bad_records.map(x => (x(14), 1)).reduceByKey(_ + _)

    // winning percentage of each stadium for first_bat_won.
    val join2 = bat_first_won.join(total_matches_per_venue).map(x => (x._1,(x._2._1 * 100 / x._2._2))).map(item => item.swap).sortByKey(false).collect.foreach(println)




    //2.Which stadium is best suitable for first bowling


    //evaluate that which stadium is most suitable for first bowling.
    //won_by_wickets value as 0 means team bowling first won
    val bowl_first_won = extracting_columns.filter(x=>x._3!="0").map(x=>(x._4,1)) .reduceByKey(_+_)


    // winning percentage of each stadium for first_bowl_won.
    val join3 = bowl_first_won.join(total_matches_per_venue).map(x => (x._1,(x._2._1 * 100 / x._2._2))).map(item => item.swap).sortByKey(false).collect.foreach(println)


  }


}
