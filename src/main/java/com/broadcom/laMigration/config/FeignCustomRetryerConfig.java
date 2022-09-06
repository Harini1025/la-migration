package com.broadcom.laMigration.config;

import com.broadcom.laMigration.util.Constants;
import feign.RetryableException;
import feign.Retryer;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Slf4j
public class FeignCustomRetryerConfig implements Retryer {
    private int retryMaxAttempt;
    private long retryInterval;
    private int attempt = 1;


    public FeignCustomRetryerConfig(int retryMaxAttempt, Long retryInterval) {
        this.retryMaxAttempt = retryMaxAttempt;
        this.retryInterval = retryInterval;
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        log.info("Feign retry attempt {} due to {} ", attempt, e.getMessage());

        if(attempt++ == retryMaxAttempt){
            throw e;
        }
        try {
            Thread.sleep(retryInterval);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

    }

    @Override
    public Retryer clone() {
        return new FeignCustomRetryerConfig(Constants.FEIGN_RETRY, Constants.FEIGN_RETRY_INTERVAL);
    }
}
