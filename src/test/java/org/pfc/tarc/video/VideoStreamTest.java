package org.pfc.tarc.video;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.pfc.tarc.config.Threads;

import reactor.core.Disposable;
import reactor.core.scheduler.Schedulers;

public class VideoStreamTest
{
    @Test
    public void testApplication()
    {
        VideoStream videoStream = new VideoStream("Persona 4 Arena Ultimax");

        Threads threads = mock(Threads.class);
        when(threads.videoThread()).thenReturn(Schedulers.immediate());

        Disposable d = videoStream.streamOfVideoFrames(threads)
                                  .subscribe();

        d.dispose();
    }
}
