package it.niedermann.nextcloud.exception;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextcloud.android.sso.helper.VersionCheckHelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;

public class ExceptionUtil {

    private ExceptionUtil() {
        // Util class
    }

    public static String getDebugInfos(@NonNull Context context, @NonNull Throwable throwable) {
        return getDebugInfos(context, throwable, null, null);
    }

    public static String getDebugInfos(@NonNull Context context, @NonNull Throwable throwable, @Nullable String flavor) {
        return getDebugInfos(context, throwable, flavor, null);
    }

    public static String getDebugInfos(@NonNull Context context, @NonNull Throwable throwable, @Nullable String flavor, @Nullable String serverAppVersion) {
        return getDebugInfos(context, Collections.singleton(throwable), flavor, serverAppVersion);
    }

    public static String getDebugInfos(@NonNull Context context, @NonNull Collection<Throwable> throwables) {
        return getDebugInfos(context, throwables, null, null);
    }

    public static String getDebugInfos(@NonNull Context context, @NonNull Collection<Throwable> throwables, @Nullable String flavor) {
        return getDebugInfos(context, throwables, flavor, null);
    }

    public static String getDebugInfos(@NonNull Context context, @NonNull Collection<Throwable> throwables, @Nullable String flavor, @Nullable String serverAppVersion) {
        final StringBuilder debugInfos = new StringBuilder()
                .append(getAppVersions(context, flavor, serverAppVersion))
                .append("\n\n---\n")
                .append(getDeviceInfos())
                .append("\n\n---");
        for (Throwable throwable : throwables) {
            debugInfos.append("\n\n").append(getStacktraceOf(throwable));
        }
        return debugInfos.toString();
    }

    private static String getAppVersions(@NonNull Context context, @Nullable String flavor, @Nullable String serverAppVersion) {
        String versions = "";
        try {
            PackageInfo pInfo = context.getApplicationContext().getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(), 0);
            versions += "App Version: " + pInfo.versionName + "\n";
            versions += "App Version Code: " + pInfo.versionCode + "\n";
        } catch (PackageManager.NameNotFoundException e) {
            versions += "App Version: " + e.getMessage() + "\n";
            e.printStackTrace();
        }

        if (serverAppVersion != null) {
            if (TextUtils.isEmpty(serverAppVersion)) {
                versions += "Server App Version: " + "unknown";
            } else {
                versions += "Server App Version: " + serverAppVersion + "\n";
            }
        }

        if (flavor != null) {
            if (TextUtils.isEmpty(flavor)) {
                versions += "App Flavor: " + "unknown";
            } else {
                versions += "App Flavor: " + flavor + "\n";
            }
        }

        versions += "\n";
        try {
            versions += "Files App Version Code: " + VersionCheckHelper.getNextcloudFilesVersionCode(context);
        } catch (PackageManager.NameNotFoundException e) {
            versions += "Files App Version Code: " + e.getMessage();
            e.printStackTrace();
        }
        return versions;
    }

    private static String getDeviceInfos() {
        return "\n"
                + "OS Version: " + System.getProperty("os.version") + "(" + Build.VERSION.INCREMENTAL + ")" + "\n"
                + "OS API Level: " + Build.VERSION.SDK_INT + "\n"
                + "Device: " + Build.DEVICE + "\n"
                + "Manufacturer: " + Build.MANUFACTURER + "\n"
                + "Model (and Product): " + Build.MODEL + " (" + Build.PRODUCT + ")";
    }

    private static String getStacktraceOf(@NonNull Throwable e) {
        final StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}