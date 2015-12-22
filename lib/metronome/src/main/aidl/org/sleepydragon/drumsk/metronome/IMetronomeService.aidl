package org.sleepydragon.drumsk.metronome;

interface IMetronomeService {
    void start(int bpm);
    void stop();
    boolean isStarted();
    int getBpm();
}
