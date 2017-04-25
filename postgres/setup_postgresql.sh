#!/bin/bash

docker pull postgres:9.6
postgres_cid=$(docker ps -aq --filter "name=postgres")
volume_cid=$(docker ps -aq --filter "name=dbdata")
if [ -z $volume_cid ]; then
  docker create -v /var/lib/postgresql/data --name dbdata postgres /bin/true
fi

if [ ! -z $volume_cid ]; then
  docker stop $postgres_cid
  docker rm $postgres_cid
fi

docker run -d -p 32768:5432 --volumes-from dbdata --name postgres postgres
sudo apt-get install -y postgresql-client
