# GoogleIndexRetriever

Have you ever found a webpage that seems to talk exactly about what you need, but it has been removed? Yes, Google cache is the answer but... What if the cache has been removed too? What if the site is just in Google Index page? You can not get the webpage back, zb>but you know it was there</b>. 

<b>Google Index Retriever</b> will try to retrieve back the index in Google, so you can get part of the text back, and maybe that removed content you need. Google cache is not there forever. From time to time, they are just removed for good. [Archive.org](https://archive.org/) and its WayBackMachine does not take as many snapshots of the less popular pages... so, there are some situations where the only part of a web that is left is in the Google Index.

Google index is that little part of text in the results page that Google search engine shows when the user searches for anything. In the "index", the searched words matching appear in bold. Google Index is the last part of a web to disappear. So there will be situations where that is the only part left. Google keeps different "indexes" from the same webpage, so, if they could be all put together, <b>the text would be reconstructed and it would maybe come up.</b>

But that is not the only situation where the tool may be useful. What if the index contains passwords, credit card numbers or any other sensitive information? In fact that was one of the reasons to create the tool: to demonstrate that removing the webpage and cache with offensive or sensitive content is not enough. <b>The content may be still reachable</b>. This is all explained in this ![presentation](https://es.slideshare.net/chemai64/no-me-indexes-que-me-cacheo).

### How does the tool work? ###

It is very easy. The tool is fed with a Google Search that produces an index result. It will try, brute forcing the Google Search ("stimulating it") to retrieve as much as possible. Then, it has some different options:

![Example with an evernote profile](http://4.bp.blogspot.com/-yfN1DgR6ytU/VTnwlBta7UI/AAAAAAAAC50/2nL715TinIE/s1600/GIR1.PNG)

* <b>One Shot button:</b> It just searches once with the information provided. Use this to try to be the more specific you can with the search string before hitting on "start button".
* <b>Start button:</b> It starts searching in automatic mode. Result box will display the time elapsed since the search started, the word that made the information to come up, and finally the longest possible sentence if it differs from the last one, so the user may reconstruct the webpage.


The logic to try to "stimulate" the index and get back the information is:

* First, try to stimulate the index with the words already found in the first index result "around" the main word searched, so it tries to retrieve the whole sentences again and again.
* If there are no more results or "words around" left, the search is repeated with keywords provided by the user, like a "dictionary attack". When this occurs, the progress bar changes its color.


Google, of course will launch a CAPTCHA from time to time because of the continuous use of their service. This is perfectly ok. Google Index Retriever will capture the CAPTCHA so it is easy to resolve and keep on going.

![Google will show a CAPTCH from time to time](http://3.bp.blogspot.com/-T7vYCWh5uDQ/VTnwnCtirLI/AAAAAAAAC58/7FCHitoT1OE/s1600/GIR2.PNG)

### Spam ###

This tool may be used as well to check if a site has been probably compromised and injected with spam and black SEO. It is usual that attackers compromise webpages and inject spam words in them so the "steal" their pagerank. 

![Using the tool to find possible "hidden" Spam in a webpage](http://1.bp.blogspot.com/-GYUrhCu8lGw/VTnwo85bNrI/AAAAAAAAC6E/FZP8WQs50mE/s1600/GIR3.PNG)

This content is not visible for visitors but only to Google robot and spider, so it is usually visible in this index. This tab works exactly the same as the other, but with another logic:

* It directly tries to search from a different set of keywords (related to spam) in a Google index result.


So this way, it is easier to know if a webpage has been compromised and injected with SEO spam.

### Other features ###

The program is written in Java, so it should work under any system and version, although it has been tested under Windows. The results may be exported to a html document in the local computer. Keywords and spamKeywords are completely customizable. They may be added individually or edited directly from a TXT file. 

![Customizable keywords](http://2.bp.blogspot.com/-Wt-r_c8FUXI/VTn1vRFaCtI/AAAAAAAAC6U/6D4HDD6Hvi8/s1600/GIR4.PNG)

