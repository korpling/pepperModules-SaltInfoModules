<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output encoding="UTF-8" indent="yes" method="html" doctype-system="about:legacy-compat"/>
    <xsl:variable name="saltinfocss">css/saltinfo.css</xsl:variable>

    <xsl:template match="/*">
        <html>
            <!--            <xsl:element name="head"></xsl:element>-->
            <head>
                <xsl:element name="link">
                    <xsl:attribute name="href">{$saltinfocss}</xsl:attribute>
                    <xsl:attribute name="rel">StyleSheet</xsl:attribute>
                    <xsl:attribute name="type">text/css</xsl:attribute>
                </xsl:element>
            </head>
            <body>
                <!-- build html sceleton -->
                <h2 id="title">
                    <xsl:value-of select="@sName"/>
                </h2>

                <xsl:apply-templates select="structuralInfo"/> 

                <div>
                    <br/>
                    <xsl:apply-templates select="metaDataInfo"/>
                    
                </div>
            </body>
        </html>
    </xsl:template>

<!-- get structural information -->
    <xsl:template name="structInfo" match="structuralInfo">
        <h3>Structural Info</h3>
        <hr/>
        <!-- insert something to enable descriptions (link to customization file?) -->
        <br/>
        <br/>
        <table class="data-structInfo">
            <thead>
                <th>Name</th>
                <th>Count</th>
            </thead>
            <tbody>
                <xsl:apply-templates select="entry" mode="structEntry">
                    <xsl:sort select="@key"/>
                </xsl:apply-templates>
            </tbody>
        </table>
    </xsl:template>

    <!-- get all structural information of the corpus -->
    <xsl:template match="entry" mode="structEntry">
        <xsl:variable name="entry" select="position()"/>
        <xsl:choose>
            <xsl:when test="$entry mod 2=1">
                <tr class="even">
                    <td class="entry-key">
                        <span class="sName_entry">
                            <xsl:value-of select="@key"/>
                            <span class="icon">
                                <i class="fa fa-info-circle"/>
                            </span>
                        </span>
                    </td>
                    <td>
                        <xsl:value-of select="text()"/>
                    </td>
                </tr>
            </xsl:when>
            <xsl:when test="$entry mod 2=0">
                <tr class="odd">
                    <td class="entry-key">
                        <span class="sName_entry">
                            <xsl:value-of select="@key"/>
                            <span class="icon">
                                <i class="fa fa-info-circle"/>
                            </span>
                        </span>
                    </td>
                    <td>
                        <xsl:value-of select="text()"/>
                    </td>
                </tr>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="metaDataInfo">
        <h4>Meta Data</h4>
        <hr/>
        <!-- insert something to enable descriptions (link to customization file?) -->
        <br/>
        <br/>
        <table class="data-metadata">
            <thead>
                <th>Name</th>
                <th>Count</th>
            </thead>
            <tbody>
                <xsl:apply-templates select="entry" mode="metaEntry">
                    <xsl:sort select="@key"/>
                </xsl:apply-templates>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template match="entry" mode="metaEntry">
        <xsl:variable name="entry" select="position()"/>
        <xsl:choose>
            <xsl:when test="$entry mod 2=1">
                <tr class="even">
                    <td class="entry-key">
                        <span class="data-entryName">
                            <xsl:value-of select="@key"/>
                        </span>
                    </td>
                    <td>
                        <xsl:value-of select="text()"/>
                    </td>
                </tr>
            </xsl:when>
            <xsl:when test="$entry mod 2=0">
                <tr class="odd">
                    <td class="entry-key">
                        <span class="data-entryName">
                            <xsl:value-of select="@key"/>
                        </span>
                    </td>
                    <td>
                        <xsl:value-of select="text()"/>
                    </td>
                </tr>
            </xsl:when>
        </xsl:choose>

    </xsl:template>

</xsl:stylesheet>
