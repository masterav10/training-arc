package org.pfc.tarc.video;

import org.bytedeco.javacpp.CharPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.winuser.WNDENUMPROC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Callback to capture the list of windows with a title. Only applied to the top
 * level applications.
 * 
 * @author Dan Avila
 *
 */
@Component
public class WindowTitles
{
    private final WNDENUMPROC callback;
    private final CharPointer lpString;
    private final WindowNativeTools tools;

    private volatile boolean found = false;

    @Autowired
    WindowTitles(String applicationName)
    {
        this(applicationName, new WindowNativeTools());
    }

    /**
     * Creates a new callback that will search for the provided application title.
     * 
     * @param applicationName the name of the application.
     * @param tools           the native context for finding windows.
     */
    public WindowTitles(String applicationName, WindowNativeTools tools)
    {
        this.lpString = new CharPointer(applicationName.length() + 1L);
        this.tools = tools;

        this.callback = new WNDENUMPROC()
        {
            @Override
            public boolean call(Pointer hwnd, long lParam)
            {
                lpString.zero();
                tools.getWindowTitle(hwnd, lpString);

                String title = lpString.getString();
                title = title.trim();

                found = applicationName.equals(title.trim());
                return !found;
            }
        };
    }

    /**
     * Runs through all windows in the system.
     * 
     * @see #isWindowWithTitleFound()
     */
    public void searchForTitle()
    {
        this.found = false;
        this.tools.getWindowTitles(callback);
    }

    /**
     * Check the result of the most recent title search.
     * 
     * @return True if during the last {@link #searchForTitle() search}, false
     *         otherwise.
     */
    public boolean isWindowWithTitleFound()
    {
        return found;
    }

    public void close()
    {
        this.callback.deallocate();
        this.lpString.deallocate();
    }
}
