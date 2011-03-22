<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:output indent="yes"/>

  <xsl:param name="uri" as="xs:string"/>
  <xsl:param name="version" as="xs:string"/>
  <xsl:param name="name" as="xs:string"/>
  <xsl:param name="description" as="xs:string"/>
  
  <xsl:template match="node() | @*" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="uri">
    <xsl:copy>
      <xsl:value-of select="$uri"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="version">
    <xsl:copy>
      <xsl:value-of select="$version"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="name">
    <xsl:copy>
      <xsl:value-of select="$name"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="description">
    <xsl:copy>
      <xsl:value-of select="$description"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>