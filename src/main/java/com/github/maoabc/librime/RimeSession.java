package com.github.maoabc.librime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

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

    public boolean simulateKeySequence(String keySequence) {
        return RimeJni.simulateKeySequence0(sessionId, keySequence);
    }

    public String getCommitText() {
        return RimeJni.getCommitText0(sessionId);
    }

    public String getComposingText() {
        return RimeJni.getComposingText0(sessionId);
    }

    public boolean selectCandidate(int index) {
        return RimeJni.selectCandidate0(sessionId, index);
    }

    @NonNull
    public Iterable<RimeCandidate> getCandidates(int idx) {
        final long ptr = RimeJni.candidateListFromIndex0(sessionId, idx);
        if (ptr == 0) {
            return Collections.emptyList();
        }
        return new Iterable<>() {
            @NonNull
            @Override
            public Iterator<RimeCandidate> iterator() {
                return new CandidateIter(RimeSession.this, ptr, idx);
            }
        };
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

    private static class CandidateIter implements Iterator<RimeCandidate> {
        //持有session对象,保证session不会提前被回收
        private final RimeSession session;
        private long iterPtr;

        private int index;

        CandidateIter(RimeSession session, long iterPtr, int index) {
            this.session = session;
            this.iterPtr = iterPtr;
            this.index = index;
        }

        @Override
        public boolean hasNext() {
            if (session.sessionId == 0 || iterPtr == 0) {
                return false;
            }
            final boolean next = RimeJni.candidateListNext0(iterPtr);
            if (!next) {
                RimeJni.candidateListEnd0(iterPtr);
                iterPtr = 0;
            }
            return next;
        }

        @Override
        public RimeCandidate next() {
            if (iterPtr == 0) return null;
            final String text = RimeJni.candidateGetText0(iterPtr);
            final String comment = RimeJni.candidateGetComment0(iterPtr);
            return new RimeCandidate(text, comment, index++);
        }

        //最后可能遗漏,进行收尾
        @Override
        protected void finalize() throws Throwable {
            synchronized (this) {
                if (iterPtr != 0) {
                    RimeJni.candidateListEnd0(iterPtr);
                    iterPtr = 0;
                }
            }
        }
    }
}
