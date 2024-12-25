package com.example.mongodb.model.notification;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

import static com.example.mongodb.model.notification.NotificationDocument.*;

@Getter
@Setter
@Document(collection = "notifications")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        visible = true // type 정보도 출력할건지 여부
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MessageNotification.class, name = "message"),
        @JsonSubTypes.Type(value = FriendRequestNotification.class, name = "friend_request"),
        @JsonSubTypes.Type(value = EventInviteNotification.class, name = "event_invite")
})
public abstract class NotificationDocument {
    @Id
    private String id;

    // mongo document 에서 역직렬화 시 "_id"를 사용
    @JsonSetter("_id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonGetter("id")
    public String getId() {
        return this.id;
    }

    private String type;
    private Long userId;
    private String message;
    private Instant timestamp;

    public NotificationDocument(String id, String type, Long userId, String message, Instant timestamp) {
        this.id = id;
        this.type = type;
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }

    @Getter
    @Setter
    public static class EventInviteNotification extends NotificationDocument {
        private Long eventId;
        private String location;

        public EventInviteNotification(String id, String type, Long userId, String message, Instant timestamp, Long eventId, String location) {
            super(id, type, userId, message, timestamp);
            this.eventId = eventId;
            this.location = location;
        }
    }

    @Getter
    @Setter
    public static class FriendRequestNotification extends NotificationDocument {
        private Long requesterId;

        public FriendRequestNotification(String id, String type, Long userId, String message, Instant timestamp, Long requesterId) {
            super(id, type, userId, message, timestamp);
            this.requesterId = requesterId;
        }
    }

    @Getter
    @Setter
    public static class MessageNotification extends NotificationDocument {
        private Long senderId;

        public MessageNotification(String id, String type, Long userId, String message, Instant timestamp, Long senderId) {
            super(id, type, userId, message, timestamp);
            this.senderId = senderId;
        }
    }
}

