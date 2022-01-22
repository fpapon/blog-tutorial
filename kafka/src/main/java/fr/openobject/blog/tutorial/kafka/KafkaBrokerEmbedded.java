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

import kafka.metrics.KafkaMetricsReporter;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import org.apache.kafka.common.utils.SystemTime;
import scala.Option;
import scala.collection.mutable.Buffer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class KafkaBrokerEmbedded {

    private final String zkConnection;

    public String brokerList;

    private KafkaServer kafkaServer;
    private File logDir;

    public KafkaBrokerEmbedded(String zkConnection) {
        this.zkConnection = zkConnection;
        this.brokerList = "localhost:2001";
    }

    public void startBroker() {
        logDir = new File("target/test-classes/kafka-log");
        logDir.mkdirs();

        Properties properties = new Properties();
        properties.setProperty("zookeeper.connect", zkConnection);
        properties.setProperty("broker.id", "1");
        properties.setProperty("group.id", "1");
        properties.setProperty("host.name", "localhost");
        properties.setProperty("port", "2001");
        properties.setProperty("log.dir", logDir.getAbsolutePath());
        properties.setProperty("num.partitions", String.valueOf(1));
        properties.setProperty("auto.create.topics.enable", String.valueOf(Boolean.TRUE));
        properties.setProperty("log.flush.interval.messages", String.valueOf(1));
        properties.setProperty("offsets.topic.replication.factor", String.valueOf(1));
        kafkaServer = startBroker(properties);
    }

    private KafkaServer startBroker(Properties props) {
        List<KafkaMetricsReporter> kmrList = new ArrayList<>();
        Buffer<KafkaMetricsReporter> metricsList = scala.collection.JavaConverters.asScala(kmrList);
        KafkaServer server = new KafkaServer(new KafkaConfig(props), new SystemTime(), Option.<String>empty(), metricsList);
        server.startup();
        return server;
    }

    public void stopBroker() {
        kafkaServer.shutdown();
        logDir.delete();
    }

}
