package com.github.maoabc.librime;

import com.github.maoabc.librime.cb.RimeNotification;

public class RimeJni {

    static {
        System.loadLibrary("rime-jni");
    }

    public static native void start0(RimeNotification notification);
}
