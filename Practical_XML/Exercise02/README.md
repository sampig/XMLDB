Practical XML: Exercise 01
==========================

Author: [ZHU, Chenfeng](http://about.me/zhuchenfeng)

## Table of contents

* [Solution](#solution)

## Solutions

### Exercise 2.1 (Recursion in Data)

``` shell
# a
saxonXSL /afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml ex02_01a.xsl
saxonXSL /afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml ex02_01a.xsl > 02_01a.xml

# b
saxonXSL ./02_01a.xml ex02_01b.xsl
saxonXSL ./02_01a.xml ex02_01b.xsl > ~/public_html/xml/02_01b.html

# c
saxonXSL /afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml ex02_01c.xsl
saxonXSL /afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml ex02_01c.xsl > ~/public_html/xml/02_01c.html

# d
saxonXSL /afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml ex02_01d.xsl > ~/public_html/xml/02_01d.xml

```





