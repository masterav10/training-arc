package org.pfc.tarc;

import static org.assertj.core.api.Assertions.*;
import static org.pfc.tarc.video.Video.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public class VideoTest
{
    @Test
    public void testListingSoundDevices() throws InterruptedException, IOException
    {
        // assertThat(ffmpeg("-list_devices true -f dshow -i dummy")).isEqualTo(0);

        final Path output = Paths.get("C:", "Users", "Dan Avila", "Videos", "test.mkv");

        if (Files.exists(output))
        {
            Files.delete(output);
        }
        
        Process process = ffmpeg("-f gdigrab -draw_mouse 0 -i title=\"Command Prompt\" -c:v ffv1 -pix_fmt yuv420p \"%s\"", output);
        process.destroy();

        assertThat(process.waitFor()).isEqualTo(0);
    }
}
