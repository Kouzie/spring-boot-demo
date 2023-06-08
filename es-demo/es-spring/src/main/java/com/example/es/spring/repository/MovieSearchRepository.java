package com.example.es.spring.repository;

import com.example.es.spring.model.MovieSearch;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MovieSearchRepository extends ElasticsearchRepository<MovieSearch, String> {
    List<MovieSearch> findByMovieNm(String movieNm, Pageable pageable);
}
