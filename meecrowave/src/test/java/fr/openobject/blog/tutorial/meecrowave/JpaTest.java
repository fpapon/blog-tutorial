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

import fr.openobject.blog.tutorial.meecrowave.persistence.entity.CustomerEntity;
import fr.openobject.blog.tutorial.meecrowave.persistence.repository.CustomerDao;
import org.apache.meecrowave.junit.MonoMeecrowave;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MonoMeecrowave.Runner.class)
public class JpaTest {

    @Inject
    private CustomerDao customerDao;

    @Test
    public void saveAndFindCustomer() {
        final CustomerEntity entity = new CustomerEntity();
        entity.setId(UUID.randomUUID().toString());
        customerDao.save(entity);
        CustomerEntity newCustomer = customerDao.find(entity.getId());
        assertNotNull(newCustomer);
        assertEquals(entity.getId(), newCustomer.getId());
        assertEquals(1, customerDao.findAll().size());
    }

}
