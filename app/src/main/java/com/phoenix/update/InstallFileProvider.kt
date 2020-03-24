package com.phoenix.update

import android.content.Context
import androidx.core.content.FileProvider

/**
 * 安装apk
 */
class InstallFileProvider : FileProvider() {
    companion object {
        /**
         * 自定义Provider，避免上层发生provider冲突
         */
        @JvmStatic
        fun getFileProviderName(context: Context): String {
            return context.packageName + ".installProvider"
        }
    }
}