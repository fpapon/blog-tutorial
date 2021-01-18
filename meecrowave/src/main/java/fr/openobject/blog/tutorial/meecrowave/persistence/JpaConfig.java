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
package fr.openobject.blog.tutorial.meecrowave.persistence;

import fr.openobject.blog.tutorial.meecrowave.persistence.entity.CustomerEntity;
import org.apache.meecrowave.jpa.api.PersistenceUnitInfoBuilder;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.xbean.recipe.ObjectRecipe;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.HashMap;
import java.util.Map;

import static org.apache.xbean.recipe.Option.CASE_INSENSITIVE_PROPERTIES;

@ApplicationScoped
public class JpaConfig {

    @Produces
    public PersistenceUnitInfoBuilder unit(final DataSource ds) {
        return new PersistenceUnitInfoBuilder()
                .setUnitName("openobject")
                .setDataSource(ds)
                .setExcludeUnlistedClasses(true)
                .addManagedClazz(CustomerEntity.class)
                .addProperty("openjpa.RuntimeUnenhancedClasses", "supported")
                .addProperty("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=true)");
    }

    @Produces
    @ApplicationScoped
    public DataSource dataSource() {
        final var recipe = new ObjectRecipe(DataSource.class);
        recipe.allow(CASE_INSENSITIVE_PROPERTIES);
        Map<String, String> datasourceProperties = new HashMap<>();
        datasourceProperties.put("url","jdbc:h2:mem:openobject;DB_CLOSE_DELAY=-1");
        datasourceProperties.put("driverClassName","org.h2.Driver");
        datasourceProperties.put("username","sa");
        datasourceProperties.put("password","");
        recipe.setAllProperties(datasourceProperties);
        return org.apache.tomcat.jdbc.pool.DataSource.class.cast(recipe.create());
    }
}
