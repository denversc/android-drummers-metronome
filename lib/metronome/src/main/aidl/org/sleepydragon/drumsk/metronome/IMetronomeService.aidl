package org.sleepydragon.drumsk.metronome;

import org.sleepydragon.drumsk.metronome.MetronomeConfig;

interface IMetronomeService {
    void start(in MetronomeConfig config);
    void stop();
    boolean isStarted();
    MetronomeConfig getConfig();
    boolean setConfig(in MetronomeConfig config);
}
