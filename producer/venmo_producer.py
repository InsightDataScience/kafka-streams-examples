#!/bin/python
from confluent_kafka import Producer
import json

p = Producer({'bootstrap.servers': 'localhost:9092'})

venmo_file = open("/home/ubuntu/venmo-2015-01-01.json", "r")

for line in venmo_file:
      p.produce('venmo', json.dumps(line).encode('utf-8'))

venmo_file.close()
p.flush()
