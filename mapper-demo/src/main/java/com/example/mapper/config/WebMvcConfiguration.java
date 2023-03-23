package com.example.mapper.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.TimeZone;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * date string convert 용도
     * */
    public static ZonedDateTime convertAllString(String isoDateTime) {
        DateTimeFormatter parser = DateTimeFormatter.ISO_DATE_TIME;
        TemporalAccessor accessor = parser.parse(isoDateTime);
        if (accessor.isSupported(ChronoField.OFFSET_SECONDS)) {
            return ZonedDateTime.from(accessor);
        } else {
            // return LocalDateTime.from(accessor).atZone(ZoneId.of("Asia/Seoul"));
            return LocalDateTime.from(accessor).atZone(ZoneId.systemDefault());
        }
    }

    /**
     * request param 에서 zone date string 변환 진행
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ZonedDateTimeConverter());
    }

    class ZonedDateTimeConverter implements Converter<String, ZonedDateTime> {
        @Override
        public ZonedDateTime convert(String source) {
            return convertAllString(source);
        }
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // for zone date time
        DateTimeFormatter dateTimeFormat = DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")  // 2022-08-10T10:36:50+09:00
                .withZone(ZoneId.of("Asia/Seoul"));
        SimpleModule module = new JavaTimeModule()
                .addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(dateTimeFormat))
                .addDeserializer(ZonedDateTime.class, new JsonDeserializer<ZonedDateTime>() {
                    @Override
                    public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
                        return convertAllString(p.getText());
                    }
                });
        objectMapper.registerModule(module);
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        // for Date class
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        objectMapper.setDateFormat(dateFormat);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // WRITE_DATES_AS_TIMESTAMPS JSON에서 날짜를 문자열로 표시

        // 출력 조건사항
        objectMapper.setVisibility(objectMapper
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
                .getVisibilityChecker()
        );
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false); // InvalidDefinitionException, empty Object -> {}
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // UnrecognizedPropertyException 처리
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true); // empty array as null
        return objectMapper;
    }
}