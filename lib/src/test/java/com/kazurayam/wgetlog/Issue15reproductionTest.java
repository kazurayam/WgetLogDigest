package com.kazurayam.wgetlog;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * reproducing the [issue #15](https://github.com/kazurayam/WgetLogDigest/issues/11)
 * where Digester failed to support the cases of
 * "No data received" and "Unable to establish SSL connection".
 */
public class Issue15reproductionTest {

    private static final Logger logger = LoggerFactory.getLogger(Issue15reproductionTest.class);

    static Path fixtureLog = Paths.get(".").resolve("src/test/fixtures/issue15-wget.log");

    static {
        assertTrue(Files.exists(fixtureLog));
    }

    @Test
    void test_no_data_received() throws IOException {
        BufferedReader br = Files.newBufferedReader(fixtureLog, Charset.forName("Shift_JIS"));
        Digester digester = new Digester(br);
        boolean found = false;
        while (digester.hasNext()) {
            WgetLogEntry entry = digester.next();
            if (entry.getStatusMessage().equals("No data received")) {
                found = true;
                logger.info(entry.toString());
            }
        }
        assertTrue(found);
    }

    @Test
    void test_unable_to_connect() throws IOException {
        BufferedReader br = Files.newBufferedReader(fixtureLog, Charset.forName("Shift_JIS"));
        Digester digester = new Digester(br);
        boolean found = false;
        while (digester.hasNext()) {
            WgetLogEntry entry = digester.next();
            if (entry.getStatusMessage().equals("Unable to establish SSL connection")) {
                found = true;
                logger.info(entry.toString());
            }
        }
        assertTrue(found);
    }
}

