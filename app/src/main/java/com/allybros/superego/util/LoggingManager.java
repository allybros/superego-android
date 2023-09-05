package com.allybros.superego.util;

import android.util.Log;

/**
 * Created by orcun on 9.08.2023
 */

public class LoggingManager {
    private static final boolean isActive = true;
    public static void log(String prefix, String message){
        if (isActive) Log.d(prefix, message);
    }
}
