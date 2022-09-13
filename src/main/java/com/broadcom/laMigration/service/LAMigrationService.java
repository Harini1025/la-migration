package com.broadcom.laMigration.service;

import com.broadcom.laMigration.util.CommonUtils;
import com.broadcom.laMigration.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Service
@Slf4j
public class LAMigrationService {

    @Autowired
    private CommonUtils commonUtils;

    public void migrateLog() throws IOException {

        int threads = Integer.parseInt(Constants.THREADS);
        int sizeInMb = Integer.parseInt(Constants.CHUNK_SIZE_IN_MB);
        String inputPath = Constants.INPUT_DIR_VM;
        String outputPath = Constants.OUTPUT_DIR_VM;

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        try (Stream<Path> files = Files.list(Paths.get(inputPath))){
            files.forEach(file -> {
                CompletableFuture.runAsync(() -> {
                    log.info("File name : " + file.getFileName().toString() + ", Thread : " + Thread.currentThread().getName());
                    try {
                        commonUtils.splitFileAndProcessJson(inputPath + file.getFileName().toString(), sizeInMb);
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                    log.info(String.valueOf(new Timestamp(System.currentTimeMillis())));
                }, executor).handle((res, err) -> {
                    if (err != null) {
                        err.printStackTrace();
                        log.error("something went wrong" + err.getMessage());
                    }
                    return res;
                }).thenRun(executor::shutdown);
            });
        }
    }
}
