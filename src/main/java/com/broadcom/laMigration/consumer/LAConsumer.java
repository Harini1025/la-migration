package com.broadcom.laMigration.consumer;

import com.broadcom.laMigration.config.FeignCustomRetryerConfig;
import com.broadcom.laMigration.config.LABadResponseConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



@FeignClient( name="la-collector-service", url = "http://logana-logcollector.10.82.175.241.nip.io"
        , configuration= {LABadResponseConfig.class, FeignCustomRetryerConfig.class})
public interface LAConsumer {

    //standard
    @RequestMapping(value = "/mdo/v2/aoanalytics/ingestion/uim_logs", method = RequestMethod.POST
            , consumes = "application/json", produces="text/plain")
    public String postStandardLogInBatch(@RequestBody String jsonArray);

    //custom
    @RequestMapping(method = RequestMethod.POST, value = "/la/v2/api/ingestion/logs"
            , consumes = "application/json", produces="text/plain")
    public  String postCustomLogInBatch(@RequestBody String jsonArray);
}
