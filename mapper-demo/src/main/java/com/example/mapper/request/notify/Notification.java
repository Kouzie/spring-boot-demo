package com.example.mapper.request.notify;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "notificationType",
        include = JsonTypeInfo.As.PROPERTY,
        visible = true // type 정보도 출력할건지 여부
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Notification.MessageNotification.class, name = "message"),
        @JsonSubTypes.Type(value = Notification.FriendRequestNotification.class, name = "friend_request"),
        @JsonSubTypes.Type(value = Notification.EventInviteNotification.class, name = "event_invite")
})
@AllArgsConstructor
public abstract class Notification {
    private String id;
    private String notificationType;
    private String userId;
    private String message;
    private Instant timestamp;

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class MessageNotification extends Notification {
        private Long senderId;

        public MessageNotification(String id, String notificationType, String userId, String message, Instant timestamp, Long senderId) {
            super(id, notificationType, userId, message, timestamp);
            this.senderId = senderId;
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class FriendRequestNotification extends Notification {
        private Long requesterId;

        public FriendRequestNotification(String id, String notificationType, String userId, String message, Instant timestamp, Long requesterId) {
            super(id, notificationType, userId, message, timestamp);
            this.requesterId = requesterId;
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class EventInviteNotification extends Notification {
        private Long eventId;
        private String location;

        public EventInviteNotification(String id, String notificationType, String userId, String message, Instant timestamp, Long eventId, String location) {
            super(id, notificationType, userId, message, timestamp);
            this.eventId = eventId;
            this.location = location;
        }
    }
}