package com.kazurayam.wgetlog;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WgetLogEntryTest {
    private static final Logger logger = LoggerFactory.getLogger(WgetLogEntryTest.class);

    String content = """
--2026-10-23 22:03:04--  http://example.com/index.html
Resolving example.com (example.com)... 153.122.205.45
Connecting to example.com (example.com)|153.122.205.45|:80... connected.
HTTP request sent, awaiting response... 200 OK
Length: unspecified [text/html]
Saving to: 'example.com/index.html'

     0K ......                                                 8.47M=0.001s

2026-10-23 22:03:04 (8.47 MB/s) - 'example.com/index.html' saved [6836]
""";

    @Test
    void test_regexURL() {
        Pattern pattern = Pattern.compile(WgetLogEntry.regexURL);
        Matcher matcher = pattern.matcher("http://example.com/index.html");
        assertTrue(matcher.matches());
    }

    @Test
    void test_regexDataTime() {
        Pattern pattern = Pattern.compile(WgetLogEntry.regexDateTime);
        Matcher matcher = pattern.matcher("2026-10-23 22:03:04");
        assertTrue(matcher.matches());
    }

    @Test
    void test_PATTERN_STARTER() {
        logger.info("WgetLogEntry.PATTERN_STARTER=" + WgetLogEntry.PATTERN_STARTER);
        Matcher matcher = WgetLogEntry.PATTERN_STARTER.matcher(
                "--2026-10-23 22:03:04--  http://example.com/index.html");
        assertTrue(matcher.matches());
        assertEquals("2026-10-23 22:03:04", matcher.group(1));
        assertEquals("http://example.com/index.html", matcher.group(2));
    }

    @Test
    void test_PATTERN_STARTER_alt() {
        logger.info("WgetLogEntry.PATTERN_STARTER=" + WgetLogEntry.PATTERN_STARTER);
        Matcher matcher = WgetLogEntry.PATTERN_STARTER.matcher(
                "--2026-10-23 22:03:07--  http://www.example.com/css/common/layout.css");
        assertTrue(matcher.find());
        assertEquals("2026-10-23 22:03:07", matcher.group(1));
        assertEquals("http://www.example.com/css/common/layout.css", matcher.group(2));
    }

    @Test
    void test_dateTimeFormatter() {
        LocalDateTime dateTime = LocalDateTime.parse("2026-10-23 22:03:04",
                WgetLogEntry.dateTimeFormatter);
        assertEquals(2026, dateTime.getYear());
        assertEquals(10, dateTime.getMonth().getValue());
        assertEquals(23, dateTime.getDayOfMonth());
        assertEquals(22, dateTime.getHour());
        assertEquals(3, dateTime.getMinute());
        assertEquals(4, dateTime.getSecond());
    }

    @Test
    void test_getContent() {
        WgetLogEntry entry = new WgetLogEntry(content);
        assertEquals(content, entry.getContent());
    }

    @Test
    void test_getDateTime() {
        WgetLogEntry entry = new WgetLogEntry(content);
        LocalDateTime dateTime = entry.getDateTime();
        assertEquals(2026, dateTime.getYear());
        assertEquals(10, dateTime.getMonth().getValue());
        assertEquals(23, dateTime.getDayOfMonth());
        assertEquals(22, dateTime.getHour());
        assertEquals(3, dateTime.getMinute());
        assertEquals(4, dateTime.getSecond());
    }

    @Test
    void test_getURL() {
        WgetLogEntry entry = new WgetLogEntry(content);
        assertEquals("http://example.com/index.html", entry.getURL().toExternalForm());
    }

    @Test
    void test_getResponseStatus() {
        WgetLogEntry entry = new WgetLogEntry(content);
        assertEquals(200, entry.getResponseStatus());
    }

    @Test
    void test_toString() {
        WgetLogEntry entry = new WgetLogEntry(content);
        String expected = "2026-10-23 22:03:04" + "\t" +
                "http://example.com/index.html" + "\t" +
                "200" + "\t" +
                "OK";
        assertEquals(expected, entry.toString());
    }
}
