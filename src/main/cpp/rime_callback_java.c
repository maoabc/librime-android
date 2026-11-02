//
// Created by assistant on 2026/1/6.
//

#include <jni.h>
#include <android/log.h>
#include <rime_api.h>
#include <pthread.h>

extern JavaVM *javaVM;


static jobject g_notification_obj = NULL;

static jmethodID notifi_methodId = NULL;

static JNIEnv *get_env(void) {
    JNIEnv *env = NULL;
    jint res = (*javaVM)->GetEnv(javaVM, (void **) &env, JNI_VERSION_1_6);
    if (res == JNI_EDETACHED) {
        if ((*javaVM)->AttachCurrentThread(javaVM, &env, NULL) == JNI_OK) {
            return NULL;
        }
    }
    return env;
}

int init_rime_java_objects(JNIEnv *env) {
    // 1. 获取 RimeNotification 的 handler 方法 ID
    jclass notifClass = (*env)->FindClass(env, "com/github/maoabc/librime/cb/RimeNotification");
    if (notifClass == NULL) {
        return -1;
    }
    notifi_methodId = (*env)->GetMethodID(env, notifClass, "handler",
                                          "(ILjava/lang/String;Ljava/lang/String;)V");

    return 0;
}

// 设置通知对象实例
void set_rime_global_notification(JNIEnv *env, jobject notification) {
    if (g_notification_obj != NULL) {
        (*env)->DeleteGlobalRef(env, g_notification_obj);
        g_notification_obj = NULL;
    }
    if (notification != NULL) {
        g_notification_obj = (*env)->NewGlobalRef(env, notification);
    }
}

// 回调函数
void rime_notification_handler(void *context_object,
                               RimeSessionId session_id,
                               const char *message_type,
                               const char *message_value) {
    if (g_notification_obj != NULL) {

        JNIEnv *env = NULL;
        jint res = (*javaVM)->GetEnv(javaVM, (void **) &env, JNI_VERSION_1_6);
        bool env_attached = false;
        if (res == JNI_EDETACHED) {
            if ((*javaVM)->AttachCurrentThread(javaVM, &env, NULL) == JNI_OK) {
                env_attached = true;
            }
        }
        if (env != NULL) {
            jstring j_type = (*env)->NewStringUTF(env, message_type ? message_type : "");
            jstring j_value = (*env)->NewStringUTF(env, message_value ? message_value : "");
            (*env)->CallVoidMethod(env, g_notification_obj, notifi_methodId, (jint) session_id,
                                   j_type, j_value);
            if (j_type) (*env)->DeleteLocalRef(env, j_type);
            if (j_value) (*env)->DeleteLocalRef(env, j_value);
        }

        if (env_attached) {
            (*javaVM)->DetachCurrentThread(javaVM);
        }
    }
}


