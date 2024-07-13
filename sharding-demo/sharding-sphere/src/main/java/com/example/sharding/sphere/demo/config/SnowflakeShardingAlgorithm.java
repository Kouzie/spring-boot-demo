package com.example.sharding.sphere.demo.config;

import com.google.common.collect.Range;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

// 커스텀 샤딩 로직을 구현.
public class SnowflakeShardingAlgorithm implements StandardShardingAlgorithm<Long> {

    private static final int DATACENTER_ID_BITS = 5;
    private static final int WORKER_ID_BITS = 5;
    private static final int SEQUENCE_BITS = 12;

    private static long toDatacenterId(long id) {
        long maskDatacenterId = ((1L << DATACENTER_ID_BITS) - 1) << (WORKER_ID_BITS + SEQUENCE_BITS);
        long datacenterId = (id & maskDatacenterId) >> (WORKER_ID_BITS + SEQUENCE_BITS);
        return datacenterId;
    }

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
        Long datacenterId = toDatacenterId(shardingValue.getValue());
        for (String targetName : availableTargetNames) {
            if (targetName.endsWith(String.valueOf(datacenterId))) {
                return targetName;
            }
        }
        throw new UnsupportedOperationException("No target found for value: " + datacenterId);
    }
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Long> shardingValue) {
        Set<String> result = new HashSet<>();
        Range<Long> valueRange = shardingValue.getValueRange();
        Long lowerEndpoint = valueRange.lowerEndpoint();
        Long upperEndpoint = valueRange.upperEndpoint();

        Long lowerDatacenterId = toDatacenterId(lowerEndpoint);
        Long upperDatacenterId = toDatacenterId(upperEndpoint);

        for (String targetName : availableTargetNames) {
            for (Long datacenterId = lowerDatacenterId; datacenterId <= upperDatacenterId; datacenterId++) {
                if (targetName.endsWith(String.valueOf(datacenterId))) {
                    result.add(targetName);
                }
            }
        }

        if (result.isEmpty()) {
            throw new UnsupportedOperationException("No target found for range: " + lowerDatacenterId + " to " + upperDatacenterId);
        }

        return result;
    }
}