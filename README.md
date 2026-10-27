# WgetLogDigest

## Motivation

Let me assume you ran the [wget](https://www.gnu.org/software/wget/) command to scrape a website of your interest. You can save the log information into a local file. You want to find out the URLs that responded "404 Not Found". The result will be useful to administrate the website. You can check the faulty URLs and do any possible improvements. The following fragment shows an example of log emitted by wget:

```text
...
--2026-10-23 22:03:04--  http://www.myapp.jp/index.html
Resolving www.myapp.jp (www.myapp.jp)... 153.122.205.45
Connecting to www.myapp.jp (www.myapp.jp)|153.122.205.45|:80... connected.
HTTP request sent, awaiting response... 200 OK
Length: unspecified [text/html]
Saving to: 'www.myapp.jp/index.html'

     0K ......                                                 8.47M=0.001s

2026-10-23 22:03:04 (8.47 MB/s) - 'www.myapp.jp/index.html' saved [6836]

Loading robots.txt; please ignore errors.

--2026-10-23 22:03:34--  http://www.myapp.jp/member/index.html
Reusing existing connection to www.myapp.jp:80.
HTTP request sent, awaiting response... 401 Unauthorized
Authentication selected: Basic realm="Please type your user-id and password"
Reusing existing connection to www.myapp.jp:80.
HTTP request sent, awaiting response... 200 OK
Length: unspecified [text/html]
Saving to: 'www.myapp.jp/member/index.html'

     0K .........                                              3.54M=0.003s

2026-10-23 22:03:34 (3.54 MB/s) - 'www.myapp.jp/member/index.html' saved [9843]

Incomplete or invalid multibyte sequence encountered
--2026-10-23 22:03:35--  http://www.myapp.jp/img/common/f_logo.png
Reusing existing connection to www.myapp.jp:80.
HTTP request sent, awaiting response... 200 OK
Length: 5098 (5.0K) [image/png]
Saving to: 'www.myapp.jp/img/common/f_logo.png'

     0K ....                                                  100% 39.9M=0s

2026-10-23 22:03:35 (39.9 MB/s) - 'www.myapp.jp/img/common/f_logo.png' saved [5098/5098]

--2026-10-23 22:03:36--  http://www.myapp.jp/privacy/index.html
Reusing existing connection to www.myapp.jp:80.
HTTP request sent, awaiting response... 404 Not Found
2026-10-23 22:03:36 ERROR 404: Not Found.

--2026-10-23 22:03:37--  http://www.myapp.jp/img/index/page_top.png
Reusing existing connection to www.myapp.jp:80.
HTTP request sent, awaiting response... 200 OK
Length: 1350 (1.3K) [image/png]
Saving to: 'www.myapp.jp/img/index/page_top.png'

     0K .   100% 15.8M=0s

...
```

You can easily find that the format of `wget`'s log is not *line-oriented*. Therefore it is difficult to post-process using line-oriented tools such as [`grep`](https://www.gnu.org/software/grep/) and [`sed`](https://www.gnu.org/software/sed/).

`WgetLogDigest` is a tiny tool in Java that transforms a wget's log file into another file in a line-oriented format. For example, the origina log entries showed above will be transformed as follows:

```text
2026-10-23 22:03:04  http://www.myapp.jp/index.html  200  OK
2026-10-23 22:03:34  http://www.myapp.jp/member/index.html  200  OK
2026-10-23 22:03:35  http://www.myapp.jp/img/common/f_logo.png  200  OK
2026-10-23 22:03:36  http://www.myapp.jp/privacy/index.html  404  Not Found
2026-10-23 22:03:37  http://www.myapp.jp/img/index/page_top.png  200 OK
```

Each lines in the output file will be TAB-delimited, like this

yyyy-MM--dd hh:mm:ss **\t** URL **\t** status **\t** message

## How to use it

A custom Gradle Plugin is provided. Here I quote a sample [build.gradle](https://github.com/kazurayam/WgetLogDigest/blob/master/rehearsal/build.gradle).

```gradle
plugins {
    id("com.kazurayam.wget-log-digest-plugin") version "0.1.7"
}

tasks.register("demonstrate", com.kazurayam.wgetlog.DigesterTask) {
    wgetLogFile = file("../plugin/src/test/fixtures/wget.log")
    outputFile = file("./build/tmp/out/output.txt")
}
```
