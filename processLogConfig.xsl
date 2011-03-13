<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="2.0">

  <xsl:output indent="yes"/>

  <xsl:template match="node() | @*">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="handler[instance/class='java.util.logging.ConsoleHandler']"/>

  <xsl:template match="handler[instance/class='org.netkernel.layer0.logging.LogFileHandler']">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="formatterClass">
    <xsl:copy>
      <xsl:text>org.netkernel.layer0.logging.ConsoleFormatter</xsl:text>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>