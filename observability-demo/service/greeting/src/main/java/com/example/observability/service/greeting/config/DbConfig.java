package com.example.observability.service.greeting.config;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.ObservationFilter;
import net.ttddyy.observation.tracing.QueryContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    // Spring Boot 가 application.yml 설정을 바탕으로 DataSource 를 자동 생성하므로
    // 별도의 빈 등록 없이도 동작합니다. 필요 자면 여기서 커스터마이징이 가능합니다.

    /**
     * ObservationFilter를 사용하여 모든 JDBC 쿼리 관측치에 'jdbc.query' 태그를 추가합니다.
     * 이 방식은 ObservationConvention을 직접 구현하는 것보다 유연하며 버전 호환성이 좋습니다.
     */
    @Bean
    public ObservationFilter sqlTagFilter() {
        return (context) -> {
            if (context instanceof QueryContext) {
                QueryContext queryContext = (QueryContext) context;
                // 첫 번째 쿼리문을 태그로 사용 (바인딩 변수가 포함된 형태)
                String sql = queryContext.getQueries().isEmpty() ? "unknown" : queryContext.getQueries().get(0);
                return context.addLowCardinalityKeyValue(KeyValue.of("jdbc.query", sql));
            }
            return context;
        };
    }
}
