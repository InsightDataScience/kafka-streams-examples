import avro.schema
from avro.datafile import DataFileReader 
from avro.io import DatumReader

schema = avro.schema.parse(open("user.avsc", "rb").read())

reader = DataFileReader(open("users.avro", "rb"), DatumReader())
for user in reader:
    print user
reader.close()
