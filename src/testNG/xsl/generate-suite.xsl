<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" version="1.0">
  <xsl:output doctype-system="http://testng.org/testng-1.0.dtd" />
  <xsl:template match="/">
    <suite name="NetKernel Tests" verbose="1">
      <xsl:apply-templates select="/tests/test"/>
    </suite>
  </xsl:template>
  
  <xsl:template match="test">
    <xsl:variable name="testlist"
                  select="document(concat('../../',substring-after(uri,'res:/')),/)"/>
    
    <xsl:variable name="original" select="."/>
    
    <xsl:for-each select="$testlist//test">
      <test name="{$original/id} / {@name}">
        <parameter name="identifier" value="{$original/id}"/>
        <parameter name="name" value="{@name}"/>
        
        <classes>
          <class name="uk.org.onegch.netkernel.antTools.testNG.TestRunner" />
        </classes>
      </test>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
