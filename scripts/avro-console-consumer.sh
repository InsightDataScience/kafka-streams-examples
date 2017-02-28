#!/bin/bash

~/confluent/bin/kafka-avro-console-consumer --new-consumer --bootstrap-server localhost:9092 --topic $1 --from-beginning
