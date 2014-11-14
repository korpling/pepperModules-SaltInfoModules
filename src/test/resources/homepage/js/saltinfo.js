/*global $:false */
'use strict';
function start() {
	var CSV_SEPARATOR = ',';
	var CSV_DOUBLEQUOTE = '"';
	var CSV_LINEBREAK = '\r\n';
	var CSV_MIME_TYPE = 'text/csv';
	var VISIBLE_ITEMS = 0;

	var NAV_BORDER = 15;
	var NAV_WIDTH = 150;

	/***************************************************************************
	 * CSV Download
	 **************************************************************************/

	/** Adds CSV download functionality to button or icon */
	$("#content").on("click", ".btn-download-csv", function(event) {
		var data = $(this).parent().parent().next().children('.svalue');
		downloadText(svalues2text(data), CSV_MIME_TYPE);
	});

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
	 * Converts the given array of svalue-data items into an csv text values are
	 * quoted because there could be line breaks see
	 * https://tools.ietf.org/html/rfc4180
	 */
	var svalues2text = function(svalues) {
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
	};

	/***************************************************************************
	 * Boxes for annotation values
	 **************************************************************************/
	$("#content").on("click", ".btn-toggle-box", toggleBox);
	function toggleBox(event) {
		var values = $('.svalue-text');
		$(values).toggleClass('boxed');
	}
	;

	/***************************************************************************
	 * Collapse/Expand annotation values
	 **************************************************************************/
	$("#content").on("click", ".btn-toogle-sannotation-dropdown",
			expandAnnoValues);
	/** toggles .svalue items in a list with an index greater than VISIBLE_ITEMS */
	function expandAnnoValues(event) {
		var svalues = $(this).parent().parent().next().children('.svalue')
				.slice(VISIBLE_ITEMS);
		svalues.toggle();
	}
	/**
	 * load from document based on id
	 */
	if (window.location.hash) {
		$('#content').load(window.location.hash.substring(1) + ".html #data",
				function() {
					$(".btn-toogle-sannotation-dropdown").each(toggledropdown);
				});
	}

	/**
	 * Subcorpus navigation: hides subtree
	 */
	$('.scorpus-item').click(function(e) {
		e.stopPropagation();
		$(this).children('ul').toggle();
		$(this).toggleClass('minimized');
	});

	// run the toggle during startup
	// $( ".btn-toogle-sannotation-dropdown").each(toggledropdown);
	$(".btn-toogle-sannotation-dropdown").each(
			function() {
				$(this).parent().next().children('.svalue').slice(0,
						VISIBLE_ITEMS).toggle();
			});

	/**
	 * loads the new content
	 */
	var loadContent = function() {
		$('#content').load(this.href + " #data", function() {
			$(".btn-toogle-sannotation-dropdown").each(toggledropdown);
		});
		window.location.hash = this.getAttribute('data-id');
		return false;
	};

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

	/***************************************************************************
	 * Tree naviagtion expandation
	 **************************************************************************/

	/**
	 * expands the navigation if document titles are to long
	 */
	var expand = function() {
		if (this.offsetWidth < this.scrollWidth) {
			$(this).animate({
				width : this.scrollWidth + NAV_BORDER
			}, NAV_WIDTH);
		}
	};

	/**
	 * collapse navigation
	 */
	var collapse = function() {
		$(this).animate({
			width : 250
		}, NAV_WIDTH);
	};

	$("#navigation").on("mouseenter", expand);
	$("#navigation").on("mouseleave", collapse);

	$(".nav-link").on("click", loadContent);

	// Load params file (params.json) into global variables
	loadParams();
	// loads customization file into global variables
	loadCustomization();
	// Load content for main page
	loadMainPage();
};

/*******************************************************************************
 * Load params file (params.json) into variables
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

/** call function start, when document was loaded entirely */
$(document).ready(start);
