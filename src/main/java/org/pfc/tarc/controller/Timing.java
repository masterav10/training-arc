package org.pfc.tarc.controller;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public final class Timing
{
    /**
     * The amount of nano seconds in one frame.
     */
    public static final long ONE_FRAME_IN_NANOS = TimeUnit.SECONDS.toNanos(1) / 60;

    public static void waitFor(Duration duration)
    {
        waitFor(duration.toNanos());
    }

    public static void waitFor(long nanos)
    {
        long sleep = nanos / 2;
        long now = System.nanoTime();

        do
        {
            LockSupport.parkNanos(sleep);
            sleep = sleep / 2;

        } while (System.nanoTime() - now < nanos);
    }
}
