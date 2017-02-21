# kafka-streams-examples

## Setup Zookeeper and Kafka cluster
* Setup [Pegasus](https://github.com/InsightDataScience/pegasus)
* Follow instructions on [Pegasus](https://github.com/InsightDataScience/pegasus) and install Zookeeper and Kafka on a cluster on AWS
* Example yml file for Kafka and setup script are in `conf/` folder

## Setup Confluent Platform and Confluent Python Kafka Client
* Setup scripts for these are in `conf/` folder. Set these up on one of the Kafka nodes.
* Start Schema Registry on this node
  `confluent/bin/schema-registry-start etc/schema-registry/schema-registry.properties`

## Run this example
* Create input and output Kafka topics for the respective application
* Use the `producer/avro-producer.py` script to produce some messages to the topic
* `cd streams-example`
* `mvn clean package`
* `java -cp target/streams-examples-0.0.1-standalone.jar example.<class-name>`
