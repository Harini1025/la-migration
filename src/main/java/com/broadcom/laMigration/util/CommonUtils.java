package com.broadcom.laMigration.util;

import com.broadcom.laMigration.consumer.LAConsumer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Component
@Slf4j
public class CommonUtils {

    @Autowired
    private LAConsumer laConsumer;
    private static  ObjectMapper objectMapper = new ObjectMapper();

    static ExecutorService executor = Executors.newFixedThreadPool(Integer.parseInt(Constants.THREADS_POOL_2));

    protected static JsonObject processLine(String line) throws JsonProcessingException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.TENANT_ID, Constants.TENANT_ID_VALUE);
        jsonObject.addProperty(Constants.LOG_TYPE, "log4j");
        jsonObject.addProperty(Constants.TEMP_FIELDS , Constants.TENANT_ID_VALUE + " dhcp-10-17-165-233 log4j dhcp-10-17-165-232 10.17.165.233 eu-region-west-1,eu-region-west-2 /opt/28jul/logs/log4j/cms.txt");
        JsonNode jsonNode = objectMapper.readTree(line);
        jsonObject.addProperty(Constants.MESSAGE, jsonNode.get("result").get("_raw").asText());
        return jsonObject;
    }


    public void splitFileAndProcessJson(String inputFilePath, int sizeOfFileInMB) throws IOException,ParseException {
        int counter = 1; //chunk counter
        String line;
        String previousLine=null;
        int  read;
        int sizeOfChunk = 1024 * 1024 * sizeOfFileInMB; // converting to bytes
        int lineCount = 0;
        JsonArray jsonArray = new JsonArray();
        byte[] buffer = new byte[sizeOfChunk];
        try(
                InputStream inputStream = new FileInputStream(inputFilePath);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        ){
            while (( read = bufferedInputStream.read(buffer))!= -1) {
                log.info("Reading file : " + inputFilePath + ", chunk no: " + counter
                        + ", read : " + read);
                InputStream dataInputStream = new ByteArrayInputStream(buffer, 0 , read);
                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( dataInputStream ) );
                while ((line = bufferedReader.readLine()) != null) {
                    if (jsonArray.size() == Integer.parseInt(Constants.JSON_ARRAY_SIZE)) {
                        JsonArray jsonArray1 = jsonArray;
                       // CompletableFuture.runAsync(() -> {
                            long lStartTime = System.currentTimeMillis();
                        log.info(laConsumer.postStandardLogInBatch(jsonArray1.toString())
                                + ", Timestamp: " + new Timestamp(System.currentTimeMillis())
                                + ", Elapsed time in milliseconds: " + (System.currentTimeMillis() - lStartTime));

                       // }, executor);
                        jsonArray = new JsonArray();
                    }
                    if (previousLine != null && previousLine.startsWith("{")) {
                        JsonObject jsonObj = processLine(previousLine + line);
                        jsonArray.add(jsonObj);
                        lineCount++;
                        previousLine = null;
                    } else if (previousLine == null && line.startsWith("{") && line.endsWith("}")) {
                        JsonObject jsonObj = processLine(line);
                        jsonArray.add(jsonObj);
                        lineCount++;
                    } else {
                        previousLine = line;
                    }
                }
                if (jsonArray.size() > 0) {
                    JsonArray jsonArray1 = jsonArray;
                    //CompletableFuture.runAsync(() -> {
                    long lStartTime = System.currentTimeMillis();
                    log.info(laConsumer.postStandardLogInBatch(jsonArray1.toString())
                            + ", Timestamp: " + new Timestamp(System.currentTimeMillis())
                           + ", Elapsed time in milliseconds: " + (System.currentTimeMillis() - lStartTime));
                    //}, executor);
                    jsonArray = new JsonArray();
                }
                log.info("Read chunk with line count: " + lineCount);
                counter++;
            }

        } catch ( IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
