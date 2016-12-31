<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes"/>
<xsl:output doctype-system="mondial.dtd" />

<!-- User-defined variables -->
<xsl:variable name="provid">prov-United-States-6</xsl:variable>
<xsl:variable name="specialorg">
    <orgid></orgid>
    <orgid>org-G-5</orgid>
    <orgid>org-G-7</orgid>
</xsl:variable>
<!-- global variables -->
<xsl:variable name="calnew" select="document('./calexit_new.xml')//country"/>
<xsl:variable name="prov" select="//province[@id=$provid]"/>
<xsl:variable name="carcodeold" select="//province[@id=$provid]/parent::country/@car_code"/>
<xsl:variable name="carcodenew" select="$calnew/string(@car_code)"/>
<xsl:variable name="countryold" select="//province[@id=$provid]/parent::country/replace(name,' ','-')"/>
<xsl:variable name="countrynew" select="//province[@id=$provid]/replace(name,' ','-')"/>
<xsl:variable name="popprovince" select="//province[@id=$provid]/population[last()]"/>
<xsl:variable name="popcountry" select="//province[@id=$provid]/parent::country/population[@year=$popprovince/@year]"/>


<xsl:template match="mondial">
<mondial>
    <xsl:apply-templates select="country"/>
    <xsl:call-template name="newcountry"/>
    <xsl:copy-of select="//continent"/>
    <xsl:apply-templates select="organization"/>
    <xsl:apply-templates select="sea|river|lake|island|mountain|desert"/>
    <xsl:apply-templates select="airport"/>
</mondial>
</xsl:template>


<!-- ==================== -->
<!-- template for country -->
<xsl:template match="country">
    <xsl:choose>
        <!-- for the original country -->
        <xsl:when test="./province/@id=$provid">
            <xsl:apply-templates select="." mode="origcountry"/>
        </xsl:when>
        <!-- for other border countries -->
        <xsl:when test="./@car_code=$calnew/border/@country">
            <xsl:apply-templates select="." mode="bordercountry"/>
        </xsl:when>
        <!-- for other countries -->
        <xsl:otherwise>
            <xsl:copy-of select="."/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>
<!-- template for original country -->
<xsl:template match="country" mode="origcountry">
    <xsl:copy>
        <xsl:for-each select="@*">
            <xsl:choose>
                <xsl:when test="name()='area'"> <!-- Re-calculate area -->
                    <xsl:attribute name="area"><xsl:value-of select="format-number(number(.)-number($prov/area),'#')"/></xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
        <xsl:apply-templates select="./child::node()"/>
    </xsl:copy>
</xsl:template>
<xsl:template match="country/child::node()[not(name()=('population','ethnicgroup','religion','language','province','border')) and not(contains(name(),'gdp_'))]">
    <xsl:copy-of select="."/>
</xsl:template>
<!-- Re-calculate population -->
<xsl:template match="country/population">
    <xsl:if test="./@year=$prov/population/@year">
        <xsl:variable name="year"><xsl:value-of select="./@year"/></xsl:variable>
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:value-of select="format-number(number(.)-number($prov/population[@year=$year]),'#')"/>
        </xsl:copy>
    </xsl:if>
</xsl:template>
<!-- Re-calculate gdp -->
<xsl:template match="country/node()[contains(name(),'gdp_')]">
    <xsl:variable name="gdptotalold"><xsl:value-of select="number(./parent::node()/gdp_total)"/></xsl:variable>
    <xsl:variable name="gdptotalnew"><xsl:value-of select="number($gdptotalold)-number($calnew/gdp_total)"/></xsl:variable>
    <xsl:choose>
        <xsl:when test="name()='gdp_total'">
            <gdp_total><xsl:value-of select="format-number($gdptotalnew,'#')"/></gdp_total>
        </xsl:when>
        <xsl:otherwise>
            <xsl:variable name="nodename"><xsl:value-of select="./name()"/></xsl:variable>
            <xsl:variable name="newvalue"><xsl:value-of select="(number(.)*number($gdptotalold)-number($calnew/child::node()[name()=$nodename])*number($calnew/gdp_total)) div number($gdptotalnew)"/></xsl:variable>
            <xsl:copy><xsl:value-of select="format-number($newvalue,'0.##')"/></xsl:copy>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>
