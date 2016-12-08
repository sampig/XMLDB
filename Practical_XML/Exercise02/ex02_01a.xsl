<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes"/>

<xsl:template match="mondial">
<waters>
<xsl:apply-templates select="sea"/>
</waters>
</xsl:template>

<xsl:template match="sea">
<sea>
    <name><xsl:value-of select="name"/></name>
    <xsl:apply-templates select="idref(@id)/parent::to/parent::node()">
        <xsl:with-param name="pid" select="string(@id)"/>
        <xsl:sort select=".//estuary//elevation" data-type="number" order="descending"/> <!-- unnecessary. it must be zero for the river to sea. -->
    </xsl:apply-templates> 
</sea>
</xsl:template>

<xsl:template match="river|lake">
<xsl:copy>
    <name><xsl:value-of select="name"/></name>
    <xsl:if test="length">
    <length><xsl:value-of select="length"/></length>
    </xsl:if>
    <xsl:apply-templates select="idref(@id)/parent::to/parent::node()">
        <xsl:with-param name="pid" select="string(@id)"/>
        <xsl:sort select=".//estuary//elevation|elevation" data-type="number" order="descending"/>
    </xsl:apply-templates>
</xsl:copy>
</xsl:template>

<!--
<xsl:template match="river">
<xsl:param name="pid"/>
<river>
    <name><xsl:value-of select="name"/></name>
    <length><xsl:value-of select="length"/></length>
    <xsl:apply-templates select="idref(@id)/parent::to/parent::river">
        <xsl:with-param name="pid" select="string(@id)"/>
        <xsl:sort select=".//estuary//elevation" data-type="number" order="descending"/>
    </xsl:apply-templates>
    <xsl:apply-templates select="idref(@id)/parent::to/parent::lake">
        <xsl:with-param name="pid" select="string(@id)"/>
        <xsl:sort select=".//estuary//elevation" data-type="number" order="descending"/>
    </xsl:apply-templates>
</river>
</xsl:template>

<xsl:template match="lake">
<xsl:param name="pid"/>
<lake>
    <name><xsl:value-of select="name"/></name>
    <xsl:apply-templates select="idref(@id)/parent::to/parent::river">
    </xsl:apply-templates>
    <xsl:apply-templates select="idref(@id)/parent::to/parent::lake">
    </xsl:apply-templates>
</lake>
</xsl:template>
-->

</xsl:stylesheet>

