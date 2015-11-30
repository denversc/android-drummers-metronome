package org.sleepydragon.drumsk.ui.api;

import android.support.annotation.NonNull;

import java.util.concurrent.atomic.AtomicReference;

import static org.sleepydragon.drumsk.util.Assert.assertNotNull;

public class Globals {

    private static final AtomicReference<Globals> sGlobals = new AtomicReference<>();

    @NonNull
    private final UiFactory mUiFactory;

    private Globals(@NonNull final UiFactory uiFactory) {
        mUiFactory = assertNotNull(uiFactory);
    }

    @NonNull
    public static UiFactory getUiFactory() {
        final Globals globals = sGlobals.get();
        if (globals == null) {
            throw new IllegalStateException("initialize() not yet invoked");
        }
        return globals.mUiFactory;
    }

    public static void initialize(@NonNull final UiFactory uiFactory) {
        assertNotNull(uiFactory);
        final Globals globals = new Globals(uiFactory);
        if (!sGlobals.compareAndSet(null, globals)) {
            throw new IllegalStateException("initialize() already invoked");
        }
    }

}
