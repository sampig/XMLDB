<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" indent="yes"/>

<xsl:template match="waters">
<html>
<head><title>Exercise 2.1b: River Length</title></head>
<body>
<h1>Exercise 2.1b: River Length</h1>
    <table border="1">
    <xsl:apply-templates select="sea"/>
    </table>
</body>
</html>
</xsl:template>

<xsl:template match="sea">
<tr>
<td rowspan="{count(./river)+1}">
<xsl:value-of select="name/text()"/>
</td>
<!-- <td></td><td></td> -->
</tr>
<xsl:apply-templates select="river"/>
</xsl:template>

<xsl:template match="river">
<tr>
    <td><xsl:value-of select="name/text()"/></td>
    <td><xsl:value-of select="sum(.//length/number())"/></td>
</tr>
</xsl:template>

</xsl:stylesheet>



