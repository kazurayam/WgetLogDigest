package com.kazurayam.wgetlog;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.Charset;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Issue11reproductionTest {

    private static final Logger logger = LoggerFactory.getLogger(Issue11reproductionTest.class);

    static Path fixtureLog = Paths.get(".").resolve("src/test/fixtures/issue11-wget.log");

    static {
        assertTrue(Files.exists(fixtureLog));
    }

    /**
     * The fixtureLog file is encoded with Shift_JIS.
     * Reading the file with the default encoding (UTF-8) will cause a MalformedInputException.
     * @throws IOException
     */
    @Test
    void test_reading_with_default_encoding_will_throw_Exception() {
        assertThrows(MalformedInputException.class, () -> {
            BufferedReader br = Files.newBufferedReader(fixtureLog);
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                lineCount++;
            }
            logger.info("Exception raised at lineCount: {}", lineCount);
        });
    }

    /**
     * The fixtureLog file is encoded with Shift_JIS.
     * Reading the file with the Shift_JIS encoding (UTF-8) will NOT cause any Exception.
     * @throws IOException
     */
    @Test
    void test_reading_with_Shift_JIS_wont_throw_Exception() throws IOException {
        BufferedReader br = Files.newBufferedReader(fixtureLog, Charset.forName("Shift_JIS"));
        String line;
        int lineCount = 0;
        while ((line = br.readLine()) != null) {
            lineCount++;
        }
        logger.info("Total lineCount: {}", lineCount);
    }

    /**
     * reproducing the [issue #11](https://github.com/kazurayam/WgetLogDigest/issues/11)
     * where "java.nio.charset.MalformedInputException: Input length = 1" was reported
     *
     * @throws IOException
     */
    @Test
    void test_issue11_reproduction() {
        assertThrows(NoSuchElementException.class, () -> {
            BufferedReader br = Files.newBufferedReader(fixtureLog);
            //                                                    ^^^ no Charset specified
            Digester digester = new Digester(br);
            assertTrue(digester.hasNext());
            int entryCount = 0;
            while (digester.hasNext()) {
                WgetLogEntry entry = digester.next();
                entryCount++;
            }
            logger.info("entryCount: #{}", entryCount);
        });
    }

    /**
     * reproducing the [issue #11](https://github.com/kazurayam/WgetLogDigest/issues/11)
     * where "java.nio.charset.MalformedInputException: Input length = 1" was reported
     *
     * @throws IOException
     */
    @Test
    void test_issue11_fixed() throws IOException {
        BufferedReader br = Files.newBufferedReader(fixtureLog, Charset.forName("Shift_JIS"));
        //                                                      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        Digester digester = new Digester(br);
        assertTrue(digester.hasNext());
        int entryCount = 0;
        while(digester.hasNext()) {
            WgetLogEntry entry = digester.next();
            entryCount++;
        }
        logger.info("entryCount: #{}", entryCount);
    }
}
