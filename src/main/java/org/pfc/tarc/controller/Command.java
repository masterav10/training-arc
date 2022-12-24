package org.pfc.tarc.controller;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class Command
{
    private final String movement;
    private final String buttons;

    public Command(String buttons)
    {
        int i = 0;

        for (; i < buttons.length(); i++)
        {
            char key = buttons.charAt(i);

            if (!('0' <= key && key <= '9'))
            {
                this.movement = buttons.substring(0, i);
                this.buttons = buttons.substring(i, buttons.length());
                return;
            }
        }

        throw new IllegalStateException("");
    }

    /**
     * Executes this command on the provide controller. Note that this method will
     * block the runner thread for a small amount of time.
     * 
     * @param controller the controller to input the command into.
     */
    public void execute(Controller controller)
    {
        for (int i = 0; i < movement.length() - 1; i++)
        {
            char value = movement.charAt(i);
            controller.execute(value);

            Timing.waitFor(Timing.ONE_FRAME_IN_NANOS);
        }

        controller.execute(movement.charAt(movement.length() - 1), this.buttons.toCharArray());
    }

    @Override
    public String toString()
    {
        return movement + ", " + buttons;
    }
}
