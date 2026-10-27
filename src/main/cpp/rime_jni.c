//
// Created by mao on 2026/1/5.
//

#include <jni.h>
#include <android/log.h>
#include <rime_api.h>
#include <stdlib.h>
#include <string.h>

#ifdef _LP64
#define jlong_to_ptr(a) ((void*)(a))
#define ptr_to_jlong(a) ((jlong)(a))
#else
#define jlong_to_ptr(a) ((void*)(int)(a))
#define ptr_to_jlong(a) ((jlong)(int)(a))
#endif

#define LOG_TAG "rime"
#define  ALOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define  ALOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define  ALOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define  ALOGW(...)  __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)

static const RimeApi *api;

static void __attribute__((constructor)) init_rime_api(void) {
    api = rime_get_api();
}


static void rime_notification_handler(void *context_object,
                                      RimeSessionId session_id,
                                      const char *message_type,
                                      const char *message_value) {

    ALOGD("rime:%d  %s %s", (int) session_id, message_type, message_value);

}

static RIME_MODULE_LIST(sample_modules, "default", "sample");


JNIEXPORT void JNICALL
Java_com_github_maoabc_librime_RimeJni_start0(JNIEnv *env, jclass clazz, jstring path,
                                              jobject notification) {
    RIME_STRUCT(RimeTraits, traits);
    traits.app_name = "simple.test";
    traits.modules = sample_modules;
    const char *user = (*env)->GetStringUTFChars(env, path, NULL);
    traits.user_data_dir = user;

    api->setup(&traits);
    api->set_notification_handler(rime_notification_handler, NULL);
    api->initialize(&traits);

    if (api->start_maintenance(True)) {
//        api->join_maintenance_thread();
    }

}

JNIEXPORT jlong JNICALL
Java_com_github_maoabc_librime_RimeJni_createSession0(JNIEnv *env, jclass clazz) {
    jlong id = (jlong) api->create_session();

    RIME_STRUCT(RimeStatus, status);
    api->get_status(id, &status);
    api->set_option(id, "ascii_mode", 0);

    return id;
}

JNIEXPORT void JNICALL
Java_com_github_maoabc_librime_RimeJni_destroySession0(JNIEnv *env, jclass clazz,
                                                       jlong session_id) {
    api->destroy_session((RimeSessionId) session_id);
}

