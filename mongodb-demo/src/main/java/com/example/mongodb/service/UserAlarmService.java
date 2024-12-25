package com.example.mongodb.service;

import com.example.mongodb.model.alarm.UserAlarmDocument;
import com.example.mongodb.model.alarm.UserAlarmRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.example.mongodb.model.alarm.UserAlarmDocument.*;

@Service
@RequiredArgsConstructor
public class UserAlarmService {

    private final UserAlarmRepository userAlarmRepository;

    @PostConstruct
    private void init() {
        List<UserAlarm> alarms = new ArrayList<>();
        EventAlarm eventAlarm = new EventAlarm();
        eventAlarm.setType("Event");
        eventAlarm.setMessage("Meeting scheduled.");
        eventAlarm.setEventId("E12345");
        eventAlarm.setEventLocation("Conference Room A");
        eventAlarm.setEventTime(Instant.now());
        WarningAlarm warningAlarm = new WarningAlarm();
        warningAlarm.setType("Warning");
        warningAlarm.setMessage("High temperature detected.");
        warningAlarm.setSeverityLevel("High");
        warningAlarm.setRequiresAction(true);
        warningAlarm.setWarningCode("W1001");
        InfoAlarm infoAlarm = new InfoAlarm();
        infoAlarm.setType("Info");
        infoAlarm.setMessage("System maintenance scheduled.");
        infoAlarm.setInfoTitle("Maintenance Notification");
        infoAlarm.setInfoSource("IT Department");
        infoAlarm.setInfoTimestamp(Instant.now());
        UserAlarmDocument userAlarm = new UserAlarmDocument();
        alarms.add(eventAlarm);
        alarms.add(warningAlarm);
        alarms.add(infoAlarm);
        userAlarm.setUserId("USER-123");
        userAlarm.setAlarms(alarms);
        userAlarmRepository.save(userAlarm);
    }

    public UserAlarmDocument getUserAlarmByUserId(String userId) {
        return userAlarmRepository.findById(userId).orElseThrow();
    }
}