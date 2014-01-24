<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output encoding="UTF-8" indent="yes" method="html" doctype-system="about:legacy-compat"/>
    <xsl:variable name="saltinfocss">css/saltinfo.css</xsl:variable>

    <xsl:template match="/*">
        <html>
<!--            <xsl:element name="head"></xsl:element>-->
            <head>
                <link href="{$saltinfocss}" rel="StyleSheet" type="text/css"/>
            </head>
            <body>
                <div class="data-view" id="data">
                    <h1><xsl:value-of select="@sName"/></h1>
                    <!--<xsl:apply-templates select="//sCorpusInfo/structuralInfo"/>-->
                    <xsl:call-template name="data" />
                    <xsl:for-each select="sLayerInfo">
                        <xsl:sort select="@sName"/>
                        <xsl:call-template name="data"/>
                    </xsl:for-each>
                </div>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="saltProjectInfo|sCorpusInfo|sDocumentInfo" name="data">
        <div class="sdocument-slayer">
            <xsl:apply-templates select="./structuralInfo">
                <xsl:sort select="node()/@sName"/>
            </xsl:apply-templates>
            <xsl:apply-templates select="metaDataInfo">
                <xsl:sort select="@sName"/>
            </xsl:apply-templates>
            <!--<br/>-->
            <div class="annotation">
                <h2><span class="data-entryName">annotations</span>:</h2>
                <table class="data-table">
                    <thead>
                        <th>Name</th>
                        <th>Counts<div class="btn-minimize">...</div></th>
                    </thead>
                    <tbody>
                        <xsl:apply-templates select="./sAnnotationInfo">
                            <xsl:sort select="@sName"/>
                        </xsl:apply-templates>
                    </tbody>
                </table>
            </div>
            <!--<xsl:apply-templates select="sLayerInfo"/>-->
        </div>
    </xsl:template>

    <xsl:template match="metaDataInfo" name="metadata">
        <div class="metadata">
            <h2><span class="data-entryName">meta data</span>:</h2>
            <table class="data-metadata">
                <thead>
                    <th>Name</th>
                    <th>Value<div class="btn-minimize">...</div></th>
                </thead>
                <tbody>
                    <xsl:apply-templates select="entry">
                        <xsl:sort select="@sName"/>
                    </xsl:apply-templates>
                </tbody>
            </table>
        </div>
    </xsl:template>
    

    <!-- Structural Info table    -->
    <xsl:template match="structuralInfo">
        <div class="structuralInfo">
            <h2><span class="data-entryName">structural info</span>:</h2>
            <table class="data-structuralInfo">
                <thead>
                    <th>Name</th>
                    <th>Count<div class="btn-minimize">...</div></th>
                </thead>
                <tbody>
                    <xsl:apply-templates select="entry">
                        <xsl:sort select="@key"/>
                    </xsl:apply-templates>
                </tbody>
            </table>
        </div>
    </xsl:template>

    <xsl:template match="sAnnotationInfo">
        <tr>
            <td>
                <span class="sannotationinfo">
                    <span class="sannotationinfo-sname">
                        <xsl:value-of select="@sName"/>
                    </span>
                    <span class="sannotationinfo-count">
                        <xsl:value-of select="sum(sValue/@occurances)"/>
                    </span>
                </span>
                <xsl:call-template name="action-buttons"/>
            </td>
            <td>
            <!-- sValues           -->
            <xsl:apply-templates>
                <xsl:sort select="text()"/>
            </xsl:apply-templates></td>
        </tr>
    </xsl:template>
    
    <xsl:template name="action-buttons">
        <xsl:element name="button">
            <xsl:attribute name="class">btn-toogle-sannotation-dropdown</xsl:attribute>
            <!--                    <xsl:attribute name="type">button</xsl:attribute>-->
            <xsl:text>Show more</xsl:text>
        </xsl:element>
        <xsl:element name="button">
            <xsl:attribute name="class">btn-download-csv</xsl:attribute>
            <!--                    <xsl:attribute name="type">button</xsl:attribute>-->
            <xsl:text>CSV</xsl:text>
        </xsl:element>
    </xsl:template>

    <xsl:template match="sValue">
        <span class="svalue">
            <span class="svalue-text"><xsl:value-of select="text()"/></span>
            <span class="svalue-occurances"><xsl:value-of select="@occurances"/></span>
        </span>
    </xsl:template>

    <xsl:template match="entry">
        <tr>
            <td>
                <span class="data-entryName"><xsl:value-of select="@key"></xsl:value-of></span>
            </td>
            <td><xsl:value-of select="text()"/></td>
        </tr>
    </xsl:template>
</xsl:stylesheet>
