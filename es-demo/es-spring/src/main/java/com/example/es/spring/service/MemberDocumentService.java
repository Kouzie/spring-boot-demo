package com.example.es.spring.service;


import com.example.es.spring.model.MemberDocument;
import com.example.es.spring.repository.MemberDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDocumentService {
    private final MemberDocumentRepository repository;

    public MemberDocument save(MemberDocument member) {
        return repository.save(member);
    }

}