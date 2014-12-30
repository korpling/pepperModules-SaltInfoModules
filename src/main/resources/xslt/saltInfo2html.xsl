<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output encoding="UTF-8" indent="yes" method="xhtml" doctype-system="about:legacy-compat"/>
    <xsl:output method="text" indent="no" name="json"/>
    <xsl:variable name="saltinfocss">css/saltinfo.css</xsl:variable>
    <xsl:variable name="minNumOfAnnos">5</xsl:variable>
<!-- set createJsonForAllAnnos to "true", if all annotations shall be loaded into json, even those with less than 5 values -->
    <xsl:variable name="createJsonForAllAnnos" select="false()" />

    <!-- buid html sceleton-->
    <xsl:template match="/*">
        <html>
            <head>
                <META http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                <xsl:element name="link">
                    <xsl:attribute name="href">{$saltinfocss}</xsl:attribute>
                    <xsl:attribute name="rel">StyleSheet</xsl:attribute>
                    <xsl:attribute name="type">text/css</xsl:attribute>
                </xsl:element>
                <link href="./css/jquery-ui.css" rel="stylesheet"/>
                <script src="./js/jquery.js"></script>
                <script src="./js/jquery-ui.js"></script>
            </head>
            <body>
                <!-- get corpus name-->
                <h2 id="title">
                    <xsl:value-of select="@sName"/>
                </h2>
                <!-- get structural info table -->
                <xsl:apply-templates select="structuralInfo"/>

                <div>
                    <br/>
                    <!-- get meta info table -->
                    <xsl:apply-templates select="metaDataInfo"/>
                </div>
                
                <!-- get annotation info table -->
                <xsl:call-template name="annoTable"/>

                <!-- set meta data info as json input -->
                <xsl:result-document href="data.json" format="json">
                    <xsl:call-template name="json"/>
                </xsl:result-document>
            </body>
        </html>
    </xsl:template>
    
    

    <!-- build structural info table -->
    <xsl:template name="structInfo" match="structuralInfo">
        <h3>Structural Info</h3>
        <hr/>
        <!-- insert something to enable descriptions (link to customization file?) -->
        <br/>
        <br/>
        <!-- create table structure -->
        <table class="data-structuralInfo">
            <thead>
                <th>Name</th>
                <th>Count</th>
            </thead>
            <tbody>
                <!-- set all structural entries -->
                <xsl:apply-templates select="entry" mode="structEntry">
                    <xsl:sort select="@key"/>
                </xsl:apply-templates>
            </tbody>
        </table>
    </xsl:template>

    <!-- get all structural entries of the corpus -->
    <xsl:template match="entry" mode="structEntry">
        <!-- get position of the entry and set class name for background colors -->
        <xsl:variable name="entry" select="position()"/>
        <xsl:choose>
            <xsl:when test="$entry mod 2=1">
                <tr class="odd">
                    <td class="entry-key">
                        <span class="tooltip">
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
                <tr class="even">
                    <td class="entry-key">
                        <span class="tooltip">
                            <xsl:attribute name="title"><xsl:value-of select="@key"/></xsl:attribute>
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
                <!-- set metadata entries -->
                <xsl:apply-templates select="entry" mode="metaEntry">
                    <xsl:sort select="@key"/>
                </xsl:apply-templates>
            </tbody>
        </table>
    </xsl:template>

    <!-- get first 5 metadata entries -->
    <xsl:template match="entry" mode="metaEntry">
        <!-- get position of the entry and set class name for background colors -->
        <xsl:variable name="entry" select="position()"/>
        <xsl:choose>
            <xsl:when test="($entry mod 2=1)">
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
            <xsl:when test="($entry mod 2=0)">
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

    <xsl:template name="annoTable">
        <div>
        <h4>Annotations</h4>
        <hr/>
        <!-- insert something to enable descriptions (link to customization file?) -->
        <br/>
        <br/>
        <table class="data-table">
            <thead>
                <th>Name</th>
                <th>Count</th>
            </thead>
            <tbody>
                <!-- set metadata entries -->
                <xsl:apply-templates select="sAnnotationInfo" mode="annoTable">
                    <xsl:sort select="@sName"/>
                </xsl:apply-templates>
            </tbody>
        </table>
        </div>
    </xsl:template>

    <xsl:template match="sAnnotationInfo" mode="annoTable">
        <xsl:variable name="sName" select="position()"/>
        <xsl:choose>
            <xsl:when test="($sName mod 2=1)">
                <tr class="odd">
                    <xsl:call-template name="annoContent"/>
                </tr>
            </xsl:when>
            <xsl:when test="($sName mod 2=0)">
                <tr class="even">
                   <xsl:call-template name="annoContent"/>
                </tr>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

