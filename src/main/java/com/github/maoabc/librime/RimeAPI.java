package com.github.maoabc.librime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.maoabc.librime.cb.RimeNotification;

public class RimeAPI {

    public static void rimeInit(@NonNull String path) {
        RimeJni.start0(path, new RimeNotification() {
            @Override
            public void handler(int sessionId, @NonNull String type, @NonNull String value) {
                System.out.println("rime: " + type + "  " + value);

            }
        });
    }

    public static RimeSession createSession() {
        long sessionId = RimeJni.createSession0();
        if (sessionId == 0L) {
            throw new RuntimeException("Failed to create Rime session");
        }
        final RimeSession rimeSession = new RimeSession(sessionId);
        rimeSession.setOption("ascii_mode", false);
        rimeSession.setOption("ascii_punct", false);
        rimeSession.setOption("emoji", true);
        System.out.println("rime: "+rimeSession.getStatus());
        return rimeSession;
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
