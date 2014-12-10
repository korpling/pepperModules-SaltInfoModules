<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    version="2.0" xmlns:jn='http://www.json.org'>
    
    <xsl:output method="text"/>
    <xsl:output method="html" indent="yes" name="html"/>
    <xsl:output method="jn:json" name="json"/>
    
    <xsl:template match="/">
        <xsl:for-each select="//testrun">
            <xsl:variable name="filename"
                select="concat('output1/',@run,'.html')" />
            <xsl:value-of select="$filename" />  <!-- Creating  -->
            <xsl:result-document href="{$filename}" format="json">
                <html><body>
                    <xsl:value-of select="@run"/>
                </body></html>
            </xsl:result-document>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>