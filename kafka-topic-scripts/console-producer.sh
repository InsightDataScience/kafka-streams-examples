#!/bin/bash

topic=$1

/usr/local/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic $1
