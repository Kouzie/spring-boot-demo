package com.example.es.client.controller;


import com.example.es.client.dto.AddCountRequestDto;
import com.example.es.client.dto.MovieAuto;
import com.example.es.client.dto.MovieAutoRequestDto;
import com.example.es.client.service.MovieAutoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.ResourceNotFoundException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/movie-rest")
public class MovieAutoController {

    private final MovieAutoService service;
    private final ObjectMapper mapper;

    @PostMapping("/index")
    public void createIndex() throws IOException {
        service.createIndex();
    }

    @DeleteMapping("/index")
    public void deleteIndex() throws IOException {
        DeleteIndexResponse response = service.deleteIndex();
        log.info("delete index response:{}", response);
    }

    @PostMapping("/index/{status}")
    public void onAndOffIndex(@PathVariable String status) throws IOException {
        if (status.equals("open")) {
            service.openIndex();
        } else {
            service.closeIndex();
        }
    }

    @PostMapping("/doc")
    public String addDoc(@RequestBody MovieAutoRequestDto requestDto) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("movieCd", requestDto.getMovieCd())
                .field("movieNm", requestDto.getMovieNm())
                .field("movieNmEn", requestDto.getMovieNmEn())
                .endObject();
        IndexResponse response = service.saveDoc(builder);
        // add doc response:IndexResponse[index=movie_rest,type=_doc,id=SCyzmogBfkTcbt6LDyqX,version=1,result=created,seqNo=1,primaryTerm=1,shards={"total":2,"successful":1,"failed":0}]
        log.info("add doc response:{}", response);
        return response.getId();
    }

    @GetMapping("/doc/{id}")
    public MovieAuto getDoc(@PathVariable String id) throws IOException {
        GetResponse response = service.findDoc(id);
        // get doc response:{"_index":"movie_rest","_type":"_doc","_id":"SCyzmogBfkTcbt6LDyqX","_version":1,"found":true,"_source":{"movieCd":"20173733","movieNm":"테스트 아이","movieNmEn":"Test Child"}}
        log.info("get doc response:{}", response);
        if (!response.isExists()) {
            throw new ResourceNotFoundException("doc not found", id);
        }
        Map<String, Object> map = response.getSource();
        MovieAuto movieAuto = mapper.convertValue(map, MovieAuto.class);
        movieAuto.setId(response.getId());
        return movieAuto;
    }

    @GetMapping("/doc/{id}/exist")
    public boolean existDoc(@PathVariable String id) throws IOException {
        boolean response = service.isExistDoc(id);
        log.info("exist doc response:{}", response);
        return response;
    }

    @PutMapping("/doc/{id}")
    public String updateDoc(@PathVariable String id, @RequestBody MovieAutoRequestDto requestDto) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("movieCd", requestDto.getMovieCd())
                .field("movieNm", requestDto.getMovieNm())
                .field("movieNmEn", requestDto.getMovieNmEn())
                .field("prdtYear", requestDto.getPrdtYear())
                .endObject();
        try {
            UpdateResponse response = service.updateDoc(builder, id);
            // update doc response:UpdateResponse[index=movie_rest,type=_doc,id=SCyzmogBfkTcbt6LDyqX,version=2,seqNo=2,primaryTerm=1,result=updated,shards=ShardInfo{total=2, successful=1, failures=[]}]
            // response.getResult() [UPDATED, NOOP] 둘중 하나 반환
            log.info("update doc response:{}", response);
            return response.getId();
        } catch (ElasticsearchStatusException e) {
            log.error("ElasticsearchStatusException invoked, msg:{}", e.getMessage());
            throw new ResourceNotFoundException(e.getMessage(), id);
        }
    }

    @PatchMapping("/doc/{id}")
    public void addCountDoc(@PathVariable String id, @RequestBody AddCountRequestDto requestDto) throws IOException {
        UpdateResponse response = service.updateDocAddCount(requestDto.getCount(), id);
        log.info("add count response:{}", response);
    }

    @PostMapping("/doc/{id}/upsert")
    public String upsertDoc(@PathVariable String id, @RequestBody MovieAutoRequestDto requestDto) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("movieCd", requestDto.getMovieCd())
                .field("movieNm", requestDto.getMovieNm())
                .field("movieNmEn", requestDto.getMovieNmEn())
                .field("prdtYear", requestDto.getPrdtYear())
                .endObject();
        // upsert doc response:UpdateResponse[index=movie_rest,type=_doc,id=test1234,version=1,seqNo=0,primaryTerm=1,result=created,shards=ShardInfo{total=2, successful=1, failures=[]}]
        // response.getResult() [created, updated] 둘중 하나
        UpdateResponse response = service.upsertDoc(builder, id);
        log.info("upsert doc response:{}", response);
        return response.getId();
    }

    @DeleteMapping("/doc/{id}")
    public void removeDoc(@PathVariable String id) throws IOException {
        DeleteResponse response = service.deleteDoc(id);
        // delete doc response:DeleteResponse[index=movie_rest,type=_doc,id=test1234,version=3,result=deleted,shards=ShardInfo{total=2, successful=1, failures=[]}]
        // response.getResult() [deleted, not_found] 둘중 하나
        log.info("delete doc response:{}", response);
    }

    @PostMapping("/bulk")
    public List<String> addAll(@RequestBody List<MovieAuto> movieAutos) throws IOException {
        BulkResponse response = service.bulkAddAll(movieAutos);
        log.info("addAll doc response:{}", response);
        return Arrays.stream(response.getItems())
                .map(itemResponse -> itemResponse.getId())
                .collect(Collectors.toList());
    }
}
