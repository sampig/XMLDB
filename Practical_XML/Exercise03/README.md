Practical XML: Exercise 03
==========================

Author: [ZHU, Chenfeng](http://about.me/zhuchenfeng)

## Table of contents

* [Solutions](#solutions)
    * [Exercise DOM](#exercise-dom)

## Solutions

### Exercise DOM

``` shell
javac -cp /afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/lib/jdom-1.1.3.jar:. org/zhuzhu/dom/MyJDOM.java

java -cp /afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/lib/jdom-1.1.3.jar:/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/lib/jaxen-1.1.1.jar:. org.zhuzhu.dom.MyJDOM [path_of_mondial] [path_of_output]

javac org/zhuzhu/dom/MyW3CDOM.java

java org.zhuzhu.dom.MyW3CDOM [path_of_mondial] [path_of_output]

saxonXQ -s:/afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml -qs:"count(//organization[idref(@headq)/parent::country=id(members/@country)])"
```

### Exercise SAX

``` shell
javac org/zhuzhu/dom/MySAX.java

java org.zhuzhu.dom.MySAX [path_of_mondial] [path_of_output1] [path_of_output2] [path_of_output3] [path_of_output4]
```


