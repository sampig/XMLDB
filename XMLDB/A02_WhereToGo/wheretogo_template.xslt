<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">
<!-- xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"  -->
    <xsl:output method="xhtml" encoding="UTF-8" indent="yes"/>
    <!-- This is a test for the usage of template. -->
    <xsl:template match="/">
        <html>
            <head>
                <link href="default.css" rel="stylesheet" type="text/css"/>
                <title>Search Result</title>
            </head>
            <body>
                <h1 id="myHeader">Search Result</h1>
                <xsl:apply-templates select="RESULT/CONDITION"/>
                <table>
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Venue Name</th>
                            <th>Start Time</th>
                            <th>Website</th>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:apply-templates select="RESULT/SESSION">
                            <xsl:sort select="STARTTIME" order="ascending" data-type="text"/>
                        </xsl:apply-templates>
                    </tbody>
                </table>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="RESULT/CONDITION">
        <div class="condition">Condition: <xsl:apply-templates select="DATE"/>
            <xsl:apply-templates select="CITY"/>
        </div>
    </xsl:template>
    <xsl:template match="DATE">
        <div>Date: <xsl:value-of select="."/>
        </div>
    </xsl:template>
    <xsl:template match="CITY">
        <div>City: <xsl:value-of select="."/>
        </div>
    </xsl:template>
    <xsl:template match="RESULT/SESSION">
        <tr>
            <td>
                <xsl:value-of select="TITLE"/>
            </td>
            <td>
                <xsl:value-of select="VENUENAME"/>
            </td>
            <td>
                <xsl:value-of select="STARTTIME"/>
            </td>
            <td>
                <xsl:choose>
                    <xsl:when test="WEBSITE/OFFICIAL or WEBSITE/TRAILER">
                        <xsl:if test="WEBSITE/OFFICIAL">
                            <div>Official: <a href="{WEBSITE/OFFICIAL}">
                                    <xsl:value-of select="WEBSITE/OFFICIAL"/>
                                </a>
                            </div>
                        </xsl:if>
                        <xsl:if test="WEBSITE/TRAILER">
                            <div>Trailer: <a href="{WEBSITE/TRAILER}">
                                    <xsl:value-of select="WEBSITE/TRAILER"/>
                                </a>
                            </div>
                        </xsl:if>
                    </xsl:when>
                    <xsl:otherwise>No Websites.</xsl:otherwise>
                </xsl:choose>
            </td>
        </tr>
    </xsl:template>
</xsl:transform>
