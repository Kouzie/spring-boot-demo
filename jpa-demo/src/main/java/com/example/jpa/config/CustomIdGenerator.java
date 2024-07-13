package com.example.jpa.config;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class CustomIdGenerator implements IdentifierGenerator {

    private final Snowflake snowflake;

    public CustomIdGenerator(Snowflake snowflake) {
        this.snowflake = snowflake;
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        // 원하는 ID 생성 로직을 구현합니다.
        // 여기서는 UUID를 예로 사용합니다.
        return snowflake.nextId();
    }
}