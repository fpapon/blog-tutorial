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
package fr.openobject.blog.tutorial.ignite;

import fr.openobject.blog.tutorial.ignite.api.ApiPojo;
import static java.lang.System.exit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

public class IgniteMapLocal {

    private static final String CACHE_NAME = "apis";

    public static void main(String[] args) {

        // cache configuration
        CacheConfiguration<String, ApiPojo> cacheConfiguration = new CacheConfiguration<>(CACHE_NAME);
        cacheConfiguration.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        cacheConfiguration.setBackups(1);
        cacheConfiguration.setCacheMode(CacheMode.PARTITIONED);
        cacheConfiguration.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);
        cacheConfiguration.setIndexedTypes(String.class, ApiPojo.class);
        cacheConfiguration.setDataRegionName("galaxyLocalDevRegion");

        // tcp discovery configuration
        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryMulticastIpFinder tcpDiscoveryMulticastIpFinder = new TcpDiscoveryMulticastIpFinder();
        tcpDiscoveryMulticastIpFinder.setAddresses(Arrays.asList("127.0.0.1"));
        tcpDiscoverySpi.setIpFinder(tcpDiscoveryMulticastIpFinder);
        tcpDiscoverySpi.setLocalAddress("127.0.0.1");
        tcpDiscoverySpi.setLocalPort(48500);
        tcpDiscoverySpi.setLocalPortRange(10);

        // tcp communication configuration
        TcpCommunicationSpi tcpCommunicationSpi = new TcpCommunicationSpi();
        tcpCommunicationSpi.setLocalAddress("127.0.0.1");
        tcpCommunicationSpi.setLocalPort(48000);

        // main Ignite configuration
        IgniteConfiguration configuration = new IgniteConfiguration();
        configuration.setIgniteInstanceName("local");
        configuration.setClientMode(true);
        configuration.setCacheConfiguration(cacheConfiguration);
        configuration.setDiscoverySpi(tcpDiscoverySpi);
        configuration.setCommunicationSpi(tcpCommunicationSpi);

        // starting the cluster
        Ignite ignite = Ignition.start(configuration);

        // get or create the cache
        IgniteCache<String, ApiPojo> cache = ignite.getOrCreateCache(cacheConfiguration);

        String dataIdRead = "";

        // write and read cache from the cache
        for (int cpt = 100000; cpt < 100100; cpt++) {
            String id = UUID.randomUUID().toString();
            dataIdRead = id;
            cache.put(id, new ApiPojo(id, "my-api-".concat(String.valueOf(cpt))));
        }

        cache.forEach(stringStringEntry -> System.out.println(">> ".concat(cache.get(stringStringEntry.getKey()).getId())));

        try {
            // Run get() without explicitly calling to loadCache().
            ApiPojo pojo = cache.get(dataIdRead);

            System.out.println("GET Result: " + pojo.getName());
            System.out.println(">> Cache size = " + cache.metrics().getCacheSize());

            // Run SQL without explicitly calling to loadCache().
            QueryCursor<List<?>> cur = cache.query(
                    new SqlFieldsQuery("select id, name from ApiPojo where name like ?").setSchema(CACHE_NAME).setArgs("my-api-100010"));

            System.out.println("SQL Result: " + cur.getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ignite.close();

        exit(0);
    }
}
