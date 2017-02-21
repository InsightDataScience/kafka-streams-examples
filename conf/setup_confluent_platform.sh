#!/bin/bash

curl -o /usr/local http://packages.confluent.io/archive/3.1/confluent-oss-3.1.2-2.11.tar.gz
tar -zxvf /usr/local/confluent-oss-3.1.2-2.11.tar.gz
mv /usr/local/confluent-oss-3.1.2-2.11 /usr/local/confluent
