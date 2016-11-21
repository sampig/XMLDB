<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" indent="yes"/>

<xsl:template match="mondial">
<html>
<head><title>Exercise 2.1c: River Length</title></head>
<body>
<h1>Exercise 2.1c: River Length</h1>
    <table border="1">
    <xsl:apply-templates select="sea"/>
    </table>
</body>
</html>
</xsl:template>

<xsl:template match="sea">
<tr>
<td colspan="2">
<xsl:value-of select="name/text()"/>
</td>
</tr>
    <xsl:apply-templates select="idref(@id)/parent::to/parent::river">
        <xsl:sort select=".//estuary//elevation" data-type="number" order="descending"/>
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="river">
<tr>
<td><xsl:value-of select="name/text()"/></td>
<td>
    <xsl:variable name="rlen">
    <xsl:call-template name="riverlength">
        <xsl:with-param name="nodeid" select="string(@id)"/>
    </xsl:call-template>
    </xsl:variable>
    <xsl:value-of select="sum($rlen/length/number())"/>
</td>
</tr>
</xsl:template>

<xsl:template name="riverlength">
    <xsl:param name="length" select="0"/>
    <xsl:param name="nodeid"/>
    <xsl:copy-of select="//id($nodeid)/length"/>
    <xsl:if test="//idref($nodeid)/parent::to/parent::river">
        <xsl:call-template name="riverlength">
            <xsl:with-param name="nodeid" select="//idref($nodeid)/parent::to/parent::river/string(@id)"/>
        </xsl:call-template>
    </xsl:if>
</xsl:template>

<!--
<xsl:function name="zzxml:riverlen" as="xs:double">
    <xsl:param name="node" as="element()"/>
    <xsl:sequence select="length/number()+sum(zzxml:riverlen(idref(@id)/parent::to/parent::river))"/>
</xsl:function>
-->

</xsl:stylesheet>

