package com.example.sharding.routing.demo.config;

import org.springframework.util.Assert;

// DataSource를 ContextHolder 에 넣기
public class ThreadLocalDatabaseContextHolder {

    private static ThreadLocal<DemoDatabase> CONTEXT = new ThreadLocal<>();
    public static DemoDatabase getClientDatabase() {
        return CONTEXT.get();
    }

    public static void setById(long snowflakeId) {
        long timestamp = getTimestampBySnowflakeId(snowflakeId);
        int idx = (int) (timestamp % 2);
        DemoDatabase demoDatabase = DemoDatabase.values()[idx];
        CONTEXT.set(demoDatabase);
    }

    public static void set(DemoDatabase demoDatabase) {
        Assert.notNull(demoDatabase, "clientDatabase cannot be null");
        CONTEXT.set(demoDatabase);
    }

    public static void clear() {
        CONTEXT.remove();
    }

    private static final int NODE_ID_BITS = 10;
    private static final int SEQUENCE_BITS = 12;
    private static final long DEFAULT_CUSTOM_EPOCH = 1420070400000L;

    private static long getTimestampBySnowflakeId(long snowflakeId) {
        long timestamp = (snowflakeId >> (NODE_ID_BITS + SEQUENCE_BITS)) + DEFAULT_CUSTOM_EPOCH;
        return timestamp;
    }
}