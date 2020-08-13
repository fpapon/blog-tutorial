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
package fr.openobject.karaf.microservices.services.customer.api;

import fr.openobject.karaf.microservices.services.customer.api.model.Address;
import fr.openobject.karaf.microservices.services.customer.api.model.Customer;

import java.util.Collection;

public interface CustomerService {

    Collection<Customer> findAll();

    Customer findById(String id);

    Customer findByLastname(String name);

    Customer create(Customer customer);

    Customer update(Customer customer);

    void delete(Customer customer);

    Address findCustomerAddress(String customerId);

    Address createCustomerAddress(String customerId, Address address);

    Address updateCustomerAddress(String customerId, Address address);

    void deleteCustomerAddress(String customerId, String addressId);

}
