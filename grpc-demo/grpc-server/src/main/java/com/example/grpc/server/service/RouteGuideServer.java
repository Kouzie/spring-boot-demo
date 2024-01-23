package com.example.grpc.server.service;


import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RouteGuideServer {

    @Value("${grpc.server.port}")
    private int port;
    private Server server;

    private RouteGuideService routeGuideService;

    public void start() throws InterruptedException, IOException {
        log.info("RouteGuideServer init invoked");
        this.routeGuideService = new RouteGuideService();
        this.server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(routeGuideService)
                .build();
        this.server.start();
        log.info("Server started, listening on " + port);
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    @PreDestroy
    public void stopServer() throws InterruptedException {
        log.info("RouteGuideServer stop invoked");
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

}
