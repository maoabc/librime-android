package com.github.maoabc.librime;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;

import com.github.maoabc.librime.cb.RimeNotification;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class RimeJniTest {

    @Test
    public void testStart() throws IOException, InterruptedException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        final File cacheDir = appContext.getCacheDir();
        RimeJni.start0("/storage/emulated/0/rime", new RimeNotification() {
            @Override
            public void handler(int sessionId, @NonNull String type, @NonNull String value) {
                System.out.println("rime: java " + type + "   " + value);
            }
        });
        Thread.sleep(3000);
        final RimeSession rimeSession = RimeAPI.createSession();
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
        final RimeStatus status = rimeSession.getStatus();
        System.out.println("rime: " + status);

        System.out.println("rime:ss " + rimeSession.getSchemaList());


        Assert.assertNotNull(status);
        final RimeConfig rimeConfig = RimeAPI.schemaOpen(status.getSchemaId());
        final String configVersion = rimeConfig.getString("config_version");
        final RimeConfig switches = rimeConfig.getItem("switches");
        final String caption = switches.getString("caption");
        System.out.println("rime: " + caption);
        final boolean foldOptions = switches.getBool("fold_options");
        final String b = rimeConfig.getString("switches/@0/name");
        System.out.println("rime: " + b);

        final String[] dateTranslators = rimeConfig.getMapKeys("date_translator");
        System.out.println("rime: " + Arrays.toString(dateTranslators));

        final String[] mapKeys = rimeConfig.getMapKeys("punctuator/full_shape");


        rimeSession.close();
    }
}
