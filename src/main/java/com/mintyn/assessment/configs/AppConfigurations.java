package com.mintyn.assessment.configs;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AppConfigurations {
    @Value("${bin.detail.api.url}")
    private String binDetailApiUrl;
}