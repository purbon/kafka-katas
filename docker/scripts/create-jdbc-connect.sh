#!/usr/bin/env bash

curl -i -X POST -H "Accept:application/json" \
    -H  "Content-Type:application/json" http://connect:18083/connectors/ \
    -d '{
      "name": "jdbc-sink-connector",
      "config": {
        "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
        "tasks.max": "2",
        "topics": "orders",
        "connection.url": " jdbc:mysql://db:3306",
        "dialect.name" : "MySqlDatabaseDialect",
        "connection.user": "root",
        "connection.password": "confluent",
        "table.name.format": "demo.${topic}",
        "insert.mode": "upsert",
        "auto.create": "true",
        "auto.evolve": "true",
        "pk.mode": "record_value",
        "pk.fields": "id"
      }
    }'
