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

import io.yupiik.fusion.framework.build.api.configuration.Property;
import io.yupiik.fusion.framework.build.api.configuration.RootConfiguration;

@RootConfiguration("fr.openobject.blog.tutorial.fusion.datasource.customer")
public record DatasourceCustomerConfiguration(
        @Property(value = "driver", documentation = "Driver to use")
        String driver,

        @Property(value = "url", documentation = "JDBC URL to use", required = true)
        String url,

        @Property(value = "username", documentation = "Database username.")
        String username,

        @Property(value = "password", documentation = "Database password.")
        String password) {
}
