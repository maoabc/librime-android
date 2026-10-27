package com.github.maoabc.librime;

public class RimeCandidate {
    public final String text;
    public final String comment;
    public final int index;

    RimeCandidate(String text, String comment, int index) {
        this.text = text;
        this.comment = comment;
        this.index = index;
    }

    @Override
    public String toString() {
        return "RimeCandidate{" +
                "text='" + text + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
