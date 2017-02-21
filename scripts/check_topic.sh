#!/usr/bin/env bash

/usr/local/kafka/bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic $1
