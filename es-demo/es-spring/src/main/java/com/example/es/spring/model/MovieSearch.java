package com.example.es.spring.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import java.util.List;

@Getter
@Setter
@Document(indexName = "movie_search")
@Mapping(mappingPath = "movie_search_mapping.json")
public class MovieSearch {
    @Id
    String id;
    String movieCd; // keyword "20174647",
    String movieNm; // text "튼튼이의 모험"
    String movieNmEn; // text "Loser’s Adventure"
    String prdtYear; // integer "2017"
    String openDt; // integer "20180131"
    String typeNm; // keyword "장편"
    String prdtStatNm; // keyword "기타", "개봉예정"
    String nationAlt; // keyword "한국"
    List<String> genreAlt; // keyword, ["드라마"],
    String repNationNm; // keyword "한국"
    String repGenreNm; // keyword "드라마"
    List<Director> directors; //[{ "peopleNm":"고봉수" }],
    List<Company> companys;

    @Getter
    @Setter
    public static class Director {
        String peopleNm; // keyword
    }

    @Getter
    @Setter
    public static class Company {
        String companyCd;
        String companyNm;
    }
}
