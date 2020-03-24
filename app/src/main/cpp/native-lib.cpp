#include <jni.h>

// 执行合并差分包，实际上就是bspatch.c中的main()方法
// 由于native-lib.cpp为c++，bspatch.c为c，所以需要使用extern
extern "C" {
extern int main(int argc, char *argv[]);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_phoenix_update_BsPatch_bspatch(
        JNIEnv *env, jobject instance,
        jstring oldApk,
        jstring newApk,
        jstring patch) {

    const char *oldApkP = env->GetStringUTFChars(oldApk, 0);
    const char *newApkP = env->GetStringUTFChars(newApk, 0);
    const char *pathFile = env->GetStringUTFChars(patch, 0);

    char *argv[4] = {
            const_cast<char *>(""),
            const_cast<char *>(oldApkP),
            const_cast<char *>(newApkP),
            const_cast<char *>(pathFile)
    };

    main(4, argv);

    env->ReleaseStringUTFChars(oldApk, oldApkP);
    env->ReleaseStringUTFChars(newApk, newApkP);
    env->ReleaseStringUTFChars(patch, pathFile);
}