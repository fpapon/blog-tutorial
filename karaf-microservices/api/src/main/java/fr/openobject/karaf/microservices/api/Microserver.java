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
package fr.openobject.karaf.microservices.api;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.openapi.OpenApiFeature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Microserver {

    private Server server;

    protected void activate(String path, Object serviceBean) {
        JAXRSServerFactoryBean bean = new JAXRSServerFactoryBean();
        bean.setAddress(path);
        bean.setBus(BusFactory.getDefaultBus());
        bean.setProvider(new JacksonJsonProvider());
        List<Feature> features = new ArrayList<>();
        OpenApiFeature feature = new OpenApiFeature();
        feature.setScan(false);
        feature.setUseContextBasedConfig(true);
        feature.setResourceClasses(Collections.singleton(serviceBean.getClass().getCanonicalName()));
        features.add(feature);
        bean.setFeatures(features);
        bean.setServiceBean(serviceBean);
        this.server = bean.create();
    }

    protected void deactivate() throws Exception {
        if (this.server != null) {
            this.server.destroy();
        }
    }

}
