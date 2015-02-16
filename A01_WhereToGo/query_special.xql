xquery version "1.0";

declare namespace z = "http://tuc.de/zhuchenfeng";

let $date := xs:date("2015-04-15")
let $city := xs:string("Goslar")
let $doc := doc("myworld.xml")
let $session := $doc//z:Production/z:Session[z:Startdate=$date][z:VenueName/@venueid=$doc//z:City[z:CityName/text()=$city]/z:Venue/@venueid]
let $head := "<!DOCTYPE RESULT SYSTEM 'wheretogo.dtd'>"

return
<RESULT xmlns="">
<DATE> {$date} </DATE>
<CITY> {$city} </CITY>
   {
    for $s in $session
    let $p := $s/parent::z:Production
    let $v := $doc//z:City/z:Venue[@venueid=$s/z:VenueName/@venueid]
    return
        <SESSION>
            <STARTTIME>{$s/z:Starttime/text()}</STARTTIME>
            <VENUENAME>{$s/z:VenueName/text()}</VENUENAME>
            <TITLE>{$p/z:Title/text()}</TITLE>
            <WEBSITE>
            {if ($p/z:Website/z:Official) then <OFFICIAL>{$p/z:Website/z:Official/text()}</OFFICIAL> else ""}
            {if ($p/z:Website/z:Trailer) then <TRAILER>{$p/z:Website/z:Trailer/text()}</TRAILER> else ""}
            </WEBSITE>
            <ADDRESS>{$v/z:VenueAddress/text()}</ADDRESS>
        </SESSION>
   }
</RESULT>


(:
declare default element namespace "http://tuc.de/zhuchenfeng";
let $d := doc("myworld.xml")//Agency//Production
let $c := <RESULT> {$d} </RESULT>
return $c
:)
