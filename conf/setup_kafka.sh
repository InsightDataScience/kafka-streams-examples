#!/bin/bash

CLUSTER_NAME=kafka_example_cluster

TEMPLATE=kafka_example_cluster.yml

peg up kafka-example-cluster.yml
peg fetch ${CLUSTER_NAME}
peg install ${CLUSTER_NAME} zookeeper
peg install ${CLUSTER_NAME} kafka
