<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema">
<xsl:output method="html" indent="yes"/>

<xsl:param name="personname"/>
<xsl:param name="yearmonth"/>

<xsl:variable name="vyear"><xsl:value-of select="substring($yearmonth,1,4)"/></xsl:variable>
<xsl:variable name="vmonth"><xsl:value-of select="substring($yearmonth,5,2)"/></xsl:variable>

<xsl:template match="schedule[@name]">
    <html>
        <head><title>The Schedule of <xsl:value-of select="@name"/></title></head>
        <body>
            <p>The Schedule of <xsl:value-of select="@name"/> in <xsl:value-of select="$yearmonth"/>: </p>
            <table border="1">
                <tr><th>Date</th><th>Start time</th><th>Name</th><th>Location</th></tr>
                <xsl:apply-templates select="./year[@n=$vyear]/month[@n=number($vmonth)]/day"/>
            </table>
        </body>
    </html>
</xsl:template>

<xsl:template match="day">
    <tr><td rowspan="{count(./entry)+1}"><xsl:value-of select="concat($vyear, '-', $vmonth, '-', format-number(@n, '00'))"/></td></tr>
    <xsl:apply-templates select="entry"/>
</xsl:template>

<xsl:template match="entry">
    <tr>
        <td><xsl:value-of select="@starttime"/></td>
        <td><xsl:value-of select="name/text()"/></td>
        <td><xsl:value-of select="location"/></td>
    </tr>
</xsl:template>

</xsl:stylesheet>
