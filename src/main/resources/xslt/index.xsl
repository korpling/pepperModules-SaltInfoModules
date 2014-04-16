<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    
    <xsl:output encoding="UTF-8" indent="yes" method="html"/>
    
    <!-- stylesheet-links -->
    <xsl:variable name="jQuerySrc">js/jquery.js</xsl:variable>
    <xsl:variable name="saltinfojs">js/saltinfo.js</xsl:variable>
    <xsl:variable name="saltinfocss">css/saltinfo.css</xsl:variable>
    <xsl:variable name="indexcss">css/index.css</xsl:variable>
    
    <!-- logo and alternative logo for internet-explorer -->
    <xsl:variable name="logoSrc">https://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/SaltNPepper_logo2010.svg</xsl:variable>
    <xsl:variable name="logoAlternative">https://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/SaltNPepper_logo2010_large.png</xsl:variable>
    
    <!-- create index.html-framework -->
    <xsl:template match="/" name="HtmlStructure">
        <html>
            <head>
                <title><xsl:value-of select="node()/@sName"/>-Overview</title>
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
    
    
    <!-- get html-stylesheet -->
    <xsl:template name="MetaInfo">
        <!-- <link href="{$treecss}" rel="StyleSheet" type="text/css"/> -->
        <link href="{$saltinfocss}" rel="StyleSheet" type="text/css"/>
        <link href="{$indexcss}" rel="StyleSheet" type="text/css"/>
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
                <li class="saltproject-item">
                    <xsl:element name="a">
                        <xsl:attribute name="href">#salt:rootCorpus&quot;/&gt;</xsl:attribute>
                        <xsl:value-of select="node()/@sName"/>
                    </xsl:element>
                </li>
                <xsl:call-template name="project"/>
            </ul>
        </div>
    </xsl:template>
    
    <!-- set name of the project as table header -->
    <xsl:template name="project">
        <xsl:element name="ul">
            <xsl:attribute name="name">
                <xsl:value-of select="@sName"/>
            </xsl:attribute>
            <xsl:attribute name="class">saltProjectInfo</xsl:attribute>
            <xsl:apply-templates mode="maincorpora" select="//saltProjectInfo/sCorpusInfo"/>
        </xsl:element>
    </xsl:template>
    
    <!-- get all maincorpora -->
    <xsl:template match="//saltProjectInfo/sCorpusInfo" mode="maincorpora">
        <xsl:element name="li">
            <xsl:attribute name="class">scorpus-item</xsl:attribute>
            <xsl:element name="a">
                <xsl:attribute name="href">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
                <xsl:value-of select="@sName"/>
            </xsl:element>
        </xsl:element>
        <xsl:element name="ul">
            <xsl:apply-templates mode="subcorpora" select="//sCorpusInfo/sCorpusInfo"/>
            <xsl:choose>
                <xsl:when test="child::node() = sDocumentInfo">
            <xsl:apply-templates mode="documents">
                <xsl:sort select="current()/@sName"/>
            </xsl:apply-templates>
                </xsl:when>
            </xsl:choose>
        </xsl:element>
    </xsl:template>
    
    <!-- get all subcorpora -->
    <xsl:template match="//sCorpusInfo/sCorpusInfo" mode="subcorpora">
        <xsl:element name="li">
            <xsl:attribute name="class">scorpus-item</xsl:attribute>
            <xsl:element name="a">
                <xsl:attribute name="href">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
                <xsl:value-of select="@sName"/>
                <!--            <xsl:apply-templates/>-->
            </xsl:element>
        </xsl:element>
        <xsl:element name="ul">
            <xsl:apply-templates mode="documents">
                <xsl:sort select="current()/@sName"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>
    
    <!-- get all documents -->
    <xsl:template match="sDocumentInfo" mode="documents">
        <xsl:element name="li">
            <xsl:attribute name="class">sdocument-item</xsl:attribute>
            <xsl:element name="a">
                <xsl:attribute name="href">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
                <xsl:value-of select="@sName"/>
            </xsl:element>
        </xsl:element>
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
            <iframe>
                <xsl:attribute name="src">
                    
                </xsl:attribute>
            </iframe>
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
</xsl:stylesheet>