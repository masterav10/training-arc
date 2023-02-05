package org.pfc.tarc.video;

import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avdevice.*;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;
import static org.pfc.tarc.video.VideoUtil.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avformat.AVInputFormat;
import org.bytedeco.ffmpeg.avutil.AVDictionary;
import org.bytedeco.javacpp.BytePointer;
import org.pfc.tarc.config.Threads;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javafx.scene.image.WritableImage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Scheduler;

@Component
public class VideoStream
{
    private final String appName;

    VideoStream(String applicationName)
    {
        this.appName = applicationName;
    }

    @Bean
    public Flux<AVPacket> streamOfVideoFrames(Threads threads)
    {
        Scheduler scheduler = threads.videoThread();

        return Flux.<AVPacket>create(emitter -> poll(scheduler, emitter))
                   .subscribeOn(scheduler)
                   .cache(1);
    }

    void poll(Scheduler scheduler, FluxSink<AVPacket> emitter)
    {
        AVInputFormat format = new AVInputFormat(null);
        while ((format = av_input_video_device_next(format)) != null)
        {
            BytePointer namePtr = format.name();

            if ("gdigrab".equals(namePtr.getString()))
            {
                break;
            }
        }

        AVDictionary options = new AVDictionary();
        av_dict_set(options, "draw_mouse", "0", 0);

        long timeout = TimeUnit.SECONDS.toNanos(2L);
        long sleepTime = 0;

        while (!emitter.isCancelled())
        {
            try
            {
                LockSupport.parkNanos(sleepTime);
                sleepTime = timeout;

                AVFormatContext ps = new AVFormatContext(null);
                check(avformat_open_input(ps, "title=" + appName, format, options));

                while (!emitter.isCancelled())
                {
                    try (AVPacket packet = new AVPacket())
                    {
                        System.out.println(System.nanoTime());
                        check(av_read_frame(ps, packet));

                        av_packet_unref(packet);

                        emitter.next(packet);
                    }
                }
            }
            catch (IllegalStateException e)
            {
                // timeout
            }
        }

        emitter.complete();
    }
}
