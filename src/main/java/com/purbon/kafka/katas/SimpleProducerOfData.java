package com.purbon.kafka.katas;

import java.util.Properties;
import java.util.Random;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.Serdes;

public class SimpleProducerOfData {

  KafkaProducer<String, String> producer;
  int send;
  int acks;

  private SimpleProducerOfData() {
    producer = new KafkaProducer<String, String>(props());
    send = 0;
    acks = 0;

  }

  public Properties props() {
    Properties properties = new Properties();
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass());
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass());
    return properties;
  }


  public static SimpleProducerOfData init() {
    SimpleProducerOfData producerOfData = new SimpleProducerOfData();
    return producerOfData;
  }
  public void send(String topic, String key, String value) {

    ProducerRecord<String, String> record  = new ProducerRecord<String, String>(topic, key, value);
    send++;
    producer.send(record, new Callback() {
      public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        if (e == null) {
          acks++;
          System.out.println("Recorded at "+recordMetadata.topic()+", "+recordMetadata.partition()+ " "+recordMetadata.offset());
        } else {
          e.printStackTrace();
        }
      }
    });
  }

  public boolean allReceived() {
    return send == acks;
  }

  public int getSend() {
    return send;
  }

  public int getAcks() {
    return acks;
  }


  public static void main(String [] args) throws Exception {

    SimpleProducerOfData producer = SimpleProducerOfData.init();

    String[] cities = new String[]{ "Barcelona", "Berlin", "Munich", "Hamburg"};
    Random rand = new Random();

    for(int i=0; i < 5000; i++) {
      String city = cities[rand.nextInt(3)];
      int value = rand.nextInt(100);
      producer.send("topic", city, ""+value);
    }

    while(!producer.allReceived()) {
      System.out.println(producer.getSend()+" "+producer.getAcks());
      Thread.sleep(1000);
    }

  }
}
