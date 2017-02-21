package example

import java.util.Properties
import java.lang.{Long => JLong}

import org.apache.kafka.common.serialization._
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.{KStream, KStreamBuilder}

object WordCountExample {

  def main(args: Array[String]) {
    val builder: KStreamBuilder = new KStreamBuilder

    val streamingConfig = {
      val settings = new Properties
      settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "map-function-scala-example")
      settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      settings.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181")
      // Specify default (de)serializers for record keys and for record values.
      settings.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
      settings.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
      settings
    }
    val stringSerde: Serde[String] = Serdes.String()
    val longSerde: Serde[JLong] = Serdes.Long()

    
    // Read the input Kafka topic into a KStream instance.
    val textLines: KStream[Array[Byte], String] = builder.stream("TextLinesTopic")

    import collection.JavaConverters.asJavaIterableConverter
    import KeyValueImplicits._
   
    val wordCounts: KStream[String, JLong] = textLines
      .flatMapValues(value => value.toLowerCase.split("\\W+").toIterable.asJava)
      .groupBy((key, word) => word)
      .count("Counts")
      .toStream()

    wordCounts.to(stringSerde, longSerde, "WordsWithCountsTopic")

    val stream: KafkaStreams = new KafkaStreams(builder, streamingConfig)
    stream.start()
  }

}
