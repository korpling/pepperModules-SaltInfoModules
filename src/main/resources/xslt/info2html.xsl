<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    <xsl:output encoding="UTF-8" indent="yes" method="html"/>
    <xsl:variable name="saltinfocss">css/saltinfo.css</xsl:variable>
    
    <xsl:template match="/saltProjectInfo|/sCorpusInfo|/sDocumentInfo">
        <html>
            <head>
                <link href="{$saltinfocss}" rel="StyleSheet" type="text/css"/>
            </head>
            <body>
                <div class="data-view">
                    <xsl:apply-templates select="//sCorpusInfo/structuralInfo"/>
                    <xsl:call-template name="data"/>
                    <xsl:for-each select="sLayerInfo">
                        <xsl:call-template name="data"/>
                    </xsl:for-each>
                </div>
            </body>
        </html>
        
    </xsl:template>
    
    <xsl:template match="saltProjectInfo|sCorpusInfo|sDocumentInfo" name="data">
        <div class="sdocument-slayer">
            <h2><xsl:value-of select="@sName"/></h2>
            <xsl:apply-templates select="./structuralInfo"/>
            <br/>
            <h2>Annotations:</h2>
            <table>
                <thead>
                    <th>Name</th>
                    <th>Counts</th>
                </thead>
                <tbody>
                    <xsl:apply-templates select="./sAnnotationInfo"/>
                </tbody>
            </table>
            <!--<xsl:apply-templates select="sLayerInfo"/>-->
        </div>
        
    </xsl:template>
    
    <xsl:template match="all">
       
        
    </xsl:template>
    
    <!-- Structural Info table    -->
    <xsl:template match="structuralInfo">
        <table>
            <thead>
                <th>Name</th>
                <th>Count</th>
            </thead>
            <tbody>
                <xsl:apply-templates select="entry"/>
            </tbody>
        </table>
        
    </xsl:template>
    
    <xsl:template match="sAnnotationInfo">
        <tr>
            <td><xsl:value-of select="@sName"></xsl:value-of></td>
            <td><xsl:apply-templates/></td>
        </tr>
    </xsl:template>
    
    <xsl:template match="sValue">
        <span class="svalue">
            <span class="svalue-text"><xsl:value-of select="text()"/></span>
            <span class="svalue-occurances"><xsl:value-of select="@occurances"/></span>
        </span>
    </xsl:template>
    
    <xsl:template match="entry">
        <tr>
            <td><xsl:value-of select="@key"></xsl:value-of></td>
            <td><xsl:value-of select="text()"/></td>
        </tr>
    </xsl:template>
    

    
</xsl:stylesheet>