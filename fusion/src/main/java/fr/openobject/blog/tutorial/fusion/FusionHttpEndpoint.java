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
package fr.openobject.blog.tutorial.fusion;

import fr.openobject.blog.tutorial.fusion.model.Customer;
import fr.openobject.blog.tutorial.fusion.persistence.CustomerManager;
import io.yupiik.fusion.framework.build.api.scanning.Bean;
import io.yupiik.fusion.framework.build.api.scanning.Injection;
import io.yupiik.fusion.http.server.api.Response;
import io.yupiik.fusion.http.server.spi.Endpoint;
import io.yupiik.fusion.json.JsonMapper;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FusionHttpEndpoint {

    @Injection
    FusionConfiguration conf;

    @Injection
    JsonMapper jsonMapper;

    @Injection
    CustomerManager customerManager;

    @Bean
    public Endpoint customersAll() {
        return Endpoint.of(
                request -> "GET".equals(request.method()) &&
                        request.path().startsWith("/customers"),

                request -> CompletableFuture.completedStage(
                        Response.of()
                                .status(200)
                                .header("content-type", "application/json")
                                .body(jsonMapper.toString(
                                        new Customer(UUID.randomUUID().toString(), "obiwan", "kenobi", "master", conf.organization())))
                                .build()));
    }

}
