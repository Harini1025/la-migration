package com.broadcom.laMigration.consumer;

import com.broadcom.laMigration.config.FeignCustomRetryerConfig;
import com.broadcom.laMigration.config.LABadResponseConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient( name="la-collector-service", url = "http://logana-logcollector.10.82.175.241.nip.io"
        , configuration= {LABadResponseConfig.class, FeignCustomRetryerConfig.class})
public interface LAConsumer {

    //standard
    @PostMapping(path= "/mdo/v2/aoanalytics/ingestion/uim_logs", consumes = "application/json" )
    public ResponseEntity<String> postStandardLogInBatch(@RequestBody String jsonArray);

    //custom
    @PostMapping(path = "/la/v2/api/ingestion/logs" , consumes = "application/json")
    public  ResponseEntity<String> postCustomLogInBatch(@RequestBody String jsonArray);
}
