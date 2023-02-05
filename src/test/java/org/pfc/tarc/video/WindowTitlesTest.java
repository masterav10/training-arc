package org.pfc.tarc.video;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import org.bytedeco.javacpp.CharPointer;
import org.bytedeco.winuser.WNDENUMPROC;
import org.junit.jupiter.api.Test;

class WindowTitlesTest
{
    @Test
    void testSearchingForAppNameWithMockedNativeContext()
    {
        final String applicationName = "app";
        final WindowNativeTools tools = mock(WindowNativeTools.class);

        WindowTitles callback = new WindowTitles(applicationName, tools);

        try (CharPointer ptrRef = new CharPointer(applicationName.length()))
        {
            doAnswer(a ->
            {
                WNDENUMPROC cb = a.getArgument(0);
                cb.call(ptrRef, 0L);
                return a;
            }).when(tools)
              .getWindowTitles(any());

            Deque<String> apps = new ArrayDeque<>(Arrays.asList(applicationName, ""));
            when(tools.getWindowTitle(any(), any())).thenAnswer(a ->
            {
                CharPointer ptr = a.getArgument(1);
                String app = apps.pop();
                ptr.putString(app);
                return app.length();
            });

            assertThat(callback.isWindowWithTitleFound()).isFalse();

            callback.searchForTitle();
            assertThat(callback.isWindowWithTitleFound()).isTrue();

            callback.searchForTitle();
            assertThat(callback.isWindowWithTitleFound()).isFalse();
        }
        finally
        {
            callback.close();
        }
    }
}
