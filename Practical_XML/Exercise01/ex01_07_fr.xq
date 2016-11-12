declare namespace x="http://www.w3.org/1999/xhtml";
declare namespace saxon="http://saxon.sf.net/";
declare namespace functx = "http://www.functx.com";

declare option saxon:output "indent=yes";
declare option saxon:output "doctype-system=ex01_07.dtd";

declare function functx:is-a-number ( $value as xs:anyAtomicType? )  as xs:boolean {
   string(number($value)) != 'NaN'
};

let $main := doc('http://www.geohive.com/cntry/france.aspx')//x:div["wrapper"]

let $countryname := $main//x:div["generalinfo"]/x:h1/text()
let $capname := $main//x:div["generalinfo"]/x:div["infoblock1"]/x:table//x:td[text()="Capital"]/parent::node()/x:td[2]/text()
let $totalpopulation := format-number(sum((
    for $p in $main//x:div["adminunits"]//x:table[@summary="administrative units"]/x:tbody/x:tr[@class="level1"]
    let $pop := replace($p/x:td[last()]/text(),",","")
    return
        if (functx:is-a-number($pop))
        then number($pop)
        else number(0)
        (: I tried empty or normalize-space or zero-length string. none of them works. :)
)), ",###")
let $provlink := (
    for $p in $main//x:div["adminunits"]//x:table[@summary="administrative units"]/x:tbody/x:tr[@class="level1"]
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
        for $province in $provtable/x:tbody/x:tr[@class="level1"]
        let $ccode := $province/x:td[@class="ccode"]/string(.)
        let $provcap := $province/x:td[3]/text()
        where $provcap != ""
        order by $ccode
        return
        element province {
            (: attribute id { $ccode }, :)
            attribute capital { concat(replace($countryname," ",""), "-", replace(translate($provcap,"()","")," ","")) },
            element name { $province/x:td[2]/text() },
            element area { $province/x:td[4]/text() },
            element population { $province/x:td[last()]/text() }
        }
    },
    element cities {
        for $city in ($citytable/x:tbody/x:tr[@class="level1"], $provtable/x:tbody/x:tr[not(x:td[3]/text()=$citytable/x:tbody/x:tr[@class="level1"]/x:td[1]/text())][@class="level1"])
        let $cityname := $city/x:td[1]/text()
        let $cityid := concat(replace($countryname," ",""), "-", replace(translate($cityname,"()","")," ",""))
        return
        if ($city/parent::node()/parent::node()/@summary="administrative units") then
        element city {
            attribute id { concat(replace($countryname," ",""), "-", replace(translate($city/x:td[3]/text(),"()","")," ","")) },
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


