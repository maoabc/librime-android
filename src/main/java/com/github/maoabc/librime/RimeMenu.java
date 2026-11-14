package com.github.maoabc.librime;

import androidx.annotation.NonNull;

import java.util.Arrays;

public final class RimeMenu {
    public final int page_size;
    public final int page_no;
    public final boolean is_last_page;
    public final int highlighted_candidate_index;
    @NonNull
    public final RimeCandidate[] candidates;
    @NonNull
    public final char[] select_keys;

    RimeMenu(int page_size, int page_no, boolean is_last_page,
             int highlighted_candidate_index,
             @NonNull RimeCandidate[] candidates,
             @NonNull char[] select_keys) {
        this.page_size = page_size;
        this.page_no = page_no;
        this.is_last_page = is_last_page;
        this.highlighted_candidate_index = highlighted_candidate_index;
        this.candidates = candidates;
        this.select_keys = select_keys;
    }

    @NonNull
    @Override
    public String toString() {
        return "RimeMenu{" +
                "page_size=" + page_size +
                ", page_no=" + page_no +
                ", is_last_page=" + is_last_page +
                ", highlighted_candidate_index=" + highlighted_candidate_index +
                ", candidates=" + Arrays.toString(candidates) +
                ", select_keys=" + Arrays.toString(select_keys) +
                '}';
    }
}
