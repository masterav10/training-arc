package org.pfc.tarc;

import static org.bytedeco.vigem.preset.vigemspec.*;
import static org.bytedeco.vigemclient.global.vigemclient.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.vigemclient.DS4_REPORT_EX;
import org.bytedeco.vigemclient.VIGEM_CLIENT;
import org.bytedeco.vigemclient.VIGEM_TARGET;
import org.bytedeco.xinput.XINPUT_GAMEPAD;
import org.bytedeco.xinput.XINPUT_STATE;
import org.bytedeco.xinput.global.xinput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Converts any XINPUT device into a PS4 device. Useful when combined with
 * Playstation Remote play.
 * 
 * @author Dan Avila
 *
 */
public class PS4ControllerEmulator extends Application
{
    private static final Map<Integer, Integer> DPAD = new LinkedHashMap<>();
    private static final Map<Integer, Integer> BUTTONS = new LinkedHashMap<>();

    static
    {
        DPAD.put(XUSB_GAMEPAD_DPAD_UP | XUSB_GAMEPAD_DPAD_LEFT, DS4_BUTTON_DPAD_NORTHWEST);
        DPAD.put(XUSB_GAMEPAD_DPAD_UP | XUSB_GAMEPAD_DPAD_RIGHT, DS4_BUTTON_DPAD_NORTHEAST);
        DPAD.put(XUSB_GAMEPAD_DPAD_DOWN | XUSB_GAMEPAD_DPAD_LEFT, DS4_BUTTON_DPAD_SOUTHWEST);
        DPAD.put(XUSB_GAMEPAD_DPAD_DOWN | XUSB_GAMEPAD_DPAD_RIGHT, DS4_BUTTON_DPAD_SOUTHEAST);
        DPAD.put(XUSB_GAMEPAD_DPAD_UP, DS4_BUTTON_DPAD_NORTH);
        DPAD.put(XUSB_GAMEPAD_DPAD_DOWN, DS4_BUTTON_DPAD_SOUTH);
        DPAD.put(XUSB_GAMEPAD_DPAD_RIGHT, DS4_BUTTON_DPAD_EAST);
        DPAD.put(XUSB_GAMEPAD_DPAD_LEFT, DS4_BUTTON_DPAD_WEST);

        BUTTONS.put(XUSB_GAMEPAD_A, DS4_BUTTON_CROSS);
        BUTTONS.put(XUSB_GAMEPAD_B, DS4_BUTTON_CIRCLE);
        BUTTONS.put(XUSB_GAMEPAD_X, DS4_BUTTON_SQUARE);
        BUTTONS.put(XUSB_GAMEPAD_Y, DS4_BUTTON_TRIANGLE);

        BUTTONS.put(XUSB_GAMEPAD_START, DS4_BUTTON_OPTIONS);

        BUTTONS.put(XUSB_GAMEPAD_LEFT_SHOULDER, DS4_BUTTON_SHOULDER_LEFT);
        BUTTONS.put(XUSB_GAMEPAD_RIGHT_SHOULDER, DS4_BUTTON_SHOULDER_RIGHT);

        BUTTONS.put(XUSB_GAMEPAD_LEFT_THUMB, DS4_BUTTON_THUMB_LEFT);
        BUTTONS.put(XUSB_GAMEPAD_RIGHT_THUMB, DS4_BUTTON_THUMB_RIGHT);
    }

    public static void main(String... args)
    {
        System.setProperty("javafx.animation.fullspeed", "false");

        launch(PS4ControllerEmulator.class);
    }

    private static final Logger LOG = LoggerFactory.getLogger(PS4ControllerEmulator.class);

    private static final void check(int error)
    {
        if (error != VIGEM_ERROR_NONE)
        {
            LOG.error("ViGEm Bus connection failed with error code: 0x{}", Integer.toHexString(error));
        }
    }

    private PointerPointer<VIGEM_CLIENT> client;
    private PointerPointer<VIGEM_TARGET> pad;
    private AnimationTimer timer;

    @Override
    public void init() throws Exception
    {
        this.client = vigem_alloc();
        check(vigem_connect(client));

        this.pad = vigem_target_ds4_alloc();
        check(vigem_target_add(client, pad));

        this.timer = new AnimationTimer()
        {
            final int dwInputState = 0;
            final XINPUT_STATE inRef = new XINPUT_STATE();
            final DS4_REPORT_EX out = new DS4_REPORT_EX();

            @Override
            public void handle(long now)
            {
                xinput.XInputGetState(dwInputState, inRef);
                XINPUT_GAMEPAD in = inRef.Gamepad();

                out.Report_bTriggerL(in.bLeftTrigger());
                out.Report_bTriggerR(in.bRightTrigger());

                out.Report_bThumbLX((byte) (in.sThumbLX() / 256 + Byte.MAX_VALUE + 1));
                out.Report_bThumbLY((byte) (255 - (in.sThumbLY() / 256 + Byte.MAX_VALUE + 1)));

                out.Report_bThumbRX((byte) (in.sThumbRX() / 256 + Byte.MAX_VALUE + 1));
                out.Report_bThumbRY((byte) (255 - (in.sThumbRY() / 256 + Byte.MAX_VALUE + 1)));

                int padOut = pad(in);
                int buttonsOut = buttons(in);
                int triggers = triggers(in);
                out.Report_wButtons((short) (padOut | buttonsOut | triggers));

                byte specials = special(in);
                out.Report_bSpecial(specials);

                check(vigem_target_ds4_update_ex(client, pad, out));

                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1L));
            }

            private byte special(XINPUT_GAMEPAD in)
            {
                int wButtonsIn = in.wButtons();

                if ((XUSB_GAMEPAD_BACK & wButtonsIn) == XUSB_GAMEPAD_BACK)
                {
                    return DS4_SPECIAL_BUTTON_TOUCHPAD;
                }

                return 0;
            }

            private int triggers(XINPUT_GAMEPAD in)
            {
                int triggerOut = 0;

                if (Byte.toUnsignedInt(in.bLeftTrigger()) > 200)
                {
                    triggerOut |= DS4_BUTTON_TRIGGER_LEFT;
                }

                if (Byte.toUnsignedInt(in.bRightTrigger()) > 200)
                {
                    triggerOut |= DS4_BUTTON_TRIGGER_RIGHT;
                }

                return triggerOut;
            }

            private int pad(XINPUT_GAMEPAD in)
            {
                int wButtonsIn = in.wButtons();

                for (Entry<Integer, Integer> entry : DPAD.entrySet())
                {
                    int xbox = entry.getKey();

                    if ((xbox & wButtonsIn) == xbox)
                    {
                        return entry.getValue();
                    }
                }

                return DS4_BUTTON_DPAD_NONE;
            }

            private int buttons(XINPUT_GAMEPAD in)
            {
                int wButtonsIn = in.wButtons();
                int wButtonsOut = 0;

                for (Entry<Integer, Integer> entry : BUTTONS.entrySet())
                {
                    int xbox = entry.getKey();

                    if ((xbox & wButtonsIn) == xbox)
                    {
                        wButtonsOut |= entry.getValue();
                    }
                }

                return wButtonsOut;
            }
        };
    }

    @Override
    public void stop() throws Exception
    {
        this.timer.stop();

        vigem_target_remove(client, pad);
        vigem_target_free(pad);
        vigem_free(client);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setTitle("PS4 Controller Emulator");
        primaryStage.setScene(new Scene(new Pane(), 300, 100));
        primaryStage.show();
        this.timer.start();
    }
}
