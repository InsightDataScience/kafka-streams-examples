package example

import java.util.Properties
import java.lang.{Long => JLong}

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{ read, write, writePretty }

import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization._
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.{KStream, KStreamBuilder}
import org.apache.avro.generic.GenericRecord
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.kafka.streams.processor.TimestampExtractor
import org.apache.kafka.streams.processor.WallclockTimestampExtractor
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient

case class APerson(name: String, age: Integer)
case class PersonId(name: String, id: Integer)
case class APersonCount(name: String, sum: Long)

object AvroCountExample {

  def main(args: Array[String]) {
    val builder: KStreamBuilder = new KStreamBuilder
    implicit val formats = DefaultFormats
    val streamingConfig = {
      val settings = new Properties
      settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "avro-word-count-example")
      settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      settings.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181")
      settings.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      settings.put("schema.registry.url", "http://localhost:8081")
      // Specify default (de)serializers for record keys and for record values.
      settings.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
      settings.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, classOf[GenericAvroSerde])
      settings.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, classOf[WallclockTimestampExtractor])
      settings
    }
    val stringSerde: Serde[String] = Serdes.String()
    val longSerde: Serde[JLong] = Serdes.Long()

    val avroLines: KStream[String, GenericRecord] = builder.stream("postgres_accounts")
    import KeyValueImplicits._

//    val personStream: KStream[String, APerson] = avroLines.mapValues(createAPerson)
    val personStream: KStream[String, PersonId] = avroLines.mapValues(createPersonId)

    val outputStream: KStream[String, String] = personStream
      .map { (k, v) => new KeyValue(v.name, v.name) }

    val nameCounts: KStream[String, GenericRecord] = outputStream
      .groupByKey(stringSerde, stringSerde)
      .count("Counts")
      .toStream()
      .map { (k, v) => new KeyValue(k, createAPersonCount(k, v)) }
      .map { (k, v) => new KeyValue(null, createAvroObject(v.name, v.sum)) }
//      .map { (k, v) => new KeyValue(k, compact(render(createJObject(v.name, v.sum)))) }

    nameCounts.foreach((k, v) => println(s"Computed: key=$k value=$v"))

//    outputStream.to(stringSerde, stringSerde, "JsonNameTopic")
    nameCounts.to("redshift_test5")

    val stream: KafkaStreams = new KafkaStreams(builder, streamingConfig)
    stream.start()
  }

  def createAPerson(x: GenericRecord): APerson = {
    APerson(x.get("name").toString, x.get("age").asInstanceOf[Integer])
  }

  def createPersonId(x: GenericRecord): PersonId = {
    PersonId(x.get("name").toString, x.get("id").asInstanceOf[Integer])
  }

  def createAPersonCount(k: String, v: JLong): APersonCount = {
    implicit val formats = DefaultFormats
    APersonCount(k, v)
  }

  def createJObject(name: String, sum: Long): JObject = {val jobject: JObject = ("name" -> ("string" -> name)) ~ ("sum" -> sum); jobject}

  def createAvroObject(name: String, sum: Long) = {
    val opSchema: String = "{\"type\":\"record\",\"name\":\"redshift_test4\",\"fields\":[{\"name\":\"sum\",\"type\":\"int\"},{\"name\":\"name\",\"type\":\"string\"}]}"
    val parser = new Schema.Parser()
    val schema: Schema = parser.parse(opSchema)
    val avroRecord = new GenericData.Record(schema)
    avroRecord.put("name", name)
    avroRecord.put("sum", sum)
    avroRecord
  }
}
