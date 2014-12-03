/*global $:false */
'use strict';
/** call function main, when document was loaded entirely */
$(document).ready(main);

/**
 * This method is called when complete document was loaded. This makes this function a main method.
 * */
function main() {
	/***************************************************************************
	 * CSV Download
	 **************************************************************************/

	/** Adds CSV download functionality to button or icon */
	$("#content").on("click", ".btn-download-csv", function(event) {
		var data = $(this).parent().parent().next().children('.svalue');
		downloadText(convertToCSV(data), CSV_MIME_TYPE);
	});

	/** Boxes for annotation values*/
	$("#content").on("click", ".btn-toggle-box", toggleBox);

	/***************************************************************************
	 * Tooltips
	 **************************************************************************/
	$("#content").on({
		mouseenter : function() {
			var contextNode = $('#' + $(this).text());
			console.log(contextNode.text());
			// console.log($('#' + $(this).text()).text());

			contextNode.fadeIn();
			var p = $(this).position();
			// $('#' + $(this).text()).css('top', p.top + this.offsetHeight);
			// $('#' + $(this).text()).css('left', p.left - 90 +
			// this.offsetWidth / 2);

			console.log("left: " + p.left);
			console.log("top: " + p.top);
			console.log(this);

			var offset = $(this).offset();
			console.log("offset.left: " + offset.left);
			console.log("offset.top: " + offset.top);

			contextNode.css('top', p.top);
			contextNode.css('left', p.left + offset.left);
		},
		mouseleave : function() {
			$('#' + $(this).text()).fadeOut();
		}
	}, ".sName_entry");

	console.log("WIDTH OF NAV: "+document.getElementById("navigation").offsetWidth);

    // Load params file (params.json) into global variables
	loadParams();
	// loads customization file into global variables
	loadCustomization();
	
	// Load content for main page
	loadMainPage();
	
	// load annis link 
	//TODO this can't work, since load of customization file is asynchronous
	if (annisLink!=""){
		console.log($("#search_me"));
		$("#search_me").style.visibility= 'visible';
	}else{
		console.log("HALLO:"+ annisLink);
	}
};


var NAV_BORDER = 15;
var NAV_WIDTH = 150;

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
var annisLink="";

/** Defines an object of type Author having a name and aemail address**/
function Author(name, eMail){
	this.name= name;
	this.eMail=eMail;
}

/** loads customization file and files variables **/
function loadCustomization(){
	/** load customization file */
	$.getJSON("customization.json", function(json) {
	    shortDescription = json.shortDescription;
		description = json.description;
		for (var i=0;i< json.annotators.length;i++){
			annotators[annotators.length]= new Author(json.annotators[i].name, json.annotators[i].eMail);
		}
		annisLink= json.annisLink;
		console.log("annisLink: "+annisLink);
	});
}

/** loads params file and fills variables */
function loadParams() {
	/** load customization file */
	$.getJSON("params.json", function(json) {
		corpusName = json.corpusName;
	});
}

/*******************************************************************************
 * ANNIS link management
 ******************************************************************************/

/** Open ANNIS in extra tab or window */
function goANNIS() {
	var link= annisLink;
	link= link + "#c="+corpusName;
	console.log("link goes to: "+link);
	window.open(link,'_blank');
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



