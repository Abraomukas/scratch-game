package org.example;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

public class MainTest {

    @Test
    public void testMainHasFourArgs() {
        // given
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = {"--config", "config.json", "--betting-amount", "100"};
        String expected = "Creating a 3x3 matrix...";

        // when
        Main.main(args);
        String capturedOutput = outContent.toString();

        // then
        assertTrue(capturedOutput.contains(expected));
    }

    @Test
    public void testConfigParamMissing() {

    }

    @Test
    public void testBettingAmountParamMissing() {

    }

    @Test
    public void testNotWinning() {

    }

}