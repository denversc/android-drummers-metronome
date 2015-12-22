package org.sleepydragon.drumsk.main;

interface IMetronomeService {
    void start(int bpm);
    void stop();
    boolean isStarted();
    int getBpm();
}
