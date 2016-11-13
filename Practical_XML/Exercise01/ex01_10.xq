declare namespace saxon="http://saxon.sf.net/";

declare option saxon:output "indent=yes";

declare variable $rname external;

declare variable $doc := doc('/afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml');

declare function local:wholeriver($name as xs:string) as element()* {
    let $inputriver := $doc//river[name=$name or @id=$name]
    let $riverid := $inputriver/@id/string(.)
    let $riverlength := $inputriver/length/number(.)
    let $downstream := local:downstream($riverid, $riverid)
    let $upstream := local:upstream($riverid, $riverid)
    let $totallength := $riverlength + local:uplength(element upstream {$upstream}) + local:downlength($downstream)
    return
    if (empty($inputriver)) then element result { concat("River (", $name, ") cannot be found.") }
    else
    element riversystem {
        element input {
            attribute id { $inputriver/@id/string(.) },
            attribute length { $riverlength },
            $name
        },
        element totallength { $totallength },
(:
        element totalnumber {
            element upnumber {},
            element downnumber {}
        },
:)
        element upstream {
            $upstream
        },
        element downstream {
            $downstream
        }
    }
};

declare function local:uplength($ups as element()*) as xs:double {
    if (empty($ups)) then 0
    else
    sum(
        for $up in $ups/up
        return
        if (empty($up) or string(node-name($up))="end") then 0
        else
        $up/@length/number(.) + local:uplength($up)
    )
};

declare function local:downlength($downs as element()*) as xs:double {
    if (empty($downs)) then 0
    else
    sum(
        for $down in $downs
        return
        if (empty($down) or string(node-name($down))="end") then 0
        else
        $down/number(@length) + local:downlength($down/down)
    )
};

declare function local:downstream($id as xs:string, $uid as xs:string) as element()* {
    let $river := $doc//node()[@id=$id]
    let $tos := $river/to[@watertype != "sea"]
    return
        for $to in $tos
        let $toid := $to/@water/string(.)
        let $tonode := $doc//node()[@id=$toid]
        return
        if (empty($to)) then ()
        else if ($uid=$toid) then ()
        else
        element down {
            attribute id { $toid },
            attribute name { $tonode/name/string(.) },
            attribute type { $to/@watertype/string(.) (:node-name($toriver):) },
            attribute length {
                let $l := $tonode/length/number(.)
                return
                if (empty($tonode/length) or string($l) = "NaN") then 0
                else $l
            },
            local:downstream($toid, $id)
        }
};

declare function local:upstream($id as xs:string, $did as xs:string) as element()* {
    let $river := $doc//river[@id=$id]
    let $froms := $doc//idref($id)/parent::to/parent::node()
    return
        for $from in $froms
        let $fromid := $from/@id/string(.)
        return
        if (empty($from)) then ()
        else if ($did=$fromid) then ()
        else
        element up {
            attribute id { $fromid },
            attribute name { $from/name/string(.) },
            attribute type { node-name($from) },
            attribute length {
                let $l := $from/length/number(.)
                return
                if (empty($from/length) or string($l) = "NaN") then 0
                else $l
            },
            local:upstream($fromid, $id)
        }
};

local:wholeriver($rname)

