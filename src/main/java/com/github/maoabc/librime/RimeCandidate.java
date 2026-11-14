package com.github.maoabc.librime;

import androidx.annotation.NonNull;

public final class RimeCandidate {
    @NonNull
    public final String text;
    @NonNull
    public final String comment;
    public final int index;

    RimeCandidate(@NonNull String text, @NonNull String comment, int index) {
        this.text = text;
        this.comment = comment;
        this.index = index;
    }

    // 只用于测试
    public static RimeCandidate forTest(@NonNull String text) {
        return new RimeCandidate(text, "", -1);
    }

    @NonNull
    @Override
    public String toString() {
        return "RimeCandidate{" +
                "text='" + text + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
