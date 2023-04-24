package com.example.kafka.client.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminClientComponent {

    private final AdminClient client;


    public ListTopicsResult listTopics() {
        ListTopicsResult ltr = client.listTopics();
        return ltr;
    }

    public DescribeConfigsResult describeTopicConfigs(String topic) {
        ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topic);
        // get the current topic configuration
        DescribeConfigsResult dcr = client.describeConfigs(Collections.singleton(resource));
        return dcr;
    }

    public CreateTopicsResult createTopics(String topic, Integer numPartitions, Short replicationFactor) {
        NewTopic newTopic = new NewTopic(topic, numPartitions, replicationFactor);
        CreateTopicsResult ctr = client
                .createTopics(
                        Collections.singleton(newTopic),
                        new CreateTopicsOptions().timeoutMs(10000) // create topic timeout
                );
        return ctr;
    }

    public DeleteTopicsResult deleteTopics(List<String> topics) {
        DeleteTopicsResult dtr = client.deleteTopics(topics);
        return dtr;
    }

    public AlterConfigsResult alterTopicConfigs(String topic) {
        ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topic);
        // create a new entry for updating the retention.ms value on the same topic
        ConfigEntry retentionEntry = new ConfigEntry(TopicConfig.RETENTION_MS_CONFIG, "60000");
        Map<ConfigResource, Config> updateConfig = new HashMap<>();
        updateConfig.put(resource, new Config(Collections.singleton(retentionEntry)));
        AlterConfigsResult acr = client.alterConfigs(updateConfig);
        return acr;
    }
}
