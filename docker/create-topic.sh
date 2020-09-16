#!/usr/bin/env bash

docker exec kafka kafka-topics --bootstrap-server localhost:9092 \
            --create --topic topic --partitions 3 --replication-factor 1

