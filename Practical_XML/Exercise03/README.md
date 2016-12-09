Practical XML: Exercise 03
==========================

Author: [ZHU, Chenfeng](http://about.me/zhuchenfeng)

## Table of contents

* [Solutions](#solutions)

## Solutions

``` shell
javac -cp /afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/lib/jdom-1.1.3.jar:. org/zhuzhu/dom/MyJDOM.java

java -cp /afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/lib/jdom-1.1.3.jar:. org.zhuzhu.dom.MyJDOM

javac org/zhuzhu/dom/MyW3CDOM.java

java org.zhuzhu.dom.MyW3CDOM

saxonXQ -s:/afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml -qs:"count(//organization[idref(@headq)/parent::country=id(members/@country)])"
```


