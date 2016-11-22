<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" indent="yes"/>

<xsl:variable name="pwd">/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/ex02/</xsl:variable>

<xsl:template match="mondial">
<xsl:apply-templates select="country[@car_code='D' or @car_code='I' or contains(@car_code,'L')]"/>
</xsl:template>

<xsl:template match="country">
<!-- <f><xsl:value-of select="concat('/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/ex02/',string(@car_code),'/index.html')"/></f> -->
<!-- for each country, a directory named with car code with an index.html inside. -->
<xsl:result-document href="{concat($pwd,string(@car_code),'/index.html')}">
<html>
<head></head>
<body>
<xsl:variable name="countryname">
    <xsl:value-of select="name[1]/text()"/>
</xsl:variable>
<h1><xsl:value-of select="$countryname"/></h1>
<!-- index.html contains a link to Wikipedia and some info from wikipedia. -->
<p>
    <a href="{concat('https://en.wikipedia.org/wiki/',replace($countryname,' ','_'))}">Link to Wikipedia</a>
</p>
<p>
<xsl:call-template name="capital">
    <xsl:with-param name="capid" select="string(@capital)"/>
</xsl:call-template>
</p>
<xsl:variable name="infobox" select="doc(concat('https://en.wikipedia.org/wiki/',$countryname))//table[contains(@class,'infobox') and contains(@class,'vcard')]"/>
    <table>
    <tr><td>Info from Wikipedia</td></tr>
    <tr><td>
    <xsl:copy-of select="$infobox"/>
    </td></tr>
    </table>
<xsl:apply-templates select="province">
    <xsl:with-param name="cid" select="string(@car_code)" />
    <xsl:with-param name="cname" select="$countryname"/>
</xsl:apply-templates>
<xsl:apply-templates select="city">
    <xsl:with-param name="cid" select="string(@car_code)" />
    <xsl:with-param name="cname" select="$countryname"/>
</xsl:apply-templates>
</body>
</html>
</xsl:result-document>
</xsl:template>

<xsl:template match="province">
    <xsl:param name="cid"/>
    <xsl:param name="cname"/>
    <xsl:result-document href="{concat($pwd,$cid,'/',replace(string(@id),' ',''),'/index.html')}">
    <html>
        <head><title></title></head>
        <body>
        <h1><xsl:value-of select="$cname"/> - <xsl:value-of select="name/text()"/></h1>
        </body>
    </html>
    </xsl:result-document>
    <xsl:apply-templates select="city">
        <xsl:with-param name="cid" select="$cid"/>
        <xsl:with-param name="cname" select="$cname"/>
        <xsl:with-param name="pid" select="string(@id)"/>
        <xsl:with-param name="pname" select="name[1]/text()"/>
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="city">
    <xsl:param name="cid"/>
    <xsl:param name="cname"/>
    <xsl:variable name="capid">
        <xsl:value-of select="//country[@car_code=$cid]/string(@capital)"/>
    </xsl:variable>
    <xsl:variable name="cityname">
        <xsl:value-of select="name[1]/text()"/>
    </xsl:variable>
    <xsl:result-document href="{concat($pwd,$cid,'/',replace(string(@id),' ',''),'/index.html')}">
    <html>
        <head><title></title></head>
        <body>
        <h1><xsl:value-of select="$cname"/> - <xsl:value-of select="$cityname"/></h1>
        <xsl:if test="$capid=string(@id)">
           <p><xsl:value-of select="$cityname"/> is located in <xsl:value-of select="//country[@car_code=$cid]/name[1]/text()"/>.</p>
        </xsl:if>
        </body>
    </html>
    </xsl:result-document>
</xsl:template>

<xsl:template match="province/city">
    <xsl:param name="cid"/>
    <xsl:param name="cname"/>
    <xsl:param name="pid"/>
    <xsl:param name="pname"/>
    <xsl:variable name="capid">
        <xsl:value-of select="//country[@car_code=$cid]/string(@capital)"/>
    </xsl:variable>
    <xsl:variable name="cityname">
        <xsl:value-of select="name[1]/text()"/>
    </xsl:variable>
    <xsl:result-document href="{concat($pwd,$cid,'/',$pid,'/',replace(string(@id),' ',''),'/index.html')}">
    <html>
        <head><title></title></head>
        <body>
        <h1><xsl:value-of select="$cname"/> - <xsl:value-of select="$cityname"/></h1>
        <xsl:if test="$capid=string(@id)">
           <p><xsl:value-of select="$cityname"/> is located in the <xsl:value-of select="parent::province/name[1]/text()"/> province of <xsl:value-of select="//country[@car_code=$cid]/name[1]/text()"/>.</p>
        </xsl:if>
        </body>
    </html>
    </xsl:result-document>
</xsl:template>

<xsl:template name="capital">
    <xsl:param name="capid"/>
    <xsl:variable name="capinfo">
       <xsl:copy-of select="//id($capid)"/>
    </xsl:variable>
    <xsl:choose>
        <xsl:when test="//id($capid)/parent::province">
            <xsl:variable name="capurl">
                <xsl:value-of select="concat(//id($capid)/parent::province/string(@id),'/',replace($capid,' ',''),'/index.html')"/>
            </xsl:variable>
            <a href="{$capurl}">Link to capital: <xsl:value-of select="$capinfo/city/name[1]/text()"/></a> (in province)
<!--
            <xsl:result-document href="{$capurl}">
                <html><body>
                    <xsl:value-of select="$capinfo/name"/> is located in the <xsl:value-of select="//id($capid)/parent::province/name/text()"/> province of <xsl:value-of select="//id($capid)/parent::province/parent::country/name/text()"/>
                </body></html>
            </xsl:result-document>
-->
        </xsl:when>
        <xsl:when test="//id($capid)/parent::country">
            <xsl:variable name="capurl">
                <xsl:value-of select="concat(replace($capid,' ',''),'/index.html')"/>
            </xsl:variable>
            <a href="{$capurl}">Link to capital: <xsl:value-of select="$capinfo/city/name[1]/text()"/></a> (not in province)
<!--
            <xsl:result-document href="{$capurl}">
                <html><body>
                    <xsl:value-of select="$capinfo/name"/> is located in <xsl:value-of select="//id($capid)/parent::country/name/text()"/>
                </body></html>
            </xsl:result-document>
-->
        </xsl:when>
    </xsl:choose>
</xsl:template>

</xsl:stylesheet>

