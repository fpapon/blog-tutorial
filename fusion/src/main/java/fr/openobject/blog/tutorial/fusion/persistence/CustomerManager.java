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
import io.yupiik.fusion.persistence.impl.DatabaseConfiguration;
import io.yupiik.fusion.persistence.impl.datasource.SimpleDataSource;

import javax.sql.DataSource;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class CustomerManager {

    private static final Logger logger = Logger.getLogger(CustomerManager.class.getName());

    private Database database;

    protected CustomerManager() {
    }

    public CustomerManager(final DatasourceCustomerConfiguration configuration) {
        DataSource dataSource = new SimpleDataSource(configuration.url(), configuration.username(), configuration.password());
        this.database = Database.of(new DatabaseConfiguration().setDataSource(dataSource));
    }

    public CustomerEntity findCustomer(String id) {
        return database.findById(CustomerEntity.class, id);
    }

    public List<CustomerEntity> findAllCustomer() {
        return database.findAll(CustomerEntity.class);
    }
}
