#!/bin/bash

wget http://www-us.apache.org/dist/avro/stable/py/avro-1.8.1.tar.gz
tar -zxvf avro-1.8.1.tar.gz
mv avro-1.8.1 avro

cd avro
sudo python setup.py install
