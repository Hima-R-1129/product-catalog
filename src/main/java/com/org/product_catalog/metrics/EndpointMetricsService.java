package com.org.product_catalog.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class EndpointMetricsService {

    private final MeterRegistry registry;
    private final ConcurrentMap<String, Counter> successCounters = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Counter> failureCounters = new ConcurrentHashMap<>();

    public EndpointMetricsService(MeterRegistry registry) {
        this.registry = registry;
    }

    public void incrementSuccess(String endpoint) {
        successCounters.computeIfAbsent(endpoint,
                ep -> Counter.builder("endpoint.success.count")
                        .tag("endpoint", ep)
                        .description("Number of successful calls for endpoint")
                        .register(registry)
        ).increment();
    }

    public void incrementFailure(String endpoint) {
        failureCounters.computeIfAbsent(endpoint,
                ep -> Counter.builder("endpoint.failure.count")
                        .tag("endpoint", ep)
                        .description("Number of failed calls for endpoint")
                        .register(registry)
        ).increment();
    }
}
