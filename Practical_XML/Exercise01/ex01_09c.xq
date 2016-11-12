declare namespace saxon="http://saxon.sf.net/";

declare option saxon:output "indent=yes";

declare variable $x external;
declare variable $end := 1;

declare function local:fiblin($a as xs:integer, $b as xs:integer, $n as xs:integer) as xs:integer {
if ($n = $end) then $b
else local:fiblin($b, $a + $b, $n - 1)
};

<result>
{ local:fiblin(0, 1, $x) }
</result>
