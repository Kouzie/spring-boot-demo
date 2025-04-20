package com.example.mtqq.adaptor;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.springframework.stereotype.Component;

import javax.security.auth.x500.X500Principal;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Component
@RequiredArgsConstructor
public class CertificateAdaptor {
    private X509Certificate rootCaCert;
    private X509Certificate intermediateCaCert;
    private PrivateKey intermediateCaKey;
    private X509Certificate clientCaCert;
    private PrivateKey clientCaKey;

    @PostConstruct
    private void init() throws IOException, CertificateException {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        // root CA 인증서와 키 로드
        try (InputStream certStream = getClass().getClassLoader().getResourceAsStream("cert/root_ca.crt");
             Reader reader = new InputStreamReader(certStream);
             PEMParser certParser = new PEMParser(reader)) {
            this.rootCaCert = new JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate((X509CertificateHolder) certParser.readObject());
        }
        // 중간 CA 인증서와 키 로드
        try (InputStream certStream = getClass().getClassLoader().getResourceAsStream("cert/intermediate_ca.crt");
             Reader reader = new InputStreamReader(certStream);
             PEMParser certParser = new PEMParser(reader)) {
            this.intermediateCaCert = new JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate((X509CertificateHolder) certParser.readObject());
        }
        try (InputStream keyStream = getClass().getClassLoader().getResourceAsStream("cert/intermediate_ca.key");
             Reader keyReader = new InputStreamReader(keyStream);
             PEMParser keyParser = new PEMParser(keyReader)) {

            Object obj = keyParser.readObject();
            if (obj instanceof PEMKeyPair) {
                this.intermediateCaKey = converter.getPrivateKey(((PEMKeyPair) obj).getPrivateKeyInfo());
            } else if (obj instanceof PrivateKeyInfo) {
                this.intermediateCaKey = converter.getPrivateKey((PrivateKeyInfo) obj);
            } else {
                throw new IllegalArgumentException("Unsupported key format: " + obj.getClass().getName());
            }
        }

        // client CA 인증서와 키 로드
        try (InputStream certStream = getClass().getClassLoader().getResourceAsStream("cert/client_123.crt");
             Reader reader = new InputStreamReader(certStream);
             PEMParser certParser = new PEMParser(reader)) {
            this.intermediateCaCert = new JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate((X509CertificateHolder) certParser.readObject());
        }
        try (InputStream keyStream = getClass().getClassLoader().getResourceAsStream("cert/client_123.key");
             Reader keyReader = new InputStreamReader(keyStream);
             PEMParser keyParser = new PEMParser(keyReader)) {

            Object obj = keyParser.readObject();
            if (obj instanceof PEMKeyPair) {
                this.intermediateCaKey = converter.getPrivateKey(((PEMKeyPair) obj).getPrivateKeyInfo());
            } else if (obj instanceof PrivateKeyInfo) {
                this.intermediateCaKey = converter.getPrivateKey((PrivateKeyInfo) obj);
            } else {
                throw new IllegalArgumentException("Unsupported key format: " + obj.getClass().getName());
            }
        }
    }

    public X500Principal getIntermediateCaIssuer() {
        return new X500Principal(intermediateCaCert.getSubjectX500Principal().getName());
    }

    public PrivateKey getIntermediatePrivateKey() {
        return intermediateCaKey;
    }

    public X509Certificate loadIntermediateCa() {
        return intermediateCaCert;
    }

    public PrivateKey getClientPrivateKey() {
        return clientCaKey;
    }

    public X509Certificate loadClientCa() {
        return clientCaCert;
    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }


    public static String convertToPem(Object obj) throws IOException {
        StringWriter writer = new StringWriter();
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(writer)) {
            pemWriter.writeObject(obj);
        }
        return writer.toString();
    }

    public static String convertPrivateToPcks8(PrivateKey privateKey) throws IOException {
        StringWriter writer = new StringWriter();
        try (JcaPEMWriter w = new JcaPEMWriter(writer)) {
            w.writeObject(new JcaPKCS8Generator(privateKey, null));
        }
        return writer.toString();
    }

    public X509Certificate loadRootCertificate() {
        return rootCaCert;
    }
}
