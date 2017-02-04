Practical XML: Exercise 04
==========================

Author: [ZHU, Chenfeng](http://about.me/zhuchenfeng)

## Table of contents

* [Solutions](#solutions)
    * [Exercise Digester](#exercise-digester)
    * [Exercise JAXB](#exercise-jaxb)
    * [Exercise CalExit via JAXB](#exercise-calexit-via-jaxb)
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
javac org/zhuzhu/xml/jaxb/PersonalSchedule.java
java org.zhuzhu.xml.jaxb.PersonalSchedule [path_of_xml] [path_of_xsd] [path_of_output]
```

### Exercise CalExit via JAXB

``` shell
# generate models
xjc -p [package_path] [path_of_XSD] -d [directory_of_output]
# compile java files
javac -d . `find [directory_of_output] -name '*.java'`
# run the application
javac org/zhuzhu/xml/jaxb/calexit/CalexitJAXB.java
java org.zhuzhu.xml.jaxb.calexit.CalexitJAXB [path_of_mondial] [path_of_output1] [path_of_new_info] [path_of_xsd]
# other
java -Djavax.xml.accessExternalDTD="all" org.zhuzhu.xml.jaxb.calexit.CalexitJAXB [path_of_mondial] [path_of_output1] [path_of_new_info] [path_of_xsd]
```

``` shell
# Validation
# check new country
saxonXQ -s:./mondial_new.xml -qs:"//country[@car_code='CAL']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//node()[name='California']/name" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//node()[name='California']/indep_date" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//node()[name='California']/government" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//node()[name='California']/encompassed" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//node()[name='California']/city[@is_country_cap]" \!indent=yes

# check original country
saxonXQ -s:./mondial_new.xml -qs:"//country[@car_code='USA']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//country[@car_code='USA']/child::node()[name()!='province']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//country[@car_code='USA']/province[name='California']" \!indent=yes

# check borders
saxonXQ -s:./mondial_new.xml -qs:"//country[@car_code='CAL']/border" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//country[@car_code='USA']/border" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//country[@car_code='MEX']/border" \!indent=yes

saxonXQ -s:./mondial_new.xml -qs:"//idref('prov-United-States-6')/parent::node()[name()!='city']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//idref('prov-California-6')/parent::node()[name()!='city']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//node()[@country='CAL']/parent::node()[name()!='city']" \!indent=yes

# check organizations:
saxonXQ -s:./mondial_new.xml -qs:"//organization[@id='org-G-10']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//organization[@id='org-AfDB']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//organization[@id='org-G-5']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//organization[@id='org-G-7']" \!indent=yes

# check nature elements:
saxonXQ -s:./mondial_new.xml -qs:"//located[@country='CAL']//parent::node()" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//sea[located/@country='CAL']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//river[located/@country='CAL']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//lake[located/@country='CAL']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//island[located/@country='CAL']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//mountain[located/@country='CAL']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//desert[located/@country='CAL']" \!indent=yes

# check airport
saxonXQ -s:./mondial.xml -qs:"count(//airport[@city=//province[name='California']/city/string(@id)])" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//airport[@country='CAL']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"count(//airport[@city=//country[name='California']/city/string(@id)])" \!indent=yes
```

## Hints

> Digester is rule-based.
> xjc is integrated in JDK(1.5+). 'xjc -help'.


