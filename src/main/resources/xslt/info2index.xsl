<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output encoding="UTF-8" method="html" indent="yes" doctype-system="about:legacy-compat"/>

    <!-- stylesheet-links -->
    <xsl:variable name="jQuerySrc">js/jquery.js</xsl:variable>
    <xsl:variable name="saltinfojs">js/saltinfo.js</xsl:variable>
    <xsl:variable name="saltinfocss">css/saltinfo.css</xsl:variable>
    <xsl:variable name="indexcss">css/index.css</xsl:variable>

    <!-- logo and alternative logo for internet-explorer -->
    <xsl:variable name="logoSrc">https://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/SaltNPepper_logo2010.svg</xsl:variable>
    <xsl:variable name="logoAlternative">https://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/SaltNPepper_logo2010_large.png</xsl:variable>

    <xsl:template match="/">
        <html>
            <head>
                
                <title><xsl:value-of select="node()/@sName"/>-Overview</title>
                <xsl:call-template name="MetaInfo"/>
                <!--<xsl:call-template name="JavaScriptElem"/>-->
            </head>
            <body>
                <xsl:call-template name="HeadOfHtml"/>
                <div id="container">
                   
                    <div id="navigation">
                        <!--<div id="nav-toggle">x</div>-->
                        <xsl:apply-templates mode="navi"/>
                    </div>
                    
    
                  <!--  <xsl:call-template name="navigation"/>-->
                    <xsl:call-template name="content"/>
                   
                </div>
                <xsl:call-template name="Footer"/>
                <xsl:call-template name="html-tooltips"></xsl:call-template>
            </body>
        </html>


    </xsl:template>
    
    <xsl:template mode="navi" match="text()|@*"></xsl:template>

    <xsl:template mode="navi" match="sCorpusInfo[@rel-location]|saltProjectInfo[@rel-location]">
        <li class="scorpus-item">
            <xsl:call-template name="scorpus-link"/>
            <ul>
                <xsl:apply-templates mode="navi" select="sDocumentInfo|sCorpusInfo|saltProjectInfo"/>
            </ul>
        </li>
       
    </xsl:template>
    
    <xsl:template mode="navi" match="sDocumentInfo[@rel-location]">
        <li>
            <xsl:call-template name="scorpus-link"/>
        </li>
    </xsl:template>
    
    <!-- Creates the link + fake id data attribute to generate the browser history    -->
    <xsl:template name="scorpus-link">
        <a>
            <xsl:attribute name="class">nav-link</xsl:attribute>
            <xsl:attribute name="href" ><xsl:value-of select="@rel-location"/>.html</xsl:attribute>
            <xsl:attribute name="data-id"><xsl:value-of select="@rel-location"/></xsl:attribute>
            <xsl:attribute name="target">content</xsl:attribute>
            <xsl:value-of select="@sName"/>
        </a>
    </xsl:template>

    <!-- get html-stylesheet -->
    <xsl:template name="MetaInfo">
        <!-- <link href="{$treecss}" rel="StyleSheet" type="text/css"/> -->
        <link href="{$saltinfocss}" rel="StyleSheet" type="text/css"/>
        <link href="{$indexcss}" rel="StyleSheet" type="text/css"/>
        <script src="{$jQuerySrc}" type="text/javascript"/>
        <script src="{$saltinfojs}" type="text/javascript"/>
    </xsl:template>


    <!-- create the header of the html -->
    <xsl:template name="HeadOfHtml">
        <div id="header">
            <img alt="{$logoAlternative}" href="http://korpling.german.hu-berlin.de/saltnpepper" src="{$logoSrc}"/>
            <h1>
                <xsl:value-of select="node()/@sName"/>
                <xsl:text>- Overview</xsl:text>
            </h1>
            <p>generated with SaltNPepper</p>
        </div>
    </xsl:template>

    <!-- create the clickable tree-table -->
    <xsl:template name="content">
        <div id="content">
           <!-- <iframe>
                <xsl:attribute name="src">

                </xsl:attribute>
                <xsl:attribute name="name">content</xsl:attribute>
            </iframe>-->
        </div>
    </xsl:template>

    <!-- create html-footer -->
    <xsl:template name="Footer">
        <div id="footer">
            <xsl:text>the here presented Information of the corpus where generated by the SaltInfoModule, part of the saltNpepper project, please see </xsl:text>
            <a href="http://korpling.german.hu-berlin.de/saltnpepper">http://korpling.german.hu-berlin.de/saltnpepper</a>

            <div class="date">
                <xsl:text> generated on </xsl:text>
                <xsl:value-of select="node()/@generatedOn"/>
            </div>
            <div class="impressum">
                <a href="http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/impressum.html">
                    <xsl:text>Impressum</xsl:text></a>
            </div>
        </div>
    </xsl:template>
    
    <xsl:template name="html-tooltips">
        <div class="data-tooltips">
            
            <p style="display:none" id="SPointingRelation" class="tooltip-text">Number of relations in the current document or corpus for loose connections like anaphoric relations. </p>
            
            <p style="display:none" id="SSpanningRelation" class="tooltip-text">Number of relations in the current document or corpus to connect ps (SSpan) with tokens (SToken).</p>
            
            <p style="display:none" id="STextualDS" class="tooltip-text">Number of textual primary data in the current document or corpus.</p>
            
            <p style="display:none" id="SSpan" class="tooltip-text">Number of ps in the current document or corpus. A p is an aggregation of a bunch of tokens containing 0..n token.</p>
            
            <p style="display:none" id="STextualRelation" class="tooltip-text">Number of relations in the current document or corpus to connect a token (SToken) with a textual data source (STextualDS).</p>
            
            <p style="display:none" id="SToken" class="tooltip-text">Number of token (smallest annotatable unit) in the current document or corpus.</p>
            
            <p style="display:none" id="SRelation" class="tooltip-text">Total number of all relations in the current document or corpus. An SRelation is an abstract relation which could be instantiated as e.g. STextualRelation, SSPanningRelation and SDominanceRelation.</p>
            
            
            <p style="display:none" id="SStructure" class="tooltip-text">Number of hierarchical structures in the current document or corpus. SStructure objects in Salt are used to represent hierarchies e.g. for constituents.</p>
            
            <p style="display:none" id="SNode" class="tooltip-text">Total number of all nodes in the current document or corpus. An SNode is an abstract node which could be instantiated as e.g. STextualDS, SToken, SSpan.</p>
            
            <p style="display:none" id="SSpanningRelation" class="tooltip-text">Number of relations in the current document or corpus to connect ps (SSpan) with tokens (SToken).</p>
            
            <p style="display:none" id="SDominanceRelation" class="tooltip-text">Number of relations in the current document or corpus to connect hierarchical nodes like SStructure with other SNode objects. This relation class is used to represent for e.g. constituents relations.</p>
            
            <p style="display:none" id="SOrderRelation" class="tooltip-text">Number of relations in the current document or corpus to order SNode objects. This class is used to manage conflicting token levels as they can occur for instance in dialogues.</p>
            
            <p style="display:none" id="STimeline" class="tooltip-text">In Salt a common timeline exists, which can be used to identify the chronological occurance of a token. For instance to identify if one token corresponding to one text occurs before or after another token corresponding to another text. This would be important in dialogue corpora.</p>
            
            <p style="display:none" id="STimelineRelation" class="tooltip-text">Number of relations in the current document or corpus to connect a token (SToken) with the common timeline (STimeline).</p>
            
            <p style="display:none" id="Default" class="tooltip-text">No description available.</p>
            
        </div>
    </xsl:template>
</xsl:stylesheet>