module namespace zzxml="http://www.zhuzhu.org/xml";

declare function zzxml:data_extraction($word as xs:string) as element(mountain) {
    let $url := concat("https://en.wikipedia.org/wiki/", replace($word, " ", "_"))
    let $webpage := doc($url)

    (: let $notfound := $webpage//div[contains(string(.), "Wikipedia does not have an article with this exact name.")] :)

    (: return if (not(empty($notfound))) :)
    (: then <mountain/> :)
    (: else :)

    let $infotable := $webpage//table[@class="infobox vcard"]
    let $name := $webpage//h1["firstHeading"]
    return
    <mountain>
    {
        element name { $name },
        element location {
            $infotable//th[text()="Location"]/parent::node()/td/child::node()/replace(normalize-space(string(.)),"\n",";")
        },
        element highestpoint {
            element elevation {
                $infotable//th[string()="Elevation"]/parent::node()/td/child::node()/string(.)
            },
            element coordinates {
                $infotable//th[string()="Coordinates"]/parent::node()/td/span[1]/child::node()/string(.)
            }
        }
    }
    </mountain>
};

