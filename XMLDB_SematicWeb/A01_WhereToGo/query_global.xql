xquery version "1.0";

declare namespace z = "http://tuc.de/zhuchenfeng";

let $keyword := "Movie"
let $doc := doc("myworld.xml")
let $prodt := $doc//z:Production//descendant-or-self::node()[fn:matches(string(),$keyword)]//ancestor::z:Production
let $city := $doc//z:City

return
<RESULT xmlns="" keyword="{$keyword}" type="global">
   {
    for $p in $prodt
    return
        <PRODUCTION>
            <TITLE>{$p/z:Title/text()}</TITLE>
            <ProductionKind>{$p/z:ProductionKind/z:KindName/text()}</ProductionKind>
            {
            for $s in $p/z:Session
            return
                <SESSION>
                    <VenueName>{$city/z:Venue[@venueid=$s/z:Venue/@venueid]/z:VenueName/text()}</VenueName>
                    <Startdate>{$s/z:Startdate/text()}</Startdate>
                    <Starttime>{$s/z:Starttime/text()}</Starttime>
                </SESSION>
            }
        </PRODUCTION>
   }
</RESULT>
