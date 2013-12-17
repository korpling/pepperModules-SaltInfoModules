<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    
    <!-- declare output-conditions -->
    <xsl:output encoding="UTF-8" indent="yes" method="html"/>
    
    <!-- stylesheet-links -->
    <xsl:variable name="jQuerySrc">js/jquery.js</xsl:variable>
    <xsl:variable name="saltinfojs">js/saltinfo.js</xsl:variable>
    <xsl:variable name="saltinfocss">css/saltinfo.css</xsl:variable>
    
    <!-- logo (and alternative logo for internet-explorer) -->
    <xsl:variable name="logoSrc">https://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/SaltNPepper_logo2010.svg</xsl:variable>
    <xsl:variable name="logoAlternative">https://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/SaltNPepper_logo2010_large.png</xsl:variable>
    
    
    <!-- create index.html-framework -->
    <xsl:template match="/" name="HtmlStructure">
        <html>
            <head>
                <title><xsl:value-of select="@sName"/>-Overview</title>
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
    
    <!-- get title of page -->
    <xsl:template name="Title">
        <title><xsl:value-of select="@sName"/>-Overview</title>
    </xsl:template>
    
    <!-- get html-stylesheet -->
    <xsl:template name="MetaInfo">
        <!-- <link href="{$treecss}" rel="StyleSheet" type="text/css"/> -->
        <link href="{$saltinfocss}" rel="StyleSheet" type="text/css"/>
    </xsl:template>
    
    <!-- contains javascript-information and computes array-elements -->
    <xsl:template name="JavaScriptElem">
        <script src="{$jQuerySrc}" type="text/javascript"/>
        <script src="{$saltinfojs}" type="text/javascript"/>
    </xsl:template>
    
    <!-- create a nested list that corresponds to the corpus tree -->
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
    
    <!-- create the header of the html -->
    <xsl:template name="HeadOfHtml">
        <div id="header">
            <img alt="{$logoAlternative}" href="http://korpling.german.hu-berlin.de/saltnpepper" src="{$logoSrc}"/>
            <h1>
                <xsl:value-of select="@sName"/>
                <xsl:text>- Overview</xsl:text>
            </h1>
            <p>generated with SaltNPepper</p>
        </div>
    </xsl:template>
    
    <!-- create the clickable tree-table -->
    <xsl:template name="content">
        <div id="content">
            <!--<xsl:call-template name="sCorpusInfoTables"/>
            <xsl:call-template name="sDocumentInfoTables"/>-->
        </div>
    </xsl:template>
    
    <!-- create html-footer -->
    <xsl:template name="Footer">
        <div id="footer">
            <xsl:text>the here presented Information of the corpus where generated by the SaltInfoModule, part of the saltNpepper project, please see </xsl:text>
            <a href="http://korpling.german.hu-berlin.de/saltnpepper">http://korpling.german.hu-berlin.de/saltnpepper</a>
            <xsl:text> generated on</xsl:text>
            <xsl:value-of select="@generatedOn"/>
            <div class="impressum">
                <a href="http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/impressum.html">
                    <xsl:text>Impressum</xsl:text></a>
            </div>
        </div>
    </xsl:template>
</xsl:stylesheet>