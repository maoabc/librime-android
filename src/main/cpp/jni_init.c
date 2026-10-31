
#include <jni.h>


JavaVM *javaVM;


extern int init_rime_java_objects(JNIEnv *env);
extern int register_native_methods(JNIEnv *env);

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVM = vm;
    JNIEnv *env;
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    if (init_rime_java_objects(env) != 0) {
        return -1;
    }
    if (register_native_methods(env) != 0) {
        return -1;
    }

    return JNI_VERSION_1_6;
}
