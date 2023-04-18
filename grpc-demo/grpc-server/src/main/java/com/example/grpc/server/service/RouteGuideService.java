package com.example.grpc.server.service;

import com.example.grpc.common.util.RouteGuideUtil;
import io.grpc.examples.routeguide.*;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.lang.Math.*;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

@Slf4j
public class RouteGuideService extends RouteGuideGrpc.RouteGuideImplBase {
    private Collection<Feature> features;
    private ConcurrentMap<Point, List<RouteNote>> routeNotes = new ConcurrentHashMap<>();

    public RouteGuideService() throws IOException {
        log.info("RouteGuideService init invoked");
        ClassPathResource resource = RouteGuideUtil.getDefaultFeaturesFile();
        this.features = RouteGuideUtil.parseFeatures(resource);
    }

    /**
     * @param request          요청값
     * @param responseObserver 서버가 응답으로 호출하는 특수 인터페이스인 observer
     */
    @Override
    public void getFeature(Point request, StreamObserver<Feature> responseObserver) {
        log.info("getFeature invoked. {}", request.toString());
        responseObserver.onNext(checkFeature(request));
        responseObserver.onCompleted();
    }

    @Override
    public void listFeatures(Rectangle request, StreamObserver<Feature> responseObserver) {
        log.info("listFeatures invoked. {}", request.toString());
        int left = min(request.getLo().getLongitude(), request.getHi().getLongitude());
        int right = max(request.getLo().getLongitude(), request.getHi().getLongitude());
        int top = max(request.getLo().getLatitude(), request.getHi().getLatitude());
        int bottom = min(request.getLo().getLatitude(), request.getHi().getLatitude());

        for (Feature feature : features) {
            if (!RouteGuideUtil.exists(feature)) {
                continue;
            }

            int lat = feature.getLocation().getLatitude();
            int lon = feature.getLocation().getLongitude();
            if (lon >= left && lon <= right && lat >= bottom && lat <= top) {
                responseObserver.onNext(feature);
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Point> recordRoute(StreamObserver<RouteSummary> responseObserver) {
        return new StreamObserver<>() {
            int pointCount;
            int featureCount;
            int distance;
            Point previous;
            long startTime = System.nanoTime();

            @Override
            public void onNext(Point point) {
                log.info("recordRoute point. {}", point.toString());
                pointCount++;
                if (RouteGuideUtil.exists(checkFeature(point))) {
                    featureCount++;
                }
                // For each point after the first, add the incremental distance from the previous point
                // to the total distance value.
                if (previous != null) {
                    distance += calcDistance(previous, point);
                }
                previous = point;
            }

            @Override
            public void onError(Throwable t) {
                log.warn("Encountered error in recordRoute", t);
            }

            @Override
            public void onCompleted() {
                long seconds = NANOSECONDS.toSeconds(System.nanoTime() - startTime);
                responseObserver.onNext(RouteSummary.newBuilder().setPointCount(pointCount)
                        .setFeatureCount(featureCount).setDistance(distance)
                        .setElapsedTime((int) seconds).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<RouteNote> routeChat(StreamObserver<RouteNote> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(RouteNote note) {
                log.info("routeChat note. {}", note.toString());
                List<RouteNote> notes = getOrCreateNotes(note.getLocation());

                // Respond with all previous notes at this location.
                for (RouteNote prevNote : notes.toArray(new RouteNote[0])) {
                    responseObserver.onNext(prevNote);
                }

                // Now add the new note to the list
                notes.add(note);
            }

            @Override
            public void onError(Throwable t) {
                log.warn("Encountered error in routeChat, {}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    private Feature checkFeature(Point location) {
        for (Feature feature : features) {
            if (feature.getLocation().getLatitude() == location.getLatitude()
                    && feature.getLocation().getLongitude() == location.getLongitude()) {
                return feature;
            }
        }
        // No feature was found, return an unnamed feature.
        return Feature.newBuilder().setName("").setLocation(location).build();
    }

    private static int calcDistance(Point start, Point end) {
        int r = 6371000; // earth radius in meters
        double lat1 = toRadians(RouteGuideUtil.getLatitude(start));
        double lat2 = toRadians(RouteGuideUtil.getLatitude(end));
        double lon1 = toRadians(RouteGuideUtil.getLongitude(start));
        double lon2 = toRadians(RouteGuideUtil.getLongitude(end));
        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        double a = sin(deltaLat / 2) * sin(deltaLat / 2)
                + cos(lat1) * cos(lat2) * sin(deltaLon / 2) * sin(deltaLon / 2);
        double c = 2 * atan2(sqrt(a), sqrt(1 - a));
        return (int) (r * c);
    }

    private List<RouteNote> getOrCreateNotes(Point location) {
        List<RouteNote> notes = Collections.synchronizedList(new ArrayList<RouteNote>());
        List<RouteNote> prevNotes = routeNotes.putIfAbsent(location, notes);
        return prevNotes != null ? prevNotes : notes;
    }

}
