package com.kazurayam.wgetlog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Objects;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RewindableLineReader {
    private static final Logger logger = LoggerFactory.getLogger(RewindableLineReader.class);

    BufferedReader br;
    Stack<String> lineStack;
    int lineCount;

    public RewindableLineReader(Reader reader) {
        br = new BufferedReader(reader);
        lineCount = 0;
        lineStack = new Stack<>();
    }

    /**
     *
     * @return a line with \n, else null at the end of the source Reader
     */
    public String getLine() {
        String line = null;
        if (lineStack.isEmpty()) {
            line = getLineSanitizingException();
            if (line != null) {
                line += "\n";
            }
        } else {
            line = lineStack.pop();
        }
        return line;
    }

    /**
     *
     * @return a line of string without \n, else null. When any Exception raised,
     * the Exception will be logged and continue reading the next line.
     * Any Exception won't be raised.
     */
    private String getLineSanitizingException() {
        String line = null;
        try {
            lineCount++;
            line = br.readLine();
        } catch (Exception e) {
            logger.error("failed to read a line #{}", lineCount);
            line = getLineSanitizingException();
        }
        return line;
    }

    public void ungetLine(String line) {
        Objects.requireNonNull(line);
        lineStack.push(line);
    }
    public void close() {
        try {
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
