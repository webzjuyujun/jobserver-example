package com.jd.local.test.jobserver

/**
  * Created by yujunjun on 17-1-13.
  */

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.sql.types._
import org.apache.spark.sql._
import org.apache.spark.sql.Row
import spark.jobserver._

case class NamedDataFrame(df: DataFrame, forceComputation: Boolean,
                          storageLevel: StorageLevel) extends NamedObject
class DataFramePersister extends NamedObjectPersister[NamedDataFrame] {
  override def persist(namedObj: NamedDataFrame, name: String) {
    namedObj match {
      case NamedDataFrame(df, forceComputation, storageLevel) =>
        require(!forceComputation || storageLevel != StorageLevel.NONE,
          "forceComputation implies storageLevel != NONE")
        //these are not supported by DataFrame:
        //df.setName(name)
        //df.getStorageLevel match
        df.persist(storageLevel)
        // perform some action to force computation
        if (forceComputation) df.count()
    }
  }

  override def unpersist(namedObj: NamedDataFrame) {
    namedObj match {
      case NamedDataFrame(df, _, _) =>
        df.unpersist(blocking = false)
    }
  }

  /**
    * Calls df.persist(), which updates the DataFrame's cached timestamp, meaning it won't get
    * garbage collected by Spark for some time.
    * @param namedDF the NamedDataFrame to refresh
    */
  override def refresh(namedDF: NamedDataFrame): NamedDataFrame = namedDF match {
    case NamedDataFrame(df, _, storageLevel) =>
      df.persist(storageLevel)
      namedDF
  }

}
object NamedObjectTest extends SparkJob with NamedObjectSupport {
  implicit def dataFramePersister: NamedObjectPersister[NamedDataFrame] = new DataFramePersister

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[4]").setAppName("WordCountExample")
    val sc = new SparkContext(conf)
    val config = ConfigFactory.parseString("")
    val results = runJob(sc, config)
    println("Result is " + results)
  }

  def validate(sql: SparkContext, config: Config): SparkJobValidation = SparkJobValid

  private def rows(sc: SparkContext): RDD[Row] = {
    sc.parallelize(List(Row(1, "a"), Row(2, "b"), Row(55, "c")))
  }

  def runJob(sc: SparkContext, config: Config): Any = {
    val sqlContext = new SQLContext(sc)
    val struct = StructType(
      StructField("i", IntegerType, true) ::
        StructField("b", StringType, true) :: Nil)
    val df = sqlContext.createDataFrame(rows(sc), struct)
    namedObjects.update("df1", NamedDataFrame(df, false, StorageLevel.MEMORY_ONLY))

  }

}
