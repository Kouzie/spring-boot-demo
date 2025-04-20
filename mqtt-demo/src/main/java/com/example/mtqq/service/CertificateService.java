package com.example.mtqq.service;

import com.example.mtqq.adaptor.CertificateAdaptor;
import com.example.mtqq.dto.KeyPairResponseDto;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateAdaptor certificateAdaptor;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public KeyPairResponseDto issueCertificate(String cn) throws Exception {
        // 1. 클라이언트 키쌍 생성
        KeyPair clientKeyPair = certificateAdaptor.generateKeyPair();

        // 2. Subject (클라이언트 정보), X500NameBuilder를 이용해 DN 구성
        X500Name clientSubject = new X500NameBuilder(BCStyle.INSTANCE)
                .addRDN(BCStyle.CN, cn)
                .addRDN(BCStyle.O, "MyCompany")
                .addRDN(BCStyle.OU, "Development")
                .addRDN(BCStyle.C, "KR")
                .addRDN(BCStyle.ST, "Seoul")
                .addRDN(BCStyle.L, "Gangnam")
                .build();

        // 3. intermediate CA 인증서 및 개인키 로드
        X509Certificate intermediateCert = certificateAdaptor.loadIntermediateCa();
        PrivateKey intermediatePrivateKey = certificateAdaptor.getIntermediatePrivateKey();

        // 4. root CA 인증서 로드
        X509Certificate rootCert = certificateAdaptor.loadRootCertificate();

        // 5. 인증서 유효기간 설정
        Date notBefore = new Date();
        Date notAfter = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000); // 1년

        // 6. 클라이언트 인증서 생성 (서명 전)
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                intermediateCert,                                      // issuer = intermediate CA
                BigInteger.valueOf(System.currentTimeMillis()),        // serial number
                notBefore,
                notAfter,
                clientSubject,
                clientKeyPair.getPublic()
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider("BC")
                .build(intermediatePrivateKey);

        X509Certificate clientCert = new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certBuilder.build(signer));

        // 3. PEM 문자열로 변환
        String privatePem = certificateAdaptor.convertPrivateToPcks8(clientKeyPair.getPrivate());
        String clientCertPem = certificateAdaptor.convertToPem(clientCert);
        String intermediateCertPem = certificateAdaptor.convertToPem(intermediateCert); // Intermediate CA 인증서
        String rootCertPem = certificateAdaptor.convertToPem(rootCert); // Root CA 인증서
        String fullCertChainPem = clientCertPem + "\n" + intermediateCertPem + "\n" + rootCertPem;

        // 4. 응답 구성
        return new KeyPairResponseDto(fullCertChainPem, privatePem);
    }
}
