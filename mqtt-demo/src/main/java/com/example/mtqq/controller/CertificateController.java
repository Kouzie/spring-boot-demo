package com.example.mtqq.controller;


import com.example.mtqq.dto.KeyPairResponseDto;
import com.example.mtqq.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.POST})
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping("/issue-certificate")
    public KeyPairResponseDto issueCertificate(@RequestParam String clientId) throws Exception {
        return certificateService.issueCertificate(clientId);
    }
}