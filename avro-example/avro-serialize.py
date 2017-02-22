import avro.schema
from avro.datafile import DataFileWriter
from avro.io import DatumWriter

schema = avro.schema.parse(open("user.avsc", "rb").read())

writer = DataFileWriter(open("users.avro", "wb"), DatumWriter(), schema)
writer.append({"name": "User1", "age": 30})
writer.append({"name": "User2", "age": 24})
writer.close()
