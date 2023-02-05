package org.pfc.tarc.video;

import static org.bytedeco.ffmpeg.global.avutil.*;

import java.io.IOException;
import java.util.Arrays;

import org.bytedeco.javacpp.Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VideoUtil
{
    private static final Logger LOG = LoggerFactory.getLogger(VideoUtil.class);

    public static Process ffmpeg(String command, Object... args)
    {
        final String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
        final String resolved = String.format(command, args);
        String[] inputArgs = resolved.split(" ");

        String[] commandArray = new String[inputArgs.length + 1];
        Arrays.setAll(commandArray, i -> i == 0 ? ffmpeg : inputArgs[i - 1]);

        LOG.info("ffmpeg {}", resolved);

        ProcessBuilder pb = new ProcessBuilder(commandArray);

        try
        {
            return pb.inheritIO()
                     .start();
        }
        catch (IOException e)
        {
            throw new IllegalStateException(e);
        }
    }

    public static final int check(int error)
    {
        if (error != 0)
        {
            byte[] ptr = new byte[1024];
            av_make_error_string(ptr, ptr.length, error);
            String msg = String.format("%d: %s", error, new String(ptr).trim());

            throw new IllegalStateException(msg);

        }

        return error;
    }
}
