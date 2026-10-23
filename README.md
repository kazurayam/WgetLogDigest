# WgetLogParser

wgetコマンドが出力した実行時ログをファイルに保存したとする。ログファイルのなかから "404 Not Found" となった
あやしいURLを抽出してリストを作りたいと思った。ログの例を下記に示す。

```text
--2026-10-23 22:03:04--  http://www.aomori-gankaikai.jp/index.html
Resolving www.aomori-gankaikai.jp (www.aomori-gankaikai.jp)... 153.122.205.45
Connecting to www.aomori-gankaikai.jp (www.aomori-gankaikai.jp)|153.122.205.45|:80... connected.
HTTP request sent, awaiting response... 200 OK
Length: unspecified [text/html]
Saving to: 'www.aomori-gankaikai.jp/index.html'

     0K ......                                                 8.47M=0.001s

2026-10-23 22:03:04 (8.47 MB/s) - 'www.aomori-gankaikai.jp/index.html' saved [6836]

Loading robots.txt; please ignore errors.

...(中略)


--2026-10-23 22:03:34--  http://www.aomori-gankaikai.jp/member/index.html
Reusing existing connection to www.aomori-gankaikai.jp:80.
HTTP request sent, awaiting response... 401 Unauthorized
Authentication selected: Basic realm="Please type your user-id and password"
Reusing existing connection to www.aomori-gankaikai.jp:80.
HTTP request sent, awaiting response... 200 OK
Length: unspecified [text/html]
Saving to: 'www.aomori-gankaikai.jp/member/index.html'

     0K .........                                              3.54M=0.003s

2026-10-23 22:03:34 (3.54 MB/s) - 'www.aomori-gankaikai.jp/member/index.html' saved [9843]

Incomplete or invalid multibyte sequence encountered
--2026-10-23 22:03:35--  http://www.aomori-gankaikai.jp/img/common/f_logo.png
Reusing existing connection to www.aomori-gankaikai.jp:80.
HTTP request sent, awaiting response... 200 OK
Length: 5098 (5.0K) [image/png]
Saving to: 'www.aomori-gankaikai.jp/img/common/f_logo.png'

     0K ....                                                  100% 39.9M=0s

2026-10-23 22:03:35 (39.9 MB/s) - 'www.aomori-gankaikai.jp/img/common/f_logo.png' saved [5098/5098]

--2026-10-23 22:03:36--  http://www.aomori-gankaikai.jp/privacy/index.html
Reusing existing connection to www.aomori-gankaikai.jp:80.
HTTP request sent, awaiting response... 404 Not Found
2026-10-23 22:03:36 ERROR 404: Not Found.

--2026-10-23 22:03:37--  http://www.aomori-gankaikai.jp/img/index/page_top.png
Reusing existing connection to www.aomori-gankaikai.jp:80.
HTTP request sent, awaiting response... 200 OK
Length: 1350 (1.3K) [image/png]
Saving to: 'www.aomori-gankaikai.jp/img/index/page_top.png'

     0K .   100% 15.8M=0s

...中略


```


wgetのログ情報のエントリーは 行の先頭がハイフン2つ `--` である行によって区切られている。
先頭行にURL文字列が含まれている。しかしそのURLにアクセスを試みた時に `200 OK` だったのか
`404 Not Found` になったのかの区別は先頭行には含まれていない。先頭行に続く数行後に "Not Found" が現れるので
はじめて先頭行のURLへのアクセスが失敗したことが判明する。

grepのような行指向のソフトウェアでwgetのログから "404 Not Found" になったURLを
抽出することができるだろうか？残念ながらできない。 `--` を目印として 複数行をひとまとめとして
ログのエントリーを切り出すことをするヘルパが必要である。

WgetLogParserはこの問題に対処するために開発されたJavaクラスである。



