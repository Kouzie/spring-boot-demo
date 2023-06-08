package com.example.es.client.service;


import com.example.es.client.dto.SearchRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieSearchService {

    private final RestHighLevelClient client;

    private static final String INDEX_NAME = "movie_search";
    private static final String TYPE_NAME = "_doc";

    public SearchResponse searchByMovieNm(SearchRequestDto requestDto) throws IOException {
        String fieldName = "movieNm";
        // 검색 쿼리 설정
        QueryBuilder query = QueryBuilders.matchQuery(fieldName, requestDto.getMovieNm());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(query)
                .from(0)
                .size(5)
                .sort(new FieldSortBuilder("movieCd").order(SortOrder.DESC));
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        searchRequest.types(TYPE_NAME);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return searchResponse;
    }
}
