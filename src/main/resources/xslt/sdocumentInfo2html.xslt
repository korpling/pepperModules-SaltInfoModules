<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="variables.xslt"/>
    <xsl:import href="style.xslt"/>
    <xsl:variable name="InfoImg">
        <img class="infoimg" align="{$InfoImgAlign}" alt="" src="{$InfoImgSrc}"/>
    </xsl:variable>
    
    <xsl:template match="/">
        <html>
            <head/>
            <body>
                <div class="data-view">
                    <xsl:attribute name="id">
                        <xsl:value-of select="node()/@id"/>
                    </xsl:attribute>
                    <h2>
                        <xsl:value-of select="node()/@sName"/>
                    </h2>
                    <div class="layer-overview">
                        <xsl:text>Layers:</xsl:text>
                        <xsl:for-each select="//sLayerInfo">
                            <a class="slayer-link">
                                <xsl:attribute name="href">
                                    <xsl:text>#</xsl:text>
                                    <xsl:value-of
                                        select="translate(parent::node()/@id,':,/,-,.','')"/>
                                    <xsl:text>_layer_</xsl:text>
                                    <xsl:value-of select="node()/@sName"/>
                                </xsl:attribute>
                                <xsl:value-of select="node()/@sName"/>
                            </a>
                            
                        </xsl:for-each>
                    </div>
                    <xsl:call-template name="sDocumentInfoTable"/>
                </div>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template name="sDocumentInfoTable">
        <xsl:param name="NO_LAYER" select="./sLayerInfo[@sName=NO_LAYER]/@sName"/>
        <xsl:apply-templates mode="MetaData" select="//metaDataInfo">
            <!-- <xsl:with-param name="metaDataInfo"
                    select="$MappingList2/elem[@maptype='metaDataInfo']"/>-->
        </xsl:apply-templates>
        <xsl:apply-templates select="//structuralInfo"/>
        <xsl:apply-templates mode="totalAnno" select="//totalSAnnotationInfo">
            <!--            <xsl:with-param name="totalSAnno" select="$totalSAnnotationInfoMap"/>-->
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
                        <!--                        <xsl:with-param name="NO_LAYER" select="$NO_LAYERMap"/>-->
                    </xsl:call-template>
                </h3>
                <xsl:apply-templates select="//structuralInfo"/>
                <xsl:choose>
                    <xsl:when test="child::node()=sAnnotationInfo">
                        <xsl:call-template name="annotationTable"/>
                    </xsl:when>
                </xsl:choose>
            </div>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="ChildNodeControl">
        <xsl:param name="NO_LAYER" select="./sLayerInfo[@sName=NO_LAYER]/@sName"/>
        <xsl:choose>
            <xsl:when test="child::node()=sAnnotationInfo|structuralInfo">
                <tr>
                    <td>
                        <xsl:choose>
                            <xsl:when test="@sName='NO_LAYER'">
                                <a class="tooltip">
                                    <!--                                    <xsl:value-of select="$NO_LAYERMap"/>-->
                                    <!--                                    <xsl:copy-of select="$InfoImg"/>-->
                                    <!-- <xsl:call-template name="layerTooltip"/> -->
                                </a>
                            </xsl:when>
                            <xsl:otherwise>
                                <a class="tooltip">
                                    <xsl:text>objects contained in layer </xsl:text>
                                    <xsl:value-of select="@sName"/>
                                    <!--                                    <xsl:copy-of select="$InfoImg"/>-->
                                    <!-- <xsl:call-template name="layerTooltip"/> -->
                                </a>
                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
                </tr>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="annotationTable">
        <table>
            <thead>
                <tr>
                    <th class="name">
                        <a class="tooltip">
                            <b>
                                <!--                                <xsl:value-of select="$sAnnotationInfoMap"/>-->
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
                            <xsl:value-of select="translate(../@sName,'/,-,.','_')"/>_anno
                            dropdown</xsl:attribute>
                        <xsl:call-template name="NameAndOccurances"/>
                        <xsl:call-template name="UnderMax"/>
                    </tr>
                </xsl:for-each>
            </tbody>
        </table>
    </xsl:template>
    
    <!-- output of name and occurrences, separated by ',' -->
    <xsl:template name="NameAndOccurances">
        <td>
            <span class="sannotationinfo">
                <span class="sannotationinfo-sname">
                    <xsl:value-of select="@sName"/>
                </span>
                <span class="sannotationinfo-count">
                    <xsl:value-of select="sum(sValue/@occurrences)"/>
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
                    /></b><xsl:text>(</xsl:text><xsl:value-of select="sum(sValue/@occurrences)"
                />)</xsl:otherwise></xsl:choose>-->
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
                        <span class="svalue-occurrences">
                            <xsl:text/>
                            <xsl:value-of select="@occurrences"/>
                            <xsl:text/>
                        </span>
                    </span>
                </xsl:for-each>
            </span>
        </td>
    </xsl:template>
    
    <!-- table for meta-data -->
    <xsl:template match="//metaDataInfo" mode="MetaData">
        <table>
            <thead>
                <tr>
                    <th class="name">
                        <a class="tooltip">
                            <!--                            <xsl:value-of select="$metaDataInfoMap"/>-->
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
    
    <xsl:template match="structuralInfo">
        <table class="structuralinfo-table">
            <thead>
                <tr>
                    <th class="name">
                        <a class="tooltip">
                            <!--                            <xsl:value-of select="$structuralInfoMap"/>-->
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
    
    <!-- totalSAnnotation-tables -->
    <xsl:template match="//totalSAnnotationInfo" mode="totalAnno">
        <table>
            <thead>
                <tr>
                    <th class="name">
                        <a class="tooltip">
                            <!--                            <xsl:value-of select="$totalSAnnotationInfoMap"/>-->
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
</xsl:stylesheet>
