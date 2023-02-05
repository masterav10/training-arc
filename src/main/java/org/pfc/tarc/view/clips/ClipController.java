package org.pfc.tarc.view.clips;

import org.pfc.tarc.config.Threads;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

@Component
public class ClipController
{
    @Bean(destroyMethod = "dispose")
    public Disposable consumerThatPopulatesLabel(Threads threads, Flux<Boolean> streamOfApplicationConnected,
            Parent rootNode)
    {
        final Scheduler fxThread = threads.fxThread();

        final Label label = (Label) rootNode.lookup("#appStatusLabel");

        return Flux.from(streamOfApplicationConnected)
                   .map(isFound -> isFound.booleanValue() ? "On" : "Off")
                   .publishOn(fxThread)
                   .doOnNext(label::setText)
                   .subscribe();
    }
}
