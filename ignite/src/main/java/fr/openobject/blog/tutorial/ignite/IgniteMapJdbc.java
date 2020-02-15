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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.store.jdbc.CacheJdbcPojoStoreFactory;
import org.apache.ignite.cache.store.jdbc.JdbcType;
import org.apache.ignite.cache.store.jdbc.JdbcTypeField;
import org.apache.ignite.cache.store.jdbc.dialect.BasicJdbcDialect;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataPageEvictionMode;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.configuration.MemoryConfiguration;
import org.apache.ignite.configuration.MemoryPolicyConfiguration;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

public class IgniteMapJdbc {

    private static final String CACHE_NAME = "apis";

    public static void main(String[] args) {

        // create Derby database
        String database = "cache/derby-ignite-db";
        String protocol = "jdbc:derby:";
        String dbschema = "APP";
        Properties properties = new Properties();
        boolean createDatabase = false;

        if (createDatabase) {
            try (Connection connection = DriverManager.getConnection(protocol + database + ";create=true", properties);) {
                connection.setAutoCommit(true);
                Statement statement = connection.createStatement();
                statement.execute("CREATE TABLE APIPOJO (ID VARCHAR(100) NOT NULL CONSTRAINT APIPOJO_PK PRIMARY KEY, NAME VARCHAR(255))");
                statement.close();
            } catch (SQLException e) {
                System.out.println("Error while instancing Derby database");
                e.printStackTrace();
                exit(2);
            }
        }

        // cache configuration
        CacheConfiguration<String, ApiPojo> cacheConfiguration = new CacheConfiguration<>(CACHE_NAME);
        cacheConfiguration.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        cacheConfiguration.setBackups(1);
        cacheConfiguration.setWriteThrough(true);
        cacheConfiguration.setReadThrough(true);
        cacheConfiguration.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);
        cacheConfiguration.setIndexedTypes(String.class, String.class);
        cacheConfiguration.setSqlSchema(dbschema);

        CacheJdbcPojoStoreFactory cacheJdbcPojoStoreFactory = new CacheJdbcPojoStoreFactory();
        cacheJdbcPojoStoreFactory.setDialect(new BasicJdbcDialect());

        EmbeddedConnectionPoolDataSource dataSource = new EmbeddedConnectionPoolDataSource();
        dataSource.setDataSourceName("ignite");
        dataSource.setDatabaseName(database);

        JdbcType jdbcType = new JdbcType();
        jdbcType.setCacheName(CACHE_NAME);
        jdbcType.setDatabaseSchema(dbschema);
        jdbcType.setDatabaseTable("APIPOJO");
        jdbcType.setKeyType(String.class);
        jdbcType.setValueType(ApiPojo.class);

        JdbcTypeField keyField = new JdbcTypeField();
        keyField.setJavaFieldName("id");
        keyField.setJavaFieldType(String.class);
        keyField.setDatabaseFieldName("id");
        keyField.setDatabaseFieldType(Types.VARCHAR);
        jdbcType.setKeyFields(keyField);

        JdbcTypeField valueFieldId = new JdbcTypeField();
        valueFieldId.setJavaFieldName("id");
        valueFieldId.setJavaFieldType(String.class);
        valueFieldId.setDatabaseFieldName("id");
        valueFieldId.setDatabaseFieldType(Types.VARCHAR);

        JdbcTypeField valueFieldName = new JdbcTypeField();
        valueFieldName.setJavaFieldName("name");
        valueFieldName.setJavaFieldType(String.class);
        valueFieldName.setDatabaseFieldName("name");
        valueFieldName.setDatabaseFieldType(Types.VARCHAR);

        jdbcType.setValueFields(valueFieldId, valueFieldName);

        cacheJdbcPojoStoreFactory.setTypes(jdbcType);
        cacheJdbcPojoStoreFactory.setDataSource(dataSource);
        cacheConfiguration.setCacheStoreFactory(cacheJdbcPojoStoreFactory);

        // cache storage configuration
        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();
        DataRegionConfiguration dataRegionConfiguration = new DataRegionConfiguration();
        dataRegionConfiguration.setName("localRegion");
        dataRegionConfiguration.setPersistenceEnabled(true);
        dataStorageConfiguration.setDataRegionConfigurations(dataRegionConfiguration);

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

        // memory configuration
        MemoryPolicyConfiguration memoryPolicyConfiguration = new MemoryPolicyConfiguration();
        memoryPolicyConfiguration.setName("500MB");
        memoryPolicyConfiguration.setInitialSize(100L * 1024 * 1024);
        memoryPolicyConfiguration.setMaxSize(500L * 1024 * 1024);
        memoryPolicyConfiguration.setPageEvictionMode(DataPageEvictionMode.RANDOM_2_LRU);

        MemoryConfiguration memoryConfiguration = new MemoryConfiguration();
        memoryConfiguration.setMemoryPolicies(memoryPolicyConfiguration);

        // main Ignite configuration
        IgniteConfiguration configuration = new IgniteConfiguration();
        configuration.setIgniteInstanceName("local");
        configuration.setClientMode(false);
        configuration.setCacheConfiguration(cacheConfiguration);
        configuration.setDataStorageConfiguration(dataStorageConfiguration);
        configuration.setDiscoverySpi(tcpDiscoverySpi);
        configuration.setCommunicationSpi(tcpCommunicationSpi);

        // starting the cluster
        Ignite ignite = Ignition.start();
        ignite.active(true);

        // get or create the cache
        IgniteCache<String, ApiPojo> cache = ignite.getOrCreateCache(cacheConfiguration);
        ignite.cache(CACHE_NAME).loadCache(null);

        String dataIdRead = "";

        // bulk write
        try (IgniteDataStreamer<String, ApiPojo> streamer = ignite.dataStreamer(CACHE_NAME)) {
            streamer.allowOverwrite(true);
            for (int cpt = 0; cpt < 100; cpt++) {
                String id = UUID.randomUUID().toString();
                dataIdRead = id;
                streamer.addData(id, new ApiPojo(id, "my-api-".concat(String.valueOf(cpt))));
            }
        }

        // write and read cache from the cache
        for (int cpt = 100000; cpt < 100100; cpt++) {
            String id = UUID.randomUUID().toString();
            cache.put(id, new ApiPojo(id, "my-api-".concat(String.valueOf(cpt))));
        }

        cache.forEach(stringStringEntry -> System.out.println(">> ".concat(cache.get(stringStringEntry.getKey()).getId())));

        try {
            // Run get() without explicitly calling to loadCache().
            ApiPojo pojo = cache.get(dataIdRead);

            System.out.println("GET Result: " + pojo.getName());

            // Run SQL without explicitly calling to loadCache().
            QueryCursor<List<?>> cur = cache.query(
                    new SqlFieldsQuery("select id, name from ApiPojo where name like ?")
                            .setArgs("my-api-1"));

            System.out.println("SQL Result: " + cur.getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ignite.close();

        exit(0);
    }
}
