package com.purbon.kafka.katas.consumers;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;

public class ASyncConsumer {

  KafkaConsumer<String, String> consumer;
  boolean filterOutEmptyRecordSets;
  int maxRecordsPerPoll;

  private ASyncConsumer(boolean filterOutEmptyRecordSets, int maxRecordsPerPoll) {
    this.maxRecordsPerPoll = maxRecordsPerPoll;
    this.filterOutEmptyRecordSets = filterOutEmptyRecordSets;
    consumer = new KafkaConsumer<>(props());
  }

  public static ASyncConsumer init(boolean filterOutEmptyRecordSets, int maxRecordsPerPoll) {
    ASyncConsumer consumer = new ASyncConsumer(filterOutEmptyRecordSets, maxRecordsPerPoll);
    return consumer;
  }
  private Properties props() {
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "async-consumer"+System.currentTimeMillis());
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

  public void consume(int max) throws InterruptedException {
    int i = 0;
    while(i < max) {
       ConsumerRecords<String, String> records =  consumer.poll(Duration.ofSeconds(2));
       System.out.println(records.count()+" consumed. " +  System.currentTimeMillis());
       if ((records.count() > 0 && filterOutEmptyRecordSets)||(!filterOutEmptyRecordSets))
            consumer.commitAsync((map, e) -> {
                 System.out.println(System.currentTimeMillis());
            });
       Thread.currentThread().sleep(1000);
       i = i+records.count();
    }
  }

  public static void main(String[] args) throws InterruptedException {

    boolean filterOutEmptyRecordSets = false;
    int maxRecordsPerPoll = 100;
    Thread t = new Thread() {
      ASyncConsumer consumer = ASyncConsumer.init(filterOutEmptyRecordSets, maxRecordsPerPoll);
      @Override
      public void run() {
        consumer.configure("topic");
        try {
          consumer.consume(3000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    };
    t.start();
    t.join(2000);

  }
}
