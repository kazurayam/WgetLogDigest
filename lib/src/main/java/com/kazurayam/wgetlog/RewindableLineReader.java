package com.kazurayam.wgetlog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.MalformedInputException;
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
    public String getLine() throws IOException {
        String line = null;
        if (lineStack.isEmpty()) {
            try {
                line = br.readLine();
                if (line != null) {
                    line += "\n";
                }
            } catch (MalformedInputException e) {
                throw new IOException("PLS specify the charset used for the input file", e);
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
