package com.phoenix.update

object BsPatch {
    external fun bspatch(oldApk: String?, newApk: String?, patch: String?)

    init {
        System.loadLibrary("native-lib")
    }
}