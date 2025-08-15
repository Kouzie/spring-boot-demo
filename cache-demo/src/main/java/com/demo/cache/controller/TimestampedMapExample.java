package com.demo.cache.controller;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class TimestampedMapExample {
    // 데이터 구조: Key와 타임스탬프 기반 값 관리
    private static ConcurrentHashMap<String, NavigableSet<TimestampedValue>> map = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        // 요청 예제
        processRequest("key1", "value1", System.currentTimeMillis(), true); // 추가 요청
        processRequest("key1", "value1", System.currentTimeMillis() - 100, false); // 삭제 요청(이전에 발생한 요청)
        ConcurrentHashMap.newKeySet()
        System.out.println(map); // 결과 확인
    }

    // 요청 처리 메서드
    public static void processRequest(String key, String value, long timestamp, boolean isAdd) {
        map.compute(key, (k, v) -> {
            if (v == null) {
                v = new ConcurrentSkipListSet<>(); // NavigableSet 사용
            }
            TimestampedValue newValue = new TimestampedValue(value, timestamp);
            
            if (isAdd) { // 추가 요청: 기존 값이 없거나 타임스탬프가 최신인 경우 삽입
                v.remove(newValue); // 중복 방지
                v.add(newValue);
            } else {
                // 삭제 요청: 값이 존재하고 타임스탬프가 이전보다 최신이면 삭제
                if (v.contains(newValue)) {
                    TimestampedValue existing = v.ceiling(newValue);
                    if (existing != null && existing.timestamp <= timestamp) {
                        v.remove(existing);
                    }
                }
            }
            return v.isEmpty() ? null : v; // 비어 있으면 키 삭제
        });
    }
}

// 타임스탬프와 값을 포함한 클래스
class TimestampedValue implements Comparable<TimestampedValue> {
    String value;
    long timestamp;

    public TimestampedValue(String value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(TimestampedValue other) {
        int result = value.compareTo(other.value);
        return result != 0 ? result : Long.compare(timestamp, other.timestamp);
    }

    @Override
    public String toString() {
        return String.format("%s@%d", value, timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimestampedValue)) return false;
        TimestampedValue that = (TimestampedValue) o;
        return timestamp == that.timestamp && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, timestamp);
    }
}