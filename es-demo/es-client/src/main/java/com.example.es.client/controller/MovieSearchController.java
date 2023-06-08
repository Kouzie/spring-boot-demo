package com.example.es.client.controller;


import com.example.es.client.dto.MovieSearch;
import com.example.es.client.dto.SearchRequestDto;
import com.example.es.client.service.MovieSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/movie-search")
public class MovieSearchController {

    private final MovieSearchService service;
    private final ObjectMapper mapper;

    @PostMapping("/search")
    public List<MovieSearch> search(@RequestBody SearchRequestDto requestDto) throws IOException {
        SearchResponse response = service.searchByMovieNm(requestDto);
        SearchHits searchHits = response.getHits();
        List<MovieSearch> result = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            MovieSearch movieSearch = mapper.convertValue(sourceAsMap, MovieSearch.class);
            result.add(movieSearch);
        }
        return result;
    }
}