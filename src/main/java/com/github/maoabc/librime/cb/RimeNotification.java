package com.github.maoabc.librime.cb;

import androidx.annotation.NonNull;

public interface RimeNotification {

    void handler(int sessionId, int type, @NonNull String value);
}
