from confluent_kafka import avro 
from confluent_kafka.avro import AvroProducer
import sys

def delivery_callback (err, msg):
    if err:
        sys.stderr.write('%% Message failed delivery: %s\n' % err)
    else:
        sys.stderr.write('%% Message delivered to %s [%d]\n' % \
                         (msg.topic(), msg.partition()))


value_schema = avro.load('ValueSchema.avsc')
key_schema = avro.load('KeySchema.avsc')
value1 = {"name": "person1", "age": 25}
key1 = {"key": "person1"}

value2 = {"name": "person2", "age": 25}
key2 = {"key": "person2"}

avroProducer = AvroProducer({'bootstrap.servers': 'localhost:9092','schema.registry.url': 'http://localhost:8081'}, default_key_schema=key_schema, default_value_schema=value_schema)
for i in range(1):
    try:
        avroProducer.produce(topic='AvroTopic', value=value1, key=key1, callback=delivery_callback)
        avroProducer.produce(topic='AvroTopic', value=value2, key=key2, callback=delivery_callback)
    except BufferError as e:
        sys.stderr.write('%% Local producer queue is full ' \
                         '(%d messages awaiting delivery): try again\n' %
                         len(avroProducer))
    avroProducer.poll(0)
avroProducer.flush()
