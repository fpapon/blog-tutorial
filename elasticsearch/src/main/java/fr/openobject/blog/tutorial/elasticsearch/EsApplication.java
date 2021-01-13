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
package fr.openobject.blog.tutorial.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;

import static java.lang.System.exit;

public class EsApplication {

    public static void main(String[] args) throws IOException {

        HttpHost host = new HttpHost("localhost", 9200);
        final RestClientBuilder builder = RestClient.builder(host);
        RestHighLevelClient client = new RestHighLevelClient(builder);

        final BoolQueryBuilder qb = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("uuid","203bd55d-05c6-497d-8441-7be0114e4400"))
                .must(QueryBuilders.matchQuery("status","PUBLISHED"));

        SearchRequest searchRequest = new SearchRequest("my-index");
        searchRequest.source().from(0).size(20).query(qb);
        final SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        final SearchHit[] hits = response.getHits().getHits();
        System.out.println("Results = " + hits.length);

        exit(0);
    }
}
