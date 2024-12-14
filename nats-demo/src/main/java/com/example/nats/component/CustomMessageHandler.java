package com.example.nats.component;

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomMessageHandler implements MessageHandler {

    private final Connection natsConnection;

    @Override
    public void onMessage(Message msg) {
        log.info("subject:{}, message:{}", msg.getSubject(), new String(msg.getData()));
        if (StringUtils.hasText(msg.getReplyTo())) {
            natsConnection.publish(msg.getReplyTo(), "OK RECEIVED".getBytes());
        }
    }
}
/*
메세지 형태는 대략 아래와 같음
{
    "subject": "foo.bar",
    "replyTo": "reply.test",
    "data": "aGVsbG8gd29ybGQ=",
    "utf8mode": false,
    "headers": null,
    "sid": "1",
    "subscription": {
        "subject": "foo.bar",
        "queueName": "testQueue",
        "dispatcher": null,
        "beforeQueueProcessor": {},
        "active": true,
        "deliveredCount": 1,
        "pendingMessageLimit": 65536,
        "pendingByteLimit": 67108864,
        "pendingMessageCount": 0,
        "pendingByteCount": 0,
        "droppedCount": 0
    },
    "connection": {
        "options": {
            "unprocessedServers": ["nats://localhost:4222"],
            "noRandomize": false,
            "connectionName": null,
            "verbose": false,
            "pedantic": false,
            "sslContext": null,
            "maxReconnect": 60,
            "maxControlLine": 4096,
            "reconnectWait": "PT2S",
            "reconnectJitter": "PT0.1S",
            "reconnectJitterTls": "PT1S",
            "connectionTimeout": "PT2S",
            "pingInterval": "PT2M",
            "requestCleanupInterval": "PT5S",
            "maxPingsOut": 2,
            "reconnectBufferSize": 8388608,
            "username": "admin",
            "password": "password",
            "token": null,
            "inboxPrefix": "_INBOX.",
            "bufferSize": 65536,
            "noEcho": false,
            "noHeaders": false,
            "noNoResponders": false,
            "maxMessagesInOutgoingQueue": 5000,
            "discardMessagesWhenOutgoingQueueFull": false,
            "ignoreDiscoveredServers": false,
            "authHandler": null,
            "reconnectDelayHandler": {},
            "errorListener": {},
            "connectionListener": {},
            "dataPortType": "io.nats.client.impl.SocketDataPort",
            "trackAdvancedStats": false,
            "traceConnection": false,
            "executor": {
                "largestPoolSize": 2,
                "completedTaskCount": 0,
                "threadFactory": {},
                "corePoolSize": 0,
                "maximumPoolSize": 2147483647,
                "queue": [],
                "shutdown": false,
                "terminating": false,
                "terminated": false,
                "rejectedExecutionHandler": {},
                "poolSize": 2,
                "activeCount": 2,
                "taskCount": 2
            },
            "serverListProvider": null,
            "usernameChars": "admin",
            "passwordChars": "password",
            "tlsrequired": false,
            "oldRequestStyle": false,
            "tokenChars": null,
            "servers": ["nats://localhost:4222"]
        },
        "statistics": {
            "repliesReceived": 0,
            "duplicateRepliesReceived": 0,
            "orphanRepliesReceived": 0,
            "reconnects": 0,
            "inMsgs": 1,
            "outMsgs": 5,
            "inBytes": 42,
            "outBytes": 236,
            "droppedCount": 0,
            "exceptions": 0,
            "pings": 1,
            "oks": 0,
            "errs": 0
        },
        "status": "CONNECTED",
        "serverInfo": {
            "serverId": "ND3JKL5CZYUC3YDNHJUPJGLWQ4S6YEBGO5O6UB6WKZXZ5VIZRW6VMCQ6",
            "serverName": "ND3JKL5CZYUC3YDNHJUPJGLWQ4S6YEBGO5O6UB6WKZXZ5VIZRW6VMCQ6",
            "version": "2.9.15",
            "host": "0.0.0.0",
            "port": 4222,
            "headersSupported": true,
            "authRequired": false,
            "maxPayload": 1048576,
            "connectURLs": [],
            "protocolVersion": 1,
            "nonce": null,
            "lameDuckMode": false,
            "clientId": 27,
            "clientIp": "172.17.0.1",
            "cluster": "my_cluster",
            "goVersion": "go1.19.6",
            "tlsrequired": false,
            "jetStreamAvailable": false
        },
        "lastError": "",
        "maxPayload": 1048576,
        "connectedUrl": "nats://localhost:4222",
        "servers": ["nats://localhost:4222"]
    },
    "statusMessage": false,
    "jetStream": false,
    "status": null
}
*/