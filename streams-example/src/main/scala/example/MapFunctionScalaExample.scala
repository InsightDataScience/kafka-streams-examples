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
    // Variant 1: using `mapValues`
    val uppercasedWithMapValues: KStream[Array[Byte], String] = textLines.mapValues(_.toUpperCase())
//    val uppercasedWithMapValues: KStream[Array[Byte], String] = textLines.mapValues( new ValueMapper[String] {
//                                                                            override def apply(value: String): String = value.toUpperCase() })
    // Write (i.e. persist) the results to a new Kafka topic called "UppercasedTextLinesTopic".
    //
    // In this case we can rely on the default serializers for keys and values because their data
    // types did not change, i.e. we only need to provide the name of the output topic.
    uppercasedWithMapValues.to("UppercasedTextLinesTopic")

    // We are using implicit conversions to convert Scala's `Tuple2` into Kafka Streams' `KeyValue`.
    // This allows us to write streams transformations as, for example:
    //
    //    map((key, value) => (key, value.toUpperCase())
    //
    // instead of the more verbose
    //
    //    map((key, value) => new KeyValue(key, value.toUpperCase())
    //

    // Variant 2: using `map`, modify value only (equivalent to variant 1)
    // val uppercasedWithMap: KStream[Array[Byte], String] = textLines.map((key: Array[Byte], value: String) => new KeyValue(key, value.toUpperCase()))
 
    // // Variant 3: using `map`, modify both key and value
    //
    // Note: Whether, in general, you should follow this artificial example and store the original
    //       value in the key field is debatable and depends on your use case.  If in doubt, don't
    //       do it.
    // val originalAndUppercased: KStream[String, String] = textLines.map((key, value) => new KeyValue(value, value.toUpperCase()))

    // Write the results to a new Kafka topic "OriginalAndUppercasedTopic".
    //
    // In this case we must explicitly set the correct serializers because the default serializers
    // (cf. streaming configuration) do not match the type of this particular KStream instance.
    // uppercasedWithMap.to(stringSerde, stringSerde, "OriginalAndUppercasedTopic")

    val stream: KafkaStreams = new KafkaStreams(builder, streamingConfig)
    stream.start()
  }

}
