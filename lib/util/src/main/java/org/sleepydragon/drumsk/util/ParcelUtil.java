package org.sleepydragon.drumsk.util;

import android.os.Parcel;
import android.support.annotation.NonNull;

public class ParcelUtil {

    private ParcelUtil() {
    }

    public static boolean readBoolean(@NonNull final Parcel parcel) {
        final int value = parcel.readInt();
        return (value != 0);
    }

    public static void writeBoolean(@NonNull final Parcel parcel, final boolean value) {
        parcel.writeInt(value ? 1 : 0);
    }

}
