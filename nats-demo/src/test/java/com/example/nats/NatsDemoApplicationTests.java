package com.example.nats;

import com.example.nats.component.core.CoreNatsComponent;
import com.example.nats.component.jetstream.JetStreamComponent;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.JetStream;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
class NatsDemoApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CoreNatsComponent coreNatsComponent;

    @Autowired
    private JetStreamComponent jetStreamComponent;

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public Connection initConnection() throws IOException {
            Connection connection = Mockito.mock(Connection.class);
            Dispatcher dispatcher = Mockito.mock(Dispatcher.class);
            JetStream jetStream = Mockito.mock(JetStream.class);

            Mockito.when(connection.createDispatcher(ArgumentMatchers.any())).thenReturn(dispatcher);
            Mockito.when(connection.createDispatcher()).thenReturn(dispatcher);
            Mockito.when(connection.jetStream()).thenReturn(jetStream);

            return connection;
        }

        @Bean
        @Primary
        public JetStream jetStream() {
            return Mockito.mock(JetStream.class);
        }
    }

    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
        assertThat(coreNatsComponent).isNotNull();
        assertThat(jetStreamComponent).isNotNull();
    }
}
