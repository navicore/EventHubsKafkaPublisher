package onextent.eventhubs.publisher

import com.typesafe.scalalogging.LazyLogging
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import org.rogach.scallop.ScallopConf

object Main extends Serializable with LazyLogging {

  def main(args: Array[String]): Unit = {

    object Args extends ScallopConf(args) {

      val help = opt[Boolean]("holp", descr = "Get help.", required = true)

      // Kafka
      val brokerList = opt[String]("broker-list", descr = "Kafka broker list", required = true)
      val topic = opt[String]("input-topic", descr = "Kafka topic to read POS data from", required = true)
      val offset = opt[String]("offset", descr = "'largest' for new, 'smallest' for all", required = false, default = Some("smallest"))

      // Spark
      val maxRate = opt[Int]("max-rate", descr = "Spark streaming max reads per second", default = Some(100))
      val duration = opt[Long]("duration", descr = "Spark streaming batch duration, in milliseconds", default = Some(1000L))
      val jobDuration = opt[Long]("job-duration", descr = "job will self-terminiate after N seconds", default = Some(30L))
      val checkpointPath = opt[String]("checkpoint-path", descr = "Spark streaming checkpoint directory path", default = Some("/tmp/bits/spark"))
      val appName = opt[String]("name", descr = "Spark application name", required = false, default = Some("KafkaTools"))
      verify()
    }

    if (Args.help()) {
      println("hiya")
      System.exit(0)
    }
    val mybrokers = Args.brokerList()
    val mytopic = Args.topic()
    logger.info(s"init config --> kafka brokers: $mybrokers topic: $mytopic")

    val processConf: SparkConf = new SparkConf(true)
      .setAppName(Args.appName())
      .setIfMissing("spark.master", "local[*]")
      .set("spark.streaming.kafka.maxRatePerPartition", Args.maxRate().toString)

    val ssc = new StreamingContext(processConf, Milliseconds(Args.duration()))

    val sc = ssc.sparkContext
    val sqlContext = new SQLContext(sc)

    process(ssc, sqlContext,  Args.topic(), Args.brokerList())

    ssc.start()
    ssc.awaitTerminationOrTimeout(Args.jobDuration().toLong * 1000)
  }

  def process(ssc: StreamingContext,  sqlContext: SQLContext, DataTopic: String, brokerList: String) : Unit = {
    logger.info("process")
    // Initialize stream of device data and processing
    val dataStream = readStream(ssc, DataTopic, brokerList)
    dataStream.foreachRDD { (rdd, time) =>
      if (rdd.isEmpty()) {
        logger.debug("no data for batch " + time)
      } else {
        logger.debug("-------- reading data ---------")

        rdd.collect().foreach( json => {
          logger.info("raw data:\n" + json)
        })
        //rawdata.show()

        logger.debug("completed batch")
      }
    }
  }

  def readStream(ssc: StreamingContext, kafkaDataTopic: String, brokerList: String): DStream[String] = {
    val kafkaParams = Map(
      ("metadata.broker.list", brokerList),
      ("auto.offset.reset", "smallest")
    )
    val raw = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, Set(kafkaDataTopic))
    raw.map(_._2)
  }

}

