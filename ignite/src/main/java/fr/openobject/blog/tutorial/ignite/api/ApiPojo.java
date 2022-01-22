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
package fr.openobject.blog.tutorial.ignite.api;

import java.io.Serializable;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class ApiPojo implements Serializable {

    private static final long serialVersionUID = 0L;

    @QuerySqlField
    private String id;
    @QuerySqlField
    private String name;

    public ApiPojo() {
    }

    public ApiPojo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof ApiPojo))
            return false;

        ApiPojo that = (ApiPojo)o;

        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;


        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;

        if (id != that.id)
            return false;

        return true;
    }

    /** {@inheritDoc} **/
    @Override public int hashCode() {
        int res = id != null ? id.hashCode() : 0;

        res = 31 * res + (name != null ? name.hashCode() : 0);

        res = 31 * res + (id.hashCode());

        return res;
    }

    /** {@inheritDoc} **/
    @Override public String toString() {
        return "ApiPojo [" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "]";
    }

}
