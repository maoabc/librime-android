package com.github.maoabc.librime;

import androidx.annotation.NonNull;

import com.github.maoabc.librime.cb.RimeNotification;

public class RimeJni {

    static {
        System.loadLibrary("rime-jni");
    }

    private RimeJni() {
    }

    static native void start0(@NonNull String path, RimeNotification notification);

    static native long createSession0();

    static native void destroySession0(long sessionId);

    static native boolean processKey0(long sessionId, int keycode, int mask);

    static native boolean simulateKeySequence0(long sessionId, String keySequence);

    static native String getCommitText0(long sessionId);

    static native String getComposingText0(long sessionId);

    static native boolean selectCandidate0(long sessionId, int index);

    static native long candidateListFromIndex0(long sessionId, int index);


    static native boolean candidateListNext0(long iteratorPtr);

    static native void candidateListEnd0(long iteratorPtr);

    static native String candidateGetText0(long iteratorPtr);

    static native String candidateGetComment0(long iteratorPtr);

    // 批量返回一定数量的序列化之后的candidate, 头两个字节表示candidate数量,
    // 之后两个字节是candidate.text的长度然后数据, comment也是一样先2字节长度
    // 再数据
    static native byte[] candidateListSlice0(long sessionId, int idx, int count);
}
