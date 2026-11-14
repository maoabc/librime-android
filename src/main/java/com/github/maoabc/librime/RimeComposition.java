package com.github.maoabc.librime;

import androidx.annotation.NonNull;

public final class RimeComposition {
    @NonNull
    public final String preedit;
    public final int cursor_pos;
    public final int sel_start;
    public final int sel_end;

    RimeComposition(@NonNull String preedit,
                    int cursor_pos, int sel_start, int sel_end) {
        this.preedit = preedit;
        this.cursor_pos = cursor_pos;
        this.sel_start = sel_start;
        this.sel_end = sel_end;
    }

    @NonNull
    @Override
    public String toString() {
        return "RimeComposition{" +
                "preedit='" + preedit + '\'' +
                ", cursor_pos=" + cursor_pos +
                ", sel_start=" + sel_start +
                ", sel_end=" + sel_end +
                '}';
    }
}
