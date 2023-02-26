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
package fr.openobject.blog.tutorial.fusion.model;

import io.yupiik.fusion.framework.build.api.persistence.Column;
import io.yupiik.fusion.framework.build.api.persistence.Id;
import io.yupiik.fusion.framework.build.api.persistence.OnInsert;
import io.yupiik.fusion.framework.build.api.persistence.OnLoad;
import io.yupiik.fusion.framework.build.api.persistence.Table;

import java.util.Objects;
import java.util.UUID;

@Table("CUSTOMER")
public record CustomerEntity(
        @Id(autoIncremented = false, order = 0) String id,
        @Column String firstname,
        @Column(name = "LAST_NAME") String lastname,
        @Column String title,
        @Column String organization) {

    @OnInsert
    public CustomerEntity onInsert() {
        return id() == null ?
                new CustomerEntity(UUID.randomUUID().toString(), firstname(), lastname(), title(), organization()) :
                this;
    }

    @OnLoad
    public CustomerEntity onLoad() {
        return Objects.isNull(title()) ?
                new CustomerEntity(id(), firstname(), lastname(), "None", organization()) :
                this;
    }
}
