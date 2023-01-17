package org.pfc.tarc.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Component
public class Threads
{
    private final Map<String, Scheduler> schedulers;

    Threads()
    {
        this.schedulers = new HashMap<>();
    }

    public final Scheduler fxThread()
    {
        return schedulers.computeIfAbsent("fxThread", key -> Schedulers.fromExecutor(Platform::runLater));
    }

    public final Scheduler eventThread()
    {
        return schedulers.computeIfAbsent("eventThread", key -> Schedulers.newSingle("eventThread"));
    }

    public final Scheduler timerThread()
    {
        return schedulers.computeIfAbsent("timerThread", key -> Schedulers.newSingle("timerThread"));
    }
}
