package net.cattaka.android.fastchecklist.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class ContextUtil {
    public static String getVersion(Context context) {
        String versionName = "";
        String packageName = context.getClass().getPackage().getName();
        PackageManager pm = context.getPackageManager();
        try {
                PackageInfo info = null;
                info = pm.getPackageInfo(packageName, 0);
                versionName = info.versionName;
        } catch (NameNotFoundException e) {
        }
        return versionName;
    }
}
