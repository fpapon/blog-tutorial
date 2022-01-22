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
package fr.openobject.karaf.microservices.endpoints.customer.address;

import fr.openobject.karaf.microservices.services.customer.api.CustomerService;
import fr.openobject.karaf.microservices.services.customer.api.model.Address;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;
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

@Path("/address")
@Component(service = CustomerAddressEndpoint.class, scope = ServiceScope.PROTOTYPE)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=customer-application)")
public class CustomerAddressEndpoint {

    private final Logger logger = LoggerFactory.getLogger(CustomerAddressEndpoint.class);

    @Reference private CustomerService customerService;

    @Path("/{customerId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findCustomerAddress(@PathParam("customerId") String customerId) {

        Address address = customerService.findCustomerAddress(customerId);
        if (address != null) {
            return Response.ok(address).build();
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
