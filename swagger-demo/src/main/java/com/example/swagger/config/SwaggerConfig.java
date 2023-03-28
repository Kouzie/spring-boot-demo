//package com.example.swagger.config;
//
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.actuate.endpoint.SecurityContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//
//import java.util.Collections;
//import java.util.Date;
//import java.util.Set;
//
//@Profile({"dev", "stg"})
//@Configuration
//public class SwaggerConfig {
//
//    private String applicationName = "swagger-demo";
//    private String version = "0.0.1";
//
//    private static final String HEADER_AUTH = "Authorization";
//    private static final String BASE_URL = "localhost";
//
//    @Bean
//    public Docket api() {
//        Server serverLocal = new Server("local", "http://localhost:8080", "local profile", Collections.emptyList(), Collections.emptyList());
//        Server serverProfile = new Server("dev", "http://kouzie.com", "dev profile", Collections.emptyList(), Collections.emptyList());
//        return new Docket(DocumentationType.OAS_30)
//                .apiInfo(getApiInfo())
//                .servers(serverLocal, serverProfile)
//                .consumes(Set.of("application/json;charset=UTF-8")) //Request Content-Type
//                .produces(Set.of("application/json;charset=UTF-8")) //Response Content-Type .pathMapping("/")
//                .forCodeGeneration(true)
//                .ignoredParameterTypes(Date.class)
//                .directModelSubstitute(java.time.LocalDate.class, Date.class)
//                .directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
//                .directModelSubstitute(java.time.LocalDateTime.class, Date.class)
//                .securitySchemes(Collections.singletonList(apiKey()))
//                .securityContexts(Collections.singletonList(securityContext()))
//                .useDefaultResponseMessages(false)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.kouzie.app." + applicationName + ".controller")) //Swagger API 문서로 만들기 원하는 basePackage 경로
//                .paths(PathSelectors.any()) // URL 경로를 지정하여 해당 URL에 해당하는 요청만 Swagger API 문서로 만듭
//                .build();
//    }
//
//    private ApiInfo getApiInfo() {
//        return new ApiInfoBuilder()
//                .title(String.format("kouzie API SERVICE(%s)", applicationName))
//                .description(String.format("kouzie API SERVICE(%s)", applicationName))
//                .contact(new Contact(String.format("kouzie API SERVICE(%s)", applicationName),
//                        "https://localhost:8080",
//                        "kouzie@kouzie.com"))
//                .version(version)
//                .build();
//    }
//
//    private ApiKey apiKey() {
//        return new ApiKey(HEADER_AUTH, HEADER_AUTH, "header");
//    }
//
//    private SecurityContext securityContext() {
//        return SecurityContext.builder()
//                .securityReferences(Collections.singletonList(defaultAuth()))
//                .forPaths(PathSelectors.any())
//                .build();
//    }
//
//    SecurityReference defaultAuth() {
//        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return new SecurityReference(HEADER_AUTH, authorizationScopes);
//    }
//}