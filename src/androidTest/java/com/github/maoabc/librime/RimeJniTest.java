package com.github.maoabc.librime;

import androidx.annotation.NonNull;

import com.github.maoabc.librime.cb.RimeNotification;

import org.junit.Test;

public class RimeJniTest {

    @Test
    public void testStart(){
        RimeJni.start0(new RimeNotification(){
            @Override
            public void handler(int sessionId, int type, @NonNull String value) {

            }
        });
    }
}
