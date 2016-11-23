<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema">
<xsl:output method="html" indent="yes"/>

<xsl:variable name="mondialpath">/afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml</xsl:variable>

<xsl:template match="org">
    <html>
    <body>
    <xsl:apply-templates select="organization"/>
    </body>
    </html>
</xsl:template>

<xsl:template match="organization">
<h1><xsl:value-of select="name/text()"/></h1>
<p>name: <xsl:value-of select="name/text()"/></p>
<p>abbreviation: <xsl:value-of select="abbrev/text()"/></p>
<xsl:if test="established">
<p><xsl:value-of select="replace(replace(string(current-date() - xs:date(established/text())),'P',''),'D','')"/> days, established at <xsl:value-of select="established/text()"/>.</p>
<p>members:
<!--
    <xsl:variable name="countrynode">
        <xsl:copy-of select="doc($mondialpath)//id(members/@country)"/>
    </xsl:variable>
    <xsl:value-of select="$countrynode/country/name[1]/text()"/>,
-->
    <table>
    <xsl:for-each select="members">
    <xsl:call-template name="member">
        <xsl:with-param name="carcode" select="@country"/>
        <xsl:with-param name="type" select="@type"/>
    </xsl:call-template>
    </xsl:for-each>
    </table>
</p>
<p>headquarter:
    <xsl:call-template name="headq">
        <xsl:with-param name="hid" select="@headq"/>
    </xsl:call-template>
</p>
<!--
<xsl:value-of select="current-date() - xs:date(established/text())"/>
-->
</xsl:if>
</xsl:template>

<xsl:template name="member">
    <xsl:param name="carcode"/>
    <xsl:param name="type"/>
    <xsl:variable name="hnode">
        <xsl:copy-of select="doc($mondialpath)//id($carcode)"/>
    </xsl:variable>
    <tr><th><xsl:value-of select="$type"/>: <xsl:value-of select="$carcode"/></th>
    <xsl:for-each select="$hnode/country">
        <!-- <xsl:variable name="country" select="."/> -->
        <td><xsl:value-of select="string(@car_code)"/>: <xsl:value-of select="name/text()"/></td>
    </xsl:for-each>
    </tr>
</xsl:template>

<xsl:template name="country">
    
</xsl:template>

<xsl:template name="headq">
    <xsl:param name="hid"/>
    <xsl:variable name="hnode">
        <xsl:copy-of select="doc($mondialpath)/id($hid)"/>
    </xsl:variable>
    <h2><xsl:value-of select="$hnode/city/name[1]/text()"/></h2>
</xsl:template>

</xsl:stylesheet>

