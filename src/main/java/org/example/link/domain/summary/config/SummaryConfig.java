package org.example.link.domain.summary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SummaryConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
