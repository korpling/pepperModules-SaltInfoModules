/*global $:false */
'use strict';
/** call function main, when document was loaded entirely */
$(document).ready(main);

/**
 * This method is called when complete document was loaded. This makes this function a main method.
 * */
function main() {
    /** Adds CSV download functionality to button or icon */
    $("#content").on("click", ".btn-download-csv", function(event) {
        var data = $(this).parent().next().children('.svalue');
        downloadText(convertToCSV(data), CSV_MIME_TYPE);
    });

    /** Creation of boxes around annotation values*/
    $("#content").on("click", ".btn-toggle-box", toggleBox);

    // Load params file (params.json) into global variables
    loadParams();
    // loads customization file into global variables
    loadCustomization();

    // Load content for main page
    loadMainPage();
};

/*******************************************************************************
 * CSV download vor annotation values
 ******************************************************************************/
var CSV_SEPARATOR = ',';
var CSV_DOUBLEQUOTE = '"';
var CSV_LINEBREAK = '\r\n';
var CSV_MIME_TYPE = 'text/csv';
var CLASS_OCCURANCE= '.anno-value-count'

/**
 * loads text as data uri
 */
function downloadText(text, mime) {
    window.location.href = 'data:' + mime + ';charset=UTF-8,' + encodeURIComponent(text);
}

/**
 * escapes double-quotes https://tools.ietf.org/html/rfc4180#section-2
 * Section 7
 */
