package com.example.grpc.client.controller;

import com.example.grpc.client.util.RouteGuideClient;
import com.example.grpc.common.util.RouteGuideUtil;
import io.grpc.examples.routeguide.Feature;
import io.grpc.examples.routeguide.Point;
import io.grpc.examples.routeguide.RouteNote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class RouteController {

    private final RouteGuideClient client;
    private final Random random = new Random();

    public RouteController() {
        log.info("RouteController invoked");
        this.client = new RouteGuideClient();
    }

    @GetMapping("/{lat}/{lon}")
    public String getFeature(@PathVariable Integer lat, @PathVariable Integer lon) {
        log.info("*** GetFeature: lat={0} lon={1}", lat, lon);
        Feature feature = client.getFeature(lat, lon);
        return feature.toString();
    }

    @GetMapping("/{lowLat}/{lowLon}/{hiLat}/{hiLon}")
    public List<String> getFeatureList(@PathVariable Integer lowLat, @PathVariable Integer lowLon,
                                       @PathVariable Integer hiLat, @PathVariable Integer hiLon) {
        log.info("*** ListFeatures: lowLat={0} lowLon={1} hiLat={2} hiLon={3}", lowLat, lowLon, hiLat, hiLon);
        Iterator<Feature> features = client.listFeatures(lowLat, lowLon, hiLat, hiLon);
        List<Feature> result = new ArrayList<>();
        for (int i = 1; features.hasNext(); i++) {
            Feature feature = features.next();
            result.add(feature);
        }
        return result.stream()
                .map(Feature::toString)
                .collect(Collectors.toList());
    }

    @PostMapping("/{numPoints}")
    public void recordRoute(@PathVariable Integer numPoints) throws InterruptedException, IOException {
        log.info("*** RecordRoute");
        List<Feature> features = RouteGuideUtil.parseFeatures(RouteGuideUtil.getDefaultFeaturesFile());
        List<Point> pointList = new ArrayList<>();
        for (int i = 0; i < numPoints; ++i) {
            int index = random.nextInt(features.size());
            Point point = features.get(index).getLocation();
            pointList.add(point);
        }
        client.recordRoute(pointList);
    }

    @PostMapping("/chat")
    public void routeChat() {
        log.info("*** RouteChat");
        RouteNote[] routeNotes = {
                newNote("First message", 0, 0),
                newNote("Second message", 0, 10_000_000),
                newNote("Third message", 10_000_000, 0),
                newNote("Fourth message", 10_000_000, 10_000_000)
        };
        client.routeChat(routeNotes);
    }

    private RouteNote newNote(String message, int lat, int lon) {
        return RouteNote.newBuilder()
                .setMessage(message)
                .setLocation(Point.newBuilder().setLatitude(lat).setLongitude(lon).build())
                .build();
    }
}
