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
import io.yupiik.fusion.persistence.api.Entity;
import io.yupiik.fusion.persistence.impl.DatabaseImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class CustomerEntityImpl implements Entity<CustomerEntity> {

    private final DatabaseImpl database;

    // table definition
    private final String table;
    private final Map<String, Entity.ColumnModel> fields;
    private final List<Entity.IdColumnModel> idFields;
    private final Collection<Entity.ColumnModel> insertFields;

    // pre-built queries
    private final String findByIdQuery;
    private final String updateQuery;
    private final String deleteQuery;
    private final String insertQuery;
    private final String findAllQuery;
    private final boolean autoIncremented;

    public CustomerEntityImpl(final DatabaseImpl database, String table, boolean autoIncremented) {
        this.database = database;
        this.table = database.getTranslation().wrapTableName(table);
        this.autoIncremented = true;

        this.fields = Map.of("id", new Entity.ColumnModel("id", String.class, false, null),
                "firstname", new Entity.ColumnModel("firstname", String.class, false, null),
                "lastname", new Entity.ColumnModel("lastname", String.class, false, null),
                "title", new Entity.ColumnModel("title", String.class, false, null),
                "organization", new Entity.ColumnModel("organization", String.class, true, null));
        this.idFields = List.of(new Entity.IdColumnModel("id", String.class, null, Objects.hash("id"), true));

        if (autoIncremented) {
            this.insertFields = fields.values().stream()
                    .filter(it -> it.field != idFields.get(0).field)
                    .collect(toList());
        } else {
            this.insertFields = fields.values();
        }

        final var byIdWhereClause = " WHERE " + idFields.stream()
                .map(f -> database.getTranslation().wrapFieldName(f.field) + " = ?")
                .collect(joining(" AND "));

        final var fieldNamesCommaSeparated = fields.values().stream()
                .map(f -> database.getTranslation().wrapFieldName(f.field))
                .collect(joining(", "));

        final var insertFieldsCommaSeparated = autoIncremented ?
                fields.values().stream()
                        .filter(it -> !Objects.equals(it.field, idFields.get(0).field))
                        .map(f -> database.getTranslation().wrapFieldName(f.field))
                        .collect(joining(", ")) :
                fieldNamesCommaSeparated;

        this.findByIdQuery = "" +
                "SELECT " +
                fieldNamesCommaSeparated +
                " FROM " + table +
                byIdWhereClause;

        this.updateQuery = "" +
                "UPDATE " + table + " SET " +
                fields.values().stream().map(f -> database.getTranslation().wrapFieldName(f.field) + " = ?").collect(joining(", ")) +
                byIdWhereClause;

        this.deleteQuery = "" +
                "DELETE FROM " + table + byIdWhereClause;

        this.insertQuery = "" +
                "INSERT INTO " + table + " (" + insertFieldsCommaSeparated + ") " +
                "VALUES (" + insertFields.stream()
                .map(f -> "?")
                .collect(joining(", ")) + ")";

        this.findAllQuery = "" +
                "SELECT " + fieldNamesCommaSeparated +
                " FROM " + table;
    }

    @Override
    public String[] ddl() {
        return new String[0];
    }

    @Override
    public Class<?> getRootType() {
        return this.getClass();
    }

    @Override
    public String getTable() {
        return this.table;
    }

    @Override
    public String getFindByIdQuery() {
        return this.findByIdQuery;
    }

    @Override
    public String getFindByAllQuery() {
        return this.findAllQuery;
    }

    @Override
    public String getUpdateQuery() {
        return this.updateQuery;
    }

    @Override
    public String getDeleteQuery() {
        return this.deleteQuery;
    }

    @Override
    public String getInsertQuery() {
        return this.insertQuery;
    }

    @Override
    public String getFindAllQuery() {
        return this.findAllQuery;
    }

    @Override
    public List<ColumnMetadata> getOrderedColumns() {
        return null;
    }

    @Override
    public boolean isAutoIncremented() {
        return this.autoIncremented;
    }

    @Override
    public void onInsert(Object instance, PreparedStatement statement) {

    }

    @Override
    public void onDelete(Object instance, PreparedStatement statement) {

    }

    @Override
    public void onUpdate(Object instance, PreparedStatement statement) {

    }

    @Override
    public void onFindById(PreparedStatement stmt, Object id) {

    }

    @Override
    public CustomerEntity onAfterInsert(Object instance, PreparedStatement statement) {
        return null;
    }

    @Override
    public String concatenateColumns(ColumnsConcatenationRequest request) {
        return null;
    }

    @Override
    public Function mapFromPrefix(String prefix, ResultSet resultSet) {
        return null;
    }

    @Override
    public Function mapFromPrefix(String prefix, String... columnNames) {
        return null;
    }

    @Override
    public Function<ResultSet, CustomerEntity> nextProvider(String[] columns, ResultSet rset) {
        return null;
    }

    @Override
    public Function<ResultSet, CustomerEntity> nextProvider(ResultSet resultSet) {
        return null;
    }
}
