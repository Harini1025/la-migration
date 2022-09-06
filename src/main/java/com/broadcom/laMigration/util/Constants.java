package com.broadcom.laMigration.util;


public class Constants {
    public static String THREADS = System.getenv("LA_TEST_NO_OF_THREADS");
    public static String TENANT_ID = System.getenv("LA_TEST_TENANT_ID");
    public static String CHUNK_SIZE_IN_MB = "10";
    public static String ACCESS_KEY = "";
    public static String SECRET_KEY = "";
    public static String REGION = "ap-south-1";
    static String BUCKET_NAME ="my-test-bucket-poc-2";
    public static String INPUT_DIR_S3 = "input/";
    public static String PROCESSED_DIR_S3 = "processed/";
    public static String FAILED_DIR_S3 = "";
    public static String INPUT_DIR_VM = "C:\\Users\\hkannan\\Desktop\\Harini\\Brodcom\\files\\";
    public static String OUTPUT_DIR_VM = "C:\\Users\\hkannan\\Desktop\\Harini\\Brodcom\\final\\";
    //public static String INPUT_DIR_VM = System.getenv("LA_TEST_INPUT_DIR_VM");
    //public static String OUTPUT_DIR_VM = System.getenv("LA_TEST_OUTPUT_DIR_VM");
    public static int JSON_ARRAY_SIZE = 2000;
    public static int FEIGN_RETRY = 5;
    public static long FEIGN_RETRY_INTERVAL = 2000L;

}
