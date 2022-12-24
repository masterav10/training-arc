package org.pfc.tarc.video;

import static org.bytedeco.global.winuser.*;

import org.bytedeco.javacpp.CharPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.systems.global.windows;
import org.bytedeco.winuser.WNDENUMPROC;

/**
 * Native utility for accessing the titles of windows in the system.
 * 
 * @author Dan Avila
 */
class WindowNativeTools
{
    /**
     * <p>
     * <i>Taken directly from <a
     * href=https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-getwindowtextw
     * >MSDN</a></i>
     * </p>
     * 
     * Copies the text of the specified window's title bar (if it has one) into a
     * buffer. If the specified window is a control, the text of the control is
     * copied. However, GetWindowText cannot retrieve the text of a control in
     * another application.
     * 
     * @param  hwnd     A handle to the window or control containing the text.
     * @param  lpString The buffer that will receive the text. If the string is as
     *                  long or longer than the buffer, the string is truncated and
     *                  terminated with a null character.
     * @return          If the function succeeds, the return value is the length, in
     *                  characters, of the copied string, not including the
     *                  terminating null character. If the window has no title bar
     *                  or text, if the title bar is empty, or if the window or
     *                  control handle is invalid, the return value is zero. To get
     *                  extended error information, call
     *                  {@link windows#GetLastError() GetLastError}
     *                  <p>
     *                  This function cannot retrieve the text of an edit control in
     *                  another application.
     *                  </p>
     */
    public int getWindowTitle(Pointer hwnd, CharPointer lpString)
    {
        final int nMaxCount = (int) lpString.capacity();

        return GetWindowTextW(hwnd, lpString, nMaxCount);
    }

    /**
     * Enumerates all top-level windows on the screen by passing the handle to each
     * window, in turn, to an application-defined callback function. EnumWindows
     * continues until the last top-level window is enumerated or the callback
     * function returns FALSE.
     * 
     * @param callback A pointer to an application-defined callback function. For
     *                 more information, see EnumWindowsProc.
     */
    public void getWindowTitles(WNDENUMPROC callback)
    {
        EnumWindows(callback, 0);
    }
}
