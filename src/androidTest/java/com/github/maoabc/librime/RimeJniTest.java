package com.github.maoabc.librime;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;

import com.github.maoabc.librime.cb.RimeNotification;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class RimeJniTest {

    @Test
    public void testStart() throws IOException, InterruptedException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        final File cacheDir = appContext.getCacheDir();
        RimeJni.start0("/storage/emulated/0/rime", new RimeNotification() {
            @Override
            public void handler(int sessionId, @NonNull String type, @NonNull String value) {
            }
        });
        Thread.sleep(3000);
        final RimeSession rimeSession = new RimeSession();
        rimeSession.processKey('w', 0);
        rimeSession.processKey('o', 0);
        final String commitText = rimeSession.getCommitText();
        final String composingText = rimeSession.getComposingText();
        int i = 0;
        while (true) {
            final RimeCandidate[] candidates = rimeSession.getCandidates(i, 10);
            if (candidates == null) break;
            for (RimeCandidate candidate : candidates) {
                System.out.println("rime:" + candidate);
            }
            i += candidates.length;
        }

        System.out.println(commitText);


        rimeSession.close();
    }
}
