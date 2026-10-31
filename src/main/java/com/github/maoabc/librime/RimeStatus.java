package com.github.maoabc.librime;

import androidx.annotation.NonNull;

public class RimeStatus {
    private final String schemaId;
    private final String schemaName;
    private final boolean isDisabled;
    private final boolean isComposing;
    private final boolean isAsciiMode;
    private final boolean isFullShape;
    private final boolean isSimplified;
    private final boolean isTraditional;
    private final boolean isAsciiPunct;

    RimeStatus(@NonNull String schemaId, @NonNull String schemaName,
               boolean isDisabled, boolean isComposing,
               boolean isAsciiMode, boolean isFullShape,
               boolean isSimplified, boolean isTraditional,
               boolean isAsciiPunct) {
        this.schemaId = schemaId;
        this.schemaName = schemaName;
        this.isDisabled = isDisabled;
        this.isComposing = isComposing;
        this.isAsciiMode = isAsciiMode;
        this.isFullShape = isFullShape;
        this.isSimplified = isSimplified;
        this.isTraditional = isTraditional;
        this.isAsciiPunct = isAsciiPunct;
    }

    @NonNull
    public String getSchemaId() {
        return schemaId;
    }

    @NonNull
    public String getSchemaName() {
        return schemaName;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public boolean isComposing() {
        return isComposing;
    }

    public boolean isAsciiMode() {
        return isAsciiMode;
    }

    public boolean isFullShape() {
        return isFullShape;
    }

    public boolean isSimplified() {
        return isSimplified;
    }

    public boolean isTraditional() {
        return isTraditional;
    }

    public boolean isAsciiPunct() {
        return isAsciiPunct;
    }

    @NonNull
    @Override
    public String toString() {
        return "RimeStatus{" +
                "schemaId='" + schemaId + '\'' +
                ", schemaName='" + schemaName + '\'' +
                ", isDisabled=" + isDisabled +
                ", isComposing=" + isComposing +
                ", isAsciiMode=" + isAsciiMode +
                ", isFullShape=" + isFullShape +
                ", isSimplified=" + isSimplified +
                ", isTraditional=" + isTraditional +
                ", isAsciiPunct=" + isAsciiPunct +
                '}';
    }
}
