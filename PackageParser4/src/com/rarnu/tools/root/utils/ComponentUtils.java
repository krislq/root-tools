package com.rarnu.tools.root.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.util.DisplayMetrics;
import com.rarnu.command.CommandResult;
import com.rarnu.command.RootUtils;
import com.rarnu.tools.root.common.CompInfo;
import com.rarnu.tools.root.common.EnableappInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ComponentUtils {

    public static boolean doEnableApplication(EnableappInfo info) {
        String source = info.filePath;
        String filePath = "";
        String dest = "";
        if (info.type == 0) {
            dest = "/system/app/";
            filePath = dest + source.replace(DirHelper.ENABLEAPP_DIR_SYSTEM, "");
        } else if (info.type == 1) {
            dest = "/data/app/";
            filePath = dest + source.replace(DirHelper.ENABLEAPP_DIR_DATA, "");
        } else if (info.type == 3) {
            dest = "/system/priv-app/";
            filePath = dest + source.replace(DirHelper.ENABLEAPP_DIR_PRIVATE, "");
        }
        try {
            CommandResult cmdRet = RootUtils.runCommand(String.format("busybox cp %s %s", source, dest), true, null);
            if (cmdRet.error.equals("")) {
                cmdRet = RootUtils.runCommand(String.format("rm %s", source), true, null);
                if (cmdRet.error.equals("")) {
                    cmdRet = RootUtils.runCommand("chmod 644 " + filePath, true, null);
                }
                info.filePath = filePath;
            }
            return cmdRet.error.equals("");
        } catch (Throwable th) {
            return false;
        }
    }

    public static boolean doDisableApplication(EnableappInfo info) {
        String source = info.info.sourceDir;
        String filePath = "";
        String dest = "";
        if (info.type == 0) {
            dest = DirHelper.ENABLEAPP_DIR_SYSTEM;
            filePath = dest + source.replace("/system/app/", "");
        } else if (info.type == 1) {
            dest = DirHelper.ENABLEAPP_DIR_DATA;
            filePath = dest + source.replace("/data/app/", "");
        } else if (info.type == 3) {
            dest = DirHelper.ENABLEAPP_DIR_PRIVATE;
            filePath = dest + source.replace("/system/priv-app/", "");
        } else {
            return false;
        }
        try {
            CommandResult cmdRet = RootUtils.runCommand(String.format("busybox cp %s %s", source, dest), true, null);
            if (cmdRet.error.equals("")) {
                cmdRet = RootUtils.runCommand(String.format("rm %s", source), true, null);
                info.filePath = filePath;
            }
            return cmdRet.error.equals("");
        } catch (Throwable th) {
            return false;
        }
    }

    public static boolean doEnabledComponent(Context context, ComponentName receiverName) {

        try {
            RootUtils.runCommand(String.format("pm enable '%s/%s'", receiverName.getPackageName(), receiverName.getClassName()), true, null);
            return context.getPackageManager().getComponentEnabledSetting(receiverName) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        } catch (Throwable th) {
            return false;
        }
    }

    public static boolean doDisableComponent(Context context, ComponentName receiverName) {
        try {
            RootUtils.runCommand(String.format("pm disable '%s/%s'", receiverName.getPackageName(), receiverName.getClassName()), true, null);
            return context.getPackageManager().getComponentEnabledSetting(receiverName) == PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        } catch (Throwable th) {
            return false;
        }
    }

    public static PackageParser.Package parsePackageInfo(PackageInfo info, DisplayMetrics dm) {
        String fileAbsPath = info.applicationInfo.publicSourceDir;
        PackageParser packageParser = new PackageParser(fileAbsPath);
        File sourceFile = new File(fileAbsPath);
        PackageParser.Package pkg = packageParser.parsePackage(sourceFile, fileAbsPath, dm, PackageParser.PARSE_IS_SYSTEM);
        return pkg;
    }

    public static List<CompInfo> getPackageRSList(Context context, PackageParser.Package pkg) {
        List<CompInfo> lstComponentInfo = new ArrayList<CompInfo>();
        if (context != null) {
            PackageManager pm = context.getPackageManager();
            List<PackageParser.Activity> lstReceiver = pkg.receivers;
            for (PackageParser.Activity a : lstReceiver) {
                CompInfo info = new CompInfo();
                info.component = a;
                info.fullPackageName = a.getComponentName().getClassName();
                info.enabled = pm.getComponentEnabledSetting(a.getComponentName()) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                lstComponentInfo.add(info);
            }

            List<PackageParser.Service> lstService = pkg.services;
            for (PackageParser.Service s : lstService) {
                CompInfo info = new CompInfo();
                info.component = s;
                info.fullPackageName = s.getComponentName().getClassName();
                info.enabled = pm.getComponentEnabledSetting(s.getComponentName()) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                lstComponentInfo.add(info);
            }
        }
        return lstComponentInfo;
    }

    public static ComponentName getPackageComponentName(PackageParser.Component<?> comp) {
        return comp.getComponentName();
    }

    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo si : serviceList) {
            if (si.service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
