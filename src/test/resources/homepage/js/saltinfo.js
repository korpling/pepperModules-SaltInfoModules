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
		var data = $(this).parent().parent().next().children('.svalue');
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

/**
 * loads text as data uri
 */
function downloadText(text, mime) {
	window.location.href = 'data:' + mime + ';charset=UTF-8,'
			+ encodeURIComponent(text);
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
				var valuecount = $(this).children('.svalue-occurrences')
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
	var values = $(this).parent().parent().next().children().children('.svalue-text');
	$(values).toggleClass('boxed');
}	

/*******************************************************************************
 * Load params file (params.json and customization.json) into variables
 ******************************************************************************/
/** param file contains data of the corpus */
var FILE_PARAMS = "params.json";
/** customization file containing user defined vaules to adapt web site */
var FILE_CUSTOMIZATION = "customization.json";
/** Contains the name of the root corpus */
var corpusName = "";
/** Contains a short description of the corpus if given */
var shortDescription = "";
/** Contains a long description of the corpus if given */
var description = "";
/** Contains an array of author names*/
var annotators = [];
/** Link to ANNIS instance **/
var annisLink=null;
/** A table containing tooltips for metadata **/
var tooltips_metadata= new Object();

/** Defines an object of type Author having a name and aemail address**/
function Author(name, eMail){
	this.name= name;
	this.eMail=eMail;
}

/** loads customization file and files variables **/
function loadCustomization(){
	//set the MIME type to json, otherwise firefoy produces a warning
	$.ajaxSetup({beforeSend: function(xhr){
		if (xhr.overrideMimeType){
			xhr.overrideMimeType("application/json");
		}
	}});
	/** load customization file */
	$.getJSON("customization.json", function(json) {
	    shortDescription = json.shortDescription;
		description = json.description;
		for (var i=0;i< json.annotators.length;i++){
			annotators[annotators.length]= new Author(json.annotators[i].name, json.annotators[i].eMail);
		}
		
		//load annis links
		annisLink= json.annisLink;
		if (annisLink!=null){
			$("#search_me").css("visibility", "visible");
		}
		
		var tooltips_metadata= new Object();
		for (var i=0;i< json.tooltips_metadata.length;i++){
			tooltips_metadata[json.tooltips_metadata[i].name]= json.tooltips_metadata[i].tooltip;
		}
		console.log(tooltips_metadata);
	});
}

/** loads params file and fills variables */
function loadParams() {
	//set the MIME type to json, otherwise firefoy produces a warning
	$.ajaxSetup({beforeSend: function(xhr){
		if (xhr.overrideMimeType){
			xhr.overrideMimeType("application/json");
		}
	}});
	/** load params file */
	$.getJSON("params.json", function(json) {
		corpusName = json.corpusName;
	});
}
/*******************************************************************************
 * Collapse/expand the annotation values corresponding to an annotation name,
 * when there are more annotations as predefined treshhold.
 ******************************************************************************/
var NUM_OF_SET_VALUES= 5;
		var SYMBOL_UP = "fa fa-compress";
		var SYMBOL_DOWN = "fa fa-expand";
		var annoTable = null;

		function loadAnnoMap(file){
			if (file!= null){
				//set the MIME type to json, otherwise firefoy produces a warning
				$.ajaxSetup({beforeSend: function(xhr){
					if (xhr.overrideMimeType){
						xhr.overrideMimeType("application/json");
					}
				}});
				$.getJSON(file, function(json) {
						annoTable= json;
				});
			}else{
				console.error("Cannot load annotation map file, since the passed file was empty.");
			}
		}
		/**
		 * Expands the annotation values for the cell corresponding to 
		 * passed annoName.
		 **/
		function expandValues(annoName){
			var td= document.getElementById(annoName+"_values");
			var span= td.children[0];
			var slot= annoTable[annoName];
			for (var i= NUM_OF_SET_VALUES; i< slot.length;i++){
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
		/**
		 * Collapses the annotation values for the cell corresponding to 
		 * passed annoName.
		 **/
		function collapseValues(annoName){
			var td= document.getElementById(annoName+"_values");
			if (td.children.length > NUM_OF_SET_VALUES){
				//for better performance, first collect all items to be removed and make batch remove
				var $removalList= $();
				for (var i= NUM_OF_SET_VALUES; i< td.children.length;i++){
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
/*******************************************************************************
 * Add the jQuery Tooltip styling mechanism to tooltip elements and style them
 * see: http://jqueryui.com/tooltip/
 ******************************************************************************/
function styleToolTips() {
            $('.tooltip').tooltip({
               show: "true", 
               close: function(event, ui){
                  ui.tooltip.hover(function(){
                     $(this).stop(true).fadeTo(10, 1); 
                  },
                  function(){
                     $(this).fadeOut('10', function(){
                        $(this).remove();
                     });
                  });
               }
            });
         };
/*******************************************************************************
 * Adding tooltips for metadata and annotation names
 ******************************************************************************/
function addTooltip(container){
	console.log("HEY");
	console.log(container);
}

/*******************************************************************************
 * ANNIS link management
 ******************************************************************************/
var CLASS_CLICKIFY="clickify-anno";
var CLASS_DECLICKIFY="declickify-anno";


/** Open ANNIS in extra tab or window */
function goANNIS(annoName, annoValue) {
	if (	(annisLink!= null) &&
			(corpusName!= null)){
		var link= annisLink;
		// add fragment to url
		link= link + "#";
		
		//create query query (query by the mean of annis, not URI query) part
		if (annoName!= null){
			link= link +"_q=";
			
			var annoPart=annoName;
			if (annoValue!= null){
				annoPart= annoPart + "=\""+annoValue+"\"";
			}
			link= link+ btoa(annoPart)+ "&";
		}
		// create corpus part tu url
		if (corpusName!= null){
			link= link + "_c="+btoa(corpusName);
		}
		//open link in new window
		window.open(link,'_blank');
	}
}

/** Makes a button clickable, means to add class clickify-anno to it **/
function clickifyMe(element){
	if (	(annisLink!= null) &&
			(corpusName!= null)){
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
						document.getElementById("corpusTitle").innerHTML = corpusName;
						if (description != null) {
							document.getElementById("corpusDescription").innerHTML = description;
						}
						if (annotators!= null){
							var authorElement= document.getElementById("annotators");
							for (var i=0;i< annotators.length;i++){
								var span= document.createElement('div');
								span.innerHTML=annotators[i].name+": <a href='mailto:"+annotators[i].eMail+"'>"+annotators[i].eMail+"<a/>";
								authorElement.appendChild(span);
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
