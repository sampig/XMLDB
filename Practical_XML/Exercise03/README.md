Practical XML: Exercise 03
==========================

Author: [ZHU, Chenfeng](http://about.me/zhuchenfeng)

## Table of contents

* [Solutions](#solutions)
    * [Exercise DOM](#exercise-dom)
    * [Exercise SAX](#exercise-sax)
    * [Exercise StAX](#exercise-stax)

## Solutions

### Exercise DOM

``` shell
javac -cp /afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/lib/jdom-1.1.3.jar:. org/zhuzhu/dom/MyJDOM.java

java -cp /afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/lib/jdom-1.1.3.jar:/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/lib/jaxen-1.1.1.jar:. org.zhuzhu.dom.MyJDOM [path_of_mondial] [path_of_output]

javac org/zhuzhu/dom/MyW3CDOM.java

java org.zhuzhu.dom.MyW3CDOM [path_of_mondial] [path_of_output]

saxonXQ -s:/afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml -qs:"count(//organization[idref(@headq)/parent::country=id(members/@country)])" \!indent=yes
```

### Exercise SAX

``` shell
javac org/zhuzhu/dom/MySAX.java

java org.zhuzhu.dom.MySAX [path_of_mondial] [path_of_output1] [path_of_output2] [path_of_output3] [path_of_output4]
```

### Exercise StAX

``` shell
javac org/zhuzhu/dom/MyStAX.java

java org.zhuzhu.dom.MyStAX [path_of_mondial] [path_of_output1] [path_of_output2]
```

### Exercise Calexit

All the main elements:

``` xpath
distinct-values(/mondial/*/name())
```

- country
- continent
- organization
- sea
- river
- lake
- island
- mountain
- desert
- airport


All the elements in country:

``` xpath
distinct-values(/mondial/country/*/name())
```

- name
- population
- population_growth
- infant_mortality
- gdp_total
- gdp_agri
- gdp_ind
- gdp_serv
- inflation
- unemployment
- indep_date
- government
- encompassed
- ethnicgroup
- religion
- language
- border
- city
- localname
- province
- dependent


``` xpath
distinct-values(//idref("prov-United-States-6")/parent::node()[name()!="city"]/name())
```

located

``` xpath
distinct-values(//node()[@province and name()!="city" and ancestor-or-self::node()/name()!="country" and ancestor-or-self::node()/name()!="city" and ancestor-or-self::node()/name()!="province"]/name())
```

located

``` xpath
distinct-values(//node()[@province and name()!="city" and ancestor-or-self::node()/name()!="country" and ancestor-or-self::node()/name()!="city" and ancestor-or-self::node()/name()!="province"]/parent::node()/name())
```

sea
river
source
estuary
lake
island
mountain
desert


Once a province or a city leaves a country.

1. Add new country Element.
    - Change its attributes and children.
        - Attributes: car_code+, area+, capital, memberships+; id-, country-
        - Children: area-
    - Change its cities.
        - Attributes: change id and country, remove province.
    - Add new information for it.
    - Get the element of the province or the city. Change it to the country and add it under "mondial".
2. Modify original Country.
    - Change its area.
    - Remove the province.
3. Modify other Element.
    - Change the nature.
    - Change the organization.
    - Change the airport.



``` shell
javac -cp /afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/lib/jdom-1.1.3.jar:/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/lib/jaxen-1.1.1.jar:. org/zhuzhu/xml/calexit/CalexitJDOM.java

java -cp /afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/lib/jdom-1.1.3.jar:/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/lib/jaxen-1.1.1.jar:. org.zhuzhu.xml.calexit.CalexitJDOM [path_of_mondial] [path_of_output1] [path_of_new_info]


```

Check the results:

``` shell
xmllint -loaddtd -valid -noout mondial_new.xml

saxonXQ -s:./mondial_new.xml -qs:"//country[@car_code='CAL']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//node()[name='California']/name" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//idref('prov-United-States-6')/parent::node()[name()!='city']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//idref('prov-California-6')/parent::node()[name()!='city']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//node()[@country='CAL']/parent::node()[name()!='city']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//node()[@country='CAL']/parent::node()[name()!='city']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//organization[@id='org-G-10']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//organization[@id='org-G-5']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//organization[@id='org-G-7']" \!indent=yes
```

>>> The "Cursor API" using XMLStreamReader and XMLStreamWriter;
>>> The "Iterator API" using XMLEventReader andXMLEventWriter;

>>> Outputting an empty element with a single tag, <example/>, is not possible with the Iterator API using XMLEventWriter.


