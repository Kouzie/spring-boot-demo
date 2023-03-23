package com.example.websock.tomcat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Profile("tomcat")
@Configuration
public class TomcatWebSocketConfig {

    /**
     * spring bean 에 등록된 @ServerEndpoint 객체 노출
     * */
    @Bean
    public ServerEndpointExporter endpointExporter(){
        return new ServerEndpointExporter();
    }
}
