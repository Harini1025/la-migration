package com.broadcom.laMigration.util;


public class Constants {
    public static String THREADS = System.getenv("no_of_threads");
    public static String TENANT_ID = System.getenv("tenant_id");
    public static String CHUNK_SIZE_IN_MB = "10";
    public static String ACCESS_KEY = "";
    public static String SECRET_KEY = "";
    public static String REGION = "ap-south-1";
    public static String JAVA_HOME = System.getenv("JAVA_HOME");
    static String BUCKET_NAME ="my-test-bucket-poc-2";
    public static String INPUT_DIR_S3 = "input/";
    public static String PROCESSED_DIR_S3 = "processed/";
    public static String FAILED_DIR_S3 = "";
    public static String INPUT_DIR_VM = "C:\\Users\\hkannan\\Desktop\\Harini\\Brodcom\\files\\";
    public static String OUTPUT_DIR_VM = "C:\\Users\\hkannan\\Desktop\\Harini\\Brodcom\\final\\";
    public static int JSON_ARRAY_SIZE = 1000;
    public static int FEIGN_RETRY = 5;
    public static long FEIGN_RETRY_INTERVAL = 2000L;

}
