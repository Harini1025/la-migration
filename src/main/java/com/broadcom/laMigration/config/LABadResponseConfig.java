package com.broadcom.laMigration.config;


import com.broadcom.laMigration.util.CommonUtils;
import com.broadcom.laMigration.util.Constants;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

@Slf4j
@Configuration
public class LABadResponseConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            int status = response.status();
            log.error("Error status : "+ status);
            return new Exception(status + response.body().toString());
        };
    }
}
