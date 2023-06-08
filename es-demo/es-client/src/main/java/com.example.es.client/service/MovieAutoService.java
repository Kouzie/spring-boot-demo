package com.example.es.client.service;


import com.example.es.client.component.IndexComponent;
import com.example.es.client.dto.MovieAuto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieAutoService {

    private final RestHighLevelClient client;
    private final IndexComponent indexComponent;

    private static final String INDEX_NAME = "movie_rest";
    private static final String TYPE_NAME = "_doc";
    private static final String INDEX_ALIAS_NAME = "movie_auto_alias";

    @PostConstruct
    private void init() throws IOException {
        createIndex();
    }

    public void createIndex() throws IOException {
        XContentBuilder indexBuilder = XContentFactory.jsonBuilder()
                .startObject()
                .startObject(TYPE_NAME)
                .startObject("properties")
                .startObject("movieCd")
                .field("type", "keyword")
                .field("store", "true")
                .field("index_options", "docs")
                .endObject() // end movieCd
                .startObject("movieNm")
                .field("type", "text")
                .field("store", "true")
                .field("index_options", "docs")
                .endObject() // end movieNm
                .startObject("movieNmEn")
                .field("type", "text")
                .field("store", "true")
                .field("index_options", "docs")
                .endObject() // end movieNmEn
                .endObject() // end properties
                .endObject() // end doc
                .endObject(); // end start
        CreateIndexResponse response = indexComponent.createIndex(indexBuilder, INDEX_NAME, INDEX_ALIAS_NAME);
        if (response != null) {
            log.info("create index result:{}", response);
        }
    }

    public DeleteIndexResponse deleteIndex() throws IOException {
        return indexComponent.deleteIndex(INDEX_NAME);
    }

    public OpenIndexResponse openIndex() throws IOException {
        return indexComponent.openIndex(INDEX_NAME);
    }

    public CloseIndexResponse closeIndex() throws IOException {
        return indexComponent.closeIndex(INDEX_NAME);
    }

    public IndexResponse saveDoc(XContentBuilder builder) throws IOException {
        IndexRequest request = new IndexRequest(INDEX_NAME, TYPE_NAME)
                .source(builder);
        // 결과 조회
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        return response;
    }

    public UpdateResponse updateDoc(XContentBuilder builder, String id) throws IOException {
        UpdateRequest request = new UpdateRequest(INDEX_NAME, TYPE_NAME, id)
                .doc(builder);
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        return response;
    }

    public UpdateResponse updateDocAddCount(Integer count, String id) throws IOException {
        UpdateRequest request = new UpdateRequest(INDEX_NAME, TYPE_NAME, id);
        Map<String, Object> parameters = Collections.singletonMap("count", count);
        Script inline = new Script(ScriptType.INLINE, "painless", "ctx._source.prdtYear += params.count", parameters);
        request.script(inline);
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        return response;
    }

    public UpdateResponse upsertDoc(XContentBuilder builder, String id) throws IOException {
        IndexRequest indexRequest = new IndexRequest(INDEX_NAME, TYPE_NAME, id)
                .source(builder);
        XContentBuilder upsertBuilder = XContentFactory.jsonBuilder()
                .startObject()
                .field("createAt", new Date())
                .endObject();
        UpdateRequest upsertRequest = new UpdateRequest(INDEX_NAME, TYPE_NAME, id)
                .doc(upsertBuilder).upsert(indexRequest);
        UpdateResponse response = client.update(upsertRequest, RequestOptions.DEFAULT);
        return response;
    }

    public DeleteResponse deleteDoc(String id) throws IOException {
        DeleteRequest request = new DeleteRequest(INDEX_NAME, TYPE_NAME, id);
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        return response;
    }

    public GetResponse findDoc(String id) throws IOException {
        // 요청
        GetRequest request = new GetRequest(INDEX_NAME, TYPE_NAME, id);
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        return response;
    }

    public boolean isExistDoc(String id) throws IOException {
        GetRequest getRequest = new GetRequest(INDEX_NAME, TYPE_NAME, id);
        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        return exists;
    }

    public BulkResponse bulkAddAll(List<MovieAuto> movieAutos) throws IOException {
        List<DocWriteRequest> docWriteRequests = new ArrayList<>();
        for (MovieAuto movieAuto : movieAutos) {
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("movieCd", movieAuto.getMovieCd())
                    .field("movieNm", movieAuto.getMovieNm())
                    .field("movieNmEn", movieAuto.getMovieNmEn())
                    .field("prdtYear", movieAuto.getGetPrdtYear())
                    .endObject();
            IndexRequest indexRequest = new IndexRequest(INDEX_NAME, TYPE_NAME)
                    .source(builder);
            docWriteRequests.add(indexRequest);
        }
        BulkRequest request = new BulkRequest().add(docWriteRequests);
        return client.bulk(request, RequestOptions.DEFAULT);
    }
}
