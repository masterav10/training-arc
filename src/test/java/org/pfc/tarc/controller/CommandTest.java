package org.pfc.tarc.controller;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.pfc.tarc.controller.Command;

public class CommandTest
{
    @Test
    void test()
    {
        assertThat(new Command("236AB")).hasToString("236, AB");
    }
}
