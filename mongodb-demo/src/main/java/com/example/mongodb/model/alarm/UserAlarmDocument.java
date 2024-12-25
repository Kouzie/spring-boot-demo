package com.example.mongodb.model.alarm;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Document(collection = "user-alarm") // MongoDB의 컬렉션 이름
@NoArgsConstructor
public class UserAlarmDocument {
    @Id
    private String userId;
    private List<UserAlarm> alarms;

    @Getter
    @Setter
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            property = "type",
            visible = true // type 정보도 출력할건지 여부
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = EventAlarm.class, name = "Event"),
            @JsonSubTypes.Type(value = WarningAlarm.class, name = "Warning"),
            @JsonSubTypes.Type(value = InfoAlarm.class, name = "Info")
    })
    public static abstract class UserAlarm {
        private String type;
        private String message;
    }

    @Getter
    @Setter
    public static class EventAlarm extends UserAlarm {
        private String eventId;        // 관련 이벤트 ID
        private String eventLocation;  // 이벤트 장소
        private Instant eventTime; // 이벤트 발생 시간
    }

    @Getter
    @Setter
    public static class WarningAlarm extends UserAlarm {
        private String severityLevel;  // 경고 수준 (e.g., "High", "Medium", "Low")
        private boolean requiresAction; // 사용자 조치 필요 여부
        private String warningCode;     // 경고 코드
    }

    @Getter
    @Setter
    public static class InfoAlarm extends UserAlarm {
        private String infoTitle;      // 정보 제목
        private String infoSource;     // 정보 출처
        private Instant infoTimestamp; // 정보 제공 시간
    }
}