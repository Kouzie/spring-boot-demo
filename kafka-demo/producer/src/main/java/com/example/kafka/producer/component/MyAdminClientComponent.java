package com.example.kafka.producer.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyAdminClientComponent {
    private final AdminClient adminClient;

    public Map<String, TopicListing> listTopics() {
        try {
            return adminClient.listTopics().namesToListings().get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("토픽 목록 조회 중단", e);
            throw new RuntimeException("토픽 목록 조회 실패: " + e.getMessage(), e);
        } catch (ExecutionException e) {
            log.error("토픽 목록 조회 실패", e);
            throw new RuntimeException("토픽 목록 조회 실패: " + e.getMessage(), e);
        }
    }

    public void createTopics(String topic, Integer numPartitions, Short replicationFactor) {
        try {
            NewTopic newTopic = new NewTopic(topic, numPartitions, replicationFactor);
            adminClient.createTopics(
                    Collections.singleton(newTopic),
                    new CreateTopicsOptions().timeoutMs(10000)
            ).all().get();
            log.info("토픽 생성 완료 - topic: {}, partitions: {}, replicationFactor: {}", 
                    topic, numPartitions, replicationFactor);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("토픽 생성 중단 - topic: {}", topic, e);
            throw new RuntimeException("토픽 생성 실패: " + e.getMessage(), e);
        } catch (ExecutionException e) {
            log.error("토픽 생성 실패 - topic: {}", topic, e);
            throw new RuntimeException("토픽 생성 실패: " + e.getMessage(), e);
        }
    }

    public Map<ConfigResource, Config> describeTopicConfigs(String topic) {
        try {
            ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, topic);
            return adminClient.describeConfigs(Collections.singletonList(configResource)).all().get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("토픽 설정 조회 중단 - topic: {}", topic, e);
            throw new RuntimeException("토픽 설정 조회 실패: " + e.getMessage(), e);
        } catch (ExecutionException e) {
            log.error("토픽 설정 조회 실패 - topic: {}", topic, e);
            throw new RuntimeException("토픽 설정 조회 실패: " + e.getMessage(), e);
        }
    }

    public void deleteTopics(List<String> topics) {
        try {
            adminClient.deleteTopics(topics).all().get();
            log.info("토픽 삭제 완료 - topics: {}", topics);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("토픽 삭제 중단 - topics: {}", topics, e);
            throw new RuntimeException("토픽 삭제 실패: " + e.getMessage(), e);
        } catch (ExecutionException e) {
            log.error("토픽 삭제 실패 - topics: {}", topics, e);
            throw new RuntimeException("토픽 삭제 실패: " + e.getMessage(), e);
        }
    }

    public void alterTopicConfigs(String topic) {
        try {
            ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topic);
            ConfigEntry retentionEntry = new ConfigEntry(TopicConfig.RETENTION_MS_CONFIG, "60000");
            Map<ConfigResource, Config> updateConfig = new HashMap<>();
            updateConfig.put(resource, new Config(Collections.singleton(retentionEntry)));
            adminClient.alterConfigs(updateConfig).all().get();
            log.info("토픽 설정 변경 완료 - topic: {}", topic);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("토픽 설정 변경 중단 - topic: {}", topic, e);
            throw new RuntimeException("토픽 설정 변경 실패: " + e.getMessage(), e);
        } catch (ExecutionException e) {
            log.error("토픽 설정 변경 실패 - topic: {}", topic, e);
            throw new RuntimeException("토픽 설정 변경 실패: " + e.getMessage(), e);
        }
    }
}

