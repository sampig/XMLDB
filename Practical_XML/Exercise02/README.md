Practical XML: Exercise 01
==========================

Author: [ZHU, Chenfeng](http://about.me/zhuchenfeng)

## Table of contents

* [Solutions](#solutions)

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

### Exercise 2.2 (Generation of static Web Pages)

``` shell
saxonXSL /afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial-europe.xml ex02_02.xsl

tree ~/public_html/xml/ex02/
```

[Link](http://user.informatik.uni-goettingen.de/~chenfeng.zhu/xml/ex02/)


### Exercise 2.3 (Generation of dynamic Web Pages)

``` shell
saxonXSL /afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml ex02_03a.xsl
more ~/public_html/xml/ex02/org/[name].xml

cp ex02_03b.xsl ~/public_html/xml/ex02/org/
saxonXSL -a ~/public_html/xml/ex02/org/[name].xml
```


