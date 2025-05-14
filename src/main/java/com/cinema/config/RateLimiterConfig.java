package com.cinema.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfig {
    @Bean
    public Bucket createNewBucket() {
        Bandwidth limitPerMinute = Bandwidth.simple(100, Duration.ofMinutes(1));
        Bandwidth limitPerHour = Bandwidth.simple(1000, Duration.ofHours(1));
        return Bucket.builder()
            .addLimit(limitPerMinute)
            .addLimit(limitPerHour)
            .build();
    }
} 