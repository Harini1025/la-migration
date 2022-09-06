package com.broadcom.laMigration.util;

import com.broadcom.laMigration.consumer.LAConsumer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Paths;

@Component
public class CommonUtils {

    @Autowired
    private LAConsumer laConsumer;

    //todo jackson parser
    protected static JSONObject processLine(String line) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(line);
        JSONObject jsonOutput = new JSONObject();
        jsonOutput.put("tenant_id", Constants.TENANT_ID);
        //must include logic to find log type based on message
        jsonOutput.put("logtype", "log4j");
        jsonOutput.put("message", ((JSONObject) json.get("result")).get("_raw"));
        jsonOutput.put("temp_fields" , Constants.TENANT_ID + " dhcp-10-17-165-233 log4j dhcp-10-17-165-232 10.17.165.233 eu-region-west-1,eu-region-west-2 /opt/28jul/logs/log4j/cms.txt");
        return jsonOutput;
    }

    protected static void writeToFile(JSONObject line, PrintWriter f)  {
        try{
            f.append(line.toString()+"\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void writeJsonArrayToFile(JSONArray jsonArray, String fileNameOutput, String outputPath) throws IOException {
        fileNameOutput = fileNameOutput.replace(".json","");
        FileWriter file = new FileWriter(outputPath + fileNameOutput + ".json");
        file.write(jsonArray.toJSONString());
        file.close();
    }

    public void splitFileAndProcessJson(String inputFilePath, int sizeOfFileInMB, String outputPath) throws IOException,ParseException {
        int counter = 1; //chunk counter
        int jsonArraySize = 1; //not req when making api call
        //todo add line count
        int lineCount = 0, read;
        String line, previousLine=null;
        int sizeOfChunk = 1024 * 1024 * sizeOfFileInMB; // converting to bytes
        byte[] buffer = new byte[sizeOfChunk];
        JSONArray jsonArray = new JSONArray();
        //todo to read lines from chunks
        try(
                InputStream inputStream = new FileInputStream(inputFilePath);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        ){
            while (( read = bufferedInputStream.read(buffer))!= -1) {
               // memoryStats();
                System.out.println("Reading file : " + inputFilePath + ", chunk no: " + counter
                        + ", read : " + read);
                InputStream dataInputStream = new ByteArrayInputStream(buffer, 0 , read);
                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( dataInputStream ) );

                while( (line  = bufferedReader.readLine()) != null )
                {
                    if(jsonArray.size() == Constants.JSON_ARRAY_SIZE){
                        //make api call instead of storing it in local system
                        //writeJsonArrayToFile(jsonArray, Paths.get(inputFilePath).getFileName().toString() + jsonArraySize, outputPath);
                        ResponseEntity rs = laConsumer.postStandardLogInBatch(jsonArray.toJSONString());
                        int status = rs.getStatusCode().value();
                        System.out.println(status + rs.getBody().toString());
                        jsonArraySize++;
                        jsonArray.clear();
                    }
                    if(previousLine != null && previousLine.startsWith("{")){
                        JSONObject jsonObj = processLine(previousLine + line);
                        jsonArray.add(jsonObj);
                        lineCount++;
                        previousLine = null;
                    }
                    else if( previousLine == null && line.startsWith("{") &&  line.endsWith("}")) {
                        JSONObject jsonObj = processLine(line);
                        jsonArray.add(jsonObj);
                        lineCount++;
                    }else{
                        previousLine = line;
                    }
                }
                if(jsonArray.size() > 0){
                    //make api call instead of storing it in local system
                    ResponseEntity rs = laConsumer.postStandardLogInBatch(jsonArray.toJSONString());
                    int status = rs.getStatusCode().value();
                    System.out.println(status + rs.getBody().toString());
                    //writeJsonArrayToFile(jsonArray, Paths.get(inputFilePath).getFileName().toString() + jsonArraySize, outputPath);
                    jsonArraySize++;
                    jsonArray.clear();
                }
                System.out.println("Read chunk with line count: "+lineCount);
                counter++;

            }
        } catch (ParseException  | IOException e) {
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
        ///System.out.println("Total Memory: " + instance.totalMemory() / mb);
        // free memory
        //System.out.println("Free Memory: " + instance.freeMemory() / mb);
        // used memory
        System.out.println("Used Memory: "
                + (instance.totalMemory() - instance.freeMemory()) / mb);
        // Maximum available memory
        //System.out.println("Max Memory: " + instance.maxMemory() / mb);
    }

    public void sendLogs() throws IOException {
        File  f = new File("C:\\Users\\hkannan\\Desktop\\Harini\\Brodcom\\final\\BigDataNew11.json");
        BufferedReader i = new BufferedReader(new FileReader(f));
        String line = i.readLine();
        System.out.println(line);
        ResponseEntity rs = laConsumer.postStandardLogInBatch(line);
        int status = rs.getStatusCode().value();
        System.out.println(status + rs.getBody().toString());
    }

}
