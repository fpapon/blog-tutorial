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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ZooKeeperEmbeddedTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperEmbeddedTest.class);

    @Test
    public void testServer() throws IOException, InterruptedException {
        ZooKeeperEmbedded zooKeeperEmbedded = new ZooKeeperEmbedded();
        zooKeeperEmbedded.startServer();

        LOGGER.info("Test :: waiting for 5s...");
        Thread.sleep(5000);

        zooKeeperEmbedded.stopServer();
    }

}
