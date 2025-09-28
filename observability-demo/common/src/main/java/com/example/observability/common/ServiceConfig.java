package com.example.observability.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

@Configuration
public class ServiceConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // KotlinModule은 Kotlin 코드를 Java로 변환하는 과정에서 제거됩니다.
        objectMapper.registerModules(
                new ParameterNamesModule(),  // 기본생성자 없어도 직렬화
                new JavaTimeModule() // JSR310 모듈 등록
        );
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true); // [] -> null
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true); // "" -> null
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE); // 네이밍 전략
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // null 필드는 변환 X
        // UnrecognizedPropertyException 처리, 알수없는 필드 처리 X
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        Duration timeout = Duration.ofSeconds(2);
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout) // tcp connection 수립 대기시간
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(timeout); // 읽기 대기시간

        // Kotlin의 apply 블록을 Java 스타일로 변경
        JdkClientHttpRequestFactory factoryWithReadTimeout = new JdkClientHttpRequestFactory(httpClient);
        factoryWithReadTimeout.setReadTimeout(Duration.ofSeconds(5));
        return factoryWithReadTimeout;
    }

    /**
     * spring 6.1 부터 restTemplate 대체
     * spring boot 에서 RestClient.Builder 에 적용
     * */
    @Bean
    public RestClientCustomizer restClientCustomizer(
            ClientHttpRequestFactory factory,
            ObjectMapper objectMapper
    ) {
        return builder -> {
            builder
                    .requestFactory(factory) // ClientHttpRequestFactory 주입
                    .messageConverters(converters -> {
                        converters.removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter); // 기존 jackson converter 제거
                        converters.add(new MappingJackson2HttpMessageConverter(objectMapper)); // Custom ObjectMapper 적용
                    });
        };
    }

    @Bean
    @Primary
    public RestClient restClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder.build();
    }
}
