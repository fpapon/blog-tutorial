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
package fr.openobject.karaf.microservices.services.customer.impl;

import fr.openobject.karaf.microservices.services.customer.api.CustomerService;
import fr.openobject.karaf.microservices.services.customer.api.model.Address;
import fr.openobject.karaf.microservices.services.customer.api.model.Customer;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component(immediate = true)
public class CustomerServiceImpl implements CustomerService {

    private Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private Collection<Customer> customerList = new ArrayList<>();
    private Map<String, Address> customerAddressList = new HashMap<>();

    @Activate
    public void activate(ComponentContext componentContext) {
        final Dictionary<String, Object> props;
    }

    @Deactivate
    public void deactivate() {
        //
    }

    @Override
    public Collection<Customer> findAll() {
        return customerList;
    }

    @Override
    public Customer findById(String id) {
        return customerList.stream().filter(customer -> customer.getId().equals(id)).findFirst().get();
    }

    @Override
    public Customer findByLastname(String lastname) {
        return customerList.stream().filter(customer -> customer.getLastname().equals(lastname)).findFirst().get();
    }

    @Override
    public Customer create(Customer customer) {
        customer.setId(UUID.randomUUID().toString());
        customerList.add(customer);
        return customer;
    }

    @Override
    public Customer update(Customer customer) {
        customerList.remove(customer);
        customerList.add(customer);
        return customer;
    }

    @Override
    public void delete(Customer customer) {
        customerList.remove(customer);
    }

    @Override
    public Address findCustomerAddress(String customerId) {
        return this.customerAddressList.get(customerId);
    }

    @Override
    public Address createCustomerAddress(String customerId, Address address) {
        address.setId(UUID.randomUUID().toString());
        return this.customerAddressList.put(customerId, address);
    }

    @Override
    public Address updateCustomerAddress(String customerId, Address address) {
        return this.customerAddressList.replace(customerId, address);
    }

    @Override
    public void deleteCustomerAddress(String customerId, String addressId) {
        this.customerAddressList.remove(customerId, this.customerAddressList.get(customerId).getId());
    }

}
