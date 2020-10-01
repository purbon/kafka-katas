#!/usr/bin/env bash

openssl req -new -x509 -keyout snakeoil-ca-1.key -out snakeoil-ca-1.crt -days 365 -subj '/CN=ca1.test.confluentdemo.io/OU=TEST/O=CONFLUENT/L=PaloAlto/ST=Ca/C=US' -passin pass:confluent -passout pass:confluent

for i in app
do
  echo "------------------------------- $i -------------------------------"
  ./create-cert.sh $i
  ./create-cert-jks.sh $i
done
