package com.example.es.spring.controller;

import com.example.es.spring.dto.AddMemberRequestDto;
import com.example.es.spring.model.MemberDocument;
import com.example.es.spring.model.MovieSearch;
import com.example.es.spring.service.MemberDocumentService;
import com.example.es.spring.service.MovieSearchService;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final MovieSearchService movieSearchService;
    private final MemberDocumentService memberDocumentService;

    @GetMapping("/movie-search/movieNm/{movieNm}")
    public List<MovieSearch> getByName(@PathVariable String movieNm,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        List<MovieSearch> articleByAuthorName = movieSearchService.findByMovieNm(movieNm, pageable);
        return articleByAuthorName;
    }

    @PostMapping("/movie-search/query")
    public List<MovieSearch> getByQuery() {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("movieNm", "우리"))
                .build();
        List<MovieSearch> result = movieSearchService.findByQuery(searchQuery);
        return result;
    }

    @PostMapping("/member")
    public MemberDocument addMember(@RequestBody AddMemberRequestDto requestDto) {
        return memberDocumentService.save(new MemberDocument(requestDto));
    }
}
