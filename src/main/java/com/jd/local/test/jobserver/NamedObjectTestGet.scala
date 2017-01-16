package com.jd.local.test.jobserver

/**
  * Created by yujunjun on 17-1-13.
  */
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.{SparkConf, SparkContext}
import spark.jobserver.{NamedRddSupport, SparkJob, SparkJobValid, SparkJobValidation}
import spark.jobserver._


object NamedObjectTestGet extends SparkTestJob with NamedObjectSupport{
  def runJob(sc: SparkContext, config: Config): Any = {
    import scala.concurrent.duration._
    implicit val timeout = akka.util.Timeout(10000 millis)

    val NamedDataFrame(df2,_,_) = namedObjects.get[NamedDataFrame]("df1").get

    df2.count()
  }
}
