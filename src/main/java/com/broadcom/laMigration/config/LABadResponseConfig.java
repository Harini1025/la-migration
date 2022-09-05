package com.broadcom.laMigration.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LABadResponseConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            int status = response.status();
            System.out.println("Error status : "+ status);
            return new Exception(status + response.body().toString());
        };
    }
}
