<?xml version="1.0" encoding="UTF-8"?>
<!-- TODO: accumulation for maincorpora with more than 1 subcorpus & merging Values in accumulated total-rows -->
<!-- repair toggleRow for subcorpora-totalTable -->
<!-- optimize sorting -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="variables.xslt"/>
    <xsl:import href="style.xslt"/>
    <xsl:output encoding="UTF-8" indent="yes" method="html"/>
    <xsl:key match="//sDocumentInfo/structuralInfo//entry" name="structEntryKey" use="@key"/>
    <xsl:key match="//sDocumentInfo/totalSAnnotationInfo//sAnnotationInfo" name="totalAnnoName" use="@sName"/>
    <xsl:key match="//sValue" name="totalValue" use="."/>
    <!-- FLAG -->
    <!-- delete content to run without accumulation -->
    <xsl:variable name="accumulation">t</xsl:variable>
    <!-- i-img -->
    <xsl:variable name="InfoImg">
        <img class="infoimg" align="{$InfoImgAlign}" alt="" src="{$InfoImgSrc}"/>
    </xsl:variable>
    <!-- creates the html-structure -->
    <xsl:template match="/" name="HtmlStructure">
        <html>
            <head>
                <xsl:call-template name="Title"/>
                <xsl:call-template name="MetaInfo"/>
                <xsl:call-template name="JavaScriptElem"/>
            </head>
            <body>
                <xsl:call-template name="HeadOfHtml"/>
                <xsl:call-template name="navigation"/>
                <xsl:call-template name="content"/>
                <xsl:call-template name="Footer"/>
            </body>
        </html>
    </xsl:template>

    <!-- creates a title-tag in the html-head -->
    <xsl:template name="Title">
        <title><xsl:apply-templates select="/saltProjectInfo"/>-Overview</title>
    </xsl:template>

    <!-- matches on the sCorpusInfo-element (main-corpus) and prints its name -->
    <xsl:template match="saltProjectInfo" name="RootCorpus">
        <xsl:value-of select="@sName"/>
    </xsl:template>

    <!-- creates the header of the html -->
    <xsl:template name="HeadOfHtml">
        <div id="header">
            <img alt="{$logoAlternative}" href="http://korpling.german.hu-berlin.de/saltnpepper" src="{$logoSrc}"/>
            <h1>
                <xsl:apply-templates select="saltProjectInfo"/>
                <xsl:call-template name="RootCorpus"/>
                <xsl:text>- Overview</xsl:text>
            </h1>
            <p>generated with SaltNPepper</p>
        </div>
    </xsl:template>

    <!-- creates the clickable tree-table -->
    <xsl:template name="content">
        <div id="content">
            <!--<xsl:apply-templates select="/saltProjectInfo/sCorpusInfo" mode="MainCorpusTable"/><xsl:apply-templates select="//sCorpusInfo/sCorpusInfo" mode="SubCorpusTable"/><xsl:apply-templates select="//sDocumentInfo" mode="DocumentTable"/>-->
            <xsl:call-template name="sCorpusInfoTables"/>
            <xsl:call-template name="sDocumentInfoTables"/>
        </div>
    </xsl:template>

    <!-- creates the html-footer -->
    <xsl:template name="Footer">
        <div id="footer">
            <xsl:text>the here presented Information of the corpus where generated by the SaltInfoModule module, part of the saltNpepper project, please see</xsl:text>
            <a href="http://korpling.german.hu-berlin.de/saltnpepper">http://korpling.german.hu-berlin.de/saltnpepper</a>
            <xsl:text>generated on</xsl:text>
            <xsl:call-template name="RootElement"/>
            <div class="impressum">
                <a href="http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/impressum.html">
                    <xsl:text>Impressum</xsl:text></a>
            </div>
        </div>
    </xsl:template>

    <!-- matches on the root-Element and prints the "generatedOn"-date and time -->
    <xsl:template name="RootElement">
        <xsl:value-of select="saltProjectInfo/@generatedOn"/>
    </xsl:template>

    <!-- contains link to stylesheet -->
    <xsl:template name="MetaInfo">
        <!-- <link href="{$treecss}" rel="StyleSheet" type="text/css"/> -->
        <link href="{$saltinfocss}" rel="StyleSheet" type="text/css"/>
    </xsl:template>

    <!-- contains javascript-information and computes array-elements -->
    <xsl:template name="JavaScriptElem">
        <script src="{$jQuerySrc}" type="text/javascript"/>
        <script src="{$saltinfojs}" type="text/javascript"/>
    </xsl:template>

    <!-- creates a nested list that corresponds to the corpus tree -->
    <xsl:template name="navigation">
        <div id="navigation">
            <ul class="saltproject-list">
                <xsl:for-each select="//saltProjectInfo">
                    <li class="saltproject-item">
                        <xsl:element name="a">
                            <!--TODO: SaltProjectInfo sollte auch ne ID haben -->
                            <xsl:attribute name="href">#salt:rootCorpus&quot;/&gt;</xsl:attribute>
                            <xsl:value-of select="@sName"/>
                        </xsl:element>
                    </li>
                    <xsl:call-template name="navcorpusInfo"/>
                </xsl:for-each>
            </ul>
        </div>
    </xsl:template>

    <!-- recursive template which generates tree list with sublists and anchor links to every
    document and corpus-->
    <xsl:template name="navcorpusInfo">
        <ul class="subcorpus-list">
            <xsl:for-each select="sCorpusInfo">
                <li class="scorpus-item">
                    <xsl:element name="a">
                        <xsl:attribute name="href">
                            <xsl:text>#</xsl:text>
                            <xsl:value-of select="translate(@id,':,/,-,.','')"/></xsl:attribute>
                        <xsl:value-of select="@sName"/>
                    </xsl:element>
                </li>
                <xsl:call-template name="navcorpusInfo"/>
            </xsl:for-each>
            <xsl:for-each select="sDocumentInfo">
                <li class="sdocument-item">
                    <xsl:element name="a">
                        <xsl:attribute name="href">
                        <xsl:text>#</xsl:text>
                        <xsl:value-of select="translate(@id,':,/,-,.','')"/></xsl:attribute>
                        <xsl:value-of select="@sName"/>
                    </xsl:element>
                </li>
            </xsl:for-each>
        </ul>
    </xsl:template>

    <!-- creates the content for a sCorpus element -->
    <xsl:template name="sCorpusInfoTables">
        <xsl:for-each select="//sCorpusInfo">
            <div class="data-view" style="display:none;">
                <xsl:attribute name="id">
                    <xsl:value-of select="translate(@id,':,/,-,.','')"/>
                </xsl:attribute>
                <h2>
                    <xsl:value-of select="@sName"/>
                </h2>
                <xsl:call-template name="SubCorpusTable"/>
            </div>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="sDocumentInfoTables">
        <xsl:for-each select="//sDocumentInfo">
            <div class="data-view" style="display:none;">
                <xsl:attribute name="id">
                    <xsl:value-of select="translate(@id,':,/,-,.','')"/>
                </xsl:attribute>
                <h2>
                    <xsl:value-of select="@sName"/>
                </h2>
                <div class="layer-overview">
                    <xsl:text>Layers:</xsl:text>
                    <xsl:for-each select="sLayerInfo">
                        <a class="slayer-link">
                            <xsl:attribute name="href">
                                <xsl:text>#</xsl:text>
                                <xsl:value-of select="translate(parent::node()/@id,':,/,-,.','')"/>
                                <xsl:text>_layer_</xsl:text>
                                <xsl:value-of select="@sName"/>
                            </xsl:attribute>
                            <xsl:value-of select="@sName"/>
                        </a>
                    </xsl:for-each>
                </div>
                <xsl:call-template name="sDocumentInfoTable"/>
            </div>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="annotationTable">
        <table>
            <thead>
                <tr>
                    <th class="name">
                        <a class="tooltip">
                            <b>
                                <xsl:value-of select="$sAnnotationInfoMap"/>
                            </b>
                            <!-- <xsl:copy-of select="$InfoImg"/> -->
                            <!-- <xsl:call-template name="AnnoTooltip"/> -->
                        </a>
                    </th>
                    <th class="values">
                        <div class="btn-minimize">...</div>
                    </th>
                </tr>
            </thead>
            <tbody>
                <xsl:for-each select="sAnnotationInfo">
                    <xsl:sort select="@sName"/>
                    <tr>
                        <xsl:attribute name="class">
                            <xsl:value-of select="translate(../../@id,'/,-,.','_')"/>
                            <xsl:value-of select="translate(../@sName,'/,-,.','_')"/>_anno dropdown</xsl:attribute>
                            <xsl:call-template name="NameAndOccurances"/>
                        <xsl:call-template name="UnderMax"/>
                    </tr>
                </xsl:for-each>
            </tbody>
        </table>
    </xsl:template>
    <xsl:template match="structuralInfo">
        <table class="structuralinfo-table">
            <thead>
                <tr>
                    <th class="name">
                        <a class="tooltip">
                            <xsl:value-of select="$structuralInfoMap"/>
                            <xsl:copy-of select="$InfoImg"/>
                            <!-- <xsl:call-template name="structTooltip"/> -->
                        </a>
                    </th>
                    <th class="values">
                        <div class="btn-minimize">...</div>
                    </th>
                </tr>
            </thead>
            <tbody>
                <xsl:apply-templates mode="sName" select="parent::node()"/>
                <xsl:apply-templates mode="SNodeEntry" select="entry[@key = 'SNode']"/>
                <xsl:apply-templates mode="SRelationEntry" select="entry[@key = 'SRelation']"/>
                    <xsl:apply-templates mode="Entry" select="entry">
                        <xsl:sort select="@key"/>
                    </xsl:apply-templates>
            </tbody>
        </table>
    </xsl:template>

    <!-- creates tables for each document -->
    <xsl:template name="sDocumentInfoTable">
        <xsl:param name="NO_LAYER" select="./sLayerInfo[@sName=NO_LAYER]/@sName"/>
        <xsl:apply-templates mode="MetaData" select="metaDataInfo">
            <!-- <xsl:with-param name="metaDataInfo"
                    select="$MappingList2/elem[@maptype='metaDataInfo']"/>-->
        </xsl:apply-templates>
        <xsl:apply-templates select="structuralInfo"/>
        <xsl:apply-templates mode="totalAnno" select="totalSAnnotationInfo">
            <xsl:with-param name="totalSAnno" select="$totalSAnnotationInfoMap"/>
        </xsl:apply-templates>
        <xsl:for-each select="sLayerInfo">
            <xsl:sort order="ascending" select="@sName"/>
            <div class="sdocument-slayer">
                <h3>
                    <xsl:attribute name="id">
                        <xsl:value-of select="translate(parent::node()/@id,':,/,-,.','')"/>
                        <xsl:text>_layer_</xsl:text>
                        <xsl:value-of select="@sName"/>
                    </xsl:attribute>
                    <xsl:call-template name="ChildNodeControl">
                        <xsl:with-param name="NO_LAYER" select="$NO_LAYERMap"/>
                    </xsl:call-template>
                </h3>
                    <xsl:apply-templates select="structuralInfo"/>
                <xsl:choose>
                    <xsl:when test="child::node()=sAnnotationInfo">
                        <xsl:call-template name="annotationTable" />
                    </xsl:when>
                </xsl:choose>
            </div>
        </xsl:for-each>
    </xsl:template>
    <!-- creates a table for each subcorpus -->
    <xsl:template name="SubCorpusTable">
        <div class="sub-corpus table">
            <!-- <xsl:attribute name="id"><xsl:value-of select="translate(@id,':,/,-,.','')"/></xsl:attribute>-->
            <xsl:apply-templates mode="MetaData" select="metaDataInfo">
                <!-- <xsl:with-param name="metaDataInfo"
                    select="$MappingList2/elem[@maptype='metaDataInfo']"/>-->
            </xsl:apply-templates>
            <table>
                <thead>
                    <tr>
                        <th class="name">
                            <a class="tooltip">
                                <xsl:value-of select="$structuralInfoMap"/>
                                <xsl:copy-of select="$InfoImg"/>
                                <!-- <xsl:call-template name="structTooltip"/> -->
                            </a>
                        </th>
                        <th class="values">
                            <div class="btn-minimize">...</div>
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <xsl:call-template name="sName"/>
                    <tr>
                        <td>
                            <a class="tooltip">
                                <b>
                                    <xsl:value-of select="$SDocument"/>
                                </b>
                                <xsl:copy-of select="$InfoImg"/>
                                <!-- <xsl:call-template name="sDocumentTooltip"/> -->
                            </a>
                        </td>
                        <td>
                            <!-- <xsl:for-each select="sDocumentInfo"><xsl:apply-templates select="sDocumentInfo" mode="Document"/></xsl:for-each>-->
                            <xsl:call-template name="sDocumentList"/>
                        </td>
                    </tr>
                    <xsl:call-template name="structInfoCorpus"/>
                </tbody>
            </table>
            <!--   <xsl:choose><xsl:when test="$accumulation"><xsl:call-template name="totalAnnoCorpus"/></xsl:when></xsl:choose>-->
            <xsl:apply-templates mode="totalAnno" select="totalSAnnotationInfo">
                <!-- <xsl:with-param name="totalSAnno" select="$MappingList2/elem[@maptype='totalSAnnotationInfo']"/>-->
            </xsl:apply-templates>
        </div>
    </xsl:template>
    <xsl:template name="sDocumentList">
        <span class="sdocumentlist-data">
            <xsl:for-each select="sDocumentInfo">
                <span class="sdocument">
                    <xsl:value-of select="@sName"/>
                </span>
            </xsl:for-each>
            <xsl:for-each select="sCorpusInfo">
                <xsl:call-template name="sDocumentList"/>
            </xsl:for-each>
        </span>
    </xsl:template>
    <!-- sums up the documents of a subcorpus -->
    <xsl:template match="sDocumentInfo" mode="Document">
        <b>
            <xsl:value-of select="@sName"/>
        </b>
        <xsl:choose>
            <xsl:when test="position()=last()">
                <xsl:text/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>, </xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!--   <!-\- creates a table for each maincorpus -\-><xsl:template match="/saltProjectInfo/sCorpusInfo" mode="MainCorpusTable"><div class="main-corpus table"><xsl:attribute name="id"><xsl:value-of select="translate(@id,':,/,-,.','')"/></xsl:attribute><xsl:apply-templates mode="MetaData" select="metaDataInfo"><!-\- <xsl:with-param name="metaDataInfo"
                    select="$MappingList2/elem[@maptype='metaDataInfo']"/>-\-></xsl:apply-templates><table><tr><td><a class="tooltip"><!-\- <xsl:value-of select="$MappingList2/elem[@maptype='structuralInfo']"/>-\-><xsl:copy-of select="$InfoImg"/><xsl:call-template name="structTooltip"/></a></td></tr><xsl:call-template name="sName"/><xsl:choose><xsl:when test="child::node()=sCorpusInfo"><tr><td><a class="tooltip"><b><xsl:value-of select="$SCorpus"/></b><xsl:copy-of select="$InfoImg"/><xsl:call-template name="sCorpusTooltip"/></a></td><td><xsl:apply-templates select="sCorpusInfo" mode="Subcorpora"/></td></tr></xsl:when></xsl:choose><!-\-         <xsl:choose><xsl:when test="count(child::node()=sCorpusInfo) &lt; 2"><xsl:call-template name="structInfoCorpus"/></xsl:when><!-\- insert rule for maincorpus with more than one subcorpus -\-></xsl:choose>-\-></table><xsl:choose><xsl:when test="$accumulation"><!-\-      <xsl:choose><xsl:when test="count(child::node()=sCorpusInfo) &lt; 2"><xsl:call-template name="totalAnnoCorpus"/></xsl:when></xsl:choose>-\-></xsl:when></xsl:choose><xsl:apply-templates mode="totalAnno" select="totalSAnnotationInfo"><!-\- <xsl:with-param name="totalSAnno"
                    select="$MappingList2/elem[@maptype='totalSAnnotationInfo']"/>-\-></xsl:apply-templates></div></xsl:template><!-\-  sums up the subcorpora of a maincorpus  -\-><xsl:template match="sCorpusInfo" mode="Subcorpora"><b><xsl:value-of select="@sName"/></b><xsl:choose><xsl:when test="position()=last()"><xsl:text/></xsl:when><xsl:otherwise><xsl:text>, </xsl:text></xsl:otherwise></xsl:choose></xsl:template>-->

    <!-- table for meta-data -->
    <xsl:template match="metaDataInfo" mode="MetaData">
        <table>
            <thead>
                <tr>
                    <th class="name">
                        <a class="tooltip">
                            <xsl:value-of select="$metaDataInfoMap"/>
                            <xsl:copy-of select="$InfoImg"/>
                            <!-- <xsl:call-template name="metaTooltip"/> -->
                        </a>
                    </th>
                    <th class="values">
                        <div class="btn-minimize">...</div>
                    </th>
                </tr>
            </thead>
            <tbody>
                <xsl:for-each select="entry">
                    <tr>
                        <td>
                            <b>
                                <xsl:value-of select="@key"/>
                            </b>
                        </td>
                        <td>
                            <b>
                                <xsl:value-of select="text()"/>
                            </b>
                        </td>
                    </tr>
                </xsl:for-each>
            </tbody>
        </table>
    </xsl:template>
    <!--  extra-td for the "click"-button in annotations  -->
    <xsl:template name="ToggleTd">
        <td>
            <img align="right" src="http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/down_arrow_circle2.png">
                <xsl:attribute name="onclick">toggle_visibility('

                    <xsl:value-of select="translate(../../@id,'/,-,.','_')"/>
                    <xsl:value-of select="translate(../@sName,'/,-,.','_')"/>
                    _

                    <xsl:value-of select="position()"/>
                    ');</xsl:attribute>
            </img>
        </td>
    </xsl:template>
    <!-- totalAnnotation toggle-td -->
    <xsl:template name="ToggleTdTotal">
        <td>
            <img align="right" src="http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/down_arrow_circle2.png">
                <xsl:attribute name="onclick">toggle_visibility('

                    <xsl:value-of select="translate(../../@id,'/,-,.','_')"/>
                    <xsl:value-of select="translate(../@sName,'/,-,.','_')"/>
                    _

                    <xsl:value-of select="position()"/>
                    _total');</xsl:attribute>
            </img>
        </td>
    </xsl:template>
    <!-- totalAnnotation toggle-td -->
    <xsl:template name="ToggleTdTotal2">
        <td>
            <img align="right" src="http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/down_arrow_circle2.png">
                <xsl:attribute name="onclick">
                    <xsl:text>toggle_visibility('</xsl:text>
                    <xsl:value-of select="translate(../@id,'/,-,.','_')"/>
                    <xsl:value-of select="translate(@sName,'/,-,.','_')"/>
                    _

                    <xsl:value-of select="position()"/>
                    _total2');</xsl:attribute>
            </img>
        </td>
    </xsl:template>
    <!-- structural toggle-td -->
    <xsl:template name="ToggleTd2">
        <td>
            <img align="right" src="http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/down_arrow_circle.png">
                <xsl:attribute name="onclick">toggle_visibility2('

                    <xsl:value-of select="translate(../@id,'/,-,.','_')"/>
                    <xsl:value-of select="translate(@sName,'/,-,.','_')"/>
                    _struct');</xsl:attribute>
            </img>
        </td>
    </xsl:template>
    <!-- annotation toggle-td -->
    <xsl:template name="ToggleTd3">
        <td>
            <img align="right" src="http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/down_arrow_circle.png">
                <xsl:attribute name="onclick">toggle_visibility2('

                    <xsl:value-of select="translate(../@id,'/,-,.','_')"/>
                    <xsl:value-of select="translate(@sName,'/,-,.','_')"/>
                    _anno');</xsl:attribute>
            </img>
        </td>
    </xsl:template>
    <!-- creates a table-row with SName and value plus infobox -->
    <xsl:template name="sName">
        <tr>
            <td>
                <a class="tooltip">
                    <b>
                        <xsl:value-of select="$SName"/>
                    </b>
                    <xsl:copy-of select="$InfoImg"/>
                    <!-- <xsl:call-template name="sNameTooltip"/> -->
                </a>
            </td>
            <td>
                <b>
                    <xsl:value-of select="@sName"/>
                </b>
            </td>
        </tr>
    </xsl:template>
    <xsl:template mode="sName" match="sDocumentInfo|sLayerInfo">
        <tr>
            <td>
                <a class="tooltip">
                    <b>
                        <xsl:value-of select="$SName"/>
                    </b>
                    <xsl:copy-of select="$InfoImg"/>
                    <!-- <xsl:call-template name="sNameTooltip"/> -->
                </a>
            </td>
            <td>
                <b>
                    <xsl:value-of select="@sName"/>
                </b>
            </td>
        </tr>
    </xsl:template>
    <!-- templates for @key-order -->
    <xsl:template match="entry[@key = 'SNode']" mode="SNodeEntry">
        <tr>
            <td>
                <a class="tooltip">
                    <b>
                        <xsl:value-of select="@key"/>
                    </b>
                    <xsl:copy-of select="$InfoImg"/>
                    <!-- <xsl:call-template name="Tooltip"/> -->
                </a>
            </td>
            <td>
                <b>
                    <xsl:value-of select="text()"/>
                </b>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="entry[@key = 'SRelation']" mode="SRelationEntry">
        <tr>
            <td>
                <a class="tooltip">
                    <b>
                        <xsl:value-of select="@key"/>
                    </b>
                    <xsl:copy-of select="$InfoImg"/>
                    <!-- <xsl:call-template name="Tooltip"/> -->
                </a>
            </td>
            <td>
                <b>
                    <xsl:value-of select="text()"/>
                </b>
            </td>
        </tr>
    </xsl:template>
    <!-- creates table-rows for each structural entry and value plus infobox -->
    <xsl:template match="entry" mode="Entry">
        <xsl:choose>
        <xsl:when test="self::node()[@key != 'SNode'][@key != 'SRelation']">
            <!--<xsl:for-each select="current()[@key != 'SRelation']">-->
            <tr>
                <td>
                    <a class="tooltip">
                        <b>
                            <xsl:value-of select="@key"/>
                        </b>
                        <xsl:copy-of select="$InfoImg"/>
                        <!-- <xsl:call-template name="Tooltip"/> -->
                    </a>
                </td>
                <td>
                    <b>
                        <xsl:value-of select="text()"/>
                    </b>
                </td>
            </tr>
            <!--</xsl:for-each>-->
        </xsl:when>
    </xsl:choose>
    </xsl:template>
    <!-- totalSAnnotation-tables -->
    <xsl:template match="totalSAnnotationInfo" mode="totalAnno">
        <table>
            <thead>
                <tr>
                    <th class="name">
                        <a class="tooltip">
                            <xsl:value-of select="$totalSAnnotationInfoMap"/>
                            <xsl:copy-of select="$InfoImg"/>
                            <!-- <xsl:call-template name="totalAnnoTooltip"/> -->
                        </a>
                    </th>
                    <th class="values">
                        <div class="btn-minimize">...</div>
                    </th>
                </tr>
            </thead>
            <tbody>
                <xsl:for-each select="sAnnotationInfo">
                    <xsl:sort select="@sName"/>
                    <tr>
                        <xsl:call-template name="NameAndOccurances"/>
                        <xsl:call-template name="UnderMax"/>
                    </tr>
                </xsl:for-each>
            </tbody>
        </table>
    </xsl:template>
    <!-- shows first n values (n = changeable in style.xslt) -->
    <xsl:template name="FirstOfMax">
        <xsl:for-each select="sValue[position() &lt; $maxSize+1]">
            <xsl:sort select="text()"/>
            <b>
                <xsl:value-of select="text()"/>
            </b>
            <xsl:text> (</xsl:text>
            <xsl:choose>
                <xsl:when test="position()!=last()">
                    <xsl:value-of select="@occurances"/>
                    <xsl:text>), </xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="@occurances"/>
                    <xsl:text>)</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>
    <!-- shows rest of the values -->
    <xsl:template name="RestOfMax">
        <xsl:for-each select="sValue[position() &gt; $maxSize]">
            <xsl:sort select="text()"/>
            <b>
                <xsl:value-of select="text()"/>
            </b>
            <xsl:text> (</xsl:text>
            <xsl:choose>
                <xsl:when test="position()=last()">
                    <xsl:value-of select="@occurances"/>
                    <xsl:text>)</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="@occurances"/>
                    <xsl:text>), </xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>
    <!-- is used, when number of values is lower than n -->
    <xsl:template name="UnderMax">
        <td class="count-data">
            <span class="svalue-data">
                <xsl:for-each select="sValue">
                    <xsl:sort select="text()"/>
                    <span class="svalue">
                        <span class="svalue-text">
                            <xsl:value-of select="text()"/>
                        </span>
                        <span class="svalue-occurances">
                            <xsl:text/>
                            <xsl:value-of select="@occurances"/>
                            <xsl:text/>
                        </span>
                    </span>
                </xsl:for-each>
            </span>
        </td>
    </xsl:template>
    <!-- output of name and occurances, separated by ',' -->
    <xsl:template name="NameAndOccurances">
        <td>
            <span class="sannotationinfo">
                <span class="sannotationinfo-sname">
                    <xsl:value-of select="@sName"/>
                </span>
                <span class="sannotationinfo-count">
                    <xsl:value-of select="sum(sValue/@occurances)"/>
                </span>
            </span>
            <xsl:element name="input">
                <xsl:attribute name="class">btn-toogle-sannotation-dropdown</xsl:attribute>
                <xsl:attribute name="type">button</xsl:attribute>
                <xsl:attribute name="value">Show more</xsl:attribute>
            </xsl:element>
          <!-- <input class="btn-toogle-sannotation-dropdown" type="button" id="YADEADA">

           </input>-->
        </td>
        <!--  </xsl:when><xsl:otherwise><b><xsl:value-of select="@sName"
                    /></b><xsl:text>(</xsl:text><xsl:value-of select="sum(sValue/@occurances)"
                />)</xsl:otherwise></xsl:choose>-->
    </xsl:template>

    <!-- creates only tables for annotated layer -->
    <xsl:template name="ChildNodeControl">
        <xsl:param name="NO_LAYER" select="./sLayerInfo[@sName=NO_LAYER]/@sName"/>
        <xsl:choose>
            <xsl:when test="child::node()=sAnnotationInfo|structuralInfo">
                <tr>
                    <td>
                        <xsl:choose>
                            <xsl:when test="@sName='NO_LAYER'">
                                <a class="tooltip">
                                    <xsl:value-of select="$NO_LAYERMap"/>
                                    <xsl:copy-of select="$InfoImg"/>
                                    <!-- <xsl:call-template name="layerTooltip"/> -->
                                </a>
                            </xsl:when>
                            <xsl:otherwise>
                                <a class="tooltip">
                                    <xsl:text>objects contained in layer </xsl:text>
                                    <xsl:value-of select="@sName"/>
                                    <xsl:copy-of select="$InfoImg"/>
                                    <!-- <xsl:call-template name="layerTooltip"/> -->
                                </a>
                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
                </tr>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <!-- accumulation of structuralInfos for subcorpora -->
    <xsl:template name="structInfoCorpus">
        <xsl:variable name="structEntry" select="//sDocumentInfo/structuralInfo/entry[count(.|key('structEntryKey', @key)[1])=1]"/>
        <xsl:for-each select="$structEntry">
            <tr>
                <td>
                    <a class="tooltip">
                        <b>
                            <xsl:value-of select="@key"/>
                        </b>
                        <xsl:copy-of select="$InfoImg"/>
                        <!-- <xsl:call-template name="Tooltip"/> -->
                    </a>
                </td>
                <td>
                    <b>
                        <xsl:value-of select="sum(//sDocumentInfo/structuralInfo/entry[@key = current()/@key])"/>
                    </b>
                </td>
            </tr>
        </xsl:for-each>
    </xsl:template>
    <!-- accumulation of totalSAnnotationInfo for subcorpora -->
    <!--<xsl:template name="totalAnnoCorpus"><xsl:variable name="totalsNames"
            select="//sDocumentInfo/totalSAnnotationInfo//sAnnotationInfo[count(.|key('totalAnnoName',@sName)[1])=1]"/><xsl:variable name="totalValues" select="sValue[count(.|key('totalValue',.)[1])=1]"/><table><tr><td><a class="tooltip"><!-\- <xsl:value-of select="$MappingList2/elem[@maptype='totalSAnnotationInfo']"/>-\-><xsl:copy-of select="$InfoImg"/><xsl:call-template name="totalAnnoTooltip"/></a></td></tr><xsl:for-each select="$totalsNames"><xsl:sort select="@sName"/><tr><td width="{$tdwidth}" valign="{$tdValignWhenToggled}"><b><xsl:value-of select="@sName"/></b>(<xsl:value-of
                            select="sum(//sDocumentInfo/totalSAnnotationInfo/sAnnotationInfo[@sName = current()/@sName]/@occurances)"
                        />) </td><xsl:choose><xsl:when test="count(sValue) &gt; $maxSize"><td><xsl:for-each
                                    select="//sDocumentInfo/totalSAnnotationInfo/sAnnotationInfo[@sName = current()/@sName]//sValue[position() &lt; $maxSize+1]"><xsl:sort select="text()"/><b><xsl:value-of select="."/></b><xsl:text>&#160;(</xsl:text><xsl:choose><xsl:when test="position()!=last()"><xsl:value-of
                                                select="sum(//sDocumentInfo/totalSAnnotationInfo//sAnnotationInfo[@sName=current()/parent::node()/@sName]//sValue[text()=current()/text()]/@occurances)"/><xsl:text>), </xsl:text></xsl:when><xsl:otherwise><xsl:value-of
                                                select="sum(//sDocumentInfo/totalSAnnotationInfo//sAnnotationInfo[@sName=current()/parent::node()/@sName]//sValue[text()=current()/text()]/@occurances)"/><xsl:text>)</xsl:text></xsl:otherwise></xsl:choose></xsl:for-each><!-\- div for collapsable input -\-><div class="dropdown"><xsl:attribute name="id"><xsl:value-of
                                            select="translate(../@id,'/,-,.','_')"/><xsl:value-of
                                            select="translate(@sName,'/,-,.','_')"
                                            />_<xsl:value-of select="position()"
                                        />_total2</xsl:attribute><xsl:for-each
                                        select="//sDocumentInfo/totalSAnnotationInfo/sAnnotationInfo[@sName = current()/@sName]//sValue[position() &gt; $maxSize]"><xsl:sort select="text()"/><b><xsl:value-of select="text()"/></b><xsl:text>&#160;(</xsl:text><xsl:choose><xsl:when test="position()=last()"><xsl:value-of
                                                  select="sum(//sDocumentInfo/totalSAnnotationInfo//sAnnotationInfo[@sName=current()/parent::node()/@sName]//sValue[text()=current()/text()]/@occurances)"/><xsl:text>)</xsl:text></xsl:when><xsl:otherwise><xsl:value-of
                                                  select="sum(//sDocumentInfo/totalSAnnotationInfo//sAnnotationInfo[@sName=current()/parent::node()/@sName]//sValue[text()=current()/text()]/@occurances)"/><xsl:text>), </xsl:text></xsl:otherwise></xsl:choose></xsl:for-each></div></td><xsl:call-template name="ToggleTdTotal2"/></xsl:when><xsl:otherwise><td valign="{$tdValignWhenToggled}" colspan="{$tdColspanUnderMax}"><xsl:for-each
                                    select="//sDocumentInfo/totalSAnnotationInfo/sAnnotationInfo[@sName = current()/@sName]//sValue"><xsl:sort select="text()"/><b><xsl:value-of select="text()"/></b><xsl:text>&#160;(</xsl:text><xsl:choose><xsl:when test="position()!=last()"><xsl:value-of
                                                select="sum(//sDocumentInfo/totalSAnnotationInfo//sAnnotationInfo[@sName=current()/parent::node()/@sName]//sValue[text()=current()/text()]/@occurances)"/><xsl:text>), </xsl:text></xsl:when><xsl:otherwise><xsl:value-of
                                                select="sum(//sDocumentInfo/totalSAnnotationInfo//sAnnotationInfo[@sName=current()/parent::node()/@sName]//sValue[text()=current()/text()]/@occurances)"/><xsl:text>)</xsl:text></xsl:otherwise></xsl:choose></xsl:for-each></td></xsl:otherwise></xsl:choose></tr></xsl:for-each></table></xsl:template>-->
</xsl:stylesheet>
