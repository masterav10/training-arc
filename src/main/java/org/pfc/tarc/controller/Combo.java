package org.pfc.tarc.controller;

import static com.google.common.base.Preconditions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Combo
{
    private List<Command> commands;
    private List<Integer> minDelays;
    private List<Integer> maxDelays;

    public Combo(String commands, String minDelays, String maxDelays)
    {
        this.commands = Arrays.stream(commands.split(">"))
                              .map(String::trim)
                              .map(Command::new)
                              .collect(Collectors.toList());

        this.minDelays = Arrays.stream(minDelays.split("\\d{1,3}"))
                               .map(Integer::parseInt)
                               .collect(Collectors.toList());

        this.maxDelays = Arrays.stream(maxDelays.split("\\d{1,3}"))
                               .map(Integer::parseInt)
                               .collect(Collectors.toList());

        final int size = this.commands.size();
        checkArgument(size > 0);
        checkArgument(size == this.minDelays.size() - 1);
        checkArgument(size == this.maxDelays.size() - 1);
    }

    public Combo(String combo, String delays)
    {
        this(combo, delays, delays);
    }

    public Command getCommand(int index)
    {
        return this.commands.get(index);
    }

    public int calculateDelayRandom(int index, Random random)
    {
        final int min = this.minDelays.get(index);
        final int max = this.maxDelays.get(index);

        final int range = max - min;
        return random.nextInt(range) + min;
    }

    public int minDelay(int index)
    {
        return this.minDelays.get(index);
    }

    public int maxDelay(int index)
    {
        return this.maxDelays.get(index);
    }
}
