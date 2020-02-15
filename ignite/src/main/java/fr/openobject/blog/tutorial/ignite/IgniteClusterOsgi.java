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

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import org.osgi.annotation.bundle.Header;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Header(name = Constants.BUNDLE_ACTIVATOR, value = "${@class}")
public class IgniteClusterOsgi implements BundleActivator {

    private static final Logger logger = LoggerFactory.getLogger(IgniteClusterOsgi.class);

    private Dictionary<String, Object> properties;

    private URLClassLoader loader;
//    private Thread serverThread;
    private volatile AutoCloseable instance;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        logger.info("starting the cluster...");

        ServiceReference<?> reference = bundleContext.getServiceReference(ConfigurationAdmin.class);
        ConfigurationAdmin configurationAdmin = (ConfigurationAdmin) bundleContext.getService(reference);
        properties = configurationAdmin.getConfiguration("fr.openobject.ignite").getProperties();
        if (properties == null) {
            properties = new Hashtable<>();
        }

        final Collection<URL> paths = new HashSet<>(1);
        String repositoryPath = System.getProperty("karaf.base") + "/" + System.getProperty("karaf.default.repository");
        String version = bundleContext.getBundle().getVersion().toString().replace(".SNAPSHOT","-SNAPSHOT");
        paths.add(new URL("file:".concat(repositoryPath).concat("/fr/openobject/ignite/")
                .concat(version).concat("/ignite-").concat(version).concat(".jar")));

        loader = new URLClassLoader(paths.toArray(new URL[paths.size()]), this.getClass().getClassLoader());
        final Class<?> clusterClass = loader.loadClass("com.yupiik.galaxy.ignite.IgniteCluster");
        instance = AutoCloseable.class.cast(clusterClass.getConstructor(Dictionary.class).newInstance(properties));
        Runnable.class.cast(instance).run();

//        serverThread = new Thread() {
//
//            // server thread
//            {
//                setName(IgniteClusterOsgi.class.getName() + "-server");
//                setContextClassLoader(loader);
//            }
//
//            @Override
//            public void run() {
//                try {
//                    final Class<?> clusterClass = loader.loadClass("fr.openobject.ignite.IgniteCluster");
//                    instance = AutoCloseable.class.cast(clusterClass.getConstructor(Dictionary.class).newInstance(properties));
//                    Runnable.class.cast(instance).run();
//                } catch (final InvocationTargetException ie) {
//                    if (!InterruptedException.class.isInstance(ie.getTargetException())) {
//                        throw new IllegalStateException(ie.getTargetException());
//                    }
//                } catch (final Exception e) {
//                    throw new IllegalStateException(e);
//                }
//            }
//
//            @Override
//            public void interrupt() {
//                try {
//                    instance.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        serverThread.start();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        logger.info("stopping the cluster...");
        if (instance != null) instance.close();
    }
}
