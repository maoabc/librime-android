package com.github.maoabc.librime;

import androidx.annotation.NonNull;

public class RimeCandidate {
    public final String text;
    public final String comment;
    public final int index;

    RimeCandidate(String text, String comment, int index) {
        this.text = text;
        this.comment = comment;
        this.index = index;
    }

    public RimeCandidate(String text) {
        this(text, "", -1);
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
