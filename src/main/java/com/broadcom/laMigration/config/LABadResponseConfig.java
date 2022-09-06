package com.broadcom.laMigration.config;


import com.broadcom.laMigration.util.CommonUtils;
import com.broadcom.laMigration.util.Constants;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

@Configuration
public class LABadResponseConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            int status = response.status();
            System.out.println("Error status : "+ status);
           // System.out.println(response.request().body().toString());
            //writeFiledLogsInFile(response.request().body().toString());
            return new Exception(status + response.body().toString());
        };
    }

    public void writeFiledLogsInFile(String chunk) throws IOException {
        FileWriter f = new FileWriter(Constants.OUTPUT_DIR_VM+ new Timestamp(System.currentTimeMillis()) + ".json");
        f.write(chunk);
        f.close();
    }
}
