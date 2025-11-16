package com.example.kafka.producer.controller;

import com.example.kafka.common.dto.CreateTopicsReq;
import com.example.kafka.producer.component.MyAdminClientComponent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka")
public class MyAdminClientController {
    private final MyAdminClientComponent adminClientComponent;

    @GetMapping("/topics/list")
    public Map<String, TopicListing> listTopics() {
        return adminClientComponent.listTopics();
    }

    @GetMapping("/topics/{topic}")
    public Map<ConfigResource, Config> describeTopicConfigs(@PathVariable String topic) {
        return adminClientComponent.describeTopicConfigs(topic);
    }

    @PostMapping("/topics")
    public void createTopics(@RequestBody CreateTopicsReq createTopicsReq) {
        adminClientComponent.createTopics(
                createTopicsReq.getTopic(),
                createTopicsReq.getNumPartitions(),
                createTopicsReq.getReplicationFactor());
    }
    
    @DeleteMapping("/topics/{topic}")
    public void deleteTopics(@PathVariable String topic) {
        adminClientComponent.deleteTopics(Collections.singletonList(topic));
    }

    @PatchMapping("/topics/{topic}")
    public void alterTopics(@PathVariable String topic) {
        adminClientComponent.alterTopicConfigs(topic);
    }
}

