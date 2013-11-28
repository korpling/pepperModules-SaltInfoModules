<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

    <xsl:template match="//saltProjectInfo">
        <xsl:element name="ul">
            <xsl:attribute name="name">
                <xsl:value-of select="@sName"/>
            </xsl:attribute>
            <xsl:attribute name="class">saltProjectInfo</xsl:attribute>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="//sCorpusInfo">
        <xsl:element name="li">
            <xsl:attribute name="class">sDocumentInfo</xsl:attribute>
            <xsl:value-of select="@sName"/>
        </xsl:element>
        <xsl:element name="ul">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="//sDocumentInfo">
        <xsl:element name="li">
            <xsl:attribute name="class">sDocumentInfo</xsl:attribute>
            <xsl:element name="a">
                <xsl:attribute name="href">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
                <xsl:value-of select="@sName"/>
    <!--            <xsl:apply-templates/>-->
            </xsl:element>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>