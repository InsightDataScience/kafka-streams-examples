# kafka-streams-examples

## Setup Zookeeper and Kafka cluster
* Setup [Pegasus](https://github.com/InsightDataScience/pegasus)
* Follow instructions on [Pegasus](https://github.com/InsightDataScience/pegasus) and install Zookeeper and Kafka on a cluster on AWS
* Example yml file for Kafka and setup script are in `conf/` folder

## Setup Confluent Platform and Confluent Python Kafka Client
* Setup scripts for these are in `conf/` folder. Set these up on one of the Kafka nodes.
* Start Schema Registry on this node
  `confluent/bin/schema-registry-start etc/schema-registry/schema-registry.properties`

## Run stream example
* Create input and output Kafka topics for the respective application
* Use the `producer/avro-producer.py` script to produce some messages to the topic
* `cd streams-example`
* `mvn clean package`
* `java -cp target/streams-examples-0.0.1-standalone.jar example.<class-name>`

## Run Connect example
* Edit the source and sink connector properties. There are examples in folder `/connect-example`
* Command to start the process `confluent/bin/connect-standalone etc/schema-registry/connect-avro-standalone.properties etc/kafka-connect-jdbc/source-quickstart-postgres.properties <path-to-connector-2.properties> <path-to-connector-3.properties> ...`
