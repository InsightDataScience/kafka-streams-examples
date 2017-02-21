package example

import java.util.Properties
import java.lang.{Long => JLong}

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
//import org.json4s.jackson.Serialization
//import org.json4s.{ Extraction, NoTypeHints }
//import org.json4s.JsonDSL.WithDouble._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{ read, write, writePretty }

import com.twitter.bijection.Injection;
import com.twitter.bijection.json._
import com.twitter.bijection.json.JsonNodeInjection._
import org.codehaus.jackson.{JsonParser, JsonNode, JsonFactory}

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization._
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.{KStream, KStreamBuilder}


case class Person(name: String, age: Long)
case class PersonCount(name: String, sum: Long)

object JsonCountExample {

  def main(args: Array[String]) {
    val builder: KStreamBuilder = new KStreamBuilder
//    implicit val formats = Serialization.formats(NoTypeHints)
    implicit val formats = DefaultFormats
    val streamingConfig = {
      val settings = new Properties
      settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "json-word-count-example")
      settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      settings.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181")
      settings.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      // Specify default (de)serializers for record keys and for record values.
      settings.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
      settings.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
      settings
    }
    val stringSerde: Serde[String] = Serdes.String()
    val longSerde: Serde[JLong] = Serdes.Long()

    
    // Read the input Kafka topic into a KStream instance.
    val jsonLines: KStream[String, String] = builder.stream("JsonPersonTopic")

    import collection.JavaConverters.asJavaIterableConverter
    import KeyValueImplicits._
   
//    val wordCounts: KStream[String, JLong] = textLines
//      .flatMapValues(value: new ValueMapper[String, Long] => inj2(value).toIterable.asJava)
//      .groupBy((key, word) => word)
//      .count("Counts")
//      .toStream()

    val personStream: KStream[String, Person] = jsonLines.mapValues(createPerson)
    val nameCounts: KStream[String, String] = personStream
      .map { (k,v) => new KeyValue(v.name, v.name) }
      .groupBy((k, v) => v)
      .count("Counts")
      .toStream()
      .map((k, v) => new KeyValue(k, createPersonCount(k, v)))
      .map((k, v) => new KeyValue(k, compact(render(v.name -> v.sum))))
//      .map { (k, v) => new KeyValue() }
//     .map(createPersonCount)

//    val nameCounts

    val outputStream: KStream[String, String] = personStream
      .map { (k, v) => new KeyValue(k, v.name) }

    outputStream.to(stringSerde, stringSerde, "JsonNameTopic")
    nameCounts.to(stringSerde, stringSerde, "JsonCount")
      
    val stream: KafkaStreams = new KafkaStreams(builder, streamingConfig)
    stream.start()
  }

  def createPerson(x: String): Person = {
    implicit val formats = DefaultFormats
    val parsedX = parse(x)
    Person((parsedX \ "name").extract[String], (parsedX \ "age").extract[Long])
  }


  def createPersonCount(k: String, v: JLong): PersonCount = {
    implicit val formats = DefaultFormats
    PersonCount(k, v)
  }

}
