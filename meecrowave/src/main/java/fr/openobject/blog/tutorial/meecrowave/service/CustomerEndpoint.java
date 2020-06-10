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
package fr.openobject.blog.tutorial.meecrowave.service;

import fr.openobject.blog.tutorial.meecrowave.model.Customer;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
@Path("customer")
public class CustomerEndpoint {

    @Inject
    private Config config;

    private final Map<String, Customer> memoryStorage = new HashMap<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    @Operation(summary = "Get customer by id")
    @APIResponse(responseCode = "404", description = "Customer not found")
    @APIResponse(description = "The customer",
            responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                               schema = @Schema(implementation = Customer.class)))
    public Response find(@Parameter(description = "The id of the customer to be fetched.", required = true) @PathParam("id") String id) {
        return Optional.ofNullable(memoryStorage.get(id))
                .map(customer -> Response.ok(customer).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        return Response.ok(memoryStorage.values()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(Customer customer) {
        customer.setId(UUID.randomUUID().toString());
        memoryStorage.put(customer.getId(), customer);
        return Optional.ofNullable(memoryStorage.get(customer.getId()))
                .map(savedCustomer -> Response.ok(savedCustomer).build())
                .orElse(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
    }

}
