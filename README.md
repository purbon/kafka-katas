# The Kafka katas, a collection of simple kafka client playbooks 

This repository contains simple playbooks for common usages of the Java Apache Kafka client library. 
Here you will find help for the common question of how to do X or Y with the kafka clients.

## Consumers

### Offset commit

One of the most common challenges implementing a consumer is when to commit the processed offsets. 
This could be archived basically in three main ways, via a timer, async and sync. 

Within the last two options, users can commit all incoming data, including the ones that have not 
changes, or commit only the latest changes.

In this code base, the reader can find:

* SyncConsumer: An example of a sync consumer. Once the consumer call _commitSync()_, in this way of
functioning, the consumer coordinator will commit a message for all partitions and topics where the consumer is subscribed.
Because the call is sync the code will block until all offsets are commited.
* SyncConsumerWithParams: In this version of the consumers, the application will call _commitSync(offsets)_. 
This method will allow the client to pass exact offsets to commit. 
This option is more efficient as only commits what user is under control.
* ASyncConsumer: This consumer will call _commitAsync()_, allowing the message to travel in a parallel thread not blocking
the current consumer thread. This version will commit as well a message for each topic and partition, even if the offset has not changed.

## Testing

You can test this playbooks with the available docker setup inside the docker/ directory. 
To run this, you just need to invoke the ./up script and all will come up together. 

For sure, docker and docker-compose is need for this.
