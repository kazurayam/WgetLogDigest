/*
 *
 */
package com.kazurayam.wgetlog;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DigesterTest {
    private static final Logger logger = LoggerFactory.getLogger(DigesterTest.class);

    String content = """
--2026-10-23 22:03:04--  http://www.example.com/index.html
Resolving www.example.com (www.example.com)... 153.122.205.45
Connecting to www.example.com (www.example.com)|153.122.205.45|:80... connected.
HTTP request sent, awaiting response... 200 OK
Length: unspecified [text/html]
Saving to: 'www.example.com/index.html'

     0K ......                                                 8.47M=0.001s

2026-10-23 22:03:04 (8.47 MB/s) - 'www.example.com/index.html' saved [6836]

Loading robots.txt; please ignore errors.

--2026-10-23 22:03:06--  http://www.example.com/robots.txt
Reusing existing connection to www.example.com:80.
HTTP request sent, awaiting response... 404 Not Found
2026-10-23 22:03:06 ERROR 404: Not Found.

--2026-10-23 22:03:07--  http://www.example.com/css/common/layout.css
Reusing existing connection to www.example.com:80.
HTTP request sent, awaiting response... 200 OK
Length: unspecified [text/css]
Saving to: 'www.example.com/css/common/layout.css'

     0K .......                                                44.9M=0s

2026-10-23 22:03:07 (44.9 MB/s) - 'www.example.com/css/common/layout.css' saved [7310]

""";

    @Test void test_hasNext_next() {
        Digester digester = new Digester(new StringReader(content));
        //
        assertTrue(digester.hasNext());
        WgetLogEntry entry1 = digester.next();
        assertEquals("http://www.example.com/index.html", entry1.getURL().toString());
        assertEquals(200, entry1.getResponseStatus());
        //
        assertTrue(digester.hasNext());
        WgetLogEntry entry2 = digester.next();
        assertEquals("http://www.example.com/robots.txt", entry2.getURL().toString());
        assertEquals(404, entry2.getResponseStatus());
        //
        assertTrue(digester.hasNext());
        WgetLogEntry entry3 = digester.next();
        logger.info("entry3=" + entry3.getContent());
        logger.info("entry3.getDateTime()=" + entry3.getDateTime());
        logger.info("entry3.getURL()=" + entry3.getURL().toString());
        logger.info("entry3.getResponseStatus()=" + entry3.getResponseStatus());
        assertEquals("http://www.example.com/css/common/layout.css", entry3.getURL().toString());
        assertEquals(200, entry3.getResponseStatus());
        //
        assertFalse(digester.hasNext());
    }

    @Test
    void test_transform_a_wgetlog_into_a_line_oriented_text() throws IOException {
        Path logFile = Paths.get("src/test/fixtures/wget.log");
        Reader rdr = Files.newBufferedReader(logFile);
        Digester digester = new Digester(rdr);
        Path out = Paths.get("build/all_entries.txt");
        PrintWriter pw = new PrintWriter(Files.newBufferedWriter(out));
        while (digester.hasNext()) {
            WgetLogEntry entry = digester.next();
            pw.println(entry.toString());
        }
        pw.flush();
        pw.close();
        logger.info("find output at " + out);
    }

    @Test
    void test_find_faulty_URLs_out_of_a_lot_of_entries() throws IOException {
        Path logFile = Paths.get("src/test/fixtures/wget.log");
        Reader rdr = Files.newBufferedReader(logFile);
        Digester digester = new Digester(rdr);
        Path out = Paths.get("build/faulty_URLs.txt");
        PrintWriter pw = new PrintWriter(Files.newBufferedWriter(out));
        while (digester.hasNext()) {
            WgetLogEntry entry = digester.next();
            if (entry.getResponseStatus() > 400) {
                //logger.info(entry.toString());
                pw.println(entry.toString());
            }
        }
        pw.flush();
        pw.close();
        logger.info("find output at " + out);
    }

    @Test
    void test_main() throws IOException {
        String[] args = {"src/test/fixtures/wget.log", "build/main_output.txt"};
        Digester.main(args);
        Path out = Paths.get("build/main_output.txt");
        assertTrue(Files.size(out) > 1000, out + " is not large enough. There must be something wrong.");
    }
}
