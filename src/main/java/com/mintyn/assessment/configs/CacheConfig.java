package com.mintyn.assessment.configs;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mintyn.assessment.entities.CardVerification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;
@Configuration
public class CacheConfig {

    @Bean
    Cache<String, Object> cardVerificationCache()
    {
        return Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS)
                .build();
    }

}
