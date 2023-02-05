package org.pfc.tarc.video;

import static java.lang.String.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Duration;

import org.pfc.tarc.config.Threads;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

@Configuration
public class VideoConfig
{
    @Bean
    public String applicationName()
    {
        return "Persona 4 Arena Ultimax";
    }

    /**
     * To play use the following command:
     * 
     * ffplay -f mpegts udp://239.0.0.1:{port}
     * 
     * @return
     * @throws IOException
     * @see                https://ffmpeg.org/ffmpeg-protocols.html#udp
     */
    @Bean
    public String videoUrl() throws IOException
    {
        try (ServerSocket socket = new ServerSocket(0))
        {
            final String address = "239.0.0.1";
            final int port = socket.getLocalPort();

            return format("udp://%s:%d", address, port);
        }
    }

    @Bean
    public Flux<Boolean> streamOfApplicationConnected(Threads threads, WindowTitles titles)
    {
        final Scheduler timerThread = threads.timerThread();
        final Scheduler eventThread = threads.eventThread();

        return Flux.interval(Duration.ofSeconds(1L), timerThread)
                   .publishOn(eventThread)
                   .doOnNext(l -> titles.searchForTitle())
                   .map(l -> titles.isWindowWithTitleFound())
                   .distinctUntilChanged()
                   .cache(1);
    }
}
