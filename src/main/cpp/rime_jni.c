//
// Created by mao on 2026/1/5.
//

#include <jni.h>
#include <rime_api.h>


static const RimeApi *api;

static void __attribute__((constructor)) init_rime_api(void) {
    api = rime_get_api();
}



static void rime_notification_handler(void *context_object,
                                      RimeSessionId session_id,
                                      const char *message_type,
                                      const char *message_value) {

}


JNIEXPORT void JNICALL
Java_com_github_maoabc_librime_RimeJni_start0(JNIEnv *env, jclass clazz,
                                              jobject notification) {
    RIME_STRUCT(RimeTraits, traits);

}