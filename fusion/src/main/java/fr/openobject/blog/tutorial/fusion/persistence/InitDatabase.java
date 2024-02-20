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

import io.yupiik.fusion.framework.api.lifecycle.Start;
import io.yupiik.fusion.framework.api.scope.DefaultScoped;
import io.yupiik.fusion.framework.build.api.event.OnEvent;
import io.yupiik.fusion.framework.build.api.order.Order;
import io.yupiik.fusion.persistence.impl.datasource.tomcat.TomcatDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.stream.Collectors.joining;

@DefaultScoped
public class InitDatabase {
    private final TomcatDataSource dataSource;

    public InitDatabase(final TomcatDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void onEvent(@OnEvent @Order(10) final Start start) {
        dataSource.withConnection(c -> {
            String content;
            try (final var reader = Files.newBufferedReader(Path.of("src/ddl/01-create-database.sql"))) {
                content = reader.lines()
                        .filter(it -> !it.isBlank() && !it.startsWith("--"))
                        .collect(joining("\n"));
            } catch (final IOException e) { // will auto rollback (see withConnection)
                throw new IllegalStateException(e);
            }
            for (final var sql : content.split(";")) {
                if (sql.isBlank()) {
                    continue;
                }
                try (final var stmt = c.prepareStatement(sql.strip())) {
                    stmt.execute();
                }
            }
            if (!c.getAutoCommit()) {
                c.commit();
            }
            return null;
        });
    }
}
