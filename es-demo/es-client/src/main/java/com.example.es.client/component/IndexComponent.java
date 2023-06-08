package com.example.es.client.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndexComponent {
    private final RestHighLevelClient client;
    private static final String TYPE_NAME = "_doc";

    public Boolean isExistIndex(String indexName) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest().indices(indexName);
        boolean existsResult = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        return existsResult;
    }
    public CreateIndexResponse createIndex(XContentBuilder indexBuilder, String indexName, String indexAliasName) throws IOException {
        Boolean existsResult = isExistIndex(indexName);
        if (existsResult) {
            log.info("index already exist");
            return null;
        }
        // mapping 설정
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        createIndexRequest.mapping(TYPE_NAME, indexBuilder);
        createIndexRequest.alias(new Alias(indexAliasName));
        CreateIndexResponse response = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        return response;
    }

    public DeleteIndexResponse deleteIndex(String indexName) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        DeleteIndexResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
        return response;
    }

    public OpenIndexResponse openIndex(String indexName) throws IOException {
        OpenIndexRequest requestOpen = new OpenIndexRequest(indexName);
        OpenIndexResponse response = client.indices().open(requestOpen, RequestOptions.DEFAULT);
        return response;
    }

    public CloseIndexResponse closeIndex(String indexName) throws IOException {
        CloseIndexRequest requestClose = new CloseIndexRequest(indexName);
        CloseIndexResponse response = client.indices().close(requestClose, RequestOptions.DEFAULT);
        return response;
    }
}
