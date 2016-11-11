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
    for $p in $main//x:div["adminunits"]//x:table[@summary="administrative units"]/x:tbody/x:tr[@class="level2"]
    let $pop := replace($p/x:td[last()]/text(),",","")
    return
        if (functx:is-a-number($pop))
        then number($pop)
        else number(0)
        (: I tried empty or normalize-space or zero-length string. none of them works. :)
)), ",000")
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
    attribute capital { $capname },
    element name { $countryname },
    element population { $totalpopulation },
    element provinces {
        for $province in $provtable/x:tbody/x:tr[@class="level2"]
        let $ccode := $province/x:td[@class="ccode"]/string(.)
        let $provcap := $province/x:td[3]/text()
        where $provcap != ""
        order by $ccode
        return
        element province {
            attribute id { $ccode },
            attribute capital { $provcap },
            element name { $province/x:td[2]/text() },
            element area { $province/x:td[4]/text() },
            element population { $province/x:td[last()]/text() }
        }
    },
    element cities {
        for $city in $citytable/x:tbody/x:tr[@class="level1"]
        let $cityname := $city/x:td[1]/text()
        return
        element city {
            attribute id { $cityname }, (: I don't know what can be used as ID. complicated, we could use the provlink to get all the cities, but then we need to change the capital to ID instead of name :)
            element name { $cityname },
            element population { $city/x:td[last()]/text() }
        }
    }
}



