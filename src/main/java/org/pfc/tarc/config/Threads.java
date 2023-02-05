package org.pfc.tarc.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
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

    public Scheduler fxThread()
    {
        return schedulers.computeIfAbsent("fxThread", key -> Schedulers.fromExecutor(Platform::runLater));
    }

    public Scheduler eventThread()
    {
        return schedulers.computeIfAbsent("eventThread", key -> Schedulers.newSingle("eventThread"));
    }

    public Scheduler videoThread()
    {
        return schedulers.computeIfAbsent("videoThread", key -> Schedulers.newSingle("videoThread"));
    }

    public Scheduler timerThread()
    {
        return schedulers.computeIfAbsent("timerThread", key -> Schedulers.newSingle("timerThread"));
    }

    @PreDestroy
    void closeAll()
    {
        for (Scheduler scheduler : this.schedulers.values())
        {
            scheduler.dispose();
        }
    }
}