<!-- Re-calculate groups of population -->
<xsl:template match="country/ethnicgroup|country/religion|country/language">
    <xsl:variable name="typename"><xsl:value-of select="string(.)"/></xsl:variable>
    <xsl:variable name="provvalue">
        <xsl:choose>
            <xsl:when test="$calnew/child::node()[string(.)=$typename]">
                <xsl:value-of select="number($calnew/child::node()[string(.)=$typename]/@percentage)*number($popprovince)"/>
            </xsl:when>
            <xsl:otherwise>0</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="newpercent"><xsl:value-of select="(number(./@percentage)*number($popcountry)-number($provvalue)) div (number($popcountry)-number($popprovince))"/></xsl:variable>
    <xsl:copy>
        <xsl:attribute name="percentage"><xsl:value-of select="format-number($newpercent,'0.##')"/></xsl:attribute>
        <xsl:copy-of select="./child::node()"/>
    </xsl:copy>
</xsl:template>
<!-- Re-calculate border -->
<xsl:template match="country/border">
    <xsl:choose>
        <xsl:when test="@country=$calnew/border/@country">
            <xsl:variable name="mycarcode"><xsl:value-of select="./@country"/></xsl:variable>
            <border country="{@country}" length="{number(@length)-number($calnew/border[@country=$mycarcode]/@length)}"/>
        </xsl:when>
        <xsl:when test="not(following-sibling::border)">
            <xsl:copy-of select="."/>
            <border country="{$carcodenew}" length="{$calnew/border[@country=$carcodeold]/@length}"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:copy-of select="."/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>
<!-- remove the province -->
<xsl:template match="country/province">
    <xsl:if test="not(./@id=$provid)">
        <xsl:copy-of select="."/>
    </xsl:if>
</xsl:template>

<!-- template for border country -->
<xsl:template match="country" mode="bordercountry">
    <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:variable name="mycarcode"><xsl:value-of select="./@car_code"/></xsl:variable>
        <xsl:variable name="mynewborder">
            <border country="{$carcodenew}" length="{$calnew/border[@country=$mycarcode]/@length}"/>
        </xsl:variable>
        <xsl:for-each select="./child::node()">
            <xsl:choose>
                <xsl:when test="name()='border' and ./@country=$carcodeold">
                    <xsl:copy>
                        <xsl:attribute name="country"><xsl:value-of select="@country"/></xsl:attribute>
                        <xsl:attribute name="length"><xsl:value-of select="number(@length)-number($mynewborder/border/@length)"/></xsl:attribute>
                    </xsl:copy>
                    <xsl:copy-of select="$mynewborder"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:copy>
</xsl:template>
<!-- template for country -->


<!-- ======================== -->
<!-- template for new country -->
<xsl:template name="newcountry">
<country>
    <!-- attributes -->
    <xsl:attribute name="car_code"><xsl:value-of select="$carcodenew"/></xsl:attribute>
    <xsl:attribute name="area"><xsl:value-of select="$prov/area"/></xsl:attribute>
    <xsl:attribute name="capital"><xsl:value-of select="replace($prov/@capital, $countryold, $countrynew)"/></xsl:attribute>
    <xsl:attribute name="memberships">
        <xsl:variable name="newmembers">
            <xsl:call-template name="calnewmembers">
                <xsl:with-param name="input" select="//country[@car_code=$carcodeold]/string(@memberships)"/>
                <xsl:with-param name="count" select="1"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="normalize-space(replace($newmembers,'  ',' '))"/>
    </xsl:attribute>
    <!-- normal child nodes -->
    <name><xsl:value-of select="//province[@id=$provid]/string(name)"/></name>
    <xsl:copy-of select="//province[@id=$provid]/population"/>
    <xsl:variable name="encompassed">
        <xsl:if test="//country[@car_code=$carcodeold]/encompassed/string(@percentage)='100'">
            <xsl:copy-of select="//country[@car_code=$carcodeold]/encompassed"/>
        </xsl:if>
    </xsl:variable>
    <xsl:for-each select="$calnew/child::node()">
        <xsl:choose>
            <!-- add indep_date, government and encompassed. -->
            <xsl:when test="name()='unemployment'">
                <xsl:copy-of select="."/>
                <indep_date from="{$carcodeold}"><xsl:value-of select="format-dateTime(current-dateTime(), '[Y0001]-[M01]-[D01]')"/></indep_date>
                <government>constitution-based federal republic; strong democratic tradition</government>
                <xsl:copy-of select="$encompassed"/>
            </xsl:when>
            <xsl:when test="name()='ethnicgroup'">
                <xsl:if test="not(./parent::node()/unemployment)">
                    <indep_date from="{$carcodeold}"><xsl:value-of select="current-date()"/></indep_date>
                    <government>constitution-based federal republic; strong democratic tradition</government>
                    <xsl:if test="//country[@car_code=$carcodeold]/encompassed/string(@percentage)='100'">
                        <xsl:copy-of select="//country[@car_code=$carcodeold]/encompassed"/>
                    </xsl:if>
                </xsl:if>
                <xsl:copy-of select="."/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:for-each>
    <!-- new city -->
    <xsl:apply-templates select="//province[@id=$provid]/city" mode="newcountry"/>
