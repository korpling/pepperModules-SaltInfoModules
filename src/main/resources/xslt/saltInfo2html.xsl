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
    <!-- check if file is main corpus -->
    <xsl:variable name="isMainCorpus" select="sCorpusInfo/@sName=$corpusname"/>
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
    <xsl:variable name="currentFile">
        <xsl:value-of select="replace(root()/node()/@id,'.*/','')"></xsl:value-of>
    </xsl:variable>
    <!-- define output name for json file -->
    <xsl:param name="jsonOutputName">./anno_<xsl:value-of select="root()/node()/@sName"/>.json</xsl:param>
    <xsl:param name="jsonOutputNameDeprecated">./anno_<xsl:value-of select="$currentFile"/>.json</xsl:param>
    <xsl:variable name="jsonOutputPath">./<xsl:value-of select="substring-after(replace(root()/node()/@id, $currentFile, concat('anno_', $currentFile, '.json')), 'salt:/')"/></xsl:variable>
    
    <!-- descriptions of sections (structural info, meta data and annotations) -->
    <xsl:variable name="structuralInfoDesc">Structural data are those, which were necessary to create the Salt model. Since Salt is a graph-based model, all model elements are either nodes or relations between them. Salt contains a set of subtypes of the node element like SToken, STextualDS (primary data), SSpan etc. and a set of subtypes of the relation element like SSpanning Relation, SDominanceRelation, SPointingRelation etc. This section gives an overview of the amount of these elements used in this corpus/document.</xsl:variable>
    <xsl:variable name="metaDataDesc">The meta data of a document or a corpus give some information about its provenance e.g. from where does the primary data came from, who annotated it or when and so on.</xsl:variable>
    <xsl:variable name="annotationDesc">This section contains all annotations contained in this document or corpus. Annotations in Salt are attribute-value-pairs. This table contains the frequencies of all annotation names and annotation values.</xsl:variable>
    <!-- tooltip descriptions for structural elements -->
    <xsl:variable name="SNode">Number of token (smallest annotatable unit) in the current document or corpus.</xsl:variable>
    <xsl:variable name="SRelation">Total number of all relations in the current document or corpus. An SRelation is an abstract relation which could be instantiated as e.g. STextualRelation, SSPanningRelation and SDominanceRelation.</xsl:variable>
    <xsl:variable name="SSpan">Number of ps in the current document or corpus. A p is an aggregation of a bunch of tokens containing 0..n token.</xsl:variable>
    <xsl:variable name="SSpanningRelation">Number of relations in the current document or corpus to connect ps (SSpan) with tokens (SToken).</xsl:variable>
    <xsl:variable name="STextualDS">Number of relations in the current document or corpus to connect a token (SToken) with a textual data source (STextualDS).</xsl:variable>
    <xsl:variable name="STimeline">In Salt a common timeline exists, which can be used to identify the chronological occurrence of a token. For instance to identify if one token corresponding to one text occurs before or after another token corresponding to another text. This would be important in dialogue corpora.</xsl:variable>
    <xsl:variable name="SToken">Number of token (smallest annotatable unit) in the current document or corpus.</xsl:variable>
    <xsl:variable name="SPointingRelation">Number of relations in the current document or corpus for loose connections like anaphoric relations.</xsl:variable>
    <xsl:variable name="STextualRelation">Number of relations in the current document or corpus to connect a token (SToken) with a textual data source (STextualDS).</xsl:variable>
    <xsl:variable name="SStructure">Number of hierarchical structures in the current document or corpus. SStructure objects in Salt are used to represent hierarchies e.g. for constituents.</xsl:variable>
    <xsl:variable name="SDominanceRelation">Number of relations in the current document or corpus to connect hierarchical nodes like SStructure with other SNode objects. This relation class is used to represent for e.g. constituents relations.</xsl:variable>
    <xsl:variable name="SOrderRelation">Number of relations in the current document or corpus to order SNode objects. This class is used to manage conflicting token levels as they can occur for instance in dialogues.</xsl:variable>
    <xsl:variable name="STimelineRelation">Number of relations in the current document or corpus to connect a token (SToken) with the common timeline (STimeline).</xsl:variable>
    
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
                <xsl:if test="sAnnotationInfo">
                <xsl:call-template name="annoTable"/>
                </xsl:if>

                <!-- set meta data info as json input -->
                
                    <xsl:result-document href="{$jsonOutputName}" format="json">
                            <xsl:call-template name="json"/>
                    </xsl:result-document>
                    
                <xsl:if test="$isMainCorpus"> 
                    <xsl:result-document href="main.html" format="main">
                        <xsl:call-template name="main"/>
                    </xsl:result-document>
                </xsl:if>
                
                <xsl:apply-templates select="sLayerInfo">
                    <xsl:sort select="@sName"/>
                </xsl:apply-templates>
                
                <script>
                  	 addTooltips_MetaData();
                  	 addTooltips_AnnotationNames();
                  	 styleToolTips();
                </script>
            </body>
            
        </html>
    </xsl:template>
    
    

    <!-- build structural info table -->
    <xsl:template name="structInfo" match="structuralInfo">
        <xsl:if test="not(empty(child::node()))">
        <h3>Structural Info</h3>
        <hr/>
        <!-- paragraph for description -->
        <p id="structInfoDescription">
            <xsl:value-of select="$structuralInfoDesc"/>
        </p>
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
        </xsl:if>
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
                            <xsl:attribute name="title"><xsl:call-template name="structTooltips"/></xsl:attribute>
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
                            <xsl:attribute name="title"><xsl:call-template name="structTooltips"/></xsl:attribute>
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
        <xsl:if test="not(empty(child::node()))">
        <h4>Meta Data</h4>
        <hr/>
        <!-- paragraph for description -->
        <p id="metaDataDescription">
            <xsl:value-of select="$metaDataDesc"/>
        </p>
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
        </xsl:if>
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
        <xsl:if test="not(empty(sAnnotationInfo/child::node()))">
        <div>
        <h4>Annotations</h4>
        <hr/>
            <!-- paragraph for description -->
            <p id="annoDescription">
               <xsl:value-of select="$annotationDesc"/>
            </p>
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
        </div>
        </xsl:if>
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

