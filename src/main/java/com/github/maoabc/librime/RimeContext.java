package com.github.maoabc.librime;

import androidx.annotation.NonNull;

public final class RimeContext {
    @NonNull
    public final RimeComposition composition;
    @NonNull
    public final RimeMenu menu;

    RimeContext(@NonNull RimeComposition composition, @NonNull RimeMenu menu) {
        this.composition = composition;
        this.menu = menu;
    }

    public static RimeContext forTest(@NonNull String composingText, String... candidates) {
        final RimeComposition rimeComposition = new RimeComposition(composingText, 0, 0, 0);

        RimeCandidate[] rimeCandidates = new RimeCandidate[0];
        if (candidates != null) {
            rimeCandidates = new RimeCandidate[candidates.length];
            for (int i = 0; i < candidates.length; i++) {
                rimeCandidates[i] = RimeCandidate.forTest(candidates[i]);
            }
        }
        final RimeMenu menu = new RimeMenu(1, 0, true, 0, rimeCandidates, new char[0]);
        return new RimeContext(rimeComposition, menu);
    }

    @NonNull
    @Override
    public String toString() {
        return "RimeContext{" +
                "composition=" + composition +
                ", menu=" + menu +
                '}';
    }
}
