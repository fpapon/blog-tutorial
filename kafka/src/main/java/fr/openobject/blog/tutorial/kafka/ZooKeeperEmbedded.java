/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.openobject.blog.tutorial.kafka;

import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class ZooKeeperEmbedded {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperEmbedded.class);

    public String host = "localhost";
    public int port = 2000;
    private int tickTime = 500;

    private ServerCnxnFactory cnxnFactory;
    private File snapshotDir;
    private File logDir;
    private ZooKeeperServer zooKeeperServer;

    public void startServer() throws IOException, InterruptedException {
        LOGGER.info("Starting ZooKeeper server on " + host + ":" + port + "...");
        snapshotDir = new File("target/test-classes/zk-snapshot");
        snapshotDir.mkdirs();
        logDir = new File("target/test-classes/zk-log");
        logDir.mkdirs();

        try {
            zooKeeperServer = new ZooKeeperServer(snapshotDir, logDir, tickTime);
            cnxnFactory = new NIOServerCnxnFactory();
            cnxnFactory.configure(new InetSocketAddress(host, port), 1024);
            cnxnFactory.startup(zooKeeperServer);
        } catch (InterruptedException | IOException e) {
            throw new IOException(e);
        }
        LOGGER.info("ZooKeeper started!");
    }

    public void stopServer() throws IOException, InterruptedException {
        LOGGER.info("Shutting down ZooKeeper server...");
        cnxnFactory.shutdown();
        zooKeeperServer.shutdown();

        logDir.delete();
        snapshotDir.delete();
        LOGGER.info("ZooKeeper stopped!");
    }

}
