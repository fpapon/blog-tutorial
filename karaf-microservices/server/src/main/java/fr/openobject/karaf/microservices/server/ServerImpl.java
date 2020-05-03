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
package fr.openobject.karaf.microservices.server;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import fr.openobject.karaf.microservices.api.Microservice;
import fr.openobject.karaf.microservices.api.Server;
import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharingFilter;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.ws.rs.core.Application;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

@Component(immediate = true)
public class ServerImpl extends Application implements Server  {

    private Logger logger = LoggerFactory.getLogger(ServerImpl.class);

    private String alias = "/api";

    private Dictionary<String, Object> properties;

    @Reference private HttpService httpService;

    private Set<Microservice> microservices;

    private ServiceTracker<?,?> serviceTracker;

    private Servlet restServlet;

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(CrossOriginResourceSharingFilter.class);
        classes.add(JacksonJsonProvider.class);
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> set = new HashSet<>();
        set.addAll(microservices);
        return set;
    }

    @Activate
    public void activate(ComponentContext componentContext) throws InvalidSyntaxException {
        this.microservices = new HashSet<>();
        this.properties = componentContext.getProperties();
        this.serviceTracker = new ServiceTracker<>(componentContext.getBundleContext(),
                componentContext.getBundleContext().createFilter("(|(" + Constants.OBJECTCLASS + "=" + Microservice.class.getName() + "))"),
                new ServiceTrackerCustomizer<>() {

                    public synchronized Object addingService(final ServiceReference reference) {
                        final Object obj = componentContext.getBundleContext().getService(reference);
                        microservices.add(Microservice.class.cast(obj));
                        unregister();
                        register();
                        return obj;
                    }

                    public synchronized void  removedService(final ServiceReference reference, final Object service) {
                        microservices.remove(Microservice.class.cast(service));
                        componentContext.getBundleContext().ungetService(reference);
                        unregister();
                        register();
                    }

                    public synchronized void modifiedService(final ServiceReference reference, final Object service) {
                        //do nothing
                    }
                });
        this.serviceTracker.open();

        Collection<ServiceReference<Microservice>> list = componentContext.getBundleContext().getServiceReferences(Microservice.class,null);
        list.stream().forEach(ref -> this.microservices.add(componentContext.getBundleContext().getService(ref)));
    }

    @Deactivate
    public void deactivate() {
        httpService.unregister(alias);
    }

    private void register() {
        final Dictionary<String, Object> restServletProps = new Hashtable<>();
        restServletProps.put("servlet-name", "microservices-servlet");
        this.restServlet = new CXFNonSpringJaxrsServlet(this);
//        OpenApiFeature feature = new OpenApiFeature();
//        feature.setScan(false);
//        feature.setUseContextBasedConfig(true);
//        restServletProps.put("jaxrs.features", feature);
        try {
            httpService.registerServlet(alias, restServlet, restServletProps, null);
        } catch (ServletException | NamespaceException e) {
            logger.error("Unable to register servlet");
            throw new ComponentException("Unable to register servlet");
        }
        logger.info("Jaxrs servlet register");
    }

    private void unregister() {
        if (this.restServlet != null) {
            httpService.unregister(alias);
            this.restServlet = null;
        }
    }
}
