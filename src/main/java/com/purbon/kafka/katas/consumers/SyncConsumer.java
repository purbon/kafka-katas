package com.purbon.kafka.katas.consumers;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serdes;

public class SyncConsumer {

  KafkaConsumer<String, String> consumer;
  boolean filterOutEmptyRecordSets;
  int maxRecordsPerPoll;

  private SyncConsumer(boolean filterOutEmptyRecordSets, int maxRecordsPerPoll) {
    this.filterOutEmptyRecordSets = filterOutEmptyRecordSets;
    this.maxRecordsPerPoll = maxRecordsPerPoll;
    this.consumer = new KafkaConsumer<>(props());
  }

  public static SyncConsumer init(boolean filterOutEmptyRecordSets, int maxRecordsPerPoll) {
    SyncConsumer consumer = new SyncConsumer(filterOutEmptyRecordSets, maxRecordsPerPoll);
    return consumer;
  }
  private Properties props() {
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "sync-consumer"+System.currentTimeMillis());
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Serdes.String().deserializer().getClass());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Serdes.String().deserializer().getClass());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxRecordsPerPoll);
    return props;
  }

  public void configure(String topic) {
    consumer.subscribe(Collections.singletonList(topic));
  }

  public void consume(int max) {
    int i = 0;
    while(i < max) {
       ConsumerRecords<String, String> records =  consumer.poll(Duration.ofSeconds(2));
       System.out.println(records.count()+" consumed");
      if ((records.count() > 0 && filterOutEmptyRecordSets)||(!filterOutEmptyRecordSets))
           consumer.commitSync();
       i = i+records.count();
    }
  }

  public static void main(String[] args) {
    boolean filterOutEmptyRecordSets = true;
    int maxRecordsPerPoll = 500;
    SyncConsumer consumer = SyncConsumer.init(filterOutEmptyRecordSets, maxRecordsPerPoll);
    consumer.configure("topic");
    consumer.consume(2000);
  }
}
