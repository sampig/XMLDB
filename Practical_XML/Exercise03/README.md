Practical XML: Exercise 03
==========================

Author: [ZHU, Chenfeng](http://about.me/zhuchenfeng)

## Table of contents

* [Solutions](#solutions)
    * [Exercise DOM](#exercise-dom)
    * [Exercise SAX](#exercise-sax)
    * [Exercise StAX](#exercise-stax)
* [Hints](#hints)

## Solutions

### Exercise DOM

``` shell
# JDOM
javac -cp [path_of_jdom-1.1.3.jar]:. org/zhuzhu/xml/dom/MyJDOM.java
java -cp [path_of_jdom-1.1.3.jar]:[path_of_jaxen-1.1.1.jar]:. org.zhuzhu.xml.dom.MyJDOM [path_of_mondial] [path_of_output]

# W3C DOM
javac org/zhuzhu/xml/dom/MyW3CDOM.java
java org.zhuzhu.xml.dom.MyW3CDOM [path_of_mondial] [path_of_output]

# Check via XPath
saxonXQ -s:./mondial.xml -qs:"count(//organization[idref(@headq)/parent::country=id(members/@country)])" \!indent=yes
```

### Exercise SAX

``` shell
javac org/zhuzhu/xml/sax/MySAX.java

java org.zhuzhu.xml.sax.MySAX [path_of_mondial] [path_of_output1] [path_of_output2] [path_of_output3] [path_of_output4]
```

### Exercise StAX

``` shell
javac org/zhuzhu/xml/stax/MyStAX.java

java org.zhuzhu.xml.stax.MyStAX [path_of_mondial] [path_of_output1] [path_of_output2]
```

### Exercise Calexit

#### Common

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
    - Add "continent" from country.
    - Add "indep_date".
    - Add ""is_country_cap" for new capital city.
    - Get the element of the province or the city. Change it to the country and add it under "mondial".
2. Modify original Country.
    - Change its area.
    - Re-calculate population, gdp, ethnicgroup, religion, language and border.
    - Remove the province.
3. Modify other countries.
    - Change its border.
4. Modify other Element.
    - Change the nature. (//node()[count(./located[@country='USA'])>=1])
        - Change the "located": remove province and add new located, or remove province and change country.
        - Change its parent: change country attribute.
    - Change the organization.
        - Get the members element and chagne its country attribute (add new carcode).
    - Change the airport.
        - Change the city attribute.
        - Change the country attribute with new carcode.


#### via JDOM

``` shell
javac -cp [path_of_jdom-1.1.3.jar]:[path_of_jaxen-1.1.1.jar]:. org/zhuzhu/xml/calexit/CalexitJDOM.java

java -cp [path_of_jdom-1.1.3.jar]:[path_of_jaxen-1.1.1.jar]:. org.zhuzhu.xml.calexit.CalexitJDOM [path_of_mondial] [path_of_output1] [path_of_new_info]
```

Check the results:

``` shell
xmllint -loaddtd -valid -noout mondial_new.xml

# check new country
saxonXQ -s:./mondial_new.xml -qs:"//country[@car_code='CAL']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//country[@car_code='USA']/province[name='California']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//node()[name='California']/name" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//node()[name='California']/indep_date" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//node()[name='California']/government" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//node()[name='California']/encompassed" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//node()[name='California']/city[@is_country_cap]" \!indent=yes

# check borders
saxonXQ -s:./mondial_new.xml -qs:"//country[@car_code='CAL']/border" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//country[@car_code='USA']/border" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//country[@car_code='MEX']/border" \!indent=yes

saxonXQ -s:./mondial_new.xml -qs:"//idref('prov-United-States-6')/parent::node()[name()!='city']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//idref('prov-California-6')/parent::node()[name()!='city']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//node()[@country='CAL']/parent::node()[name()!='city']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//node()[@country='CAL']/parent::node()[name()!='city']" \!indent=yes

# check organizations:
saxonXQ -s:./mondial_new.xml -qs:"//organization[@id='org-G-10']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//organization[@id='org-G-5']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//organization[@id='org-G-7']" \!indent=yes

# check nature elements:
saxonXQ -s:./mondial_new.xml -qs:"//sea[@id='sea-Pacific']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//river[@id='river-Colorado_River']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//river[@id='river-TruckeeRiver']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//river[@id='river-AmargosaRiver']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//mountain[@id='mount-Mt_Whitney']" \!indent=yes

# check airport
saxonXQ -s:./mondial.xml -qs:"count(//airport[@city=//province[name='California']/city/string(@id)])" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"//airport[@country='CAL']" \!indent=yes
saxonXQ -s:./mondial_new.xml -qs:"count(//airport[@city=//country[name='California']/city/string(@id)])" \!indent=yes
```

#### via XSLT

``` shell
saxonXSL [path_of_mondial] calexitXSLT.xsl > [path_of_output1]

xmllint -loaddtd -valid -noout mondial_new_xslt.xml
```

## Hints

> The "Cursor API" using XMLStreamReader and XMLStreamWriter;
> 
> The "Iterator API" using XMLEventReader andXMLEventWriter;

> Outputting an empty element with a single tag, <example/>, is not possible with the Iterator API using XMLEventWriter.
> 
> SAX is faster than DOM, but has a few shortcomings. For instance, SAX can only read XML structures; not write to them. So, if you need to write to an XML file, you need to use DOM. Also, SAX is faster reading flatter XML structures. The more nested your XML structure is, the better you will be using DOM instead of SAX. The following link provides a few details regarding [DOM vs. SAX](http://www.ibm.com/support/knowledgecenter/SSB23S_1.1.0.10/com.ibm.ztpf-ztpfdf.doc_put.10/gtpx1/domsax.html?cp=SSB23S_1.1.0.10)


