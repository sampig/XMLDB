<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" indent="yes" />

  <xsl:template match="TerminCalendar">
    <html>
      <head>
        <link rel="stylesheet" href="css/calendar.css" />
        <title>Entry List</title>
      </head>
      <body>
        <h2>Entry List</h2>
        <table>
          <tr>
            <th>Date</th>
            <th>Start time</th>
            <th>Name</th>
            <th>Location</th>
            <th>Duration</th>
          </tr>
          <xsl:apply-templates select="//entry" />
        </table>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="entry">
    <tr>
      <td>
        <xsl:value-of
          select="concat(format-number(../../../@n,'0000'),'-',format-number(../../@n,'00'),'-',format-number(../@n,'00'))" />
      </td>
      <td>
        <xsl:value-of select="string(@starttime)" />
      </td>
      <td>
        <xsl:value-of select="string(name)" />
      </td>
      <td>
        <xsl:value-of select="string(location)" />
      </td>
      <td>
        <xsl:value-of select="string(@duration)" />
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>
