<html xsl:version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/XHTML">
    <head>
        <title>Search Result</title>
    </head>
    <body>
        <h1>Search Result</h1>
        <div>Condition: <xsl:value-of select="RESULT/CONDITION/DATE"/>, <xsl:value-of select="RESULT/CONDITION/CITY"/></div>
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
                <xsl:for-each select="RESULT/SESSION">
                    <xsl:sort select="STARTTIME" order="ascending" data-type="text"/>
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
                                        <div>Official:
                                            <a href="{WEBSITE/OFFICIAL}">
                                                <xsl:value-of select="WEBSITE/OFFICIAL"/>
                                            </a>
                                        </div>
                                    </xsl:if>
                                    <xsl:if test="WEBSITE/TRAILER">
                                        <div>Trailer:
                                            <a href="{WEBSITE/TRAILER}">
                                                <xsl:value-of select="WEBSITE/TRAILER"/>
                                            </a>
                                        </div>
                                    </xsl:if>
                                </xsl:when>
                                <xsl:otherwise>
                                    No Websites.
                                </xsl:otherwise>
                            </xsl:choose>
                        </td>
                    </tr>
                </xsl:for-each>
            </tbody>
        </table>
    </body>
</html>