JNIEXPORT jboolean JNICALL
Java_com_github_maoabc_librime_RimeJni_processKey0(JNIEnv *env, jclass clazz, jlong session_id,
                                                   jint keycode, jint mask) {
    Bool result = api->process_key((RimeSessionId) session_id, (int) keycode, (int) mask);
    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_github_maoabc_librime_RimeJni_simulateKeySequence0(JNIEnv *env, jclass clazz,
                                                            jlong session_id,
                                                            jstring key_sequence) {
    const char *seq = (*env)->GetStringUTFChars(env, key_sequence, NULL);
    if (!seq) return JNI_FALSE;
    Bool result = api->simulate_key_sequence((RimeSessionId) session_id, seq);
    (*env)->ReleaseStringUTFChars(env, key_sequence, seq);
    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jstring JNICALL
Java_com_github_maoabc_librime_RimeJni_getCommitText0(JNIEnv *env, jclass clazz, jlong session_id) {
    RIME_STRUCT(RimeCommit, commit);
    if (api->get_commit((RimeSessionId) session_id, &commit)) {
        jstring text = (*env)->NewStringUTF(env, commit.text ? commit.text : "");
        api->free_commit(&commit);
        return text;
    }
    return NULL;
}

JNIEXPORT jstring JNICALL
Java_com_github_maoabc_librime_RimeJni_getComposingText0(JNIEnv *env, jclass clazz,
                                                         jlong session_id) {
    RIME_STRUCT(RimeContext, context);
    if (api->get_context((RimeSessionId) session_id, &context)) {
        const char *preedit = (context.composition.preedit) ? context.composition.preedit : "";
        jstring text = (*env)->NewStringUTF(env, preedit);
        api->free_context(&context);
        return text;
    }
    return NULL;
}

JNIEXPORT jboolean JNICALL
Java_com_github_maoabc_librime_RimeJni_selectCandidate0(JNIEnv *env, jclass clazz, jlong session_id,
                                                        jint index) {
    Bool result = api->select_candidate((RimeSessionId) session_id, (size_t) index);
    return result ? JNI_TRUE : JNI_FALSE;
}


JNIEXPORT jlong JNICALL
Java_com_github_maoabc_librime_RimeJni_candidateListFromIndex0(JNIEnv *env, jclass clazz,
                                                               jlong session_id, jint index) {

    RimeCandidateListIterator *iterator = (RimeCandidateListIterator *) calloc(1,
                                                                               sizeof(RimeCandidateListIterator));
    if (!iterator) return 0;

    Bool result = api->candidate_list_from_index((RimeSessionId) session_id, iterator, (int) index);
    if (result) {
        return ptr_to_jlong(iterator);
    } else {
        free(iterator);
        return 0;
    }
}

JNIEXPORT jboolean JNICALL
Java_com_github_maoabc_librime_RimeJni_candidateListNext0(JNIEnv *env, jclass clazz,
                                                          jlong iterator_ptr) {
    RimeCandidateListIterator *iterator = (RimeCandidateListIterator *) jlong_to_ptr(iterator_ptr);
    Bool result = api->candidate_list_next(iterator);
    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_github_maoabc_librime_RimeJni_candidateListEnd0(JNIEnv *env, jclass clazz,
                                                         jlong iterator_ptr) {
    RimeCandidateListIterator *iterator = (RimeCandidateListIterator *) jlong_to_ptr(iterator_ptr);
    api->candidate_list_end(iterator);
    free(iterator);
}


JNIEXPORT jstring JNICALL
Java_com_github_maoabc_librime_RimeJni_candidateGetText0(JNIEnv *env, jclass clazz,
                                                         jlong iterator_ptr) {
    RimeCandidateListIterator *iterator = (RimeCandidateListIterator *) jlong_to_ptr(iterator_ptr);
    const char *text = iterator->candidate.text ? iterator->candidate.text : "";
    return (*env)->NewStringUTF(env, text);
}

JNIEXPORT jstring JNICALL
Java_com_github_maoabc_librime_RimeJni_candidateGetComment0(JNIEnv *env, jclass clazz,
                                                            jlong iterator_ptr) {
    RimeCandidateListIterator *iterator = (RimeCandidateListIterator *) jlong_to_ptr(iterator_ptr);
    const char *comment = iterator->candidate.comment ? iterator->candidate.comment : "";
    return (*env)->NewStringUTF(env, comment);
}

JNIEXPORT jbyteArray JNICALL
Java_com_github_maoabc_librime_RimeJni_candidateListSlice0(JNIEnv *env, jclass clazz,
                                                           jlong session_id, jint idx, jint count) {
    RimeCandidateListIterator iterator = {0};
#define MAX_BUF 8*1024

    Bool result = api->candidate_list_from_index((RimeSessionId) session_id, &iterator, idx);
    if (!result) {
        return NULL;
    }
    char buf[MAX_BUF];
    int buf_idx = 2;
    int rcount = 0;
    while (rcount < count && api->candidate_list_next(&iterator)) {
        const char *text = iterator.candidate.text ? iterator.candidate.text : "";
        const char *comment = iterator.candidate.comment ? iterator.candidate.comment : "";
        uint16_t text_len = (uint16_t) strlen(text);
        uint16_t comment_len = (uint16_t) strlen(comment);

        if (buf_idx + 4 + text_len + comment_len > sizeof(buf)) {
            break;
        }

        buf[buf_idx++] = (text_len & 0xFF);
        buf[buf_idx++] = ((text_len >> 8) & 0xFF);
        memcpy(&buf[buf_idx], text, text_len);
        buf_idx += text_len;

        buf[buf_idx++] = (comment_len & 0xFF);
        buf[buf_idx++] = ((comment_len >> 8) & 0xFF);
        memcpy(&buf[buf_idx], comment, comment_len);
        buf_idx += comment_len;
        rcount++;
    }
    api->candidate_list_end(&iterator);

    if (rcount == 0) {
        return NULL;
    }

    buf[0] = (rcount & 0xFF);
    buf[1] = ((rcount >> 8) & 0xFF);

    jbyteArray byteArray = (*env)->NewByteArray(env, (jsize) buf_idx);
    (*env)->SetByteArrayRegion(env, byteArray, 0, (jsize) buf_idx, (jbyte *) buf);
    return byteArray;
}
