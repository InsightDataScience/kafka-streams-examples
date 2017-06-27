#!/bin/bash

/usr/local/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic $1 --from-beginning --property print.key=true --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
