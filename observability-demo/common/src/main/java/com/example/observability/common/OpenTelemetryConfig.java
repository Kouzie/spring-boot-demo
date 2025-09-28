package com.example.observability.common;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.UUID;

@Configuration
public class OpenTelemetryConfig {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${server.version:1.0.0}")
    private String serviceVersion;

    @Bean
    public Resource resource() {
        String instanceId = getInstanceId();
        Attributes attributes = Attributes.of(
                AttributeKey.stringKey("service.namespace"), "demo-service",
                AttributeKey.stringKey("service.name"), appName,
                AttributeKey.stringKey("service.instance.id"), instanceId,
                AttributeKey.stringKey("service.version"), serviceVersion
        );
        return Resource.getDefault().merge(Resource.create(attributes));
    }

    private String getInstanceId() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return UUID.randomUUID().toString();
        }
    }

    @Bean
    public SdkTracerProvider sdkTracerProvider(Resource resource) {
        OtlpGrpcSpanExporter exporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://otel:4317")
                .setTimeout(Duration.ofSeconds(5))
                .build();
        return SdkTracerProvider.builder()
                .setResource(resource)
                .addSpanProcessor(BatchSpanProcessor.builder(exporter).build())
                .setSampler(Sampler.traceIdRatioBased(1.0)) // 필요 시 낮추기
                .build();
    }

    @Bean
    public SdkLoggerProvider sdkLoggerProvider(Resource resource) {
        OtlpGrpcLogRecordExporter exporter = OtlpGrpcLogRecordExporter.builder()
                .setEndpoint("http://otel:4317")
                .setTimeout(Duration.ofSeconds(5))
                .build();
        return SdkLoggerProvider.builder()
                .addLogRecordProcessor(BatchLogRecordProcessor.builder(exporter).build())
                .setResource(resource)
                .build();
    }

    @Bean // ★ Micrometer가 참조할 OpenTelemetrySdk 를 반드시 노출
    public OpenTelemetry openTelemetry(SdkLoggerProvider sdkLoggerProvider,
                                       SdkTracerProvider sdkTracerProvider) {
        ContextPropagators propagators = ContextPropagators.create(
                TextMapPropagator.composite(
                        W3CTraceContextPropagator.getInstance(),
                        W3CBaggagePropagator.getInstance()
                )
        );

        OpenTelemetrySdk otel = OpenTelemetrySdk.builder()
                // .setMeterProvider(sdkMeterProvider) prometheus 대체
                .setLoggerProvider(sdkLoggerProvider)
                .setTracerProvider(sdkTracerProvider)
                .setPropagators(propagators)
                .build();

        GlobalOpenTelemetry.resetForTest(); // 혹시 이전 글로벌 인스턴스가 있으면 리셋
        GlobalOpenTelemetry.set(otel);
        OpenTelemetryAppender.install(otel); // install log agent in log appender

        return otel;
    }
}
