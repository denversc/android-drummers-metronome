package org.sleepydragon.drumsk.metronome;

interface IMetronomeService {
    void start(int bpm, boolean audioEnabled, boolean vibrateEnabled);
    void stop();
    boolean isStarted();
    int getBpm();
    int isAudioEnabled();
    int isVibrateEnabled();
}
