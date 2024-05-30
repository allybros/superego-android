package com.allybros.superego.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.allybros.superego.R;

public class ClientContextUtil {

    private ClientContextUtil() {}

    /**
     * Retrieves the version name of the application.
     *
     * @param context The application context.
     * @return A string representing the version name of the application, or an empty string if the version name
     *         cannot be retrieved.
     */
    public static String getVersionName(Context context) {
        // Add version name
        String packageName = context.getPackageName();
        String versionName;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("ApiTaskVersion", "Unable to get version name");
            return "";
        }
    }

    /**
     * Retrieves the application locale.
     *
     * @param context The application context.
     * @return A string representing the application locale.
     */
    public static String getApplicationLocale(Context context) {
        return context.getString(R.string.locale);
    }


    /**
     * Checks if there is an active network connection
     *
     * @param context The application context
     * @return Returns true if there is an active network connection, false otherwise
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo an = cm.getActiveNetworkInfo();
        return an != null && an.isConnectedOrConnecting();
    }

}
