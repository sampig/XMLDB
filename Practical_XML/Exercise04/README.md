Practical XML: Exercise 04
==========================

Author: [ZHU, Chenfeng](http://about.me/zhuchenfeng)

## Table of contents

* [Solutions](#solutions)
    * [Exercise Digester](#exercise-digester)
    * [Exercise JAXB](#exercise-jaxb)
* [Hints](#hints)

## Solutions

### Exercise Digester

``` shell
javac -cp [path_of_commons-digester3-3.2.jar]:[path_of_commons-beanutils-1.9.3.jar]:[path_of_commons-logging-1.2.jar]:[path_of_commons-collections-2.1.1.jar]:[path_of_cglib-2.2.2.jar]:. org/zhuzhu/xml/digester/MyDigester2.java [path_of_mondial]

java -cp [path_of_commons-digester3-3.2.jar]:[path_of_commons-beanutils-1.9.3.jar]:[path_of_commons-logging-1.2.jar]:[path_of_commons-collections-2.1.1.jar]:[path_of_cglib-2.2.2.jar]:. org.zhuzhu.xml.digester.MyDigester2 [path_of_mondial]
```

### Exercise JAXB

``` shell
# generate models
xjc -p [package_path] [path_of_XSD] -d [directory_of_output]
# compile java files
javac -d . `find [directory_of_output] -name '*.java'`
# run the application
javac org/zhuzhu/xml/jaxb
java org.zhuzhu.xml.jaxb.PersonalSchedule [path_of_xml] [path_of_xsd] [path_of_output]
```


## Hints

> Digester is rule-based.
> xjc is integrated in JDK(1.5+). 'xjc -help'.


