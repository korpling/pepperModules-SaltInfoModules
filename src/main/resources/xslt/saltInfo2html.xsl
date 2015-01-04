<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <!-- first output in html format, use xhtml for wellformedness of unary tags -->
    <xsl:output encoding="UTF-8" indent="yes" method="xhtml" doctype-system="about:legacy-compat"/>
    <!-- second output file for information saved in json format -->
    <xsl:output method="text" indent="no" name="json" encoding="UTF-8"/>
    <xsl:output method="html" indent="yes" name="main" encoding="UTF-8"/>
    <!-- path to the used css file -->
    <xsl:variable name="saltinfocss">css/saltinfo.css</xsl:variable>
    <!-- set the minimum of annotations shown at the tables if uncollapsed -->
    <xsl:variable name="minNumOfAnnos">5</xsl:variable>
<!-- set createJsonForAllAnnos to "true", if all annotations shall be loaded into json, even those with less than 5 values -->
    <xsl:variable name="createJsonForAllAnnos" select="false()" />
    <xsl:variable name="isMainCorpus" select="sCorpusInfo/@sName=$corpusname"></xsl:variable>
    <!-- get corpus name and save it as a variable for later use -->
    <xsl:variable name="corpusname">
        <xsl:choose>
            <xsl:when test="string-length(root()/node()/@id) - string-length(replace(root()/node()/@id, '/', '')) > 1">
            <xsl:value-of select="substring-before(substring-after(root()/node()/@id, 'salt:/'),'/')"/>
        </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="substring-after(root()/node()/@id, 'salt:/')"/>
            </xsl:otherwise>
    </xsl:choose>
    </xsl:variable>
    <xsl:param name="jsonOutputName">./anno_<xsl:value-of select="$corpusname"/>.json</xsl:param>

    <!-- buid html sceleton-->
    <xsl:template match="sCorpusInfo|sDocumentInfo">
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
                <xsl:if test="$isMainCorpus">
                    <xsl:result-document href="{$jsonOutputName}" format="json">
                            <xsl:call-template name="json"/>
                    </xsl:result-document>
                    
                    
                    <xsl:result-document href="main.html" format="main">
                        <xsl:call-template name="main"/>
                    </xsl:result-document>
                </xsl:if>
                
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
                                <i class="fa fa-info-circle icon"/>
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
                                <i class="fa fa-info-circle icon"/>
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
        <table>
            <thead>
                <th>Name</th>
                <th>Values</th>
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
                <tr class="odd">
                    <td class="entry-key">
                        <span class="metadata-name">
                            <xsl:value-of select="@key"/>
                        </span>
                    </td>
                    <td>
                        <xsl:value-of select="text()"/>
                    </td>
                </tr>
            </xsl:when>
            <xsl:when test="($entry mod 2=0)">
                <tr class="even">
                    <td class="entry-key">
                        <span class="metadata-name">
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
                <th>Values</th>
            </thead>
            <tbody>
                <!-- set metadata entries -->
                <xsl:apply-templates select="sAnnotationInfo" mode="annoTable">
                    <xsl:sort select="@sName"/>
                </xsl:apply-templates>
            </tbody>
        </table>
        <script>
          	 addTooltips_MetaData();
          	 addTooltips_AnnotationNames();
          	 styleToolTips();
        </script>
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
                <span class="svalue">
                    <span class="svalue-text" onmouseover="clickifyMe($(this));" onmouseout="$(this).removeClass(CLASS_CLICKIFY);$(this).addClass(CLASS_DECLICKIFY);">
                        <xsl:attribute name="onclick">goANNIS('<xsl:value-of select="./parent::sAnnotationInfo/@sName"></xsl:value-of>', this.innerHTML);</xsl:attribute><xsl:value-of select="text()"/>
                    </span>
                </span>
                <span class="anno-value-count">
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
                    onmouseout="$(this).removeClass(CLASS_CLICKIFY);$(this).addClass(CLASS_DECLICKIFY);"
                    onclick="goANNIS(this.innerHTML);">
                    <xsl:value-of select="@sName"/>
                </span>
                <span class="anno-count">
                    <xsl:value-of select="@occurrences"/>
                </span>
            </span>
            <i class="fa fa-download btn-download-csv icon tooltip" title="Downloads annotation values and concerning occurances as CSV file (you need to expand the view to download all values)"/>
            <i class="fa fa-square-o btn-toggle-box icon tooltip" title="Draws boxes around annotation values to find whitespaces"></i>
            
            <xsl:choose>
                <xsl:when test="count(sValue) &lt; 6"></xsl:when>
                <xsl:otherwise>
                    <i class="fa fa-expand icon tooltip" title="Expands/Collapses annotation values">
                            <xsl:attribute name="id">
                                <xsl:value-of select="@sName"/><xsl:text>_btn</xsl:text>
                            </xsl:attribute>
                            <xsl:attribute name="onClick">
                                <xsl:text>loadAndExpandAnnoValues('./anno_</xsl:text>
                                <xsl:value-of select="$corpusname"/>.json<xsl:text>','</xsl:text>
                                <xsl:value-of select="@sName"/><xsl:text>')</xsl:text>
                            </xsl:attribute>
                    </i>
                    
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

    <xsl:template match="sValue" mode="ValueJson">{"value":"<xsl:value-of select="normalize-unicode(normalize-space(replace(text(), '&quot;','\\&quot;')))"/>", "occurances": "<xsl:value-of select="@occurrences"/>
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
    
    <xsl:template name="main">
        <html>
            <head>
                <title>
                    About
                </title>
            </head>
            <body>
                <h2 id="corpusTitle">
                    <xsl:value-of select="$corpusname"/>
                </h2>
                <hr/>
                <article id="corpusDescription">
                    This web page was produced by SaltInfoModule a module for the Pepper converter framework.
                </article>
                <h3>Annotators</h3>
                <hr/>
                <article id="annotators"></article>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
