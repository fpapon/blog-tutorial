/*
 * Copyright (c) 2019-Present - François Papon - Openobject.fr - https://openobject.fr
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package fr.openobject.blog.tutorial.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class KafkaBrokerEmbeddedTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaBrokerEmbeddedTest.class);
    private static final String topicName = "test-topic";
    private static final String groupId = "1";
    private static final String recordKey = "my.key";
    private static final String recordValue = "any value";

    @Test
    public void testServer() throws IOException, InterruptedException, ExecutionException {

        ZooKeeperEmbedded zooKeeperEmbedded = new ZooKeeperEmbedded();
        zooKeeperEmbedded.startServer();

        KafkaBrokerEmbedded kafkaBrokerEmbedded = new KafkaBrokerEmbedded(zooKeeperEmbedded.host + ":"+ zooKeeperEmbedded.port);
        kafkaBrokerEmbedded.startBroker();

        Map<String, Object> propertiesProducer = new HashMap<>();
        propertiesProducer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokerEmbedded.brokerList);
        propertiesProducer.put(ProducerConfig.CLIENT_ID_CONFIG, "1");
        propertiesProducer.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "none");
        propertiesProducer.put(ProducerConfig.ACKS_CONFIG, "all");
        propertiesProducer.put(ProducerConfig.RETRIES_CONFIG, 0);
        propertiesProducer.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        propertiesProducer.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        propertiesProducer.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        propertiesProducer.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        propertiesProducer.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);
        propertiesProducer.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 2097152);

        try (final KafkaProducer<String, String> producer = new KafkaProducer<>(propertiesProducer)) {
            producer.send(new ProducerRecord<>(topicName, recordKey, recordValue)).get();
            producer.flush();
        }

        LOGGER.info("Test :: message published, waiting for 5s...");
        Thread.sleep(5000);

        Map<String, Object> propertiesConsumer = new HashMap<>();
        propertiesConsumer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokerEmbedded.brokerList);
        propertiesConsumer.put(ConsumerConfig.CLIENT_ID_CONFIG, "2");
        propertiesConsumer.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        propertiesConsumer.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        propertiesConsumer.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        propertiesConsumer.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        propertiesConsumer.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        propertiesConsumer.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        propertiesConsumer.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        propertiesConsumer.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); //latest, earliest, none

        try (final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(propertiesConsumer)) {
            consumer.subscribe(Collections.singleton(topicName));
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
            Assert.assertEquals(1, records.count());
            ConsumerRecord<String, String> record = records.iterator().next();
            Assert.assertEquals(recordKey, record.key());
            Assert.assertEquals(recordValue, record.value());
        }

        LOGGER.info("Test :: message consumed, waiting for 5s...");
        Thread.sleep(5000);

        kafkaBrokerEmbedded.stopBroker();
        zooKeeperEmbedded.stopServer();
    }

}
