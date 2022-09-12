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
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class CommonUtils {

    @Autowired
    private LAConsumer laConsumer;
    private static  ObjectMapper objectMapper = new ObjectMapper();

    static ExecutorService executor = Executors.newFixedThreadPool(Integer.parseInt(Constants.THREADS_POOL_2));

    protected static JsonObject processLine(String line) throws ParseException, JsonProcessingException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("tenant_id", Constants.TENANT_ID);
        jsonObject.addProperty("logtype", "log4j");
        jsonObject.addProperty("temp_fields" , Constants.TENANT_ID + " dhcp-10-17-165-233 log4j dhcp-10-17-165-232 10.17.165.233 eu-region-west-1,eu-region-west-2 /opt/28jul/logs/log4j/cms.txt");
        JsonNode jsonNode = objectMapper.readTree(line);
        jsonObject.addProperty("message", jsonNode.get("result").get("_raw").asText());
        return jsonObject;
    }

    protected static void writeJsonArrayToFile(JsonArray jsonArray, String fileNameOutput, String outputPath) throws IOException {
        fileNameOutput = fileNameOutput.replace(".json","");
        FileWriter file = new FileWriter(outputPath + fileNameOutput + ".json");
        file.write(jsonArray.toString());
        file.close();
    }

    public void splitFileAndProcessJson(String inputFilePath, int sizeOfFileInMB, String outputPath) throws IOException,ParseException {
        int counter = 1; //chunk counter
        String line, previousLine=null;
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
               // memoryStats();
                log.info("Reading file : " + inputFilePath + ", chunk no: " + counter
                        + ", read : " + read);
                InputStream dataInputStream = new ByteArrayInputStream(buffer, 0 , read);
                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( dataInputStream ) );
                while ((line = bufferedReader.readLine()) != null) {
                    if (jsonArray.size() == Integer.parseInt(Constants.JSON_ARRAY_SIZE)) {
                        JsonArray jsonArray1 = jsonArray;
                       // CompletableFuture.runAsync(() -> {
                            //writeJsonArrayToFile(jsonArray1, Paths.get(inputFilePath).getFileName().toString() + lineCount, outputPath);
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
                        //writeJsonArrayToFile(jsonArray1, Paths.get(inputFilePath).getFileName().toString() + number, outputPath);
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

    public static void memoryStats() {
        int mb = 1024 * 1024;
        // get Runtime instance
        Runtime instance = Runtime.getRuntime();
        //System.out.println("***** Heap utilization statistics [MB] *****\n");
        // available memory
        //System.out.println("Total Memory: " + instance.totalMemory() / mb);
        // free memory
        //System.out.println("Free Memory: " + instance.freeMemory() / mb);
        // used memory
        System.out.println("Used Memory: "
                + (instance.totalMemory() - instance.freeMemory()) / mb);
        // Maximum available memory
        //System.out.println("Max Memory: " + instance.maxMemory() / mb);
    }

    public void sendLogs() throws IOException {
        String inputPath = "C:\\Users\\hkannan\\Desktop\\Harini\\Brodcom\\final\\";
       /* File  f = new File("C:\\Users\\hkannan\\Desktop\\Harini\\Brodcom\\final\\BigDataNew1C500.json");
        BufferedReader i = new BufferedReader(new FileReader(f));
        String line = i.readLine();
        System.out.println(line);
        ResponseEntity rs = laConsumer.postStandardLogInBatch(line);
        int status = rs.getStatusCode().value();
        System.out.println(status + rs.getBody().toString());*/

        Files.list(Paths.get(inputPath)).forEach( (file) -> {
            CompletableFuture.runAsync(() -> {
                System.out.println("File name : " + file.getFileName().toString() + ", Thread : " + Thread.currentThread().getName()
                        + " Timestamp: " + new Timestamp(System.currentTimeMillis()));
                File  f = new File(inputPath+file.getFileName().toString());
                BufferedReader i = null;
                try {
                    i = new BufferedReader(new FileReader(f));
                    String line = i.readLine();
                    long lStartTime = System.currentTimeMillis();
                   System.out.println(laConsumer.postStandardLogInBatch(line)
                           + " Timestamp: " + new Timestamp(System.currentTimeMillis()) +
                           " Elapsed time in milliseconds: " + (System.currentTimeMillis() - lStartTime));
                }  catch (IOException e) {
                    e.printStackTrace();
                }
            }, executor).handle((res, err) -> {
                if (err != null) {
                    err.printStackTrace();
                    System.out.println("something went wrong" + err.getMessage());
                }
                return res;
            }).thenRun(executor::shutdown);
        });
    }



}
