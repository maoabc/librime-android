package com.github.maoabc.librime;

import androidx.annotation.NonNull;

public class RimeSchema {
    private final String schema_id;


    private final String name;

    RimeSchema(String schema_id, String name) {
        this.schema_id = schema_id;
        this.name = name;
    }

    public String getSchema_id() {
        return schema_id;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return "RimeSchema{" +
                "schema_id='" + schema_id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
