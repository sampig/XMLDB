declare namespace x="http://www.w3.org/1999/xhtml";
declare namespace saxon="http://saxon.sf.net/";

declare option saxon:output "indent=yes";
declare option saxon:output "doctype-system=ex01_07.dtd";

let $main := doc('http://www.geohive.com/cntry/germany.aspx')//x:div["wrapper"]

let $countryname := $main//x:div["generalinfo"]/x:h1/text()
let $capname := $main//x:div["generalinfo"]/x:div["infoblock1"]/x:table//x:td[text()="Capital"]/parent::node()/x:td[2]/text()
let $totalpopulation := format-number(sum((
    for $p in $main//x:div["adminunits"]//x:table[@summary="administrative units"]/x:tbody/x:tr[@class="level2"]
    let $pop := $p/x:td[last()]/text()
    return
        if (empty($pop))
        then 0
        else number(replace($pop,",",""))
)), ",###")
let $provlink := (
    for $p in $main//x:div["adminunits"]//x:table[@summary="administrative units"]/x:tbody/x:tr[@class="level2"]
    let $link := $p/x:td[1]/x:a
    where $link != ""
    return $link/@href/string(.)
)

let $provdiv := $main//x:div["adminunits"]
let $provtable := $provdiv//x:table[@summary="administrative units"]

let $citydiv := $main//x:div["cities"]
let $citytable := $citydiv//x:table[@summary="cities"]

return
element country {
    attribute capital { concat(replace($countryname," ",""), "-", replace($capname," ","")) },
    element name { $countryname },
    element population { $totalpopulation },
    element provinces {
        for $province in $provtable/x:tbody/x:tr[@class="level2"]
        let $ccode := replace(replace($province/x:td[@class="ccode"]/string(.), "»", "")," ","")
        let $provcap := $province/x:td[3]/text()
        where $provcap != ""
        order by $ccode
        return
        element province {
            (: attribute id { $ccode }, :)
            attribute capital { concat(replace($countryname," ",""), "-", replace($provcap," ","")) },
            element name { $province/x:td[2]/text() },
            element area { $province/x:td[4]/text() },
            element population { $province/x:td[last()]/text() }
        }
    },
    element cities {
        (: for $city in $citytable/x:tbody/x:tr[@class="level1"] :)
        for $city in ($citytable/x:tbody/x:tr[@class="level1"], $provtable/x:tbody/x:tr[not(x:td[3]/text()=$citytable/x:tbody/x:tr[@class="level1"]/x:td[1]/text())][@class="level2"])
        (: add the cities which are in the province table but not in the city table.:)
        let $cityname := $city/x:td[1]/text()
        let $cityid := concat(replace($countryname," ",""), "-", replace($cityname," ",""))
        return
        if ($city/parent::node()/parent::node()/@summary="administrative units") then
        element city {
            attribute id { concat(replace($countryname," ",""), "-", replace($city/x:td[3]/text()," ","")) },
            element name { $city/x:td[3]/text() },
            element population {}
        }
        else
        element city {
            attribute id { $cityid },
            element name { $cityname },
            element population { $city/x:td[last()]/text() }
        }
    }
}


(: <!ELEMENT country (name,population,provinces,cities)> :)
(: <!ELEMENT name (#PCDATA)> :)
(: <!ELEMENT population (#PCDATA)> :)
(: <!ELEMENT provinces (province*)> :)
(: <!ELEMENT cities (city*)> :)
(: <!ELEMENT province (name,area,population)> :)
(: <!ELEMENT area (#PCDATA)> :)
(: <!ELEMENT city (name,population)> :)
(:  :)
(: <!ATTLIST country capital IDREF #IMPLIED> :)
(: <!ATTLIST province capital IDREF #IMPLIED> :)
(: <!ATTLIST city id ID #REQUIRED> :)

(: declare boundary-space preserve; for format but doesn't work :)

