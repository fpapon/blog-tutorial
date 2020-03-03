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
package fr.openobject.blog.tutorial.meecrowave;

import fr.openobject.blog.tutorial.meecrowave.model.Address;
import fr.openobject.blog.tutorial.meecrowave.model.Customer;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import javax.ws.rs.core.Response;
import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.junit.MonoMeecrowave;
import org.apache.meecrowave.testing.ConfigurationInject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(MonoMeecrowave.Runner.class)
public class CustomerTest {

    @ConfigurationInject
    private Meecrowave.Builder configuration;

    @Test
    public void saveAndFindCustomer() {
        final Client client = ClientBuilder.newClient();
        try {
            Customer customer = new Customer();
            customer.setFirstname("Obiwan");
            customer.setLastname("Kenobi");
            customer.setNationalId("123451347665");
            customer.setAddress(new Address());
            customer.getAddress().setRoadNumber("245");
            customer.getAddress().setRoadName("Liberty St");
            customer.getAddress().setExtra1("Floor 1");
            customer.getAddress().setExtra2("Apt 23");
            customer.getAddress().setZipCode("CA-94114");
            customer.getAddress().setCity("San Francisco");
            customer.getAddress().setCountry("USA");

            Response reponse = client.target("http://localhost:" + configuration.getHttpPort())
                    .path("tutorial/customer")
                    .request(APPLICATION_JSON_TYPE)
                    .post(Entity.json(customer));
            Assert.assertNotNull(reponse.getEntity());
            String customerId = Customer.class.cast(reponse.getEntity()).getId();
            Assert.assertNotNull(customerId);

            Customer getCustomer = client.target("http://localhost:" + configuration.getHttpPort())
                    .path("tutorial/customer/".concat(customerId))
                    .request(APPLICATION_JSON_TYPE)
                    .get(Customer.class);
            Assert.assertNotNull(getCustomer);
            Assert.assertEquals(customerId, getCustomer.getId());

        } finally {
            client.close();
        }
    }

}
