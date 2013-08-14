<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0" xmlns:msxsl="urn:schemas-microsoft-com:xslt">
    <!--    variables to entry/@key    -->
    <xsl:variable name="STextualDS">
        <xsl:value-of
            select="saltProjectInfo/sCorpusInfo/sCorpusInfo/sDocumentInfo/structuralInfo/entry[@key = 'STextualDS']/@key"
        />
    </xsl:variable>
    <xsl:variable name="SToken">
        <xsl:value-of
            select="saltProjectInfo/sCorpusInfo/sCorpusInfo/sDocumentInfo/structuralInfo/entry[@key = 'SToken']/@key"
        />
    </xsl:variable>
    <xsl:variable name="SSpan" select="saltProjectInfo/sCorpusInfo/sCorpusInfo/sDocumentInfo/structuralInfo/entry[@key = 'SSpan']/@key"> 
    </xsl:variable>
    
    <xsl:variable name="STextualRelation">
        <xsl:value-of
            select="saltProjectInfo/sCorpusInfo/sCorpusInfo/sDocumentInfo/structuralInfo/entry[@key = 'STextualRelation']/@key"
        />
    </xsl:variable>
    <xsl:variable name="SSpanningRelation">
        <xsl:value-of
            select="saltProjectInfo/sCorpusInfo/sCorpusInfo/sDocumentInfo/structuralInfo/entry[@key = 'SSpanningRelation']/@key"
        />
    </xsl:variable>
    <xsl:variable name="SPointingRelation">
        <xsl:value-of
            select="saltProjectInfo/sCorpusInfo/sCorpusInfo/sDocumentInfo/structuralInfo/entry[@key = 'SPointingRelation']/@key"
        />
    </xsl:variable>
    <xsl:variable name="SRelation"><xsl:value-of
        select="saltProjectInfo/sCorpusInfo/sCorpusInfo/sDocumentInfo/structuralInfo/entry[@key = 'SRelation']/@key"
    /></xsl:variable>
    <xsl:variable name="SNode">
        <xsl:value-of
            select="saltProjectInfo/sCorpusInfo/sCorpusInfo/sDocumentInfo/structuralInfo/entry[@key = 'SNode']/@key"
        />
    </xsl:variable>
    <xsl:variable name="SStructure">
        <xsl:value-of
            select="saltProjectInfo/sCorpusInfo/sCorpusInfo/sDocumentInfo/structuralInfo/entry[@key = 'SStructure']/@key"
        />
    </xsl:variable>
    <xsl:variable name="SDominanceRelation">
        <xsl:value-of
            select="saltProjectInfo/sCorpusInfo/sCorpusInfo/sDocumentInfo/structuralInfo/entry[@key = 'SDominanceRelation']/@key"
        />
    </xsl:variable>
    <xsl:variable name="SOrderRelation">
        <xsl:value-of
            select="saltProjectInfo/sCorpusInfo/sCorpusInfo/sDocumentInfo/structuralInfo/entry[@key = 'SOrderRelation']/@key"
        />
    </xsl:variable>
    
    <xsl:key name="hierarchicalTypeKey" match="//sLayerInfo/sAnnotationInfo" use="@type"/>
    <!-- Mappinglists -->
<!--    <xsl:variable name="MappingList1">
        <attr maptype="SToken">token annotations</attr>
        <attr maptype="SSpan">spanning annotations</attr>
        <attr maptype="SStructure">hierarchical annotations</attr>
        <attr maptype="SPointingRelation">pointing relations</attr>
        <attr maptype="SDominanceRelation">dominance relations</attr>
        <attr maptype="SSpanningRelation">spanning relations</attr>
        <attr maptype="STextualRelation">textual relations</attr>
        <attr maptype="SOrderRelations">order relations</attr>
        <attr maptype="SRelation">all relations</attr>
        <attr maptype="SNode">nodes</attr>
        <attr maptype="STextualDS"></attr>
    </xsl:variable>-->
