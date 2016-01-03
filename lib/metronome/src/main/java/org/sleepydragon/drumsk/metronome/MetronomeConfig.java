package org.sleepydragon.drumsk.metronome;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.sleepydragon.drumsk.util.ParcelUtil;

public class MetronomeConfig implements Parcelable {

    public static final Parcelable.Creator<MetronomeConfig> CREATOR = new ParcelableCreatorImpl();

    public final int bpm;
    public final boolean audioEnabled;
    public final boolean vibrateEnabled;

    public MetronomeConfig(final int bpm, final boolean audioEnabled,
            final boolean vibrateEnabled) {
        this.bpm = bpm;
        this.audioEnabled = audioEnabled;
        this.vibrateEnabled = vibrateEnabled;
    }

    public MetronomeConfig(@NonNull final Parcel parcel) {
        this.bpm = parcel.readInt();
        this.audioEnabled = ParcelUtil.readBoolean(parcel);
        this.vibrateEnabled = ParcelUtil.readBoolean(parcel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel parcel, final int flags) {
        parcel.writeInt(bpm);
        ParcelUtil.writeBoolean(parcel, audioEnabled);
        ParcelUtil.writeBoolean(parcel, vibrateEnabled);
    }

    private static class ParcelableCreatorImpl implements Parcelable.Creator<MetronomeConfig> {

        @Override
        public MetronomeConfig createFromParcel(final Parcel parcel) {
            return new MetronomeConfig(parcel);
        }

        @Override
        public MetronomeConfig[] newArray(final int size) {
            return new MetronomeConfig[size];
        }

    }

}
