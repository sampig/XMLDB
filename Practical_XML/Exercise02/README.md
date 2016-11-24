Practical XML: Exercise 02
==========================

Author: Ankita Bajpai, Azadeh Amiri, [Chenfeng ZHU](http://about.me/zhuchenfeng), Dorna Amiri, Michael Debono

## Table of contents

* [Solutions](#solutions)

## Solutions

### Exercise 2.1 (Recursion in Data)

``` shell
# a
saxonXSL /afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml ex02_01a.xsl
saxonXSL /afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml ex02_01a.xsl > ~/public_html/xml/ex02/02_01a.xml
saxonXQ -s:/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/ex02/02_01a.xml -qs:"//sea[name='North Sea']"

# b
saxonXSL ./02_01a.xml ex02_01b.xsl
saxonXSL ./02_01a.xml ex02_01b.xsl > ~/public_html/xml/02_01b.html

# c
saxonXSL /afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml ex02_01c.xsl
saxonXSL /afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml ex02_01c.xsl > ~/public_html/xml/02_01c.html

# d
saxonXSL /afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml ex02_01d.xsl > ~/public_html/xml/02_01d.xml
saxonXQ -s:/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/ex02/02_01d.xml -qs:"//sea[name='Mediterranean Sea']//river[source/latitude<0]"
saxonXQ -s:/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/ex02/02_01d.xml -qs:"//sea[name='Black Sea']//*[self::river or self::lake]/name/string(@country)"
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



