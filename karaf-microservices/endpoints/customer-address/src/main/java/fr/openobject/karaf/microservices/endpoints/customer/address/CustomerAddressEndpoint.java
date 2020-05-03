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
package fr.openobject.karaf.microservices.endpoints.customer.address;

import fr.openobject.karaf.microservices.api.Microservice;
import fr.openobject.karaf.microservices.services.customer.api.CustomerService;
import fr.openobject.karaf.microservices.services.customer.api.model.Address;
import fr.openobject.karaf.microservices.services.customer.api.model.Customer;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Dictionary;

@Path("/customer-address")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true)
@SecuritySchemes(@SecurityScheme(type = SecuritySchemeType.HTTP, scheme = "bearer"))
@Component(immediate = true, service = Microservice.class)
public class CustomerAddressEndpoint implements Microservice {

    private Logger logger = LoggerFactory.getLogger(CustomerAddressEndpoint.class);

    private Dictionary<String, Object> properties;

    @Reference private CustomerService customerService;

    @Activate
    public void activate(ComponentContext componentContext) {
        this.properties = componentContext.getProperties();
        //
    }

    @Deactivate
    public void deactivate() {
        //
    }

    @Path("/{customerId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findCustomerAddress(@PathParam("customerId") String customerId) {

        Customer customer = customerService.findById(customerId);
        if (customer != null) {
            return Response.ok(customer).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/{customerId}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCustomerAddress(@PathParam("customerId") String customerId, Address address) {

        address = customerService.createCustomerAddress(customerId, address);
        if (address != null) {
            return Response.ok(address).build();
        } else {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    @Path("/{customerId}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomerAddress(@PathParam("customerId") String customerId, Address address) {

        address = customerService.updateCustomerAddress(customerId, address);
        if (address != null) {
            return Response.ok(address).build();
        } else {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    @Path("/{customerId}/{addressId}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomerAddress(@PathParam("customerId") String customerId, @PathParam("addressId") String addressId) {

        customerService.deleteCustomerAddress(customerId, addressId);
        return Response.ok().build();
    }
}
