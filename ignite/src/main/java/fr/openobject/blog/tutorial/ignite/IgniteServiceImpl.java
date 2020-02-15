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

import fr.openobject.blog.tutorial.ignite.api.IgniteService;
import java.util.Arrays;
import java.util.Dictionary;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = {IgniteService.class},
        name = "fr.openobject.blog.tutorial.ignite",
        immediate = true,
        configurationPolicy = ConfigurationPolicy.REQUIRE
)
public class IgniteServiceImpl implements IgniteService {

    private static final Logger logger = LoggerFactory.getLogger(IgniteServiceImpl.class);

    private Ignite ignite;
    private IgniteConfiguration configuration;
    private String instanceMode;

    private String getProperty(
            Dictionary<String, Object> properties, String key, String defaultValue) {
        return (properties.get(key) != null) ? (String) properties.get(key) : defaultValue;
    }

    @Activate
    public void activate(ComponentContext componentContext) throws Exception {
        logger.info("Galaxy :: starting Ignite instance...");
        Dictionary<String, Object> properties = componentContext.getProperties();

        configuration = new IgniteConfiguration();
        instanceMode = getProperty(properties, "ignite.instance.mode", "server");
        String dataRegionName = getProperty(properties, "ignite.cluster.data.region.name", "galaxyLocalRegion");
        String workingDirectory = getProperty(properties, "ignite.cluster.working.directory", System.getProperty("karaf.data", "/tmp").concat("/ignite-").concat(instanceMode));
        String clusterName = getProperty(properties, "ignite.cluster.instance.name", "galaxyLocalInstance");
        String discoveryAddresses = getProperty(properties, "ignite.cluster.discovery.addresses", "127.0.0.1");
        String discoveryLocalAddresses = getProperty(properties, "ignite.cluster.discovery.local.addresses", "127.0.0.1");
        String discoveryLocalPort = getProperty(properties, "ignite.cluster.discovery.local.port", "48500");
        String discoveryLocalPortRange = getProperty(properties, "ignite.cluster.discovery.local.port.range", "10");
        String communicationLocalAddresses = getProperty(properties, "ignite.cluster.communication.local.addresses", "127.0.0.1");
        String communicationLocalPort = getProperty(properties, "ignite.cluster.communication.local.port", "48000");

        // cache storage configuration
        DataRegionConfiguration dataRegionConfiguration = new DataRegionConfiguration();
        dataRegionConfiguration.setName(dataRegionName);
        dataRegionConfiguration.setPersistenceEnabled(instanceMode.equals("server"));

        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();
        dataStorageConfiguration.setDataRegionConfigurations(dataRegionConfiguration);
        dataStorageConfiguration.setStoragePath(workingDirectory.concat("/storage"));
        dataStorageConfiguration.setWalPath(workingDirectory.concat("/wal"));
        dataStorageConfiguration.setWalArchivePath(workingDirectory.concat("/wal-archives"));

        // tcp discovery configuration
        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryMulticastIpFinder tcpDiscoveryMulticastIpFinder = new TcpDiscoveryMulticastIpFinder();
        tcpDiscoveryMulticastIpFinder.setAddresses(Arrays.asList(discoveryAddresses));
        tcpDiscoverySpi.setIpFinder(tcpDiscoveryMulticastIpFinder);
        tcpDiscoverySpi.setLocalAddress(discoveryLocalAddresses);
        tcpDiscoverySpi.setLocalPort(Integer.valueOf(discoveryLocalPort));
        tcpDiscoverySpi.setLocalPortRange(Integer.valueOf(discoveryLocalPortRange));

        // tcp communication configuration
        TcpCommunicationSpi tcpCommunicationSpi = new TcpCommunicationSpi();
        tcpCommunicationSpi.setLocalAddress(communicationLocalAddresses);
        tcpCommunicationSpi.setLocalPort(Integer.valueOf(communicationLocalPort));

        // main Ignite configuration
        configuration.setIgniteInstanceName(clusterName);
        configuration.setWorkDirectory(workingDirectory);
        configuration.setClientMode(instanceMode.equals("client"));
        configuration.setDataStorageConfiguration(dataStorageConfiguration);
        configuration.setDiscoverySpi(tcpDiscoverySpi);
        configuration.setCommunicationSpi(tcpCommunicationSpi);
        configuration.setMetricsLogFrequency(0);
    }

    @Deactivate
    public void deactivate(BundleContext bundleContext) {
        if (ignite != null) ignite.close();
    }

    @Override
    public void start() {
        ignite = Ignition.start(configuration);
    }

    @Override
    public void activate() {
        if ("server".equals(instanceMode)) {
            ignite.active(true);
        }
    }

    @Override
    public void stop() {
        if (ignite != null) ignite.close();
    }
}
