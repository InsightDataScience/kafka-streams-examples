curl -L https://github.com/edenhill/librdkafka/archive/v0.9.2-RC1.tar.gz | sudo tar zxvf -C /usr/local
cd /usr/local/librdkafka-0.9.2-RC1/
./configure --prefix=/usr
make -j
sudo make install

cd ~
git clone https://github.com/confluentinc/confluent-kafka-python.git /usr/local
cd /usr/local/confluent-kafka-python
sudo C_INCLUDE_PATH=... LIBRARY_PATH=.. pip install .[avro]

