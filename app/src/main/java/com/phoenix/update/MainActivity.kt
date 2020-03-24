package com.phoenix.update

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        tv_version.text = "版本号：${Utils.getVersionName(this)}"
        tv_version.text = Utils.getVersionName(this)

        btn_update.setOnClickListener {
            download()
        }
    }

    private fun download() {
        BackgroundTask(this).execute()
    }

    private class BackgroundTask(var context: Context) :
        AsyncTask<Void?, Void?, File?>() {

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d("GJH", "onPreExecute ")
        }

        override fun doInBackground(vararg params: Void?): File? {
            //模拟下载
            val oldApk = Utils.getAppPath(context.applicationContext)
            val downloadPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)?.absolutePath

            val patchFile = downloadPath + File.separator + "old-to-new.patch"
            val newApk = downloadPath + File.separator + "new.apk"

            Log.d("GJH", "doInBackground \n$oldApk \n$newApk \n$patchFile")

            if (File(patchFile).exists()) {
                val newApkFile = File(newApk)
                if (newApkFile.exists()) {
                    newApkFile.delete()
                }
                try {
                    BsPatch.bspatch(oldApk, newApk, patchFile)
                    return newApkFile
                } catch (e: Exception) {
                    return null
                }
            }

            return null
        }

        override fun onPostExecute(result: File?) {
            super.onPostExecute(result)
            if (result != null) {
                Log.d("GJH", "onPostExecute ${result.absolutePath} ${result.isFile}")
                Utils.installAPK(context, result)
            }
        }

        override fun onCancelled() {
            super.onCancelled()
            Log.d("GJH", "onCancelled ")
        }
    }

}
