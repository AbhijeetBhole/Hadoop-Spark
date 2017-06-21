val sqlc = new org.apache.spark.sql.SQLContext(sc);

import sqlc.implicits._;

val pertolData = sc.textFile("/user/plutoabhi/poc1/petrol.txt")
val pertolDataRdd = pertolData.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter };

case class pet(
DistrictID:String,
Distributer_name:String,
Buy_rate:Float,
Sell_rate:Float,
volumeIN:Integer,
volumeOUT:Integer,
Year:Integer
)

val pertolDataDF = pertolDataRdd.map(rec => { val r = rec.split(",");
pet(r(0),r(1),r(2).replace('$',' ').trim.toFloat,r(3).replace('$',' ').trim.toFloat,r(4).toInt,r(5).toInt,r(6).toInt)
}).toDF();

pertolDataDF.registerTempTable("petrol");

#1)In real life what is the total amount of petrol in volume sold by every distributor?

val PetrolSoldByDistributor = sqlc.sql("select Distributer_name,SUM(volumeOUT) as Total_Petrol_Sold FROM petrol GROUP BY Distributer_name");

2)Which are the top 10 distributors ID’s for selling petrol and also display the amount of petrol sold in volume by them individually?

val topTenDistributors = sqlc.sql("SELECT DistrictID,volumeOUT as Petrol_Sold FROM petrol order by volumeOUT desc limit 10");