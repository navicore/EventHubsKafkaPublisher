package onextent.eventhubs.publisher

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

object Main extends Serializable with LazyLogging {

  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load().getConfig("main")

    val sparkConfig = new SparkConf()
    sparkConfig.set("spark.cores.max", "2")
    val ssc = new StreamingContext(new SparkContext(sparkConfig), Seconds(config.getString("kafka.batchDuration").toInt))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> config.getString("kafka.brokerList"),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> config.getString("kafka.consumerGroup"),
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array(config.getString("kafka.topic"))

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    stream.map(record => (record.key, record.value)).foreachRDD(rdd => rdd.foreach(o => {
      println(s"key ${o._1} val: ${o._2}")
      //todo: ejs write the val (o._2) to event hubs!
      //todo: ejs write the val (o._2) to event hubs!
      //todo: ejs write the val (o._2) to event hubs!
      //todo: ejs write the val (o._2) to event hubs!
      //todo: ejs write the val (o._2) to event hubs!
      //todo: ejs write the val (o._2) to event hubs!
      //todo: ejs write the val (o._2) to event hubs!
      //todo: ejs write the val (o._2) to event hubs!
    }))

    ssc.start()
    ssc.awaitTermination()

  }
}

