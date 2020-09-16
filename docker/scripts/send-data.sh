#!/usr/bin/env bash

VALUE_SCHEMA=$(</scripts/schema.json)

cat /scripts/data.txt | xargs -n1 echo | kafka-avro-console-producer \
--broker-list kafka:29092 --topic orders \
--property value.schema="$VALUE_SCHEMA"

# echo '{"id": 1, "product": "foo", "quantity": 99, "price": 50}'
