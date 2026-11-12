package com.github.maoabc.librime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.charset.StandardCharsets;

public class RimeConfig implements AutoCloseable {
    private long ptr;

    RimeConfig(long ptr) {
        this.ptr = ptr;
    }


    public boolean getBool(@NonNull String key) {
        if (ptr == 0L) return false;
        return RimeJni.configGetBool0(ptr, key);
    }

    public int getInt(@NonNull String key) {
        if (ptr == 0L) return 0;
        return RimeJni.configGetInt0(ptr, key);
    }

    public double getDouble(@NonNull String key) {
        if (ptr == 0L) return 0;
        return RimeJni.configGetDouble0(ptr, key);
    }

    @NonNull
    public String getString(@NonNull String key) {
        if (ptr == 0L) return "";
        final byte[] bytes = RimeJni.configGetString0(ptr, key);
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Nullable
    public RimeConfig getItem(@NonNull String key) {
        if (ptr == 0L) return null;
        long subPtr = RimeJni.configGetItem0(ptr, key);
        if (subPtr == 0L) {
            return null;
        }
        return new RimeConfig(subPtr);
    }

    public int listSize(@NonNull String key) {
        if (ptr == 0L) return 0;
        return RimeJni.configListSize0(ptr, key);
    }

    @NonNull
    public String[] getMapKeys(@NonNull String key) {
        return getKeys(key, true);
    }

    @NonNull
    public String[] getListKeys(@NonNull String key) {
        return getKeys(key, true);
    }

    @NonNull
    private String[] getKeys(@NonNull String key, boolean map) {
        if (ptr == 0L) return new String[0];
        final byte[] bytes = RimeJni.getConfigKeys0(ptr, key, map);
        if (bytes == null || bytes.length < 2) {
            return new String[0];
        }
        int bufIdx = 0;
        int count = (bytes[bufIdx++] & 0xFF) | ((bytes[bufIdx++] & 0xFF) << 8);
        final String[] keys = new String[count];
        for (int i = 0; i < count; i++) {
            int len = (bytes[bufIdx++] & 0xFF) | ((bytes[bufIdx++] & 0xFF) << 8);
            final String name = new String(bytes, bufIdx, len, StandardCharsets.UTF_8);
            keys[i] = name;
            bufIdx += len;
        }
        return keys;
    }


    @Override
    public void close() {
        synchronized (this) {
            if (ptr != 0L) {
                RimeJni.configClose0(ptr);
                ptr = 0L;
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }
}
