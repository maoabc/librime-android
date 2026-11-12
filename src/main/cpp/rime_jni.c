//
// Created by mao on 2026/1/5.
//

#include <jni.h>
#include <android/log.h>
#include <rime_api.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <sys/param.h>

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

const RimeApi *api;

static void __attribute__((constructor)) init_rime_api(void) {
    api = rime_get_api();
}

extern void set_rime_global_notification(JNIEnv *env, jobject notification);

extern void rime_notification_handler(void *context_object,
                                      RimeSessionId session_id,
                                      const char *message_type,
                                      const char *message_value);

static RIME_MODULE_LIST(sample_modules, "default", "lua", "predict");


static void
start0(JNIEnv *env, jclass clazz, jstring path,
       jobject notification) {
    set_rime_global_notification(env, notification);

    RIME_STRUCT(RimeTraits, traits);
    traits.app_name = "rime.myrime";
    traits.modules = sample_modules;
    const char *user = (*env)->GetStringUTFChars(env, path, NULL);
    traits.user_data_dir = user;

    api->setup(&traits);
    api->set_notification_handler(rime_notification_handler, NULL);
    api->initialize(&traits);

}


static jboolean
startMaintenance0(JNIEnv *env, jclass clazz,
                  jboolean full_check) {
    Bool result = api->start_maintenance(full_check ? True : False);
    return result ? JNI_TRUE : JNI_FALSE;
}

static jboolean
isMaintenanceMode0(JNIEnv *env, jclass clazz) {
    Bool result = api->is_maintenance_mode();
    return result ? JNI_TRUE : JNI_FALSE;
}

static void
joinMaintenanceThread0(JNIEnv *env, jclass clazz) {
    api->join_maintenance_thread();
}

static jboolean
prebuild0(JNIEnv *env, jclass clazz) {
    Bool result = api->prebuild();
    return result ? JNI_TRUE : JNI_FALSE;
}

static jboolean
deploy0(JNIEnv *env, jclass clazz) {
    Bool result = api->deploy();
    return result ? JNI_TRUE : JNI_FALSE;
}

static jboolean
syncUserData0(JNIEnv *env, jclass clazz) {
    Bool result = api->sync_user_data();
    return result ? JNI_TRUE : JNI_FALSE;
}


static jlong
createSession0(JNIEnv *env, jclass clazz) {
    return (jlong) api->create_session();
}

static void
destroySession0(JNIEnv *env, jclass clazz,
                jlong session_id) {
    api->destroy_session((RimeSessionId) session_id);
}

static jboolean
processKey0(JNIEnv *env, jclass clazz, jlong session_id,
            jint keycode, jint mask) {
    Bool result = api->process_key((RimeSessionId) session_id, (int) keycode, (int) mask);
    return result ? JNI_TRUE : JNI_FALSE;
}


static jbyteArray
getCommitText0(JNIEnv *env, jclass clazz, jlong session_id) {
    RIME_STRUCT(RimeCommit, commit);
    if (api->get_commit((RimeSessionId) session_id, &commit)) {
        if (commit.text == NULL) {
            api->free_commit(&commit);
            return NULL;
        }
        size_t len = strlen(commit.text);

        jbyteArray arr = (*env)->NewByteArray(env, (jsize) len);
        (*env)->SetByteArrayRegion(env, arr, 0, (jsize) len, (const jbyte *) commit.text);
        api->free_commit(&commit);
        return arr;
    }
    return NULL;
}

static jbyteArray
getComposingText0(JNIEnv *env, jclass clazz,
                  jlong session_id) {
    RIME_STRUCT(RimeContext, context);
    if (api->get_context((RimeSessionId) session_id, &context)) {
        if (context.composition.preedit == NULL) {
            api->free_context(&context);
            return NULL;
        }
        size_t len = strlen(context.composition.preedit);
        jbyteArray arr = (*env)->NewByteArray(env, (jsize) len);
        (*env)->SetByteArrayRegion(env, arr, 0, (jsize) len,
                                   (const jbyte *) context.composition.preedit);
        api->free_context(&context);
        return arr;
    }
    return NULL;
}