</country>
</xsl:template>
<!-- add new city -->
<xsl:template match="province/city" mode="newcountry">
    <xsl:copy>
        <xsl:for-each select="@*">
            <xsl:choose>
                <xsl:when test="name()='id'">
                    <xsl:attribute name="id"><xsl:value-of select="replace(., $countryold, $countrynew)"/></xsl:attribute>
                </xsl:when>
                <xsl:when test="name()='country'">
                    <xsl:attribute name="country"><xsl:value-of select="$carcodenew"/></xsl:attribute>
                </xsl:when>
                <xsl:when test="name()='province'">
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
        <xsl:if test="string(@id)=$prov/@capital">
            <xsl:attribute name="is_country_cap">yes</xsl:attribute>
        </xsl:if>
        <xsl:for-each select="./child::node()">
            <xsl:copy-of select="."/>
        </xsl:for-each>
    </xsl:copy>
</xsl:template>
<!-- remove useless memberships in attribute -->
<xsl:template name="calnewmembers">
    <xsl:param name="input"/>
    <xsl:param name="count"/>
    <xsl:choose>
        <xsl:when test="not($count>count($specialorg/orgid)) and $specialorg/string(orgid[number($count)])=''">
            <xsl:call-template name="calnewmembers">
                <xsl:with-param name="input" select="$input"/>
                <xsl:with-param name="count" select="$count+1"/>
            </xsl:call-template>
        </xsl:when>
        <xsl:when test="not($count>count($specialorg/orgid)) and not($specialorg/string(orgid[number($count)])='')">
            <xsl:call-template name="calnewmembers">
                <xsl:with-param name="input" select="replace($input,$specialorg/orgid[$count],'')"/>
                <xsl:with-param name="count" select="$count+1"/>
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="$input"/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>
<!-- template for new country -->


<!-- ========================== -->
<!-- templates for organization -->
<xsl:template match="organization">
    <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:apply-templates select="./child::node()"/>
    </xsl:copy>
</xsl:template>
<!-- template for children of organization -->
<xsl:template match="organization/child::node()">
    <xsl:choose>
        <xsl:when test="./name()='members'">
            <xsl:if test="id(./@country)=//province[@id=$provid]/parent::country and not(./parent::organization/@id=$specialorg/orgid)">
                <members type="{./@type}" country="{concat(@country, ' ', $carcodenew)}"/>
            </xsl:if>
            <xsl:if test="not(id(./@country)=//province[@id=$provid]/parent::country) or (./parent::organization/@id=$specialorg/orgid)">
                <xsl:copy-of select="."/>
            </xsl:if>
        </xsl:when>
        <xsl:otherwise>
            <xsl:copy-of select="."/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>
<!-- templates for organization -->


