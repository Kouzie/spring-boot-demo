package com.example.grpc.client.util;

import io.grpc.*;
import io.grpc.examples.routeguide.*;
import io.grpc.examples.routeguide.RouteGuideGrpc.RouteGuideBlockingStub;
import io.grpc.examples.routeguide.RouteGuideGrpc.RouteGuideStub;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class RouteGuideClient {
    private final RouteGuideBlockingStub blockingStub;
    private final RouteGuideStub asyncStub;

    public RouteGuideClient() {
        ManagedChannel channel = Grpc
                .newChannelBuilder("localhost:8080", InsecureChannelCredentials.create())
                .build();
        blockingStub = RouteGuideGrpc.newBlockingStub(channel);
        asyncStub = RouteGuideGrpc.newStub(channel);
    }

    /**
     * Blocking unary call example.  Calls getFeature and prints the response.
     *
     * @return
     */
    public Feature getFeature(int lat, int lon) {
        Point request = Point.newBuilder().setLatitude(lat).setLongitude(lon).build();
        Feature feature = blockingStub.getFeature(request);
        return feature;
    }

    /**
     * Blocking server-streaming example. Calls listFeatures with a rectangle of interest. Prints each
     * response feature as it arrives.
     *
     * @return
     */
    public Iterator<Feature> listFeatures(int lowLat, int lowLon, int hiLat, int hiLon) {
        Rectangle request = Rectangle.newBuilder()
                .setLo(Point.newBuilder().setLatitude(lowLat).setLongitude(lowLon).build())
                .setHi(Point.newBuilder().setLatitude(hiLat).setLongitude(hiLon).build())
                .build();
        Iterator<Feature> features = blockingStub.listFeatures(request);
        return features;
    }

    /**
     * Async client-streaming example. Sends {@code numPoints} randomly chosen points from {@code
     * features} with a variable delay in between. Prints the statistics when they are sent from the
     * server.
     */
    public void recordRoute(List<Point> pointList) throws InterruptedException {
        StreamObserver<RouteSummary> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(RouteSummary summary) {
                info("Finished trip with {} points. Passed {} features. Travelled {} meters. It took {} seconds.",
                        summary.getPointCount(), summary.getFeatureCount(), summary.getDistance(), summary.getElapsedTime());
            }

            @Override
            public void onError(Throwable t) {
                warning("RecordRoute Failed: {}", Status.fromThrowable(t));
            }

            @Override
            public void onCompleted() {
                info("Finished RecordRoute");
            }
        };

        StreamObserver<Point> requestObserver = asyncStub.recordRoute(responseObserver);
        for (Point point : pointList) {
            requestObserver.onNext(point);
            // Sleep for a bit before sending the next one.
            Thread.sleep(1500);
        }
        // Mark the end of requests
        requestObserver.onCompleted();
    }

    /**
     * Bi-directional example, which can only be asynchronous. Send some chat messages, and print any
     * chat messages that are sent from the server.
     */
    public void routeChat(RouteNote[] routeNotes) {
        final CountDownLatch finishLatch = new CountDownLatch(1);
        StreamObserver<RouteNote> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(RouteNote note) {
                info("Got message \"{}\" at {}, {}", note.getMessage(), note.getLocation().getLatitude(), note.getLocation().getLongitude());
            }

            @Override
            public void onError(Throwable t) {
                warning("RouteChat Failed: {}", Status.fromThrowable(t));
            }

            @Override
            public void onCompleted() {
                info("Finished RouteChat");
            }
        };
        StreamObserver<RouteNote> requestObserver = asyncStub.routeChat(responseObserver);

        for (RouteNote routeNote : routeNotes) {
            info("Sending message \"{}\" at {}, {}", routeNote.getMessage(), routeNote.getLocation().getLatitude(), routeNote.getLocation().getLongitude());
            requestObserver.onNext(routeNote);
        }
        requestObserver.onCompleted();
    }


    private void info(String msg, Object... params) {
        log.info(msg, params);
    }

    private void warning(String msg, Object... params) {
        log.warn(msg, params);
    }
}