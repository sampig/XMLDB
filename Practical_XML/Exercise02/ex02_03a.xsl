<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes"/>

<xsl:variable name="pwd">/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/ex02/org/</xsl:variable>

<xsl:template match="mondial">
<xsl:apply-templates select="organization['org-EU']"/>
</xsl:template>

<xsl:template match="organization">
<xsl:result-document href="{concat($pwd,'/',abbrev[1]/text(),'.xml')}">
    <xsl:processing-instruction name="xml-stylesheet">
    <xsl:text>type="text/xsl" href="ex02_03b.xsl"</xsl:text>
<!--
        <xsl:text>type="text/xsl" href="/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/workspace/xml/ex02_03b.xsl"</xsl:text>
-->
    </xsl:processing-instruction>
    <org><xsl:copy-of select="."/></org>
</xsl:result-document>
</xsl:template>

</xsl:stylesheet>