<!-- =================== -->
<!-- template for nature -->
<xsl:template match="sea|river|lake|island|mountain|desert|sea//child::node()|river//child::node()|lake//child::node()|island//child::node()|mountain//child::node()|desert//child::node()">
    <xsl:choose>
        <xsl:when test="id(./@province)=//province[@id=$provid]">
            <xsl:choose>
                <xsl:when test="count(id(./@province))=1">
                    <xsl:copy>
                        <xsl:attribute name="country"><xsl:value-of select="$carcodenew"/></xsl:attribute>
                        <xsl:apply-templates select="./child::node()"/>
                    </xsl:copy>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy>
                        <xsl:for-each select="@*">
                            <xsl:choose>
                                <xsl:when test="name()='province'">
                                    <xsl:attribute name="province"><xsl:value-of select="normalize-space(replace(replace(.,$provid,''),'  ',' '))"/></xsl:attribute>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:for-each>
                        <xsl:apply-templates select="./child::node()"/>
                    </xsl:copy>
                    <xsl:copy>
                        <xsl:attribute name="country"><xsl:value-of select="$carcodenew"/></xsl:attribute>
                        <xsl:apply-templates select="./child::node()"/>
                    </xsl:copy>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:when>
        <xsl:when test="id(.//child::node()/@province)=//province[@id=$provid]">
            <xsl:choose>
                <xsl:when test="id(./child::node()/@province)=//province[@id=$provid]">
                    <xsl:copy>
                        <xsl:for-each select="@*">
                            <xsl:if test="name()!='country'">
                                <xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
                            </xsl:if>
                            <xsl:if test="name()='country'">
                                <xsl:choose>
                                    <xsl:when test="count(id(./parent::node()/child::node()/@province))=1">
                                        <xsl:attribute name="{name()}"><xsl:value-of select="$carcodenew"/></xsl:attribute>
                                    </xsl:when>
                                    <xsl:when test="count(id(./parent::node()/child::node()[@country=$carcodeold]/@province))=1">
                                        <xsl:choose>
                                            <xsl:when test="starts-with(string(.), concat($carcodeold,' '))">
                                                <xsl:attribute name="{name()}"><xsl:value-of select="replace(.,concat($carcodeold,' '),concat($carcodenew,' '))"/></xsl:attribute>
                                            </xsl:when>
                                            <xsl:when test="ends-with(string(.), concat(' ',$carcodeold))">
                                                <xsl:attribute name="{name()}"><xsl:value-of select="replace(.,concat(' ',$carcodeold),concat(' ',$carcodenew))"/></xsl:attribute>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:attribute name="{name()}"><xsl:value-of select="replace(.,concat(' ',$carcodeold,' '),concat(' ',$carcodenew,' '))"/></xsl:attribute>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:attribute name="{name()}"><xsl:value-of select="concat(., ' ',$carcodenew)"/> </xsl:attribute>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:if>
                        </xsl:for-each>
                        <xsl:apply-templates select="./child::node()"/>
                    </xsl:copy>
                </xsl:when>
                <xsl:otherwise>
                    <!-- Normally, this won't happen. -->
                    <xsl:copy>
                        <xsl:copy-of select="@*"/>
                        <xsl:apply-templates select="./child::node()"/>
                    </xsl:copy>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
            <xsl:copy-of select="."/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>
<!-- template for nature -->


<!-- ==================== -->
<!-- template for airport -->
<xsl:template match="airport">
    <xsl:choose>
        <xsl:when test="./@city and ./@city=//province[@id=$provid]/city/@id">
            <airport iatacode="{@iatacode}" city="{replace(@city, $countryold, $countrynew)}" country="{$carcodenew}">
                <xsl:copy-of select="./child::node()"/>
            </airport>
        </xsl:when>
        <xsl:otherwise>
            <xsl:copy-of select="."/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>
<!-- template for airport -->


<!-- Useless: organization
<xsl:template match="organization1">
    <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:choose>
            <xsl:when test=".=id(//province[@id=$provid]/parent::country/@memberships) and not(@id=$specialorg/orgid)">
                <xsl:apply-templates select="./child::node()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="./child::node()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:copy>
</xsl:template>
-->
<!-- Useless: nature
<xsl:template match="sea//child::node()|river//child::node()|lake//child::node()|island//child::node()|mountain//child::node()|desert//child::node()">
    <xsl:choose>
        <xsl:when test="id(./@province)=//province[@id=$provid]">
            <xsl:choose>
                <xsl:when test="count(id(./@province))=1">
                    <xsl:copy>
                        <xsl:attribute name="country"><xsl:value-of select="$carcodenew"/></xsl:attribute>
                        <xsl:apply-templates select="./child::node()"/>
                    </xsl:copy>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy>
                        <xsl:attribute name="country"><xsl:value-of select="./@country"/></xsl:attribute>
                        <xsl:apply-templates select="./child::node()"/>
                    </xsl:copy>
                    <xsl:copy>
                        <xsl:attribute name="country"><xsl:value-of select="$carcodenew"/></xsl:attribute>
                        <xsl:apply-templates select="./child::node()"/>
                    </xsl:copy>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:when>
        <xsl:when test="id(.//located/@province)=//province[@id=$provid]">
            <xsl:copy>
                <xsl:copy-of select="@*"/>
                <xsl:apply-templates select="./child::node()"/>
            </xsl:copy>
        </xsl:when>
        <xsl:otherwise>
            <xsl:copy-of select="."/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>
-->

</xsl:stylesheet>
