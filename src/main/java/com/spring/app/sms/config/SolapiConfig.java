package com.spring.app.sms.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(SolapiProperties.class)
public class SolapiConfig {
	
	@Bean
    WebClient solapiWebClient(SolapiProperties props) {
        return WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .build();
    }
	
}