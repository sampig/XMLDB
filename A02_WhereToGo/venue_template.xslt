<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.opengis.net/kml/2.2" xmlns:zz="http://tuc.de/zhuchenfeng">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:template match="/">
        <kml>
            <Document>
                <xsl:apply-templates select="zz:MyWorld/zz:City"/>
            </Document>
        </kml>
    </xsl:template>
    <xsl:template match="zz:MyWorld/zz:City">
        <Folder>
            <Name>
                <xsl:value-of select="zz:CityName"/>
            </Name>
            <open>0</open>
            <description>All the venues in <xsl:value-of select="zz:CityName"/>
            </description>
            <xsl:apply-templates select="zz:Venue"/>
        </Folder>
    </xsl:template>
    <xsl:template match="zz:Venue">
        <Placemark>
            <name><xsl:value-of select="zz:VenueName"/></name>
            <address><xsl:value-of select="zz:VenueAddress"/></address>
            <description> </description>
            <Point>
                <coordinates>, </coordinates>
            </Point>
        </Placemark>
    </xsl:template>
</xsl:transform>
