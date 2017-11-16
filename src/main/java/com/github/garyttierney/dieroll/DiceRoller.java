package com.github.garyttierney.dieroll;

import java.util.Arrays;
import java.util.Random;
import java.util.StringJoiner;
import java.util.function.Function;

final class DiceRoller {

    private final Function<Integer, Integer> rng;

    DiceRoller(Random random) {
        this((faces) -> random.nextInt(faces - 1) + 1);
    }

    DiceRoller(Function<Integer, Integer> rng) {
        this.rng = rng;
    }

    String roll(int faces, int quantity, int modifier) {
        if (faces <= 1) {
            throw new IllegalArgumentException("Expected >= 2 faces on the dice, " + faces + " given");
        }

        if (quantity < 1) {
            throw new IllegalArgumentException("Expected at least 1 dice roll, " + quantity + " given");
        }

        final StringJoiner resultsJoiner = new StringJoiner(" + ");
        final int[] rolls = new int[quantity];

        for (int idx = 0; idx < rolls.length; idx++) {
            rolls[idx] = rng.apply(faces);
            resultsJoiner.add(Integer.toString(rolls[idx]));
        }

        final String results = resultsJoiner.toString();
        final int total = Arrays.stream(rolls).sum();
        final char op = modifier < 0 ? '-' : '+';

        final StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(total + modifier);

        if (modifier != 0 || quantity > 1) {
            resultBuilder
                    .append(' ')
                    .append('(')
                    .append(quantity > 1 ? results : total)
                    .append(' ')
                    .append(op)
                    .append(' ')
                    .append(Math.abs(modifier))
                    .append(')');
        }

        return resultBuilder.toString();
    }
}