<!-- get first 5 occurences -->
    <xsl:template match="sValue">
        <xsl:choose>
            <xsl:when test="position() &lt; 6">
                <span class="svalue">
                    <span class="svalue-text" onmouseover="clickifyMe($(this));" onmouseout="$(this).removeClass(CLASS_CLICKIFY);$(this).addClass(CLASS_DECLICKIFY);">
                        <xsl:attribute name="onclick">goANNIS('<xsl:value-of select="./parent::sAnnotationInfo/@sName"></xsl:value-of>', this.innerHTML);</xsl:attribute><xsl:value-of select="text()"/>
                    </span>
                </span>
                <span class="anno-value-count">
                    <xsl:value-of select="@occurrence"/>
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
                    <xsl:value-of select="@occurrence"/>
                </span>
            </span>
            <i class="fa fa-download btn-download-csv icon tooltip" title="Downloads annotation values and corresponding occurrences as CSV file (you need to expand the view to download all values)"/>
            <i class="fa fa-square-o btn-toggle-box icon tooltip" title="Draws boxes around annotation values to find whitespaces"></i>
            
            <xsl:choose>
                <xsl:when test="count(sValue) &lt; 6"></xsl:when>
                <xsl:otherwise>
                    <i class="fa fa-expand icon tooltip" title="Expands/Collapses annotation values">
                            <xsl:attribute name="id">
                                <xsl:value-of select="@sName"/><xsl:text>_btn</xsl:text>
                            </xsl:attribute>
                            <xsl:attribute name="onClick">
                                <xsl:text>loadAndExpandAnnoValues('</xsl:text><xsl:value-of select="$jsonOutputPath"/><xsl:text>','</xsl:text>
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
            <xsl:apply-templates select="sValue" mode="ValueJson">
                <xsl:sort select="text()"></xsl:sort>
            </xsl:apply-templates>
            <xsl:choose>
                <xsl:when test="position()!=last()">"],
                </xsl:when>
                <xsl:otherwise>"]
                </xsl:otherwise>
            </xsl:choose></xsl:when>
    <xsl:otherwise>
        <xsl:if test="count(.//sValue) > 5">"<xsl:value-of select="@sName"/>": [
            <xsl:apply-templates select="sValue" mode="ValueJson">
                <xsl:sort select="text()"></xsl:sort>
            </xsl:apply-templates>
            <xsl:choose>
                <xsl:when test="position()!=last()">],
                </xsl:when>
                <xsl:otherwise>]
                </xsl:otherwise>
            </xsl:choose> </xsl:if>
    </xsl:otherwise></xsl:choose></xsl:template>

    <xsl:template match="sValue" mode="ValueJson">{"value":"<xsl:value-of select="normalize-unicode(normalize-space(replace(text(), '&quot;','\\&quot;')))"/>", "occurrence": "<xsl:value-of select="@occurrence"/>
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
    
    <xsl:template match="sLayerInfo">
        <xsl:if test="not(empty(child::node()))">
        <div>
            <h4><xsl:value-of select="@sName"/></h4>
            <hr/>
            <!-- paragraph for description -->
            <p id="sLayerDescription"></p>
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
        </div>
        </xsl:if>
    </xsl:template>
    
    <!-- create main page with detailed corpus description if given (customization.json) -->
    <xsl:template name="main">
        <html>
            <head>
                <title>
                    About
                </title>
            </head>
            <body>
                <h2 id="corpusTitle">
                    <xsl:value-of select="$currentFile"/>
                </h2>
                <hr/>
                <article id="corpusDescription">
                </article>
                <h3>Annotators</h3>
                <hr/>
                <article id="annotators"></article>
            </body>
        </html>
    </xsl:template>
    
    <!-- choose matching tooltip -->
    <xsl:template name="structTooltips">
        <xsl:choose>
            <xsl:when test="@key = 'SNode'"><xsl:value-of select="$SNode"/></xsl:when>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="@key = 'SRelation'"><xsl:value-of select="$SRelation"/></xsl:when>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="@key = 'SSpan'"><xsl:value-of select="$SSpan"/></xsl:when>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="@key = 'SSpanningRelation'"><xsl:value-of select="$SSpanningRelation"/></xsl:when>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="@key = 'STextualDS'"><xsl:value-of select="$STextualDS"/></xsl:when>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="@key = 'STimeline'"><xsl:value-of select="$STimeline"/></xsl:when>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="@key = 'SToken'"><xsl:value-of select="$SToken"/></xsl:when>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="@key = 'SPointingRelation'"><xsl:value-of select="$SPointingRelation"/></xsl:when>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="@key = 'STextualRelation'"><xsl:value-of select="$STextualRelation"/></xsl:when>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="@key = 'SStructure'"><xsl:value-of select="$SStructure"/></xsl:when>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="@key = 'SDominanceRelation'"><xsl:value-of select="$SDominanceRelation"/></xsl:when>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="@key = 'STimelineRelation'"><xsl:value-of select="$STimelineRelation"/></xsl:when>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