static jboolean
selectCandidate0(JNIEnv *env, jclass clazz, jlong session_id,
                 jint index) {
    Bool result = api->select_candidate((RimeSessionId) session_id, (size_t) index);
    return result ? JNI_TRUE : JNI_FALSE;
}


static jbyteArray
candidateListSlice0(JNIEnv *env, jclass clazz,
                    jlong session_id, jint idx, jint count, jint max_char) {
    RimeCandidateListIterator iterator = {0};
#define MAX_BUF 4*1024

    Bool result = api->candidate_list_from_index((RimeSessionId) session_id, &iterator, idx);
    if (!result) {
        return NULL;
    }
    char buf[MAX_BUF];
    int buf_idx = 2;
    int rcount = 0;
    int text_rlen = 0;
    while (rcount < count && api->candidate_list_next(&iterator)) {
        const char *text = iterator.candidate.text ? iterator.candidate.text : "";
        const char *comment = iterator.candidate.comment ? iterator.candidate.comment : "";
        uint16_t text_len = (uint16_t) strlen(text);
        uint16_t comment_len = (uint16_t) strlen(comment);

        if (buf_idx + 4 + text_len + comment_len > sizeof(buf)) {
            // 缓存空间不足就退出, 应该很少候选能在1千字以上吧?
            break;
        }
        text_rlen += text_len;
        if (rcount > 0 && text_rlen > max_char) {
            // 限制初次的候选总长度, 打字时让这个高速返回, 打开候选界面lazy加载时
            // 可以把它调高就不会影响按count加载
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

static jbyteArray
getStatus0(JNIEnv *env, jclass clazz, jlong session_id) {
    char tmp[256];
    RIME_STRUCT(RimeStatus, status);
    Bool res = api->get_status((RimeSessionId) session_id, &status);
    if (!res) {
        return NULL;
    }


    const char *schema_id = status.schema_id ? status.schema_id : "";
    const char *schema_name = status.schema_name ? status.schema_name : "";

    uint16_t schema_id_len = (uint16_t) strlen(schema_id);
    uint16_t schema_name_len = (uint16_t) strlen(schema_name);

    char *buf;
    int need_size = 4 + schema_id_len + schema_name_len + 7;
    if (need_size > sizeof(tmp)) {
        buf = malloc(need_size);
        if (buf == NULL) {
            api->free_status(&status);
            return NULL;
        }
    } else {
        buf = tmp;
    }
    int buf_idx = 0;

    buf[buf_idx++] = (schema_id_len & 0xFF);
    buf[buf_idx++] = ((schema_id_len >> 8) & 0xFF);
    memcpy(&buf[buf_idx], schema_id, schema_id_len);
    buf_idx += schema_id_len;

    buf[buf_idx++] = (schema_name_len & 0xFF);
    buf[buf_idx++] = ((schema_name_len >> 8) & 0xFF);
    memcpy(&buf[buf_idx], schema_name, schema_name_len);
    buf_idx += schema_name_len;

    buf[buf_idx++] = status.is_disabled ? 1 : 0;
    buf[buf_idx++] = status.is_composing ? 1 : 0;
    buf[buf_idx++] = status.is_ascii_mode ? 1 : 0;
    buf[buf_idx++] = status.is_full_shape ? 1 : 0;
    buf[buf_idx++] = status.is_simplified ? 1 : 0;
    buf[buf_idx++] = status.is_traditional ? 1 : 0;
    buf[buf_idx++] = status.is_ascii_punct ? 1 : 0;

    api->free_status(&status);

    jbyteArray byteArray = (*env)->NewByteArray(env, (jsize) buf_idx);
    (*env)->SetByteArrayRegion(env, byteArray, 0, (jsize) buf_idx, (jbyte *) buf);

    if (buf != tmp) {
        free(buf);
    }

    return byteArray;
}

static jbyteArray
getSchemas0(JNIEnv *env, jclass clazz, jlong session_id) {
    RimeSchemaList schema_list = {0};
    Bool res = api->get_schema_list(&schema_list);
    if (!res) {
        return NULL;
    }

    char buf[2048];

    int buf_idx = 0;
    const size_t schema_size = schema_list.size;

    buf[buf_idx++] = ((uint16_t) schema_size & 0xFF);
    buf[buf_idx++] = (((uint16_t) schema_size >> 8) & 0xFF);

    for (size_t i = 0; i < schema_size; i++) {
        RimeSchemaListItem *item = &schema_list.list[i];
        const char *schema_id = item->schema_id ? item->schema_id : "";
        const char *name = item->name ? item->name : "";
        uint16_t id_len = (uint16_t) strlen(schema_id);
        uint16_t name_len = (uint16_t) strlen(name);

        if (buf_idx + (2 + id_len) + (2 + name_len) > sizeof(buf)) {
            break;
        }

        buf[buf_idx++] = (id_len & 0xFF);
        buf[buf_idx++] = ((id_len >> 8) & 0xFF);
        memcpy(&buf[buf_idx], schema_id, id_len);
        buf_idx += id_len;

        buf[buf_idx++] = (name_len & 0xFF);
        buf[buf_idx++] = ((name_len >> 8) & 0xFF);
        memcpy(&buf[buf_idx], name, name_len);
        buf_idx += name_len;
    }

    api->free_schema_list(&schema_list);

    jbyteArray byteArray = (*env)->NewByteArray(env, (jsize) buf_idx);
    (*env)->SetByteArrayRegion(env, byteArray, 0, (jsize) buf_idx, (jbyte *) buf);

    return byteArray;
}

static jboolean
commitComposition0(JNIEnv *env, jclass clazz,
                   jlong session_id) {
    Bool result = api->commit_composition((RimeSessionId) session_id);
    return result ? JNI_TRUE : JNI_FALSE;
}

static void
clearComposition0(JNIEnv *env, jclass clazz,
                  jlong session_id) {
    api->clear_composition((RimeSessionId) session_id);
}

static jboolean
selectCandidateOnCurrentPage0(JNIEnv *env, jclass clazz,
                              jlong session_id, jint index) {
    Bool result = api->select_candidate_on_current_page((RimeSessionId) session_id, (size_t) index);
    return result ? JNI_TRUE : JNI_FALSE;
}

static jboolean
deleteCandidate0(JNIEnv *env, jclass clazz, jlong session_id,
                 jint index) {
    Bool result = api->delete_candidate((RimeSessionId) session_id, (size_t) index);
    return result ? JNI_TRUE : JNI_FALSE;
}

static jboolean
deleteCandidateOnCurrentPage0(JNIEnv *env, jclass clazz,
                              jlong session_id, jint index) {
    Bool result = api->delete_candidate_on_current_page((RimeSessionId) session_id, (size_t) index);
    return result ? JNI_TRUE : JNI_FALSE;
}

static jboolean
highlightCandidate0(JNIEnv *env, jclass clazz,
                    jlong session_id, jint index) {
    Bool result = api->highlight_candidate((RimeSessionId) session_id, (size_t) index);
    return result ? JNI_TRUE : JNI_FALSE;
}

static jboolean
highlightCandidateOnCurrentPage0(JNIEnv *env, jclass clazz,
                                 jlong session_id,
                                 jint index) {
    Bool result = api->highlight_candidate_on_current_page((RimeSessionId) session_id,
                                                           (size_t) index);
    return result ? JNI_TRUE : JNI_FALSE;
}

static jboolean
changePage0(JNIEnv *env, jclass clazz, jlong session_id,
            jboolean backward) {
    Bool result = api->change_page((RimeSessionId) session_id, backward ? True : False);
    return result ? JNI_TRUE : JNI_FALSE;
}

static jbyteArray
getInput0(JNIEnv *env, jclass clazz, jlong session_id) {
    const char *input = api->get_input((RimeSessionId) session_id);
    if (input == NULL) {
        return NULL;
    }
    size_t len = strlen(input);
    jbyteArray arr = (*env)->NewByteArray(env, (jsize) len);
    (*env)->SetByteArrayRegion(env, arr, 0, (jsize) len, (const jbyte *) input);
    return arr;
}

static jboolean
setInput0(JNIEnv *env, jclass clazz, jlong session_id,
          jbyteArray input) {
    if (input == NULL) {
        return JNI_FALSE;
    }
    jbyte *bytes = (*env)->GetByteArrayElements(env, input, NULL);
    if (bytes == NULL) {
        return JNI_FALSE;
    }

    Bool result = api->set_input((RimeSessionId) session_id, (char *) bytes);

    (*env)->ReleaseByteArrayElements(env, input, bytes, JNI_ABORT);

    return result ? JNI_TRUE : JNI_FALSE;
}

static void
setOption0(JNIEnv *env, jclass clazz, jlong session_id,
           jstring option, jboolean value) {
    if (option == NULL) {
        return;
    }
    const char *opt_chars = (*env)->GetStringUTFChars(env, option, NULL);
    if (opt_chars == NULL) {
        return;
    }
    api->set_option((RimeSessionId) session_id, opt_chars, value ? True : False);
    (*env)->ReleaseStringUTFChars(env, option, opt_chars);
}

static jboolean
getOption0(JNIEnv *env, jclass clazz, jlong session_id,
           jstring option) {
    if (option == NULL) {
        return JNI_FALSE;
    }
    const char *opt_chars = (*env)->GetStringUTFChars(env, option, NULL);
    if (opt_chars == NULL) {
        return JNI_FALSE;
    }
    Bool result = api->get_option((RimeSessionId) session_id, opt_chars);
    (*env)->ReleaseStringUTFChars(env, option, opt_chars);
    return result ? JNI_TRUE : JNI_FALSE;
}

static void
setProperty0(JNIEnv *env, jclass clazz, jlong session_id,
             jstring prop, jstring value) {
    if (prop == NULL || value == NULL) {
        return;
    }
    const char *prop_chars = (*env)->GetStringUTFChars(env, prop, NULL);
    if (prop_chars == NULL) {
        return;
    }


    const char *val_chars = (*env)->GetStringUTFChars(env, value, NULL);
    if (val_chars == NULL) {
        (*env)->ReleaseStringUTFChars(env, prop, prop_chars);
        return;
    }

    api->set_property((RimeSessionId) session_id, prop_chars, val_chars);

    (*env)->ReleaseStringUTFChars(env, prop, prop_chars);
    (*env)->ReleaseStringUTFChars(env, value, val_chars);
}

static jstring
getProperty0(JNIEnv *env, jclass clazz, jlong session_id,
             jstring prop) {
    if (prop == NULL) {
        return NULL;
    }
    const char *prop_chars = (*env)->GetStringUTFChars(env, prop, NULL);
    if (prop_chars == NULL) {
        return NULL;
    }
    char buf[256];
    Bool result = api->get_property((RimeSessionId) session_id, prop_chars, buf, sizeof(buf));
    (*env)->ReleaseStringUTFChars(env, prop, prop_chars);

    if (!result) {
        return NULL;
    }
    return (*env)->NewStringUTF(env, buf);
}

static jstring
getCurrentSchema0(JNIEnv *env, jclass clazz,
                  jlong session_id) {
    char buf[256];
    Bool result = api->get_current_schema((RimeSessionId) session_id, buf, sizeof(buf));
    if (!result) {
        return NULL;
    }
    return (*env)->NewStringUTF(env, buf);
}

static jboolean
selectSchema0(JNIEnv *env, jclass clazz, jlong session_id,
              jstring schema_id) {
    if (schema_id == NULL) {
        return JNI_FALSE;
    }
    const char *id_chars = (*env)->GetStringUTFChars(env, schema_id, NULL);
    if (id_chars == NULL) {
        return JNI_FALSE;
    }
    Bool result = api->select_schema((RimeSessionId) session_id, id_chars);
    (*env)->ReleaseStringUTFChars(env, schema_id, id_chars);
    return result ? JNI_TRUE : JNI_FALSE;
}

static jlong
schemaOpen0(JNIEnv *env, jclass clazz, jstring schema_id) {
    if (schema_id == NULL) {
        return 0L;
    }
    const char *id_chars = (*env)->GetStringUTFChars(env, schema_id, NULL);
    if (id_chars == NULL) {
        return 0L;
    }
    RimeConfig *config = (RimeConfig *) malloc(sizeof(RimeConfig));
    if (config == NULL) {
        (*env)->ReleaseStringUTFChars(env, schema_id, id_chars);
        return 0L;
    }
    memset(config, 0, sizeof(RimeConfig));
    Bool res = api->schema_open(id_chars, config);
    (*env)->ReleaseStringUTFChars(env, schema_id, id_chars);
    if (!res) {
        free(config);
        return 0L;
    }
    return ptr_to_jlong(config);
}

static void
configClose0(JNIEnv *env, jclass clazz, jlong ptr) {
    if (ptr == 0L) {
        return;
    }
    RimeConfig *config = (RimeConfig *) jlong_to_ptr(ptr);
    api->config_close(config);
    free(config);
}

static jboolean
configGetBool0(JNIEnv *env, jclass clazz, jlong ptr,
               jstring key) {
    if (ptr == 0L || key == NULL) {
        return JNI_FALSE;
    }
    const char *key_chars = (*env)->GetStringUTFChars(env, key, NULL);
    if (key_chars == NULL) {
        return JNI_FALSE;
    }
    RimeConfig *config = (RimeConfig *) jlong_to_ptr(ptr);
    Bool val = False;
    Bool res = api->config_get_bool(config, key_chars, &val);
    (*env)->ReleaseStringUTFChars(env, key, key_chars);
    if (!res) {
        return JNI_FALSE;
    }
    return val ? JNI_TRUE : JNI_FALSE;
}

static jint
configGetInt0(JNIEnv *env, jclass clazz, jlong ptr,
              jstring key) {
    if (ptr == 0L || key == NULL) {
        return 0;
    }
    const char *key_chars = (*env)->GetStringUTFChars(env, key, NULL);
    if (key_chars == NULL) {
        return 0;
    }
    RimeConfig *config = (RimeConfig *) jlong_to_ptr(ptr);
    int val = 0;
    Bool res = api->config_get_int(config, key_chars, &val);
    (*env)->ReleaseStringUTFChars(env, key, key_chars);
    if (!res) {
        return 0;
    }
    return (jint) val;
}

static jdouble
configGetDouble0(JNIEnv *env, jclass clazz, jlong ptr,
                 jstring key) {
    if (ptr == 0L || key == NULL) {
        return 0.0;
    }
    const char *key_chars = (*env)->GetStringUTFChars(env, key, NULL);
    if (key_chars == NULL) {
        return 0.0;
    }
    RimeConfig *config = (RimeConfig *) jlong_to_ptr(ptr);
    double val = 0.0;
    Bool res = api->config_get_double(config, key_chars, &val);
    (*env)->ReleaseStringUTFChars(env, key, key_chars);
    if (!res) {
        return 0.0;
    }
    return (jdouble) val;
}

static jbyteArray
configGetString0(JNIEnv *env, jclass clazz, jlong ptr,
                 jstring key) {
    if (ptr == 0L || key == NULL) {
        return NULL;
    }
    const char *key_chars = (*env)->GetStringUTFChars(env, key, NULL);
    if (key_chars == NULL) {
        return NULL;
    }
    RimeConfig *config = (RimeConfig *) jlong_to_ptr(ptr);

    const char *str = api->config_get_cstring(config, key_chars);
    (*env)->ReleaseStringUTFChars(env, key, key_chars);
    if (str == NULL) {
        return NULL;
    }

    size_t size = strlen(str);
    jbyteArray array = (*env)->NewByteArray(env, size);
    (*env)->SetByteArrayRegion(env, array, 0, size, (const jbyte *) str);

    return array;
}

static jlong
configGetItem0(JNIEnv *env, jclass clazz, jlong ptr,
               jstring key) {
    if (ptr == 0L || key == NULL) {
        return 0L;
    }
    const char *key_chars = (*env)->GetStringUTFChars(env, key, NULL);
    if (key_chars == NULL) {
        return 0L;
    }
    RimeConfig *parent = (RimeConfig *) jlong_to_ptr(ptr);
    RimeConfig *sub = (RimeConfig *) malloc(sizeof(RimeConfig));
    if (sub == NULL) {
        (*env)->ReleaseStringUTFChars(env, key, key_chars);
        return 0L;
    }
    memset(sub, 0, sizeof(RimeConfig));
    Bool res = api->config_get_item(parent, key_chars, sub);
    (*env)->ReleaseStringUTFChars(env, key, key_chars);
    if (!res || sub->ptr == NULL) {
        free(sub);
        return 0L;
    }
    return ptr_to_jlong(sub);
}

static jint
configListSize0(JNIEnv *env, jclass clazz, jlong ptr,
                jstring key) {
    if (ptr == 0L || key == NULL) {
        return 0;
    }
    const char *key_chars = (*env)->GetStringUTFChars(env, key, NULL);
    if (key_chars == NULL) {
        return 0;
    }
    RimeConfig *config = (RimeConfig *) jlong_to_ptr(ptr);
    size_t size = api->config_list_size(config, key_chars);
    (*env)->ReleaseStringUTFChars(env, key, key_chars);
    return (jint) size;
}

static jbyteArray
getConfigKeys0(JNIEnv *env, jclass clazz, jlong ptr,
               jstring key, jboolean map) {
    char tmp[4096];
    if (ptr == 0L || key == NULL) {
        return NULL;
    }
    size_t max_cap = sizeof(tmp);
    char *buf = tmp;
    int buf_idx = 2;
    uint16_t count = 0;

    const char *key_chars = (*env)->GetStringUTFChars(env, key, NULL);
    if (key_chars == NULL) {
        return NULL;
    }

    RimeConfig *config = (RimeConfig *) jlong_to_ptr(ptr);

    RimeConfigIterator iterator;

    if (map ? api->config_begin_map(&iterator, config, key_chars) :
        api->config_begin_list(&iterator, config, key_chars)) {
        while (api->config_next(&iterator)) {
            const char *name = iterator.key ? iterator.key : "";
            const uint16_t name_len = (uint16_t) strlen(name);
            const size_t need = buf_idx + 2 + name_len;
            if (need > max_cap) {
                char *old = buf;
                max_cap = MAX(buf_idx + sizeof(tmp), need);
                buf = malloc(max_cap);
                memcpy(buf, old, buf_idx);
                if (old != tmp) {
                    free(old);
                }
            }
            buf[buf_idx++] = (name_len & 0xFF);
            buf[buf_idx++] = ((name_len >> 8) & 0xFF);
            memcpy(&buf[buf_idx], name, name_len);
            buf_idx += name_len;
            count++;
        }
        api->config_end(&iterator);
    }

    (*env)->ReleaseStringUTFChars(env, key, key_chars);

    buf[0] = (count & 0xFF);
    buf[1] = ((count >> 8) & 0xFF);

    jbyteArray byteArray = (*env)->NewByteArray(env, (jsize) buf_idx);
    (*env)->SetByteArrayRegion(env, byteArray, 0, (jsize) buf_idx, (jbyte *) buf);

    if (buf != tmp) {
        free(buf);
    }
    return byteArray;
}

static JNINativeMethod methods[] = {
        {"start0",                           "(Ljava/lang/String;Lcom/github/maoabc/librime/cb/RimeNotification;)V", (void *) start0},
        {"startMaintenance0",                "(Z)Z",                                                                 (void *) startMaintenance0},
        {"isMaintenanceMode0",               "()Z",                                                                  (void *) isMaintenanceMode0},
        {"joinMaintenanceThread0",           "()V",                                                                  (void *) joinMaintenanceThread0},
        {"prebuild0",                        "()Z",                                                                  (void *) prebuild0},
        {"deploy0",                          "()Z",                                                                  (void *) deploy0},
        {"syncUserData0",                    "()Z",                                                                  (void *) syncUserData0},
        {"createSession0",                   "()J",                                                                  (void *) createSession0},
        {"destroySession0",                  "(J)V",                                                                 (void *) destroySession0},
        {"processKey0",                      "(JII)Z",                                                               (void *) processKey0},
        {"getCommitText0",                   "(J)[B",                                                                (void *) getCommitText0},
        {"getComposingText0",                "(J)[B",                                                                (void *) getComposingText0},
        {"selectCandidate0",                 "(JI)Z",                                                                (void *) selectCandidate0},
        {"getStatus0",                       "(J)[B",                                                                (void *) getStatus0},
        {"candidateListSlice0",              "(JIII)[B",                                                             (void *) candidateListSlice0},
        {"getSchemas0",                      "(J)[B",                                                                (void *) getSchemas0},
        {"commitComposition0",               "(J)Z",                                                                 (void *) commitComposition0},
        {"clearComposition0",                "(J)V",                                                                 (void *) clearComposition0},
        {"selectCandidateOnCurrentPage0",    "(JI)Z",                                                                (void *) selectCandidateOnCurrentPage0},
        {"deleteCandidate0",                 "(JI)Z",                                                                (void *) deleteCandidate0},
        {"deleteCandidateOnCurrentPage0",    "(JI)Z",                                                                (void *) deleteCandidateOnCurrentPage0},
        {"highlightCandidate0",              "(JI)Z",                                                                (void *) highlightCandidate0},
        {"highlightCandidateOnCurrentPage0", "(JI)Z",                                                                (void *) highlightCandidateOnCurrentPage0},
        {"changePage0",                      "(JZ)Z",                                                                (void *) changePage0},
        {"getInput0",                        "(J)[B",                                                                (void *) getInput0},
        {"setInput0",                        "(J[B)Z",                                                               (void *) setInput0},
        {"setOption0",                       "(JLjava/lang/String;Z)V",                                              (void *) setOption0},
        {"getOption0",                       "(JLjava/lang/String;)Z",                                               (void *) getOption0},
        {"setProperty0",                     "(JLjava/lang/String;Ljava/lang/String;)V",                             (void *) setProperty0},
        {"getProperty0",                     "(JLjava/lang/String;)Ljava/lang/String;",                              (void *) getProperty0},
        {"getCurrentSchema0",                "(J)Ljava/lang/String;",                                                (void *) getCurrentSchema0},
        {"selectSchema0",                    "(JLjava/lang/String;)Z",                                               (void *) selectSchema0},
        {"schemaOpen0",                      "(Ljava/lang/String;)J",                                                (void *) schemaOpen0},
        {"configClose0",                     "(J)V",                                                                 (void *) configClose0},
        {"configGetBool0",                   "(JLjava/lang/String;)Z",                                               (void *) configGetBool0},
        {"configGetInt0",                    "(JLjava/lang/String;)I",                                               (void *) configGetInt0},
        {"configGetDouble0",                 "(JLjava/lang/String;)D",                                               (void *) configGetDouble0},
        {"configGetString0",                 "(JLjava/lang/String;)[B",                                              (void *) configGetString0},
        {"configGetItem0",                   "(JLjava/lang/String;)J",                                               (void *) configGetItem0},
        {"configListSize0",                  "(JLjava/lang/String;)I",                                               (void *) configListSize0},
        {"getConfigKeys0",                   "(JLjava/lang/String;Z)[B",                                             (void *) getConfigKeys0},
};

int register_native_methods(JNIEnv *env) {
    jclass clazz = (*env)->FindClass(env, "com/github/maoabc/librime/RimeJni");
    if (clazz == NULL) {
        return JNI_ERR;
    }
    if ((*env)->RegisterNatives(env, clazz, methods, sizeof(methods) / sizeof(methods[0])) < 0) {
        return JNI_ERR;
    }
    return JNI_OK;
}
