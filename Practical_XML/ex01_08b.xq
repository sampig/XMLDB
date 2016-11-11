import module namespace zzxml="http://www.zhuzhu.org/xml" at "./ex01_08.xqm";

declare namespace saxon="http://saxon.sf.net/";

declare option saxon:output "indent=yes";

let $doc := doc('/afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml')

return
element mountains {
    for $mountain in $doc//mountain[id(@country)/@car_code="D"]
    let $mname := $mountain/name/text()
    return
    zzxml:data_extraction($mname)
}

