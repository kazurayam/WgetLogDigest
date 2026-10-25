package com.kazurayam.wgetlog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WgetLogEntry {
    private static final Logger logger = LoggerFactory.getLogger(WgetLogEntry.class);

    static final String regexDateTime = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
    static final String regexURL = "(http|https)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*";

    // e.g　"--2026-10-23 22:03:06--  http://example.com/robots.txt"
    static final Pattern PATTERN_STARTER =
            Pattern.compile("--" + "(" + regexDateTime + ")" + "--\\s{2}" + "(" + regexURL + ")" );

    // e.g "HTTP request sent, awaiting response... 200 OK"
    //     "HTTP request sent, awaiting response... 404 Not Found"
    static final Pattern PATTERN_RESPONSE_STATUS =
            Pattern.compile("HTTP request sent, awaiting response... (\\d{3}) ([\\w\\s]+)$");

    static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String content = "";
    private LocalDateTime dateTime = LocalDateTime.MIN;
    private URL url = null;
    private Integer responseStatus = 0;
    private String statusMessage = "";

    public WgetLogEntry(String content) {
        Objects.requireNonNull(content);
        this.content = content;
        BufferedReader br = new BufferedReader(new StringReader(content));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher starter = PATTERN_STARTER.matcher(line);
                if (starter.matches()) {
                    dateTime = LocalDateTime.parse(starter.group(1),
                            dateTimeFormatter);
                    try {
                        url = new URI(starter.group(2)).toURL();
                    } catch (URISyntaxException e) {
                        logger.error("invalid URL string:{}", starter.group(2), e);
                    }
                }
                Matcher response = PATTERN_RESPONSE_STATUS.matcher(line);
                if (response.matches()) {
                    responseStatus = Integer.valueOf(response.group(1));
                    statusMessage = response.group(2);
                }
            }
        } catch (IOException e) {
            logger.error("failed to digest the string given", e);
        }
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public URL getURL() {
        if (url != null) {
            return url;
        } else {
            try {
                return new URI("http://foo.bar.com/").toURL();
            } catch (URISyntaxException | MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String toString() {
        return this.getDateTime().format(dateTimeFormatter) +
                "\t" +
                this.getURL().toExternalForm() +
                "\t" +
                this.getResponseStatus() +
                "\t" +
                this.getStatusMessage();
        // there should be no NEWLINE char at the tail
    }
}