<!--    <xsl:variable name="MappingList2">
        <elem maptype="metaDataInfo">meta data</elem>
        <elem maptype="structuralInfo">structural info</elem>
        <elem maptype="sAnnotationInfo">annotations</elem>
        <elem maptype="totalSAnnotationInfo">all annotations</elem>
        <elem maptype="NO_LAYER">objects having no layer</elem>
    </xsl:variable>-->
    <xsl:variable name="metaDataInfoMap">meta data</xsl:variable>
    <xsl:variable name="structuralInfoMap">structural info</xsl:variable>
    <xsl:variable name="sAnnotationInfoMap">annotations</xsl:variable>
    <xsl:variable name="totalSAnnotationInfoMap">all annotations</xsl:variable>
    <xsl:variable name="NO_LAYERMap">objects having no layer</xsl:variable>
    
    
    <!-- further variables -->
    <xsl:variable name="SName">
        <xsl:value-of select="translate(name(//@sName),'s','S')"/>
    </xsl:variable>
    <xsl:variable name="SDocument">
        <xsl:value-of select="substring-before(translate(name(//sDocumentInfo),'s','S'),'Info')"/>
    </xsl:variable>
    <xsl:variable name="SCorpus">
        <xsl:value-of select="concat(translate(substring(name(//sCorpusInfo), 1, 1),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'), substring-before(substring(name(//sCorpusInfo),2),'Info'))"/>
    </xsl:variable>
    
    
    <!-- describtion for key-tooltips -->
    <xsl:template name="Tooltip">
        <span>
            <b>
                
                <xsl:value-of select="@key"/>
            </b>
            <xsl:choose>
                <xsl:when test="@key = $SPointingRelation">
                    Number of relations in the current document or corpus for loose connections like anaphoric relations.
                </xsl:when>
                <xsl:when test="@key = $SSpanningRelation">
                    Number of relations in the current document or corpus to connect spans (SSpan) with tokens (SToken).
                </xsl:when>
                <xsl:when test="@key = $STextualDS">
                    Number of textual primary data in the current document or corpus.
                </xsl:when>
                <xsl:when test="@key = $SSpan">
                    Number of spans in the current document or corpus. A span is an aggregation of a bunch of tokens containing 0..n token.
                </xsl:when>
                <xsl:when test="@key = $STextualRelation">
                    Number of relations in the current document or corpus to connect a token (SToken) with a textual data source (STextualDS).
                </xsl:when>
                <xsl:when test="@key = $SToken">
                    Number of token (smallest annotatable unit) in the current document or corpus.
                </xsl:when>
                <xsl:when test="@key = $SRelation">
                    Total number of all relations in the current document or corpus. An SRelation is an abstract relation which could be instantiated as e.g. STextualRelation, SSPanningRelation and SDominanceRelation.
                </xsl:when>
                <xsl:when test="@key = $SStructure">
                    Number of hierarchical structures in the current document or corpus. SStructure objects in Salt are used to represent hierarchies e.g. for constituents.
                </xsl:when>
                <xsl:when test="@key = $SNode">
                    Total number of all nodes in the current document or corpus. An SNode is an abstract node which could be instantiated as e.g. STextualDS, SToken, SSpan.
                </xsl:when>
                <xsl:when test="@key = $SSpanningRelation">
                    Number of relations in the current document or corpus to connect spans (SSpan) with tokens (SToken).
                </xsl:when>
                <xsl:when test="@key = $SDominanceRelation">
                    Number of relations in the current document or corpus to connect hierarchical nodes like SStructure with other SNode objects. This relation class is used to represent for e.g. constituents relations.
                </xsl:when>
                <xsl:when test="@key = $SOrderRelation">
                    Number of relations in the current document or corpus to order SNode objects. This class is used to manage conflicting token levels as they can occur for instance in dialogues.
                </xsl:when>
                <xsl:when test="@key = 'STimeline'">
                    In Salt a common timeline exists, which can be used to identify the chronological occurance of a token. For instance to identify if one token corresponding to one text occurs before or after another token corresponding to another text. This would be important in dialogue corpora.
                </xsl:when>
                <xsl:when test="@key = 'STimelineRelation'">
                    Number of relations in the current document or corpus to connect a token (SToken) with the common timeline (STimeline).
                </xsl:when>
                <xsl:otherwise>
                    No description available.
                </xsl:otherwise>
                <!-- tooltips for tableheader -->
                </xsl:choose>
        </span>
    </xsl:template>
    
    <!-- describtion for sName-tooltip -->
    <xsl:template name="sNameTooltip">
        <span>
            <b>
                <xsl:value-of select="$SName"/>
            </b>
            Indicates the name of the represented document or corpus.
        </span>
    </xsl:template>

    <!-- describtion for sDocument-Tooltip -->
    <xsl:template name="sDocumentTooltip">
        <span>
            <b>
                <xsl:value-of select="$SDocument"/>
            </b>
            Linguistic documents (SDocument objects) being contained in the current corpus (SCorpus objects).
        </span>
    </xsl:template>
    
    <!-- describtion for sCorpus-Tooltip -->
    <xsl:template name="sCorpusTooltip">
        <span>
            <b>
                <xsl:value-of select="$SCorpus"/>
            </b>
            Linguistic documents (SDocument objects) being contained in the current corpus (SCorpus objects).
        </span>
    </xsl:template>
    
    <!-- describtion for each annotationlayer-tooltip  -->
    <xsl:template name="layerTooltip">
        <span>
            <xsl:choose>
                <xsl:when test="@sName='NO_LAYER'">
                    <b>
                        <!-- <xsl:value-of select="$MappingList2/elem[@maptype='NO_LAYER']"/> -->
                    </b>
                </xsl:when>
                <xsl:otherwise>
                    <b>
                        <xsl:value-of select="@sName"/>
                    </b>
                </xsl:otherwise>
            </xsl:choose>
                    A layer (SLayer) is a structure to group nodes and edges together into a sub-graph. Layers are often used to group a bunch of nodes and edges sharing a common semantic e.g. nodes and edges belonging to morpho-syntax can be grouped into a morpho-syntactical layer. Other examples can be layers for syntax, information structure, coreference etc.
           </span>
    </xsl:template>
    
    <!-- describtion for metaDataInfo-tooltip -->
    <xsl:template name="metaTooltip">
        <span>
            <b>
                <!-- <xsl:value-of select="$MappingList2/elem[@maptype='metaDataInfo']"/> -->
            </b>
            This section displays all the metadata of the corpus for instance the annotator's name, the author of the primary text etc. The content of this representation depends of which data are annotated in the corpus.
        </span>
    </xsl:template>
   
   <!-- describtion for structuralInfo-tooltip -->
   <xsl:template name="structTooltip">
       <span>
           <b>
               <!-- <xsl:value-of select="$MappingList2/elem[@maptype='structuralInfo']"/> -->
           </b>
           This section displays information about the structure of the corpus. For instance you can see the amount of tokens and primary texts. Since in Salt everything is a graph, you can see here all nodes and relations of your corpus.
       </span>
   </xsl:template>
    
    <!-- describtion for totalSAnnotation-tooltip -->
    <xsl:template name="totalAnnoTooltip">
        <span>
            <b>
                <!-- <xsl:value-of select="$MappingList2/elem[@maptype='totalSAnnotationInfo']"/> -->
            </b>
            This section displays the overall utterance of an annotation. For instance if 5 nodes are annotated with a POS annotation, the number of POS annotations in this section is 5. Even if 3 of them belongs to a morphology layer and 2 of them to a syntactical layer. Please note, that a node annotated like this can also belong to two several layers (e.g. to a morphology and syntax layer as well).
        </span>
    </xsl:template>
    
    <xsl:template name="AnnoTooltip">
        <span>
            <b>
                <!-- <xsl:value-of select="$MappingList2/elem[@maptype='sAnnotationInfo']"/> -->
            </b>
            This section displays all annotations being contained in this layer. Please note, that these annotations can belong to nodes or edges of different subtypes.
        </span>
    </xsl:template>
    
    <xsl:template name="Tooltip2">
        <span>
            <b>
                <xsl:value-of select="@type"/>
            </b>
            <xsl:choose>
                <xsl:when test="@type = 'SPointingRelation'">
                    Number of relations in the current document or corpus for loose connections like anaphoric relations.
                </xsl:when>
                <xsl:when test="@type = 'SSpanningRelation'">
                    Number of relations in the current document or corpus to connect spans (SSpan) with tokens (SToken).
                </xsl:when>
                <xsl:when test="@type = 'STextualDS'">
                    Number of textual primary data in the current document or corpus.
                </xsl:when>
                <xsl:when test="@type = 'SSpan'">
                    Number of spans in the current document or corpus. A span is an aggregation of a bunch of tokens containing 0..n token.
                </xsl:when>
                <xsl:when test="@type = 'STextualRelation'">
                    Number of relations in the current document or corpus to connect a token (SToken) with a textual data source (STextualDS).
                </xsl:when>
                <xsl:when test="@type = 'SToken'">
                    Number of token (smallest annotatable unit) in the current document or corpus.
                </xsl:when>
                <xsl:when test="@type = 'SRelation'">
                    Total number of all relations in the current document or corpus. An SRelation is an abstract relation which could be instantiated as e.g. STextualRelation, SSPanningRelation and SDominanceRelation.
                </xsl:when>
                <xsl:when test="@type = 'SStructure'">
                    Number of hierarchical structures in the current document or corpus. SStructure objects in Salt are used to represent hierarchies e.g. for constituents.
                </xsl:when>
                <xsl:when test="@type = 'SNode'">
                    Total number of all nodes in the current document or corpus. An SNode is an abstract node which could be instantiated as e.g. STextualDS, SToken, SSpan.
                </xsl:when>
                <xsl:when test="@type = 'SSpanningRelation'">
                    Number of relations in the current document or corpus to connect spans (SSpan) with tokens (SToken).
                </xsl:when>
                <xsl:when test="@type = 'SDominanceRelation'">
                    Number of relations in the current document or corpus to connect hierarchical nodes like SStructure with other SNode objects. This relation class is used to represent for e.g. constituents relations.
                </xsl:when>
                <xsl:when test="@type = 'SOrderRelation'">
                    Number of relations in the current document or corpus to order SNode objects. This class is used to manage conflicting token levels as they can occur for instance in dialogues.
                </xsl:when>
                <xsl:when test="@type = 'STimeline'">
                    In Salt a common timeline exists, which can be used to identify the chronological occurance of a token. For instance to identify if one token corresponding to one text occurs before or after another token corresponding to another text. This would be important in dialogue corpora.
                </xsl:when>
                <xsl:when test="@type = 'STimelineRelation'">
                    Number of relations in the current document or corpus to connect a token (SToken) with the common timeline (STimeline).
                </xsl:when>
                <xsl:otherwise>
                    No description available.
                </xsl:otherwise>
                
                <!-- tooltips for tableheader -->
            </xsl:choose>
        </span>
    </xsl:template>
    
    <!--<xsl:template match="@key" mode="ReplaceFunct">
        <xsl:variable name="Replacements" select="current()"/>
         <xsl:variable name="Mapping" select="$MappingList1/*[@maptype=$Replacements]"/> 
        <xsl:choose>
            <xsl:when test="$Mapping">
                <xsl:value-of select="$Mapping"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>-->
    </xsl:stylesheet>