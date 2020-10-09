package com.purbon.kafka.katas.offsetShell;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

public class CustomOffsetShell<K,V> {

  class Pair<K, V> {

    private final K key;
    private final V value;

    public Pair(K key, V value) {
      this.key = key;
      this.value = value;
    }

    public K getKey() {
      return key;
    }

    public V getValue() {
      return value;
    }
  }

  private final String propertyFile;

  public CustomOffsetShell(String propertyFile) {
    this.propertyFile = propertyFile;
  }

  public Map<TopicPartition, Pair<Long, Long>> offsetMapping(String topic, int partitionCount) throws IOException {

    KafkaConsumer<K, V> consumer = new KafkaConsumer<K, V>(props());
    consumer.subscribe(Collections.singletonList(topic));

    List<TopicPartition> partitions = new ArrayList<>();
    for(int i = 0; i < partitionCount; i++) {
      partitions.add(new TopicPartition(topic, i));
    }

    Map<TopicPartition, Long> beginingOffsetMap = consumer.beginningOffsets(partitions);
    Map<TopicPartition, Long> endOffsetMap = consumer.endOffsets(partitions);


    Map<TopicPartition, Pair<Long, Long>> finalMap = new HashMap<>();

    beginingOffsetMap.forEach(
        (key, value) -> finalMap.put(key, new Pair<Long, Long>(value, endOffsetMap.get(key))));

    return finalMap;
  }

  public Properties props() throws IOException {
    Properties props = new Properties();
    props.load(new FileReader(propertyFile));
    return props;
  }
}
