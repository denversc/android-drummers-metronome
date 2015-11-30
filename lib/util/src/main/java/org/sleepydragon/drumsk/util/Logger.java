package org.sleepydragon.drumsk.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Formatter;
import java.util.Locale;

import static org.sleepydragon.drumsk.util.Assert.assertNotNull;

public class Logger {

    private static String sTag;

    private static final ThreadLocalFormatter sThreadLocalFormatter = new ThreadLocalFormatter();

    @NonNull
    private final String mLogPrefix;

    public Logger(@NonNull final String subTag) {
        mLogPrefix = assertNotNull(subTag) + ": ";
    }

    public Logger(@NonNull final Object subTag) {
        this(getSubTag(subTag));
    }

    public static void initialize(@NonNull final String tag) {
        sTag = tag;
    }

    @NonNull
    private static String getSubTag(@NonNull final Object object) {
        return getSubTag(object.getClass());
    }

    @NonNull
    private static String getSubTag(@NonNull final Class<?> cls) {
        return cls.getSimpleName();
    }

    public void v(@NonNull final String message, @Nullable Object... args) {
        if (BuildConfig.DEBUG) {
            Log.v(sTag, formatMessage(message, args));
        }
    }

    public void d(@NonNull final String message, @Nullable Object... args) {
        if (BuildConfig.DEBUG) {
            Log.d(sTag, formatMessage(message, args));
        }
    }

    public void i(@NonNull final String message, @Nullable Object... args) {
        Log.i(sTag, formatMessage(message, args));
    }

    public void w(@NonNull final String message, @Nullable Object... args) {
        Log.w(sTag, formatMessage(message, args));
    }

    public void w(@NonNull final Throwable exception, @NonNull final String message,
            @Nullable Object... args) {
        Log.w(sTag, formatMessage(message, args), exception);
    }

    public void e(@NonNull final String message, @Nullable Object... args) {
        Log.e(sTag, formatMessage(message, args));
    }

    public void e(@NonNull final Throwable exception, @NonNull final String message,
            @Nullable Object... args) {
        Log.e(sTag, formatMessage(message, args), exception);
    }

    private String formatMessage(@NonNull final String message, @Nullable Object... args) {
        final FormatterInfo formatter = sThreadLocalFormatter.get();
        formatter.sb.append(mLogPrefix);

        final FormatterInfo formatterInfo = sThreadLocalFormatter.get();
        if (args == null || args.length == 0) {
            formatter.sb.append(message);
        } else if (!BuildConfig.DEBUG) {
            formatterInfo.formatter.format(message, args);
        } else {
            try {
                formatterInfo.formatter.format(message, args);
            } catch (RuntimeException e) {
                formatter.sb.setLength(0);
                formatter.sb.append(message);
            }
        }

        final String formattedMessage = formatter.formatter.toString();
        if (formattedMessage.length() > 1000) {
            sThreadLocalFormatter.remove();
        } else {
            formatterInfo.sb.setLength(0);
        }

        return formattedMessage;
    }

    private static class ThreadLocalFormatter extends ThreadLocal<FormatterInfo> {

        @Override
        protected FormatterInfo initialValue() {
            return new FormatterInfo();
        }

    }

    private static class FormatterInfo {

        @NonNull
        public final StringBuilder sb;
        @NonNull
        public final Formatter formatter;

        public FormatterInfo() {
            sb = new StringBuilder();
            formatter = new Formatter(sb, Locale.US);
        }

    }

}
