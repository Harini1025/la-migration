package com.broadcom.laMigration.util;


public class Constants {
    private Constants(){
        throw new IllegalStateException("Constants");
    }
    public static final String THREADS = System.getenv("LA_TEST_NO_OF_THREADS");
    public static final String TENANT_ID = "tenant_id";
    public static final String LOG_TYPE = "logtype";
    public static final String TEMP_FIELDS = "temp_fields";
    public static final String MESSAGE = "message";
    public static final String TENANT_ID_VALUE = System.getenv("LA_TEST_TENANT_ID");
    public static final String CHUNK_SIZE_IN_MB = System.getenv("LA_TEST_CHUNK_SIZE");
    public static final String OUTPUT_DIR_VM = "C:\\Users\\hkannan\\Desktop\\Harini\\Brodcom\\final\\";
    public static final String INPUT_DIR_VM = System.getenv("LA_TEST_INPUT_DIR_VM");
    public static final String JSON_ARRAY_SIZE = System.getenv("LA_TEST_JSON_ARRAY_SIZE");
    public static final String THREADS_POOL_2 = System.getenv("LA_TEST_JSON_POOL");
    public static final int FEIGN_RETRY = 5;
    public static final long FEIGN_RETRY_INTERVAL = 2000L;

}
