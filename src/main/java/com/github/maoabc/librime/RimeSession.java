package com.github.maoabc.librime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.maoabc.librime.cb.RimeNotification;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RimeSession implements AutoCloseable {


    private long sessionId;


    RimeSession(long sessionId) {
        this.sessionId = sessionId;
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
    public RimeStatus getStatus() {
        final byte[] bytes = RimeJni.getStatus0(sessionId);
        if (bytes == null || bytes.length < 7) {
            return null;
        }

        int bufIdx = 0;
        int schemaIdLen = (bytes[bufIdx++] & 0xFF) | ((bytes[bufIdx++] & 0xFF) << 8);
        final String schemaId = schemaIdLen > 0 ? new String(bytes, bufIdx, schemaIdLen, StandardCharsets.UTF_8) : "";
        bufIdx += schemaIdLen;

        int schemaNameLen = (bytes[bufIdx++] & 0xFF) | ((bytes[bufIdx++] & 0xFF) << 8);
        final String schemaName = schemaNameLen > 0 ? new String(bytes, bufIdx, schemaNameLen, StandardCharsets.UTF_8) : "";
        bufIdx += schemaNameLen;

        boolean isDisabled = bytes[bufIdx++] != 0;
        boolean isComposing = bytes[bufIdx++] != 0;
        boolean isAsciiMode = bytes[bufIdx++] != 0;
        boolean isFullShape = bytes[bufIdx++] != 0;
        boolean isSimplified = bytes[bufIdx++] != 0;
        boolean isTraditional = bytes[bufIdx++] != 0;
        boolean isAsciiPunct = bytes[bufIdx++] != 0;

        return new RimeStatus(schemaId, schemaName, isDisabled, isComposing, isAsciiMode, isFullShape, isSimplified, isTraditional, isAsciiPunct);
    }

    @NonNull
    public List<RimeSchema> getSchemaList() {
        final byte[] bytes = RimeJni.getSchemas0(sessionId);
        if (bytes == null || bytes.length < 2) {
            return Collections.emptyList();
        }
        int bufIdx = 0;
        int count = (bytes[bufIdx++] & 0xFF) | ((bytes[bufIdx++] & 0xFF) << 8);
        final List<RimeSchema> schemas = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int idLen = (bytes[bufIdx++] & 0xFF) | ((bytes[bufIdx++] & 0xFF) << 8);
            final String schemaId = idLen > 0 ? new String(bytes, bufIdx, idLen, StandardCharsets.UTF_8) : "";
            bufIdx += idLen;

            int nameLen = (bytes[bufIdx++] & 0xFF) | ((bytes[bufIdx++] & 0xFF) << 8);
            final String name = nameLen > 0 ? new String(bytes, bufIdx, nameLen, StandardCharsets.UTF_8) : "";
            bufIdx += nameLen;

            schemas.add(new RimeSchema(schemaId, name));
        }
        return schemas;
    }

    public boolean commitComposition() {
        return RimeJni.commitComposition0(sessionId);
    }

    public void clearComposition() {
        RimeJni.clearComposition0(sessionId);
    }

    public boolean selectCandidateOnCurrentPage(int index) {
        return RimeJni.selectCandidateOnCurrentPage0(sessionId, index);
    }

    public boolean deleteCandidate(int index) {
        return RimeJni.deleteCandidate0(sessionId, index);
    }

    public boolean deleteCandidateOnCurrentPage(int index) {
        return RimeJni.deleteCandidateOnCurrentPage0(sessionId, index);
    }

    public boolean highlightCandidate(int index) {
        return RimeJni.highlightCandidate0(sessionId, index);
    }

    public boolean highlightCandidateOnCurrentPage(int index) {
        return RimeJni.highlightCandidateOnCurrentPage0(sessionId, index);
    }

    public boolean changePage(boolean backward) {
        return RimeJni.changePage0(sessionId, backward);
    }

    @NonNull
    public String getInput() {
        final byte[] bytes = RimeJni.getInput0(sessionId);
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public boolean setInput(@NonNull String input) {
        final byte[] utf8Bytes = input.getBytes(StandardCharsets.UTF_8);
        final byte[] bytes = new byte[utf8Bytes.length + 1];
        System.arraycopy(utf8Bytes, 0, bytes, 0, utf8Bytes.length);
        return RimeJni.setInput0(sessionId, bytes);
    }

    public void setOption(@NonNull String option, boolean value) {
        RimeJni.setOption0(sessionId, option, value);
    }

    public boolean getOption(@NonNull String option) {
        return RimeJni.getOption0(sessionId, option);
    }

    public void setProperty(@NonNull String prop, @Nullable String value) {
        RimeJni.setProperty0(sessionId, prop, value);
    }

    @Nullable
    public String getProperty(@NonNull String prop) {
        return RimeJni.getProperty0(sessionId, prop);
    }

    @Nullable
    public String getCurrentSchema() {
        return RimeJni.getCurrentSchema0(sessionId);
    }

    public boolean selectSchema(@NonNull String schemaId) {
        return RimeJni.selectSchema0(sessionId, schemaId);
    }

    /**
     *
     * @param idx      候选起始位置
     * @param count    候选数量
     * @param maxBytes 候选词text最大字节数, 加个数量控制在快速输入时,
     *                 与count一起限制实际返回的候选数量
     * @return 返回实际的候选词, null表示完全结束
     */
    @Nullable
    public RimeCandidate[] getCandidates(int idx, int count, int maxBytes) {
        final byte[] bytes = RimeJni.candidateListSlice0(sessionId, idx, count, maxBytes);
        if (bytes == null) return null; //表示已经结束

        int rcount = (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8);
        final RimeCandidate[] candidates = new RimeCandidate[rcount];

        for (int i = 0, buf_idx = 2; i < rcount; i++) {
            int textLen = (bytes[buf_idx++] & 0xFF) | ((bytes[buf_idx++] & 0xFF) << 8);
            final String text = textLen > 0 ? new String(bytes, buf_idx, textLen, StandardCharsets.UTF_8) : "";
            buf_idx += textLen;

            int commentLen = (bytes[buf_idx++] & 0xFF) | ((bytes[buf_idx++] & 0xFF) << 8);
            final String comment = commentLen > 0 ? new String(bytes, buf_idx, commentLen, StandardCharsets.UTF_8) : "";
            buf_idx += commentLen;
            candidates[i] = new RimeCandidate(text, comment, idx + i);
        }
        return candidates;
    }

    public RimeCandidate[] getCandidates(int idx, int count) {
        return getCandidates(idx, count, Short.MAX_VALUE); //调高单个字符数量限制,让count限制
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
