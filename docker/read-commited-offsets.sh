#!/usr/bin/env bash

docker exec kafka kafka-console-consumer \
    --formatter "kafka.coordinator.group.GroupMetadataManager\$OffsetsMessageFormatter" \
    --bootstrap-server localhost:9092 --topic __consumer_offsets --from-beginning