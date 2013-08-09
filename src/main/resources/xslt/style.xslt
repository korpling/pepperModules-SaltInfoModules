<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <!-- style for html-table -->
    <xsl:variable name="htmlWidth">100%</xsl:variable>
    <xsl:variable name="htmlCellpadding">5px</xsl:variable>
    <xsl:variable name="htmlBorder">0</xsl:variable>
    <xsl:variable name="htmlStyle">height:100%;</xsl:variable>
    
    <!-- text style -->
    <xsl:variable name="fontFamily">Arial</xsl:variable>

    <!-- style for html-header -->
    <xsl:variable name="headerColspan">2</xsl:variable>
    <xsl:variable name="headerstyle">background: -webkit-linear-gradient(top, #95c5fc, #dfeefe); background: -moz-linear-gradient(top, #95c5fc, #dfeefe);text-shadow:gray 2px 1px 3px;color:555555;font-size:30pt;</xsl:variable>
    <xsl:variable name="headerValign">middle</xsl:variable>
    <xsl:variable name="logoSrc"
        >https://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/SaltNPepper_logo2010.svg</xsl:variable><!-- http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/ -->
    <xsl:variable name="logoAlternative"/>
    <xsl:variable name="logoStyle">height:60px</xsl:variable>
    <!-- style for second header-td -->
    <xsl:variable name="header2height">1%</xsl:variable>
    <xsl:variable name="header2style">background: -webkit-gradient(linear, left top, right top, from(#2F2727), to(#95c5fc)); background: -moz-linear-gradient(left, #2F2727, #95c5fc); color:lightgray;font-size:16pt;</xsl:variable>
    

    <!-- style for tree table (border) -->
    <xsl:variable name="treetableborder">1</xsl:variable>
    <xsl:variable name="treetableBorderStyle">background-color:#95c5fc;height:100%;</xsl:variable>
    <xsl:variable name="treetablewidth">100%</xsl:variable>
    <xsl:variable name="treetableCellpadding">30px</xsl:variable>
    <xsl:variable name="treetableStyle">background-color:#ecf0f6;height:100px;vertical-align:top;font-size:11pt;</xsl:variable>
    <!-- html-tree-style -->
    <xsl:variable name="htmlTreeTdWidth">20%</xsl:variable>
    <xsl:variable name="htmlTreeTdStyle">height:100%;</xsl:variable>

    <!-- html-content-table-part -->
    <xsl:variable name="htmlContentPartStyle">height:100%;vertical-align:top;align:center;</xsl:variable>
    <xsl:variable name="ContentTableStyle">width:100%; height:98%;</xsl:variable><!--  overflow:auto; -->
    <xsl:variable name="ContentTableAlign">center</xsl:variable>

    <!-- style for html footer -->
    <xsl:variable name="footerColspan">2</xsl:variable>
    <xsl:variable name="footerStyle">text-align:center;</xsl:variable>
    <xsl:variable name="footerTextStyle">font-size:10pt;color:gray</xsl:variable>
    <xsl:variable name="impressumAlign">center</xsl:variable>

    <!-- style for tables -->
    <xsl:variable name="tdwidth">30%</xsl:variable><!-- width of first td -->
    <xsl:variable name="tdcolspan">3</xsl:variable><!-- colspan of table header -->
    <xsl:variable name="tdstyle">background:-webkit-linear-gradient(top, #aed3fc, #dfeefe); background:-moz-linear-gradient(top, #aed3fc, #dfeefe); color:gray;font-size:14pt;color:gray;font-size:14pt;cellpadding:10px;background:#aed3fc</xsl:variable><!-- style of table header -->
    <xsl:variable name="tableborder">1</xsl:variable>
    <xsl:variable name="cellspacing">0</xsl:variable>
    <xsl:variable name="tablewidth">90%</xsl:variable>
    <xsl:variable name="tdValignWhenToggled">top</xsl:variable>
    <xsl:variable name="tdColspanStructRows">2</xsl:variable>
    <xsl:variable name="tdRowExtinctionStyle">color:gray;font-size:12pt;width:100%;background:#ECF0F6;border-right:none;</xsl:variable>
    <xsl:variable name="tdAlignExtinction">center</xsl:variable>

    <!-- number of values shown, when collapsed -->
    <xsl:variable name="maxSize">5</xsl:variable>

    <!-- style for collapse-button-td -->
    <xsl:variable name="toggleTdWidth">1%</xsl:variable>
    <xsl:variable name="toggleValign">top</xsl:variable>
    <xsl:variable name="RowtoggleStyle">background:#ECF0F6;border-left:none;</xsl:variable>
    <xsl:variable name="tdStyleLeftBorder">border-left:none;</xsl:variable>
    <xsl:variable name="tdStyleRightBorder">border-right:none;</xsl:variable>
    
    <!-- style for td without toggle-button -->
    <xsl:variable name="tdColspanUnderMax">2</xsl:variable>

    <!-- info-img style -->
    <xsl:variable name="InfoImgSrc"
        >img/information.png</xsl:variable><!-- http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/ -->
    <xsl:variable name="InfoImgAlign">top</xsl:variable>
    <xsl:variable name="InfoImgStyle">padding:5px;</xsl:variable>
    
    <!-- stylesheet-link -->
    <xsl:variable name="saltinfocss">css/saltinfo.css</xsl:variable>
    <xsl:variable name="treecss">css/tree.css</xsl:variable><!-- http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/ -->
    <!-- javascript-source -->
    <xsl:variable name="treejs">js/tree.js</xsl:variable><!-- http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/ -->
</xsl:stylesheet>
