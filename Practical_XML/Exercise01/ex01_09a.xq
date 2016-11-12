declare namespace saxon="http://saxon.sf.net/";

declare option saxon:output "indent=yes";

declare variable $x external;

declare function local:fib($n as xs:integer) as xs:integer {
if ($n<=1) then $n
else (local:fib($n - 1) + local:fib($n - 2))
};

<result>
{local:fib($x)}
</result>
