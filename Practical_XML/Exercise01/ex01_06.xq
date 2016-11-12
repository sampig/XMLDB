declare namespace saxon="http://saxon.sf.net/";

declare option saxon:output "indent=yes";

let $doc := doc('/afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml')

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

