from confluent_kafka import Producer

p = Producer({'bootstrap.servers': 'localhost:9092'})
some_data_source = ['hello', 'world', 'example', 'for', 'confluent', 'python kafka producer']
for data in some_data_source:
    p.produce('mytopic', data.encode('utf-8'))
p.flush()
