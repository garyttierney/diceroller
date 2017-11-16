package com.github.garyttierney.dieroll;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.isException;

public class DiceRollerTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * A DiceRoller that returns a constant value (10).
     */
    private final DiceRoller diceRoller = new DiceRoller((faces) -> 10);

    @Test
    public void positiveModifier() throws Exception {
        String result = diceRoller.roll(10, 1, 2);
        assertThat(result, is(equalTo("12 (10 + 2)")));
    }

    @Test
    public void quantityUnder1() throws Exception {
        exception.expect(isException(instanceOf(IllegalArgumentException.class)));
        diceRoller.roll(2, -1, 2);
    }

    @Test
    public void facesUnder2() throws Exception {
        exception.expect(isException(instanceOf(IllegalArgumentException.class)));
        diceRoller.roll(1, 1, 2);
    }

    @Test
    public void quantityOver1() throws Exception {
        String result = diceRoller.roll(10, 4, 2);
        assertThat(result, is(equalTo("42 (10 + 10 + 10 + 10 + 2)")));
    }

    @Test
    public void negativeModifier() throws Exception {
        String result = diceRoller.roll(10, 1, -2);
        assertThat(result, is(equalTo("8 (10 - 2)")));
    }

    @Test
    public void noModifier() throws Exception {
        String result = diceRoller.roll(10, 1, 0);
        assertThat(result, is(equalTo("10")));
    }

}