Practical XML: Exercise 01
==========================

Author: Ankita Bajpai, [Chenfeng ZHU](http://about.me/zhuchenfeng)

## Table of contents

* [Pre Work](#pre-work)
  * [Commands](#commands)
  * [Web Tool](#web-tool)
* [Solutions](#solutions)

## Pre Work

Downlaod the [saxon](http://saxon.sourceforge.net/).

### Commands

Run Saxon:

``` shell
# directly
java -cp saxon-path/saxon9.jar net.sf.saxon.Query
java -cp saxon-path/saxon9.jar net.sf.saxon.Query xquery_file.xq

# or set alias
alias saxonXQ='java -cp saxon-path/saxon9.jar net.sf.saxon.Query'
```

RunSaxon XQuery:

``` shell
saxonXQ xquery_file.xq
saxonXQ -s xml_file.xml xquery_file.xq

saxonXQ "{doc('xml_file.xml')//xpath}"
```

### Web Tool

A web tool: 
[Mondial XML instance](http://www.dbis.informatik.uni-goettingen.de/Mondial/mondial.xml)


## Solutions

### Exercise 1.1 (Mondial - Headquarters of Organizations)

a) 61

``` xpath
//organization/id(@headq)/id(@country)/name
```

b) 183

``` xpath
//country[ not(./name[1]=//organization/id(@headq)/id(@country)/name[1])]/name[1]/text()
```

c) 38

``` xpath
//organization/id(@headq)[population[last()] > 1000000]/name[1]/text()
```

d) 297

``` xpath
//city[not(.=//organization/id(@headq))][population[last()] >  1000000]/name[1]/text()
```

e) 52

``` xpath
//organization[id(@headq)/@id=./members/id(@country)/id(@capital)/@id]/id(@headq)/name[1]/text()
```

### Exercise 1.2 (Mondial - Country Radius)

``` xquery
declare namespace math="http://www.w3.org/2005/xpath-functions/math";
for $c in //country
let $cap := $c/id(@capital)
let $lat1 := $cap/latitude
let $long1 := $cap/longitude
return
<country>{$c/name}
<capital>{$cap/name[1]}
<caplat>{$lat1}</caplat>
<caplong>{$long1}</caplong>
</capital>
<radius>
{
let $dist := (
      for $oe in $c/idref(@car_code)/parent::node()
      let $lat2 := $oe/latitude,
          $long2 := $oe/longitude
      where exists($oe/latitude) and $oe[@id != $c/@capital]
      return 6370*math:acos(math:cos($lat1 div 180*3.14)*math:cos($lat2 div 180*3.14)*math:cos(($long1 - $long2) div 180*3.14) + math:sin($lat1 div 180*3.14)*math:sin($lat2 div 180*3.14))
)
return 
max($dist)
}</radius>
</country>
```

In SQL way:

``` xquery
declare namespace math="http://exslt.org/math";
<result>
{
for $c in /mondial/country
let $cap := id($c/@capital),
    $lat1 := number($cap/latitude),
    $long1 := number($cap/longitude)
return <country>
{ $c/name }
<capital>
    {$cap/name[1]}, {$lat1}, {$long1}
</capital>
<radius>{
let $r_city := (
    for $city in $c//city
    let $lat2 := number($city/latitude),
        $long2 := number($city/longitude)
    where exists($city/latitude) and exists($city/longitude)
    return 6370*math:acos(math:cos($lat1 div 180*3.14)*math:cos($lat2 div 180*3.14)*math:cos(($long1 - $long2) div 180*3.14) + math:sin($lat1 div 180*3.14)*math:sin($lat2 div 180*3.14))
    )
let $r_sea := (
    for $sea in /mondial/sea[@country=$c/@car_code  or located/@country=$c/@car_code]
    let $lat2 := number($sea/latitude),
        $long2 := number($sea/longitude)
    where exists($sea/latitude) and exists($sea/longitude)
    return 6370*math:acos(math:cos($lat1 div 180*3.14)*math:cos($lat2 div 180*3.14)*math:cos(($long1 - $long2) div 180*3.14) + math:sin($lat1 div 180*3.14)*math:sin($lat2 div 180*3.14))
    )
let $r_mountain := (
    for $mountain in /mondial/mountain[located/@country=$c/@car_code]
    let $lat2 := number($mountain/latitude),
        $long2 := number($mountain/longitude)
    where exists($mountain/latitude) and exists($mountain/longitude)
    return 6370*math:acos(math:cos($lat1 div 180*3.14)*math:cos($lat2 div 180*3.14)*math:cos(($long1 - $long2) div 180*3.14) + math:sin($lat1 div 180*3.14)*math:sin($lat2 div 180*3.14))
    )
let $r_river := (
    for $river in /mondial/river[located/@country=$c/@car_code]
    let $lat2 := number($river/latitude),
        $long2 := number($river/longitude)
    where exists($river/latitude) and exists($river/longitude)
    return 6370*math:acos(math:cos($lat1 div 180*3.14)*math:cos($lat2 div 180*3.14)*math:cos(($long1 - $long2) div 180*3.14) + math:sin($lat1 div 180*3.14)*math:sin($lat2 div 180*3.14))
    )
let $r_lake := (
    for $lake in /mondial/lake[located/@country=$c/@car_code]
    let $lat2 := number($lake/latitude),
        $long2 := number($lake/longitude)
    where exists($lake/latitude) and exists($lake/longitude)
    return 6370*math:acos(math:cos($lat1 div 180*3.14)*math:cos($lat2 div 180*3.14)*math:cos(($long1 - $long2) div 180*3.14) + math:sin($lat1 div 180*3.14)*math:sin($lat2 div 180*3.14))
    )
return max(($r_city, $r_sea, $r_mountain, $r_river, $r_lake))
}
</radius>
</country>
}
</result>
```

### Exercise 1.3 (Mondial - neighbor populations in descending order)

``` xquery
for $c in //country
let $sumpop := sum($c/border/id(@country)/population[last()])
order by $sumpop descending
return
<country>
<name>{$c/name/text()}</name>
<pop>{$sumpop}</pop>
</country>
```

### Exercise 1.4 (Mondial - Lowest Highest Mountain)

``` xquery
let $m :=
(for $c in //continent
return //mountain[elevation = max(//mountain[id(@country)/encompassed/@continent = $c/@id]/elevation)])
return $m[elevation = min($m/elevation)]
```

### Exercise 1.5 (Mondial - Organizations and Continents)

``` xquery
//organization[count(distinct-values(./members/id(@country)/encompassed/id(@continent)))=5]

//organization[count(distinct-values(./members/id(@country)/encompassed/id(@continent)))=count(distinct-values(//continent))]
```


### Exercise 1.6 (Mondial - Non-Coverable Organizations)

``` xquery
let $organization := (
    for $o in $doc//organization
    let $oo := $doc//organization[@id != $o/@id]
    where every $org in $oo satisfies (
        some $mo in $o/members/id(@country) satisfies not($mo = $org/members/id(@country))
    )
    return $o
)
(: 24 :)
return $organization[count(./members/id(@country))=min($organization/count(members/id(@country)))]
```

``` shell
saxonXQ ex01_06.xq
```

### Exercise 1.7 (Web Data Extraction: Germany-View)

[DTD](https://github.com/sampig/XMLDB/blob/master/Practical_XML/Exercise01/ex01_07.dtd)

[Extraction from Germany](https://github.com/sampig/XMLDB/blob/master/Practical_XML/Exercise01/ex01_07_de.xq)

[Extraction from France](https://github.com/sampig/XMLDB/blob/master/Practical_XML/Exercise01/ex01_07_fr.xq)

``` shell
saxonXQ ex01_07_de.xq > de.xml
xmllint -loaddtd -valid -noout de.xml

saxonXQ ex01_07_fr.xq > fr.xml
xmllint -loaddtd -valid -noout fr.xml
```

### Exercise 1.8 (Web Data Extraction: Wikipedia)

[Module functions](https://github.com/sampig/XMLDB/blob/master/Practical_XML/Exercise01/ex01_08.xqm)

[Get info by input](https://github.com/sampig/XMLDB/blob/master/Practical_XML/Exercise01/ex01_08a.xq)

[Get info for all](https://github.com/sampig/XMLDB/blob/master/Practical_XML/Exercise01/ex01_08b.xq)

``` shell
java -cp saxon-path/saxon9.jar net.sf.saxon.Query ex01_08a.xq word="Mont Blanc"
saxonXQ ex01_08a.xq word="Mont Blanc"
saxonXQ ex01_08a.xq word="Mont Blanc1"
saxonXQ ex01_08a.xq word="Himalaya"

saxonXQ ex01_08b.xq
```

### Exercise 1.9 (User-defined Function: Functional Programming - Fibonacci)

a) Recursive method:

``` xquery
declare variable $x external;
declare function local:fib($n as xs:integer) as xs:integer {
if ($n<=1) then $n
else (local:fib($n - 1) + local:fib($n - 2))
};
<result>
{local:fib($x)}
</result>
```

b) Complexity: O(2^n)

c) Linear way:

``` xquery
declare variable $x external;
declare variable $end := 1;
declare function local:fiblin($a as xs:integer, $b as xs:integer, $n as xs:integer) as xs:integer {
if ($n = $end) then $b
else local:fiblin($b, $a + $b, $n - 1)
};
<result>
{ local:fiblin(0, 1, $x) }
</result>
```

The linear way is much faster than the recursive way.

``` shell
saxonXQ ex01_09a.xq x=5
saxonXQ ex01_09c.xq x=5
```

### Exercise 1.10 (User-defined function: Recursive Network Length)

[Solution Link](https://github.com/sampig/XMLDB/blob/master/Practical_XML/Exercise01/ex01_10.xq)

``` shell
saxonXQ ex01_10.xq rname="Aller"
saxonXQ ex01_10.xq rname="Ischim"
saxonXQ ex01_10.xq rname="Ob"
saxonXQ ex01_10.xq rname="Vuoksi"
```

