package com.example.kafka.client;

import com.example.kafka.client.component.AdminClientComponent;
import com.example.kafka.client.dto.CreateTopicsReq;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka")
public class AdminClientController {
    private final AdminClientComponent adminClientComponent;

    @GetMapping("/topics/list")
    public Map listTopics() throws ExecutionException, InterruptedException {
        ListTopicsResult ltr = adminClientComponent.listTopics();
        KafkaFuture<Map<String, TopicListing>> future = ltr.namesToListings();
        return future.get();
    }

    @PostMapping("/topics")
    public void createTopics(@RequestBody CreateTopicsReq createTopicsReq) throws ExecutionException, InterruptedException {
        CreateTopicsResult ctr = adminClientComponent.createTopics(
                createTopicsReq.getTopic(),
                createTopicsReq.getNumPartitions(),
                createTopicsReq.getReplicationFactor());
        ctr.all();
    }

    @GetMapping("/topics/{topic}")
    public Map describeTopicConfigs(@PathVariable String topic) throws ExecutionException, InterruptedException {
        DescribeConfigsResult dcr = adminClientComponent.describeTopicConfigs(topic);
        return dcr.all().get();
    }

    @DeleteMapping("/topics/{topic}")
    public void deleteTopics(@PathVariable String topic) {
        DeleteTopicsResult dtr = adminClientComponent.deleteTopics(Collections.singletonList(topic));
        dtr.all();
    }

    @PatchMapping("/topics/{topic}")
    public void alterTopics(@PathVariable String topic) {
        AlterConfigsResult acr = adminClientComponent.alterTopicConfigs(topic);
        acr.all();
    }

}
