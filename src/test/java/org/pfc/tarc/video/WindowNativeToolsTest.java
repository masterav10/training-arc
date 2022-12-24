package org.pfc.tarc.video;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.bytedeco.javacpp.CharPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.winuser.WNDENUMPROC;
import org.junit.jupiter.api.Test;

class WindowNativeToolsTest
{
    @Test
    void testWindowList()
    {
        final WindowNativeTools tools = new WindowNativeTools();
        final Set<String> windowTitles = new HashSet<>();
        final CharPointer lpString = new CharPointer(1024);

        final WNDENUMPROC callback = new WNDENUMPROC()
        {
            @Override
            public boolean call(Pointer hwnd, long lpParam)
            {
                lpString.zero();
                tools.getWindowTitle(hwnd, lpString);
                windowTitles.add(lpString.getString());

                return true;
            }
        };

        tools.getWindowTitles(callback);

        assertThat(windowTitles).isNotEmpty()
                                .anySatisfy(title -> assertThat(title).isNotBlank());

        callback.deallocate();
    }
}
