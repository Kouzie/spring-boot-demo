package com.example.grpc.common.util;

import com.google.protobuf.util.JsonFormat;
import io.grpc.examples.routeguide.Feature;
import io.grpc.examples.routeguide.FeatureDatabase;
import io.grpc.examples.routeguide.Point;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

public class RouteGuideUtil {
    private static final double COORD_FACTOR = 1e7; //10000000.000000

    // 위경도 변환
    public static double getLatitude(Point location) {
        return location.getLatitude() / COORD_FACTOR;
    }
    public static double getLongitude(Point location) {
        return location.getLongitude() / COORD_FACTOR;
    }

    public static ClassPathResource getDefaultFeaturesFile() {
        ClassPathResource resource = new ClassPathResource("route_guide_db.json");
        return resource;
    }

    public static List<Feature> parseFeatures(ClassPathResource file) throws IOException {
        InputStream input = file.getInputStream();
        try {
            Reader reader = new InputStreamReader(input, Charset.forName("UTF-8"));
            try {
                FeatureDatabase.Builder database = FeatureDatabase.newBuilder();
                JsonFormat.parser().merge(reader, database);
                return database.getFeatureList();
            } catch(Exception e) {
                e.printStackTrace();
            }
            finally {
                reader.close();
            }
        } finally {
            input.close();
        }
        return null;
    }

    public static boolean exists(Feature feature) {
        return feature != null && !feature.getName().isEmpty();
    }
}