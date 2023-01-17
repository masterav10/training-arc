package org.pfc.tarc.video;

import java.io.IOException;
import java.util.Arrays;

import org.bytedeco.javacpp.Loader;

public class VideoUtil
{
    public static Process ffmpeg(String command, Object... args)
    {
        final String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
        final String resolved = String.format(command, args);
        String[] inputArgs = resolved.split(" ");

        String[] commandArray = new String[inputArgs.length + 1];
        Arrays.setAll(commandArray, i -> i == 0 ? ffmpeg : inputArgs[i - 1]);

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
}
