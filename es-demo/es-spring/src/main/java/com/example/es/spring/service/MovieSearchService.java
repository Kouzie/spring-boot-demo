package com.example.es.spring.service;

import com.example.es.spring.model.MovieSearch;
import com.example.es.spring.repository.MovieSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieSearchService {
    private final MovieSearchRepository repository;
    private final ElasticsearchOperations operations;

    public MovieSearch save() {
        MovieSearch movieSearch = new MovieSearch();
        movieSearch = repository.save(movieSearch);
        return movieSearch;
    }

    public List<MovieSearch> findByMovieNm(String movieNm, Pageable pageable) {
        return repository.findByMovieNm(movieNm, pageable);
    }

    public List<MovieSearch> findByQuery(Query query) {
        SearchHits<MovieSearch> searchHits = operations.search(query, MovieSearch.class);
        return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }
}
