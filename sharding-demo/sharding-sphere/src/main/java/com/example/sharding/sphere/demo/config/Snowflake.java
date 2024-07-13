package com.example.sharding.sphere.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Distributed Sequence Generator.
 * Inspired by Twitter snowflake: https://github.com/twitter/snowflake/tree/snowflake-2010
 * <p>
 * This class should be used as a Singleton.
 * Make sure that you create and reuse a Single instance of Snowflake per node in your distributed system cluster.
 */
public class Snowflake {
    private static final int UNUSED_BITS = 1; // Sign bit, Unused (always set to 0)
    private static final int EPOCH_BITS = 41;
    private static final int DATACENTER_ID_BITS = 5;
    private static final int WORKER_ID_BITS = 5;
    private static final int SEQUENCE_BITS = 12;

    private static final long maxDatacenterId = (1L << DATACENTER_ID_BITS) - 1;
    private static final long maxWorkerId = (1L << WORKER_ID_BITS) - 1;
    private static final long maxSequence = (1L << SEQUENCE_BITS) - 1;

    // Custom Epoch (January 1, 2015 Midnight UTC = 2015-01-01T00:00:00Z)
    private static final long DEFAULT_CUSTOM_EPOCH = 1420070400000L;

    private final long datacenterId;
    private final long workerId;
    private final long customEpoch;

    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;

    // Create Snowflake with datacenterId, workerId, and custom epoch
    public Snowflake(long datacenterId, long workerId, long customEpoch) {
        if (datacenterId < 0 || datacenterId > maxDatacenterId) {
            throw new IllegalArgumentException(String.format("Datacenter Id must be between %d and %d", 0, maxDatacenterId));
        }
        if (workerId < 0 || workerId > maxWorkerId) {
            throw new IllegalArgumentException(String.format("Worker Id must be between %d and %d", 0, maxWorkerId));
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
        this.customEpoch = customEpoch;
    }

    // Create Snowflake with datacenterId and workerId
    public Snowflake(long datacenterId, long workerId) {
        this(datacenterId, workerId, DEFAULT_CUSTOM_EPOCH);
    }

    public synchronized long nextId() {
        long currentTimestamp = timestamp();

        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Invalid System Clock!");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                // Sequence Exhausted, wait till next millisecond.
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            // reset sequence to start with zero for the next millisecond
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;

        long id = currentTimestamp << (DATACENTER_ID_BITS + WORKER_ID_BITS + SEQUENCE_BITS)
                | (datacenterId << (WORKER_ID_BITS + SEQUENCE_BITS))
                | (workerId << SEQUENCE_BITS)
                | sequence;

        return id;
    }

    // Get current timestamp in milliseconds, adjust for the custom epoch.
    private long timestamp() {
        return Instant.now().toEpochMilli() - customEpoch;
    }

    // Block and wait till next millisecond
    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp == lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }

    public long[] parse(long id) {
        long maskDatacenterId = ((1L << DATACENTER_ID_BITS) - 1) << (WORKER_ID_BITS + SEQUENCE_BITS);
        long maskWorkerId = ((1L << WORKER_ID_BITS) - 1) << SEQUENCE_BITS;
        long maskSequence = (1L << SEQUENCE_BITS) - 1;

        long timestamp = (id >> (DATACENTER_ID_BITS + WORKER_ID_BITS + SEQUENCE_BITS)) + customEpoch;
        long datacenterId = (id & maskDatacenterId) >> (WORKER_ID_BITS + SEQUENCE_BITS);
        long workerId = (id & maskWorkerId) >> SEQUENCE_BITS;
        long sequence = id & maskSequence;

        return new long[]{timestamp, datacenterId, workerId, sequence};
    }

    @Override
    public String toString() {
        return "Snowflake Settings [EPOCH_BITS=" + EPOCH_BITS + ", DATACENTER_ID_BITS=" + DATACENTER_ID_BITS
                + ", WORKER_ID_BITS=" + WORKER_ID_BITS + ", SEQUENCE_BITS=" + SEQUENCE_BITS
                + ", CUSTOM_EPOCH=" + customEpoch
                + ", DatacenterId=" + datacenterId
                + ", WorkerId=" + workerId + "]";
    }

    // Convert Snowflake ID to OffsetDateTime
    public OffsetDateTime toOffsetDateTime(long id) {
        long timestamp = (id >> (DATACENTER_ID_BITS + WORKER_ID_BITS + SEQUENCE_BITS)) + customEpoch;
        return Instant.ofEpochMilli(timestamp).atOffset(ZoneOffset.ofHours(9));
    }
    public long toDatacenterId(long id) {
        long maskDatacenterId = ((1L << DATACENTER_ID_BITS) - 1) << (WORKER_ID_BITS + SEQUENCE_BITS);
        long datacenterId = (id & maskDatacenterId) >> (WORKER_ID_BITS + SEQUENCE_BITS);
        return datacenterId;
    }

    // Generate minimum Snowflake ID for the given OffsetDateTime within the same day
    public long getMinIdForDate(OffsetDateTime dateTime) {
        OffsetDateTime startOfDay = dateTime.toLocalDate().atStartOfDay().atOffset(ZoneOffset.ofHours(9));
        long timestamp = startOfDay.toInstant().toEpochMilli() - customEpoch;
        return timestamp << (DATACENTER_ID_BITS + WORKER_ID_BITS + SEQUENCE_BITS);
    }

    // Generate maximum Snowflake ID for the given OffsetDateTime within the same day
    public long getMaxIdForDate(OffsetDateTime dateTime) {
        OffsetDateTime endOfDay = dateTime.toLocalDate().atTime(23, 59, 59, 999_999_999).atOffset(ZoneOffset.ofHours(9));
        long timestamp = endOfDay.toInstant().toEpochMilli() - customEpoch;
        return (timestamp << (DATACENTER_ID_BITS + WORKER_ID_BITS + SEQUENCE_BITS))
                | (maxDatacenterId << (WORKER_ID_BITS + SEQUENCE_BITS))
                | (maxWorkerId << SEQUENCE_BITS)
                | maxSequence;

    }

}