<!-- get first 5 occurances -->
    <xsl:template match="sValue">
        <xsl:choose>
            <xsl:when test="position() &lt; 6">
                <span class="svalue-text">
                    <xsl:value-of select="text()"/>
                </span>
                <span class="svalue-occurrences">
                    <xsl:value-of select="@occurrences"/>
                </span>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <!-- create table content for annotations -->
    <xsl:template name="annoContent">
        <td class="entry-key">
            <span class="sannotationinfo">
                <span class="anno-sname" onmouseover="clickifyMe($(this));"
                    onmouseout="$(this).addClass('declickify-anno');"
                    onclick="goANNIS(this.innerHTML);">
                    <xsl:value-of select="@sName"/>
                </span>
                <span class="anno-count">
                    <xsl:value-of select="@occurrences"/>
                </span>
            </span>
            <span class="icon">
                <a class="btn-download-csv">
                    <i class="fa fa-download"/>
                </a>
            </span>
            <span class="icon">
                <a class="btn-toggle-box">
                    <i class="fa fa-square-o"/>
                </a>
            </span>
            <xsl:choose>
                <xsl:when test="count(sValue) &lt; 6"></xsl:when>
                <xsl:otherwise>
                    <span class="icon">
                        <a>
                            <xsl:attribute name="id">
                                <xsl:value-of select="@sName"/>
                                <xsl:text>_btn</xsl:text>
                            </xsl:attribute>
                            <xsl:attribute name="onClick">
                                <xsl:text>expandValues('</xsl:text>
                                <xsl:value-of select="@sName"/>
                                <xsl:text>')</xsl:text>
                            </xsl:attribute>
                            <i class="fa fa-expand"/>
                        </a>
                    </span>
                </xsl:otherwise>
            </xsl:choose>
            
        </td>
        <td>
            <xsl:attribute name="id">
                <xsl:value-of select="@sName"/>
                <xsl:text>_values</xsl:text>
            </xsl:attribute>
            <xsl:apply-templates select="sValue">
                <xsl:sort select="text()"/>
            </xsl:apply-templates>
        </td>
    </xsl:template>

    <xsl:template match="sAnnotationInfo" mode="annoJson">         <xsl:choose>
        <xsl:when test="$createJsonForAllAnnos">"<xsl:value-of select="@sName"/>": [
            <xsl:apply-templates select="sValue" mode="ValueJson"/>
            <xsl:choose>
                <xsl:when test="position()!=last()">"],
                </xsl:when>
                <xsl:otherwise>"]
                </xsl:otherwise>
            </xsl:choose></xsl:when>
    <xsl:otherwise>
        <xsl:if test="count(.//sValue) > 5">"<xsl:value-of select="@sName"/>": [
            <xsl:apply-templates select="sValue" mode="ValueJson"/>
            <xsl:choose>
                <xsl:when test="position()!=last()">],
                </xsl:when>
                <xsl:otherwise>]
                </xsl:otherwise>
            </xsl:choose> </xsl:if>
    </xsl:otherwise></xsl:choose></xsl:template>

    <xsl:template match="sValue" mode="ValueJson">{"value":"<xsl:value-of select="normalize-unicode(replace(text(), '&quot;','\\&quot;'))"/>", "occurances": "<xsl:value-of select="@occurrences"/>
        <xsl:choose>
            <xsl:when test="position()!=last()">"},
        </xsl:when>
        <xsl:otherwise>"}
        </xsl:otherwise>
    </xsl:choose>
    </xsl:template>
    

    <xsl:template name="json">{
        <xsl:apply-templates select="sAnnotationInfo" mode="annoJson">
                <xsl:sort select="@sName"/>
        </xsl:apply-templates>
        }
    </xsl:template>

</xsl:stylesheet>
