package com.phoenix.update;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
    public static final String PATH_DOWNLOAD = "download"; //下载路径
    public static final String PATH_APK = "apk"; //APK路径
    public static final String PATH_PATCH = "patch"; //增量包路径

    /**
     * 获取Apk路径
     */
    public static String getAppPath(Context context) {
        return context.getApplicationInfo().sourceDir;
    }

    /**
     * 外部存储是否存在
     */

    public static boolean isExternalExits() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取缓存路径
     */
    @Nullable
    public static String getCachePath(Context context) {
        if (isExternalExits()) {
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                return externalCacheDir.getPath();
            }
        } else {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null) {
                return cacheDir.getPath();
            }
        }
        return null;
    }

    /**
     * 复制文件
     *
     * @return false代表复制失败
     */
    public static boolean copyFileToOther(String oldPath, String newPath) {
        File oldFile = new File(oldPath);
        if (oldFile.exists()) { //文件存在时
            InputStream inputStream = null;
            FileOutputStream fos = null;
            try {
                //读入原文件
                inputStream = new FileInputStream(oldPath);
                fos = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int byteRead;
                while ((byteRead = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteRead);
                }
                inputStream.close();
                fos.flush();
                fos.close();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 将Asset中的文件复制到目标路径{@code desPath}
     *
     * @param fileName Asset中文件名称
     * @param desPath  目标路径
     */
    public static void copyAsset(Context context, String fileName, String desPath) {
        AssetManager assets = context.getAssets();
        try {
            InputStream inputStream = assets.open(fileName);
            writeFile(desPath, fileName, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将流写入文件
     *
     * @param path     路径
     * @param fileName 文件名称
     * @param stream   流
     */
    public static void writeFile(String path, String fileName, InputStream stream) {
        if (stream == null || TextUtils.isEmpty(fileName)) {
            return;
        }
        FileOutputStream fileOutputStream = null;
        try {
            File desPath = new File(path);
            if (!desPath.exists()) {
                desPath.mkdirs();
            }
            File desFile = new File(path + File.separator + fileName);
            if (desFile.exists()) {
                desFile.delete();
            }
            desFile.createNewFile();
            fileOutputStream = new FileOutputStream(desFile);
            byte[] b = new byte[1024];
            int r = -1;
            while ((r = stream.read(b)) != -1) {
                fileOutputStream.write(b, 0, r);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
                stream = null;
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                fileOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 跳转安装APK
     *
     * @param file APK文件
     */
    public static void installAPK(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        } else {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, InstallFileProvider.getFileProviderName(context), file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        }
        Log.d("GJH", "installAPK " + intent.toString());
        context.startActivity(intent);
    }

    /**
     * 安装apk
     */
    public static void installApk(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent intent7 = new Intent(Intent.ACTION_VIEW);
            intent7.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent7.addCategory("android.intent.category.DEFAULT");
            Uri contentUri = FileProvider.getUriForFile(context, InstallFileProvider.getFileProviderName(context), file);
            intent7.setDataAndType(contentUri, "application/vnd.android.package-archive");
            intent7.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent7);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setAction(Intent.ACTION_DEFAULT);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 获取包信息
     *
     * @param context .
     * @return 包信息
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info;
        try {
            PackageManager manager = context.getPackageManager();
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        return info;
    }

    /**
     * 获取版本号
     *
     * @param context .
     * @return VersionCode 版本号
     */
    public static int getVersionCode(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        return packageInfo == null ? -1 : packageInfo.versionCode;
    }

    /**
     * 回去版本名称
     *
     * @param context .
     * @return VersionName 版本名称
     */
    public static String getVersionName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        return packageInfo == null ? "" : packageInfo.versionName;
    }
}
