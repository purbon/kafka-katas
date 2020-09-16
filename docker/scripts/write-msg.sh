#!/usr/bin/env bash

curl -X POST -H "Content-Type: application/vnd.kafka.binary.v2+json" \
      -H "Accept: application/vnd.kafka.v2+json" \
      --data '{"records":[{"value":"<note> <to>Tove</to><from>Jani</from></note>"}]}' "http://localhost:8082/topics/xmlcontract"
