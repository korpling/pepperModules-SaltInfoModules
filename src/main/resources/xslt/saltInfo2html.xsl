<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output encoding="UTF-8" indent="yes" method="html" doctype-system="about:legacy-compat"/>
    <xsl:output method="html" indent="yes" name="html"/>
    <xsl:variable name="saltinfocss">css/saltinfo.css</xsl:variable>
    <xsl:variable name="minNumOfAnnos">5</xsl:variable>
<!-- set createJsonForAllAnnos to "true", if all annotations shall be loaded into json, even those with less than 5 values -->
    <xsl:variable name="createJsonForAllAnnos" select="false()" />

    <!-- buid html sceleton-->
    <xsl:template match="/*">
        <html>
            <head>
                <xsl:element name="link">
                    <xsl:attribute name="href">{$saltinfocss}</xsl:attribute>
                    <xsl:attribute name="rel">StyleSheet</xsl:attribute>
                    <xsl:attribute name="type">text/css</xsl:attribute>
                </xsl:element>
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
                <xsl:result-document href="data.json" format="html">
                    <xsl:call-template name="json"/>
                </xsl:result-document>
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
        <table class="data-structInfo">
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
                <tr class="even">
                    <td class="entry-key">
                        <span class="sName_entry">
                            <xsl:value-of select="@key"/>
                            <span class="icon">
                                <i class="fa fa-info-circle"/>
                            </span>
                        </span>
                    </td>
                    <td>
                        <xsl:value-of select="text()"/>
                    </td>
                </tr>
            </xsl:when>
            <xsl:when test="$entry mod 2=0">
                <tr class="odd">
                    <td class="entry-key">
                        <span class="sName_entry">
                            <xsl:value-of select="@key"/>
                            <span class="icon">
                                <i class="fa fa-info-circle"/>
                            </span>
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
        <table class="data-metadata">
            <thead>
                <th>Name</th>
                <th>Count</th>
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
                <tr class="even">
                    <td class="entry-key">
                        <span class="data-entryName">
                            <xsl:value-of select="@key"/>
                        </span>
                    </td>
                    <td>
                        <xsl:value-of select="text()"/>
                    </td>
                </tr>
            </xsl:when>
            <xsl:when test="($entry mod 2=0)">
                <tr class="odd">
                    <td class="entry-key">
                        <span class="data-entryName">
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
                <th>Count</th>
            </thead>
            <tbody>
                <!-- set metadata entries -->
                <xsl:apply-templates select="sAnnotationInfo" mode="annoTable">
                    <xsl:sort select="@sName"/>
                </xsl:apply-templates>
            </tbody>
        </table>
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
                <span class="svalue-text">
                    <xsl:value-of select="text()"/>
                </span>
                <span class="svalue-occurrences">
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
                    onmouseout="$(this).addClass('declickify-anno');"
                    onclick="goANNIS(this.innerHTML);">
                    <xsl:value-of select="@sName"/>
                </span>
                <span class="anno-count">
                    <xsl:value-of select="@occurrences"/>
                </span>
            </span>
            <span class="icon">
                <a class="btn-download-csv">
                    <i class="fa fa-download"/>
                </a>
            </span>
            <span class="icon">
                <a class="btn-toggle-box">
                    <i class="fa fa-square-o"/>
                </a>
            </span>
            <xsl:choose>
                <xsl:when test="count(sValue) &lt; 6"></xsl:when>
                <xsl:otherwise>
                    <span class="icon">
                        <a>
                            <xsl:attribute name="id">
                                <xsl:value-of select="@sName"/>
                                <xsl:text>_btn</xsl:text>
                            </xsl:attribute>
                            <xsl:attribute name="onClick">
                                <xsl:text>expandValues('</xsl:text>
                                <xsl:value-of select="@sName"/>
                                <xsl:text>')</xsl:text>
                            </xsl:attribute>
                            <i class="fa fa-expand"/>
                        </a>
                    </span>
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
            ], </xsl:when>
    <xsl:otherwise>
        <xsl:if test="count(.//sValue) > 5">"<xsl:value-of select="@sName"/>": [
            <xsl:apply-templates select="sValue" mode="ValueJson"/>
            ], </xsl:if>
    </xsl:otherwise></xsl:choose></xsl:template>

    <xsl:template match="sValue" mode="ValueJson">{"value":"<xsl:value-of select="normalize-space(translate(text(), '&quot;','&quot;'))"/>", "occurances": "<xsl:value-of select="@occurrences"/>
        <xsl:choose>
            <xsl:when test="position()!=last()">"},
        </xsl:when>
        <xsl:otherwise>"}
        </xsl:otherwise>
    </xsl:choose>
    </xsl:template>
    
    <xsl:template name="replacements">
        <xsl:variable name="escapedText">
            <xsl:call-template name="replaceSigns">
                <xsl:with-param name="value" select="text()"/>
                <xsl:with-param name="replace" select="'&quot;'"/>
                <xsl:with-param name="with" select="'\&quot;'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="normalize-space($escapedText)"/>
    </xsl:template>
    
    <xsl:template name="replaceSigns">
        <xsl:param name="value"/>
        <xsl:param name="replace"/>
        <xsl:param name="with"/>
        <xsl:choose>
            <xsl:when test="contains($value, $replace)">
                <xsl:value-of select="translate($value, $replace, $with)"/>
                
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="json">
       <!-- <script src="dist/libs/jquery.js" type="text/javascript">
            var data = '{' +-->
                {
                 <xsl:apply-templates select="sAnnotationInfo" mode="annoJson">
                <xsl:sort select="@sName"/>
            </xsl:apply-templates>
        }
          <!--  '}';
        </script>
        <script>
            var NUM_OF_SET_VALUES= {$minNumOfAnnos}
		var SYMBOL_UP = "fa fa-compress";
		var SYMBOL_DOWN = "fa fa-expand";
		var obj = JSON.parse(data);
		
		function expandValues(annoName){
			var td= document.getElementById(annoName+"_values");
			var span= td.children[0];
			var slot= obj[annoName];
			for (var i= NUM_OF_SET_VALUES; i &lt; slot.length;i++){
				var newSpan= span.cloneNode(true);
				newSpan.children[0].innerHTML= slot[i].value;
				newSpan.children[1].innerHTML= slot[i].occurance;
				td.appendChild(newSpan);
			}
			
			var $btn= $("#"+annoName+"_btn");
			$btn.children(":first").removeClass(SYMBOL_DOWN);
			$btn.children(":first").addClass(SYMBOL_UP);
			$btn.unbind('click');
			$btn.attr("onclick","collapseValues('"+annoName+"')");
		}
		
		function collapseValues(annoName){
			var td= document.getElementById(annoName+"_values");
			if (td.children.length > NUM_OF_SET_VALUES){
				//for better performance, first collect all items to be removed and make batch remove
				var $removalList= $();
				for (var i= NUM_OF_SET_VALUES; i &lt; td.children.length;i++){
					try{
						$removalList = $removalList.add(td.children[i]);
					}catch(err) {
						console.log(err.message);
					}
				}
				$removalList.remove();
			}
			
			var $btn= $("#"+annoName+"_btn");
			$btn.children(":first").removeClass(SYMBOL_UP);
			$btn.children(":first").addClass(SYMBOL_DOWN);
			$btn.unbind('click');
			$btn.attr("onclick","expandValues('"+annoName+"')");
		}
        </script>-->
    </xsl:template>

</xsl:stylesheet>
