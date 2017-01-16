package com.jd.local.test.jobserver

/**
  * Created by yujunjun on 17-1-13.
  */
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.{SparkConf, SparkContext}
import spark.jobserver.{NamedRddSupport, SparkJob, SparkJobValid, SparkJobValidation}

trait SparkTestJob extends SparkJob {
  def validate(sc: SparkContext, config: Config): SparkJobValidation = SparkJobValid
}
object EnvironmentConstructTest extends SparkTestJob with NamedRddSupport {
  def runJob(sc: SparkContext, config: Config): Any = {
    import scala.concurrent.duration._
    implicit val timeout = akka.util.Timeout(10000 millis)

    val rdd = namedRdds.getOrElseCreate(getClass.getSimpleName, {
      // anonymous generator function
      sc.parallelize(1 to 5)
    })

    // RDD should already be in cache the second time
    val rdd2 = namedRdds.get[Int](getClass.getSimpleName)
    assert(rdd2 == Some(rdd), "Error: " + rdd2 + " != " + Some(rdd))
    rdd.map { x => x * x }.collect().sum
  }
}