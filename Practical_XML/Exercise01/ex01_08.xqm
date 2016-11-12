module namespace zzxml="http://www.zhuzhu.org/xml";

declare function zzxml:removereferenceelements($elements as element()*) as element()* {
    for $e in $elements
    return
    element
        {node-name($e)}
        {$e/@*,
         $e/node()[not(@class="reference")]
        }
};

declare function zzxml:location($elements as element()*) as xs:string {
    string-join(
    for $c in $elements/child::node()
    return
        if (string(node-name($c))="br") then ";"
        else normalize-space(string($c))
    )
};

declare function zzxml:data_extraction($word as xs:string) as element(mountain) {
    let $url := concat("https://en.wikipedia.org/wiki/", replace($word, " ", "_"))
    return
    try {
    let $webpage := doc($url)

    (: Handle multiple reference. :)
    let $mayrefer := $webpage//div[contains(string(.), "may refer to:")]
    return
    if (not(empty($mayrefer))) then <mountain>"{$word}" may refer to:</mountain>
    else

    let $infotable := $webpage//table[@class="infobox vcard"]
    let $name := $webpage//h1["firstHeading"]/string(.)
    let $coo := $infotable//th[string()="Coordinates"]/parent::node()/td/span[1]
    return
    <mountain>
    {
        element name { $name },
        element location {
            replace(normalize-space(zzxml:location($infotable//th[text()="Location"]/parent::node()/td)),"\n","")
        },
        element highestpoint {
            element elevation {
                zzxml:removereferenceelements($infotable//th[string()="Elevation"]/parent::node()/td)/child::node()/string(.)
            },
            element coordinates {
                element geo-dms { $coo//span[@class="geo-dms"]/string(.) },
                element geo-dec { $coo//span[@class="geo-dec"]/string(.) },
                element geo { $coo//span[@class="geo"]/string(.) }
            }
        }
    }
    </mountain>
    } catch * {
        <mountain>"{$word}" cannot be found in Wikipedia.</mountain>
        (: <mountain>Caught error {$err:code}: {$err:description}</mountain> :)
    }

};

