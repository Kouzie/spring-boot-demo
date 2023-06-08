package com.example.es.spring.repository;

import com.example.es.spring.model.MemberDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MemberDocumentRepository extends ElasticsearchRepository<MemberDocument, String> {
}
