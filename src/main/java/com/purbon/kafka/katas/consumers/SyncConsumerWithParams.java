package com.purbon.kafka.katas.consumers;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;
import java.util.Map;

public class SyncConsumerWithParams {

  KafkaConsumer<String, String> consumer;
  boolean filterOutEmptyRecordSets;
  int maxRecordsPerPoll;

  private SyncConsumerWithParams(boolean filterOutEmptyRecordSets, int maxRecordsPerPoll) {
    this.filterOutEmptyRecordSets = filterOutEmptyRecordSets;
    this.maxRecordsPerPoll = maxRecordsPerPoll;
    this.consumer = new KafkaConsumer<>(props());
  }

  public static SyncConsumerWithParams init(boolean filterOutEmptyRecordSets, int maxRecordsPerPoll) {
    SyncConsumerWithParams consumer = new SyncConsumerWithParams(filterOutEmptyRecordSets, maxRecordsPerPoll);
    return consumer;
  }
  private Properties props() {
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "sync-with-params-consumer"+System.currentTimeMillis());
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
    Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
    while(i < max) {
       ConsumerRecords<String, String> records =  consumer.poll(Duration.ofSeconds(2));
       System.out.println(records.count()+" consumed");
       // Collected the latest offset per partition consumed.
      records
          .partitions()
          .forEach(topicPartition -> {
            List<ConsumerRecord<String, String>> partitionRecords = records.records(topicPartition);
            long lastOffset = partitionRecords.get(partitionRecords.size()-1).offset();
            offsets.put(topicPartition, new OffsetAndMetadata(lastOffset));
          });
       i = i+records.count();
    }
    consumer.commitSync(offsets);
  }

  public static void main(String[] args) {
    boolean filterOutEmptyRecordSets = true;
    int maxRecordsPerPoll = 500;
    SyncConsumerWithParams consumer = SyncConsumerWithParams
        .init(filterOutEmptyRecordSets, maxRecordsPerPoll);
    consumer.configure("topic");
    consumer.consume(2000);
  }
}
