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
package fr.openobject.blog.tutorial.meecrowave.persistence.repository;

import fr.openobject.blog.tutorial.meecrowave.persistence.entity.CustomerEntity;
import org.apache.meecrowave.jpa.api.Jpa;
import org.apache.meecrowave.jpa.api.Unit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class CustomerDao {

    @Inject
    @Unit(name = "openobject")
    private EntityManager em;

    public CustomerEntity save(final CustomerEntity customer) {
        em.persist(customer);
        return customer;
    }

    @Jpa(transactional = false)
    public CustomerEntity find(final String id) {
        return em.find(CustomerEntity.class, id);
    }

    @Jpa(transactional = false)
    public List<CustomerEntity> findAll() {
        return em.createQuery("select c from CustomerEntity c", CustomerEntity.class).getResultList();
    }

    public void insert(CustomerEntity entity) {
        em.persist(entity);
        em.flush();
    }

    public void delete(CustomerEntity entity) {
        em.remove(entity);
        em.flush();
    }
}
