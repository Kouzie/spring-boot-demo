package com.example.es.spring.model;

import com.example.es.spring.dto.AddMemberRequestDto;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

@Getter
@Document(indexName = "member")
@Mapping(mappingPath = "member_mapping.json")
public class MemberDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    protected MemberDocument() {
    }

    public MemberDocument(AddMemberRequestDto requestDto) {
        this.name = requestDto.getName();
    }
}