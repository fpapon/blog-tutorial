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
package fr.openobject.blog.tutorial.fusion.persistence;

import fr.openobject.blog.tutorial.fusion.model.CustomerEntity;
import io.yupiik.fusion.framework.api.scope.ApplicationScoped;
import io.yupiik.fusion.persistence.api.Database;
import io.yupiik.fusion.persistence.impl.datasource.tomcat.TomcatDataSource;

import java.util.List;

@ApplicationScoped
public class CustomerDao {

    private final Database database;

    private final TomcatDataSource dataSource;

    public CustomerDao(final Database database, final TomcatDataSource dataSource) {
        this.database = database;
        this.dataSource = dataSource;
    }

    public CustomerEntity findCustomer(final String id) {
        return database.findById(CustomerEntity.class, id);
    }

    public List<CustomerEntity> findAllCustomer() {
        return dataSource.read(() -> database.findAll(CustomerEntity.class));
    }
}
