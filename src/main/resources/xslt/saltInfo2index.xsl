<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output encoding="UTF-8" method="xhtml" indent="yes" doctype-public="html"/>
    <xsl:template match="saltProjectInfo">
        <html>
            <head>
                <meta charset="utf-8"/>
                <title>
                    <xsl:value-of select="@sName"/>
                </title>
                <!-- include the jQuery library -->
                <script src="dist/libs/jquery.js" type="text/javascript"/>
                <!-- include the minified jstree source -->
                <script src="dist/jstree.min.js" type="text/javascript"/>
                <script type="text/javascript" src="params.json"/>
                <script src="js/saltinfo.js" type="text/javascript"/>

                <!-- load the theme CSS file -->
                <link rel="stylesheet" href="./font-awesome/css/font-awesome.min.css"/>
                <link rel="stylesheet" href="dist/themes/default/style.min.css"/>
                <link rel="stylesheet" type="text/css" href="css/style.css"/>
                <script type="text/javascript">
                    <xsl:call-template name="jstree"/>
                </script>
            </head>
            <body>
                <xsl:call-template name="body"/>
            </body>
        </html>
    </xsl:template>

    <!-- build the jstree -->
    <xsl:template name="jstree"> 
        $(function() {
        $('#jstree2').jstree(
        { 
        'core' : { 'data' : 
        [
            <xsl:apply-templates select="sCorpusInfo" mode="main">
            <xsl:sort select="@sName"/>
        </xsl:apply-templates>
        ]
        }
        }).bind( "select_node.jstree", function(e,
        data) { var href = data.instance.get_node(data.selected[0]).original.metadata.href
        $('#content').load(href); }) }); 
    </xsl:template>

    <!-- get all main corpora -->
    <xsl:template match="sCorpusInfo" mode="main"> { 
        "text" : "<xsl:value-of select="@sName"/>",
        "metadata" : { "href" : "./<xsl:value-of select="replace(@rel-location, 'xml','html')"/>" },
        "state" : { "opened" : true }, icon : "fa fa-folder-o"<xsl:if
            test="not(empty(child::node()))">, 
            "children" : [ 
            <xsl:apply-templates
                select="sCorpusInfo" mode="sub">
                <xsl:sort select="@sName"/>
            </xsl:apply-templates> ] </xsl:if>
        }</xsl:template>

    <!-- get all subcorpora of the corpus -->
    <xsl:template match="sCorpusInfo" mode="sub"> { 
        "text" : "<xsl:value-of select="@sName"/>",
        "metadata" : { "href" : "./<xsl:value-of select="replace(@rel-location, 'xml','html')"/>" },
        icon : "fa fa-folder-o"<xsl:if test="not(empty(*))">, 
        "children" : 
        [ <xsl:apply-templates
                select="sCorpusInfo" mode="sub">
                                        <xsl:sort select="@sName" />
        </xsl:apply-templates>
            <xsl:apply-templates select="sDocumentInfo">
                                    <xsl:sort select="@sName"/>
            </xsl:apply-templates> 
            ] 
        </xsl:if> }<xsl:if test="not(empty(following-sibling::node()))">,</xsl:if>
    </xsl:template>
    
    <!-- get all subcorpora of the corpus -->
    <xsl:template match="sCorpusInfo" mode="subsub"> { 
        "text" : "<xsl:value-of select="@sName"/>",
        "metadata" : { "href" : "./<xsl:value-of select="replace(@rel-location, 'xml','html')"/>" },
        icon : "fa fa-folder-o"<xsl:if test="not(empty(*))">, 
            "children" : 
            [ <xsl:apply-templates
                select="sCorpusInfo" mode="subsub">
                <xsl:sort select="@sName" />
            </xsl:apply-templates>
            <xsl:apply-templates select="sDocumentInfo">
                <xsl:sort select="@sName"/>
            </xsl:apply-templates> 
            ] 
        </xsl:if> }<xsl:if test="not(empty(following-sibling::node()))">,</xsl:if>
    </xsl:template>
    

    <!-- get all documents of the corpus/ subcorpus -->
    <xsl:template match="sDocumentInfo"> { 
        "text" : "<xsl:value-of select="@sName"/>", 
        icon : "fa fa-file-o", 
        "metadata" : { "href" : "./<xsl:value-of select="replace(@rel-location, 'xml','html')"/>" }
        }<xsl:if test="not(empty(following-sibling::node()))">,</xsl:if>
    </xsl:template>
    
    <xsl:template name="body">
        <div id="header_wrap" class="outer">
            <header class="inner">
                <a id="search_me" onclick="goANNIS();">Search me in ANNIS</a>
                <h1 id="project_title"><xsl:value-of select="@sName"></xsl:value-of></h1>
                <h2 id="project_tagline">
                    <!-- insert function that gets description -->
                </h2>
            </header>
        </div>
        <div id="navigation">
            <span class="clickify" onclick="loadMainPage();">
                <i class="fa fa-home"></i> About</span>
            <div id="jstree2" class="demo"></div>
            
        </div>
        <div id="main_content_wrap" class="outer">
            <section id="content" />
        </div>
        <footer>
            this site was generated by <a
                href="https://github.com/korpling/pepperModules-SaltInfoModules">SaltInfoModules</a>
            a plugin for <a href="https://u.hu-berlin.de/saltnpepper">Pepper</a>
            and was generated on <xsl:value-of select="@generatedOn"/>
            <div>
                <a class="impressum" onclick="loadImpressumPage();">Impressum</a>
            </div>
        </footer>
    </xsl:template>
</xsl:stylesheet>