function escapeDQuote(string) {
    return string.replace(/"/g, '""');
}

/**
 * Converts the given array of svalue-data items into an csv text. Values are
 * quoted because there could be line breaks see
 * https://tools.ietf.org/html/rfc4180
 */
function convertToCSV(svalues) {
    var text = '';
    $(svalues).each(
        function() {
            var valuename = escapeDQuote($(this).children(
                '.svalue-text').text());
            var valuecount = $(this).children(CLASS_OCCURANCE)
                .text();
            text += CSV_DOUBLEQUOTE + valuename + CSV_DOUBLEQUOTE;
            text += CSV_SEPARATOR;
            text += CSV_DOUBLEQUOTE + valuecount + CSV_DOUBLEQUOTE;
            text += CSV_LINEBREAK;
        });
    text += CSV_LINEBREAK;
    return text;
}

/***************************************************************************
 * Boxes for annotation values
 **************************************************************************/
function toggleBox(event) {
    var values = $(this).parent().next().children().children('.svalue-text');
    $(values).toggleClass('boxed');
}

/*******************************************************************************
 * Load params file (params.json and customization.json) into variables
 ******************************************************************************/
/** param file contains data of the corpus */
var FILE_PARAMS = "params.json";
/** customization file containing user defined vaules to adapt web site */
var FILE_CUSTOMIZATION = "./customization.json";
/** Contains the name of the root corpus */
var corpusName = "";
/** Contains a short description of the corpus if given */
var shortDescription = "";
/** Contains a long description of the corpus if given */
var description = "";
/** Contains a description of "structuralInfo" if given */
var structInfoDescription = "";
/** Contains a description of "metaDataInfo" if given */
var metaDataDescription = "";
/** Contains a description of "sAnnotationInfo" if given */
var annoDescription = "";
/** Contains a description of "sLayerInfo" if given */
var sLayerDescription = "";
/** Contains an array of author names*/
var annotators = [];
/** Link to ANNIS instance **/
var annisLink = null;
/** A table containing tooltips for metadata **/
var tooltips_metadata = null;
/** A table containing tooltips for annotation  **/
var tooltips_annonames = null;

/** Defines an object of type Author having a name and aemail address**/
function Author(name, eMail) {
    this.name = name;
    this.eMail = eMail;
}

/** loads customization file and files variables **/
function loadCustomization() {
    //set the MIME type to json, otherwise firefoy produces a warning
    $.ajaxSetup({
        beforeSend: function(xhr) {
            if (xhr.overrideMimeType) {
                xhr.overrideMimeType("application/json");
            }
        }
    });
    /** load customization file */
    $.getJSON(FILE_CUSTOMIZATION, function(json) {
        shortDescription = json.shortDescription;
        description = json.description;
        for (var i = 0; i < json.annotators.length; i++) {
            annotators[annotators.length] = new Author(json.annotators[i].name, json.annotators[i].eMail);
        }

        //load annis links
        annisLink = json.annisLink;
        if (annisLink != null) {
            $("#search_me").css("visibility", "visible");
        }
        // load tooltips for meta data
		if (json.tooltips_metadata!= null){
			for (var i = 0; i < json.tooltips_metadata.length; i++) {
				if (tooltips_metadata== null){
					tooltips_metadata = new Object();
				}
				tooltips_metadata[json.tooltips_metadata[i].name] = json.tooltips_metadata[i].tooltip;
			}
		}
        if (tooltips_metadata== null){
			console.debug("No tooltips for metadata found in file '"+FILE_CUSTOMIZATION+"'. ");
		}
		
		// load tooltips for annotation names
		if (json.tooltips_annonames!= null){
			for (var i = 0; i < json.tooltips_annonames.length; i++) {
				if (tooltips_annonames== null){
					tooltips_annonames = new Object();
				}
				tooltips_annonames[json.tooltips_annonames[i].name] = json.tooltips_annonames[i].tooltip;
			}
		}
        if (tooltips_annonames== null){
			console.debug("No tooltips for annotation names found in file '"+FILE_CUSTOMIZATION+"'. ");
		}
        
        if (json.tooltips_structuralInfo!= null){
			for (var i = 0; i < json.tooltips_structuralInfo.length; i++) {
				if (tooltips_structuralInfo== null){
					tooltips_structuralInfo = new Object();
				}
				tooltips_structuralInfo[json.tooltips_structuralInfo[i].name] = json.tooltips_structuralInfo[i].tooltip;
			}
		}
        if (tooltips_structuralInfo== null){
			console.debug("No tooltips for structural info found in file '"+FILE_CUSTOMIZATION+"'. ");
		}
		
    });
}

/** loads params file and fills variables */
function loadParams() {
        //set the MIME type to json, otherwise firefoy produces a warning
        $.ajaxSetup({
            beforeSend: function(xhr) {
                if (xhr.overrideMimeType) {
                    xhr.overrideMimeType("application/json");
                }
            }
        });
        /** load params file */
        $.getJSON(FILE_PARAMS, function(json) {
            corpusName = json.corpusName;
        });
    }
/*******************************************************************************
 * Collapse/expand the annotation values corresponding to an annotation name,
 * when there are more annotations as predefined treshhold.
 ******************************************************************************/
var NUM_OF_SET_VALUES = 5;
var SYMBOL_COLLAPSE = "fa fa-compress";
var SYMBOL_EXPAND = "fa fa-expand";
var annoTable = null;
/**
 * Loads the anno map from passed file if necessary and expands
 * the cell corresponding to passed annoName
 */
function loadAndExpandAnnoValues(file, annoName) {
    //if annoTable wasn't load, load it now
    if (annoTable == null) {
        if (file != null) {
            //set the MIME type to json, otherwise firefoy produces a warning
            $.ajaxSetup({
                beforeSend: function(xhr) {
                    if (xhr.overrideMimeType) {
                        xhr.overrideMimeType("application/json");
                    }
                }
            });
            $.getJSON(file, function(json) {
                annoTable = json;
                expandAnnoValues(annoName);
            });
        } else {
            console.error("Cannot load annotation map file, since the passed file was empty.");
        }
    } else {
        expandAnnoValues(annoName);
    }
}

/**
 * Expands the annotation values for the cell corresponding to
 * passed annoName.
 **/
function expandAnnoValues(annoName) {
        var $td = $("#"+annoName + "_values");
        var $span = $td.children().eq(0);
        var slot = annoTable[annoName];
        for (var i = NUM_OF_SET_VALUES; i < slot.length; i++) {
            var $newSpan = $span.clone();
            $newSpan.children().eq(0).text(slot[i].value);
            $newSpan.children().eq(1).text(slot[i].occurance);
            $td.append($newSpan);
        }

        var $btn = $("#" + annoName + "_btn");
        $btn.removeClass(SYMBOL_EXPAND);
        $btn.addClass(SYMBOL_COLLAPSE);
        $btn.unbind('click');
        $btn.attr("onclick", "collapseValues('" + annoName + "')");
    }
/**
 * Collapses the annotation values for the cell corresponding to
 * passed annoName.
 **/
function collapseValues(annoName) {
        var $td = $("#"+annoName + "_values");
        var numOfChilds= $td.children().length;
        if (numOfChilds > NUM_OF_SET_VALUES) {
            //for better performance, first collect all items to be removed and make batch remove
            var $removalList = $();
            for (var i = NUM_OF_SET_VALUES; i < numOfChilds; i++) {
                try {
                    $removalList = $removalList.add($td.children().eq(i));
                } catch (err) {
                    console.error(err.message);
                }
            }
            $removalList.remove();
        }

        var $btn = $("#" + annoName + "_btn");
        $btn.removeClass(SYMBOL_COLLAPSE);
        $btn.addClass(SYMBOL_EXPAND);
        $btn.unbind('click');
        $btn.attr("onclick", "expandAnnoValues('" + annoName + "')");
    }
/*******************************************************************************
 * Add the jQuery Tooltip styling mechanism to tooltip elements and style them
 * see: http://jqueryui.com/tooltip/
 ******************************************************************************/
function styleToolTips() {
    $('.tooltip').tooltip({
        show: "true",
        close: function(event, ui) {
            ui.tooltip.hover(function() {
                    $(this).stop(true).fadeTo(10, 1);
                },
                function() {
                    $(this).fadeOut('10', function() {
                        $(this).remove();
                    });
                });
        }
    });
};
/*******************************************************************************
 * Adding tooltips for metadata and annotation names
 ******************************************************************************/
var CLASS_METADATA="metadata-name";
var CLASS_ANNO_NAMES="anno-sname";
var CLASS_TOOLTIP="tooltip";
/** 
 * Adds tootlips to all elements having the class CLASS_METADATA.
 **/
function addTooltips_MetaData() {
    if (tooltips_metadata!= null){
		//find all elements of class CLASS_METADATA
		var metaElements = document.getElementsByClassName(CLASS_METADATA);
		for (var i= 0; i < metaElements.length; i++){
			var tooltip= tooltips_metadata[metaElements[i].innerHTML];
			if (	(tooltip!= null)&&
					(tooltip!= "")){
				metaElements[i].title=tooltip;
				$(metaElements[i]).addClass(CLASS_TOOLTIP);
			}
		}
	}
}
var CLASS_ICON="icon";
var CLASS_FA_INFO="fa fa-info-circle";
/** 
 * Adds tootlips to all elements having the class CLASS_ANNOS_NAMES.
 **/
function addTooltips_MetaData() {
    if (tooltips_metadata!= null){
		//find all elements of class CLASS_METADATA
		var metaElements = document.getElementsByClassName(CLASS_METADATA);
		for (var i= 0; i < metaElements.length; i++){
			var tooltip= tooltips_metadata[metaElements[i].innerHTML];
			if (	(tooltip!= null)&&
					(tooltip!= "")){
				metaElements[i].title=tooltip;
				$(metaElements[i]).addClass(CLASS_TOOLTIP);
				//create icon element for info button
				var icon = $( document.createElement('i'));
				icon.addClass(CLASS_FA_INFO);
				icon.addClass(CLASS_ICON);
				$(metaElements[i]).append(icon);
			}
		}
	}
}

/** 
 * Adds tootlips all elements having the class CLASS_METADATA.
 **/
function addTooltips_AnnotationNames() {
    if (tooltips_annonames!= null){
		//find all elements of class CLASS_ANNO_NAMES
		var annoElements = document.getElementsByClassName(CLASS_ANNO_NAMES);
		for (var i= 0; i < annoElements.length; i++){
			var tooltip= tooltips_annonames[annoElements[i].innerHTML];
			if (	(tooltip!= null)&&
					(tooltip!= "")){
				annoElements[i].title=tooltip;
				$(annoElements[i]).addClass(CLASS_TOOLTIP);
				
				//create icon element for info button
				var icon = $(document.createElement('i'));
				icon.addClass(CLASS_FA_INFO);
				icon.addClass(CLASS_ICON);
				icon.addClass(CLASS_ICON);
				icon.addClass(CLASS_TOOLTIP);
				icon.attr('title', tooltip);
				$(annoElements[i]).parent().parent().append(icon);
			}
		}
	}
}

/*******************************************************************************
 * ANNIS link management
 ******************************************************************************/
var CLASS_CLICKIFY = "clickify-anno";
var CLASS_DECLICKIFY = "declickify-anno";


/** Open ANNIS in extra tab or window */
function goANNIS(annoName, annoValue) {
    if ((annisLink != null) &&
        (corpusName != null)) {
        var link = annisLink;
        // add fragment to url
        link = link + "#";

        //create query query (query by the mean of annis, not URI query) part
        if (annoName != null) {
            link = link + "_q=";

            var annoPart = annoName;
            if (annoValue != null) {
                annoPart = annoPart + "=\"" + annoValue + "\"";
            }
            link = link + btoa(annoPart) + "&";
        }
        // create corpus part tu url
        if (corpusName != null) {
            link = link + "_c=" + btoa(corpusName);
        }
        //open link in new window
        window.open(link, '_blank');
    }
}

/** Makes a button clickable, means to add class clickify-anno to it **/
function clickifyMe(element) {
    if ((annisLink != null) &&
        (corpusName != null)) {
        element.removeClass(CLASS_DECLICKIFY);
        element.addClass(CLASS_CLICKIFY);
    }
}

/*******************************************************************************
 * Load content for main page
 ******************************************************************************/
function loadMainPage() {
    $('#content')
        .load(
            'main.html',
            function() {
//            	if (corpusName != null) {
//            		$("#corpusTitle").append(corpusName);
//            	}
                if (description != null) {
                	$("#corpusDescription").append("<p>"+description+"</p>");
                }
                if (annotators != null) {
				    var annotatorElement = $("#annotators");
                    for (var i = 0; i < annotators.length; i++) {
                        var span = document.createElement('div');
                        $(span).append(annotators[i].name + ": <a href='mailto:" + annotators[i].eMail + "'>" + annotators[i].eMail + "<a/>");
                        $(annotatorElement).append(span);
                    }
                }
            });
}

/*******************************************************************************
 * Load content for impressum page
 ******************************************************************************/
function loadImpressumPage() {
    $('#content')
        .load('impressum.html');
}
