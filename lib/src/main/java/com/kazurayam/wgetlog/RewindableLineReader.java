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

    public RewindableLineReader(Reader reader) {
        br = new BufferedReader(reader);
        lineStack = new Stack<>();
    }
    public String getLine() {
        String line = null;
        if (lineStack.isEmpty()) {
            try {
                line = br.readLine();
                if (line != null) {
                    line += "\n";
                }
            } catch (IOException e) {
                logger.error("", e);
            }
        } else {
            line = lineStack.pop();
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
