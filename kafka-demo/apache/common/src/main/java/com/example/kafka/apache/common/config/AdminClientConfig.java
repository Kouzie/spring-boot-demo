package com.example.kafka.apache.common.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AdminClientConfig {
    @Value("${my.kafka.bootstrap.servers.config}")
    private String bootstrapServersConfig;
    
    @Value("${my.kafka.security.protocol:}")
    private String securityProtocol;
    
    @Value("${my.kafka.sasl.mechanism:}")
    private String saslMechanism;
    
    @Value("${my.kafka.sasl.jaas.config:}")
    private String saslJaasConfig;

    @Bean
    public AdminClient adminClient() {
        Map<String, Object> conf = new HashMap<>();
        conf.put(org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
        conf.put(org.apache.kafka.clients.admin.AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000");
        
        if (securityProtocol != null && !securityProtocol.isEmpty()) {
            conf.put("security.protocol", securityProtocol);
        }
        if (saslMechanism != null && !saslMechanism.isEmpty()) {
            conf.put("sasl.mechanism", saslMechanism);
        }
        if (saslJaasConfig != null && !saslJaasConfig.isEmpty()) {
            conf.put("sasl.jaas.config", saslJaasConfig);
        }
        
        return AdminClient.create(conf);
    }
}

