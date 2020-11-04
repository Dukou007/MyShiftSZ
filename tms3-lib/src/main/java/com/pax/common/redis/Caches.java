package com.pax.common.redis;

public class Caches {

    public static final String TERMINAL_ONLINE_CHECK = "TERMINAL_ONLINE_CHECK";
    public static final String TERMINAL_ONLINE_CHECK_ALL = "TERMINAL_ONLINE_CHECK_ALL";
    public static final String PXRETAILER_ONLINE = "PXRETAILER_ONLINE";
    public static final String PXRETAILER_ACCESS_TIME = "PXRETAILER_ACCESS_TIME";
    public static final String PXRETAILER_UPDATE_ACCESS_TIME_LOCK = "PXRETAILER_UPDATE_ACCESS_TIME_LOCK";
    public static final String PXRETAILER_UPDATE_ONLINE_STATUS_LOCK = "PXRETAILER_UPDATE_ONLINE_STATUS_LOCK";
    public static final String PXRETAILER_ACCESS_TIME_OLD = "PXRETAILER_ACCESS_TIME_OLD";
    public static final String TERMINAL_CHANGED_MESSAGE_QUEUE = "TERMINAL_CHANGED_MESSAGE_QUEUE";

    private Caches() {
    }

    public static String getTerminalCacheKey(String deviceSerialNumber) {
        return "TSN:" + deviceSerialNumber.toUpperCase();
    }

    public static String getTerminalInstalledAppHashCacheKey(String deviceSerialNumber) {
        return "installed-apps-hash:" + deviceSerialNumber;
    }
    
    public static String getTerminalSysmetricKeysHashCacheKey(String deviceSerialNumber) {
        return "sysmetric-keys-hash:" + deviceSerialNumber;
    }
}
