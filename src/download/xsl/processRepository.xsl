<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2010-2011 Christopher Cormack
  ~
  ~ Permission is hereby granted, free of charge, to any person obtaining a copy
  ~ of this software and associated documentation files (the "Software"), to deal
  ~ in the Software without restriction, including without limitation the rights
  ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  ~ copies of the Software, and to permit persons to whom the Software is
  ~ furnished to do so, subject to the following conditions:
  ~
  ~ The above copyright notice and this permission notice shall be included in
  ~ all copies or substantial portions of the Software.
  ~
  ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  ~ THE SOFTWARE.
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:onegch="http://www.onegch.org.uk"
                exclude-result-prefixes="xs onegch"
                version="2.0">
  <xsl:output indent="yes"/>
  
  <xsl:template match="node() | @*" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="repository">
    <xsl:variable name="base-repository" select="doc(concat(config/baseUrl, 'netkernel/', config/path, 'base/', config/set, '/repository.xml'))"/>
    <xsl:variable name="update-repository" select="doc(concat(config/baseUrl, 'netkernel/', config/path, 'update/', config/set, '/repository.xml'))"/>
    <xsl:variable name="security-repository" select="doc(concat(config/baseUrl, 'netkernel/', config/path, 'security/', config/set, '/repository.xml'))"/>
    
    <xsl:message>Processing '<xsl:value-of select="config/set" />' set  from repository <xsl:value-of select="concat(config/baseUrl, 'netkernel/', config/path)"/></xsl:message>
    
    <xsl:copy>
      <xsl:apply-templates select="node()" mode="repository">
        <xsl:with-param name="base-repository" select="$base-repository"/>
        <xsl:with-param name="update-repository" select="$update-repository"/>
        <xsl:with-param name="security-repository" select="$security-repository"/>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="node() | @*" mode="repository">
    <xsl:param name="base-repository"/>
    <xsl:param name="update-repository"/>
    <xsl:param name="security-repository"/>
    <xsl:copy>
      <xsl:apply-templates select="* | node()" mode="#current">
        <xsl:with-param name="base-repository" select="$base-repository"/>
        <xsl:with-param name="update-repository" select="$update-repository"/>
        <xsl:with-param name="security-repository" select="$security-repository"/>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="package" mode="repository">
    <xsl:param name="base-repository"/>
    <xsl:param name="update-repository"/>
    <xsl:param name="security-repository"/>
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      
      <xsl:variable name="base-version" select="onegch:get-version-from-respository($base-repository, text())"></xsl:variable>
      <xsl:variable name="update-version" select="onegch:get-version-from-respository($update-repository, text())"></xsl:variable>
      <xsl:variable name="security-version" select="onegch:get-version-from-respository($security-repository, text())"></xsl:variable>
      
      <xsl:choose>
        <xsl:when test="count($base-version/*)=0 and count($update-version/*)=0 and count($security-version//package/*)=0">
          <xsl:message terminate="yes">Package not found</xsl:message>
        </xsl:when>
        <xsl:when test="count($update-version/*)=0 and count($security-version/*)=0">
          <xsl:message> * Package <xsl:value-of select="concat(text(), ' ', $base-version/version/string)"/> found in base repository</xsl:message>
          <type>base</type>
          <url><xsl:value-of select="onegch:calculate-filename($base-version, ../../config)"/></url>
          <xsl:copy-of select="$base-version/*"/>
        </xsl:when>
        <xsl:when test="count($update-version/*)>0 and count($security-version/*)=0">
          <xsl:message> * Package <xsl:value-of select="concat(text(), ' ', $update-version/version/string)"/> found in update repository</xsl:message>
          <type>update</type>
          <url><xsl:value-of select="onegch:calculate-filename($update-version, ../../config)"/></url>
          <xsl:copy-of select="$update-version/*"/>
        </xsl:when>
        <xsl:when test="count($update-version/*)=0 and count($security-version/*)>0">
          <xsl:message> * Package <xsl:value-of select="concat(text(), ' ', $security-version/version/string)"/> found in security repository</xsl:message>
          <type>security</type>
          <url><xsl:value-of select="onegch:calculate-filename($security-version, ../../config)"/></url>
          <xsl:copy-of select="$security-version/*"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:message terminate="yes">Conflicting update and security versions</xsl:message>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>
  
  <xsl:function name="onegch:get-version-from-respository" as="node()">
    <xsl:param name="repository"/>
    <xsl:param name="packageName"/>
    
    <xsl:variable name="package" select="$repository//package[name=$packageName][last()]"/>
    <package>
      <xsl:if test="$package">
        <xsl:variable name="version" select="tokenize($package/version, '\.')"/>
        <name><xsl:value-of select="$package/name"/></name>
        <filename><xsl:value-of select="$package/filename"/></filename>
        <filepath><xsl:value-of select="$package/filepath"/></filepath>
        <version>
          <string><xsl:value-of select="$package/version"/></string>
          <major><xsl:value-of select="$version[1]"/></major>
          <minor><xsl:value-of select="$version[2]"/></minor>
          <patch><xsl:value-of select="$version[3]"/></patch>
        </version>
      </xsl:if>
    </package>
  </xsl:function>
  
  <xsl:function name="onegch:calculate-filename" as="xs:string">
    <xsl:param name="package" as="node()"/>
    <xsl:param name="config" as="node()"/>
    
    <xsl:value-of select="concat($config/baseUrl, $package//filepath, $package//filename)"/>
  </xsl:function>
</xsl:stylesheet>