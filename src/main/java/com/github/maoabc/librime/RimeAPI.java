package com.github.maoabc.librime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.maoabc.librime.cb.RimeNotification;

public class RimeAPI {

    public static void rimeInit(@NonNull String path, boolean fullCheck,
                                @Nullable RimeNotification notification) {
        RimeJni.start0(path, notification);
        if (RimeAPI.startMaintenance(fullCheck)) { // wait deploy
            RimeAPI.joinMaintenanceThread();
        }
    }

    public static RimeSession createSession() {
        long sessionId = RimeJni.createSession0();
        if (sessionId == 0L) {
            throw new RuntimeException("Failed to create Rime session");
        }
        return new RimeSession(sessionId);
    }

    @Nullable
    public static RimeConfig schemaOpen(@NonNull String schemaId) {
        long ptr = RimeJni.schemaOpen0(schemaId);
        if (ptr == 0L) {
            return null;
        }
        return new RimeConfig(ptr);
    }

    public static boolean startMaintenance(boolean fullCheck) {
        return RimeJni.startMaintenance0(fullCheck);
    }

    public static boolean isMaintenanceMode() {
        return RimeJni.isMaintenanceMode0();
    }

    public static void joinMaintenanceThread() {
        RimeJni.joinMaintenanceThread0();
    }

    public static boolean prebuild() {
        return RimeJni.prebuild0();
    }

    public static boolean deploy() {
        return RimeJni.deploy0();
    }

    public static boolean syncUserData() {
        return RimeJni.syncUserData0();
    }
}
