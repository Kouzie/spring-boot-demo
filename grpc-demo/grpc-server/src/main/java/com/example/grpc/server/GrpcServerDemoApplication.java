package com.example.grpc.server;

import com.example.grpc.server.service.RouteGuideServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;

@SpringBootApplication
public class GrpcServerDemoApplication {

    public static void main(String[] args) throws InterruptedException, IOException {
        SpringApplication.run(GrpcServerDemoApplication.class, args);
        RouteGuideServer routeGuideServer = SpringContext.getBean(RouteGuideServer.class);
        routeGuideServer.start();
        routeGuideServer.blockUntilShutdown();
    }
}
