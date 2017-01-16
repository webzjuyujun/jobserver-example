package com.jd.local.test.jobserver

/**
  * Created by yujunjun on 17-1-13.
  */
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.{SparkConf, SparkContext}
import spark.jobserver.{NamedRddSupport, SparkJob, SparkJobValid, SparkJobValidation}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.config.Config
import org.apache.spark.SparkContext

import org.apache.spark.sql.{DataFrame, Row, SQLContext}


object EnvironmentConstruct  extends SparkJob with NamedRddSupport {
  val SAVED_RDD = "keyValCollection"

  override def runJob(sc: SparkContext, jobConfig: Config): Any = {
    val command = jobConfig.getString("command")
    var dictionarySize = 10
    println(s"Number of objects in set $dictionarySize")
    var range = 1 to dictionarySize;
    this.namedRdds.update(SAVED_RDD,
      sc.parallelize(range).
        map(_ ->("MyKey" + _, "MyVal" + _)));

  }

  override def validate(sc: SparkContext, config: Config) = SparkJobValid

  def main(args:Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("Environment")
    val sc = new SparkContext(sparkConf)
    println(sc)

    var confStr = "command=create"
    val config = ConfigFactory.parseString(confStr)

    val res = runJob(sc, config)

  }

}