package org.sleepydragon.drumsk.util.compat;

import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.NonNull;

public class SoundPoolCompat {

    private SoundPoolCompat() {
    }

    @NonNull
    public static SoundPool createSoundPool(final int maxStreams) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return createSoundPoolApi21(maxStreams);
        } else {
            return createSoundPoolApiLessThan21(maxStreams);
        }
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static SoundPool createSoundPoolApi21(final int maxStreams) {
        final AudioAttributes audioAttributes;
        {
            final AudioAttributes.Builder builder = new AudioAttributes.Builder();
            builder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
            builder.setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION);
            audioAttributes = builder.build();
        }

        final SoundPool soundPool;
        {
            final SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(maxStreams);
            builder.setAudioAttributes(audioAttributes);
            soundPool = builder.build();
        }

        return soundPool;
    }

    @NonNull
    private static SoundPool createSoundPoolApiLessThan21(final int maxStreams) {
        //noinspection deprecation
        return new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
    }


}
