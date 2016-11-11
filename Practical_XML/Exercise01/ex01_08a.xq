import module namespace zzxml="http://www.zhuzhu.org/xml" at "./ex01_08a.xq";

declare namespace saxon="http://saxon.sf.net/";

declare option saxon:output "indent=yes";

declare variable $word external;

zzxml:data_extraction($word)

