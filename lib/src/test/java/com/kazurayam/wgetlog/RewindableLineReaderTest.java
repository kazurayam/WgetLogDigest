package com.kazurayam.wgetlog;

import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RewindableLineReaderTest {
    private static final Logger logger = LoggerFactory.getLogger(RewindableLineReaderTest.class);

    @Test
    void test_getLine_ungetLine() {
        String content = """
aaaa
--bb
cccc
""";
        RewindableLineReader rlr = new RewindableLineReader(new StringReader(content));
        String line1 = rlr.getLine();
        assertEquals("aaaa\n", line1);
        String line2 = rlr.getLine();
        assertEquals("--bb\n", line2);
        rlr.ungetLine(line2);
        String line3 = rlr.getLine();
        assertEquals("--bb\n", line3);
        String line4 = rlr.getLine();
        assertEquals("cccc\n", line4);
        String line5 = rlr.getLine();
        assertNull(line5);
    }
}
