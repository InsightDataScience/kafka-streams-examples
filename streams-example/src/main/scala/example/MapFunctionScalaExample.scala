package example

import java.util.Properties

import org.apache.kafka.common.serialization._
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.{KStream, KStreamBuilder}

object MapFunctionScalaExample {

  def main(args: Array[String]) {
    val builder: KStreamBuilder = new KStreamBuilder

    val streamingConfig = {
      val settings = new Properties
      settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "map-function-scala-example")
      settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      settings.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181")
      // Specify default (de)serializers for record keys and for record values.
      settings.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray.getClass.getName)
      settings.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
      settings
    }
    val stringSerde: Serde[String] = Serdes.String()

    // Read the input Kafka topic into a KStream instance.
    val textLines: KStream[Array[Byte], String] = builder.stream("TextLinesTopic")

    import KeyValueImplicits._
    val uppercasedWithMapValues: KStream[Array[Byte], String] = textLines.mapValues(_.toUpperCase())
    // Write (i.e. persist) the results to a new Kafka topic called "UppercasedTextLinesTopic".
    //
    // In this case we can rely on the default serializers for keys and values because their data
    // types did not change, i.e. we only need to provide the name of the output topic.
    uppercasedWithMapValues.to("UppercasedTextLinesTopic")

    val stream: KafkaStreams = new KafkaStreams(builder, streamingConfig)
    stream.start()
  }

}
