#!/bin/bash

wget https://s3.amazonaws.com/redshift-downloads/drivers/RedshiftJDBC42-1.2.1.1001.jar
mv RedshiftJDBC42-1.2.1.1001.jar /usr/local/confluent/share/java/kafka-connect-jdbc/
