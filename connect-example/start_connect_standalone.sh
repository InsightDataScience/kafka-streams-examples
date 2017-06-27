#!/bin/bash

args=("$@")

/usr/local/confluent/bin/connect-standalone /usr/local/confluent/etc/schema-registry/connect-avro-standalone.properties ${args[@]}
