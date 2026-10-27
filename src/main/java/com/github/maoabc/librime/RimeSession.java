package com.github.maoabc.librime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.charset.StandardCharsets;

public class RimeSession implements AutoCloseable {


    private long sessionId;


    public RimeSession() {
        this.sessionId = RimeJni.createSession0();
        if (this.sessionId == 0L) {
            throw new RuntimeException("Failed to create Rime session");
        }
    }

    public long getSessionId() {
        return sessionId;
    }

    public boolean processKey(int keycode, int mask) {
        return RimeJni.processKey0(sessionId, keycode, mask);
    }


    @NonNull
    public String getCommitText() {
        final byte[] bytes = RimeJni.getCommitText0(sessionId);
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @NonNull
    public String getComposingText() {
        final byte[] bytes = RimeJni.getComposingText0(sessionId);
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public boolean selectCandidate(int index) {
        return RimeJni.selectCandidate0(sessionId, index);
    }

    @Nullable
    public RimeCandidate[] getCandidates(int idx, int count) {
        final byte[] bytes = RimeJni.candidateListSlice0(sessionId, idx, count);
        if (bytes == null) return null; //表示已经结束
        int rcount = (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8);
        final RimeCandidate[] candidates = new RimeCandidate[rcount];

        for (int i = 0, buf_idx = 2; i < rcount; i++) {
            int textLen = (bytes[buf_idx++] & 0xFF) | ((bytes[buf_idx++] & 0xFF) << 8);
            final String text = new String(bytes, buf_idx, textLen, StandardCharsets.UTF_8);
            buf_idx += textLen;
            int commentLen = (bytes[buf_idx++] & 0xFF) | ((bytes[buf_idx++] & 0xFF) << 8);
            final String comment = commentLen > 0 ? new String(bytes, buf_idx, commentLen, StandardCharsets.UTF_8) : "";
            candidates[i] = new RimeCandidate(text, comment, idx + i);
        }
        return candidates;
    }

    @Override
    public void close() {
        synchronized (this) {
            if (sessionId != 0L) {
                RimeJni.destroySession0(sessionId);
                sessionId = 0;
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
