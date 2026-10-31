package com.github.maoabc.librime;

import androidx.annotation.NonNull;

import com.github.maoabc.librime.cb.RimeNotification;

class RimeJni {

    static {
        System.loadLibrary("rime-jni");
    }

    private RimeJni() {
    }

    static native void start0(@NonNull String path, RimeNotification notification);

    static native boolean startMaintenance0(boolean fullCheck);

    static native boolean isMaintenanceMode0();

    static native void joinMaintenanceThread0();

    static native boolean prebuild0();

    static native boolean deploy0();

    static native boolean syncUserData0();


    static native long createSession0();

    static native void destroySession0(long sessionId);

    static native boolean processKey0(long sessionId, int keycode, int mask);

    static native byte[] getCommitText0(long sessionId);

    static native byte[] getComposingText0(long sessionId);

    static native boolean selectCandidate0(long sessionId, int index);

    // 序列化的RimeStatus结构体
    static native byte[] getStatus0(long sessionId);

    // 批量返回一定数量的序列化之后的candidate, 头两个字节表示candidate数量,
    // 之后两个字节是candidate.text的长度然后数据, comment也是一样先2字节长度
    // 再数据
    static native byte[] candidateListSlice0(long sessionId, int idx, int count);


    // 返回序列化之后的RimeSchemaList结构体
    static native byte[] getSchemas0(long sessionId);

    static native boolean commitComposition0(long sessionId);

    static native void clearComposition0(long sessionId);

    static native boolean selectCandidateOnCurrentPage0(long sessionId, int index);

    static native boolean deleteCandidate0(long sessionId, int index);

    static native boolean deleteCandidateOnCurrentPage0(long sessionId, int index);

    static native boolean highlightCandidate0(long sessionId, int index);

    static native boolean highlightCandidateOnCurrentPage0(long sessionId, int index);

    static native boolean changePage0(long sessionId, boolean backward);

    static native byte[] getInput0(long sessionId);

    // 保证传递进去的input最后以'\0'结尾
    static native boolean setInput0(long sessionId, byte[] input);

    static native void setOption0(long sessionId, String option, boolean value);

    static native boolean getOption0(long sessionId, String option);

    static native void setProperty0(long sessionId, String prop, String value);

    static native String getProperty0(long sessionId, String prop);

    static native String getCurrentSchema0(long sessionId);

    static native boolean selectSchema0(long sessionId, String schemaId);

    // RimeConfig
    static native long schemaOpen0(String schemaId);

    static native void configClose0(long ptr);

    static native boolean configGetBool0(long ptr, String key);

    static native int configGetInt0(long ptr, String key);

    static native double configGetDouble0(long ptr, String key);

    static native byte[] configGetString0(long ptr, String key);

    static native long configGetItem0(long ptr, String key);

    static native int configListSize0(long ptr, String key);

    static native byte[] getConfigMapKeys0(long ptr, String key);

}
