package org.example;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
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
        // given
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = {"--c", "config.json", "--betting-amount", "100"};
        String expected = "The first parameter is NOT correct! It has to be '--config'\n";

        // when
        Main.main(args);
        String capturedOutput = outContent.toString();

        // then
        assertEquals(expected, capturedOutput);
    }

    @Test
    public void testBettingAmountParamMissing() {
        // given
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = {"--config", "config.json", "--amount", "100"};
        String expected = "The second parameter is NOT correct! It has to be '--betting-amount'\n";

        // when
        Main.main(args);
        String capturedOutput = outContent.toString();

        // then
        assertEquals(expected, capturedOutput);
    }
}