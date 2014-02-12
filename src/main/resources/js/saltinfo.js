
$(document).ready(function(){
  CSV_SEPARATOR = ',';
  CSV_DOUBLEQUOTE = '"';
  CSV_LINEBREAK = '\n';
  CSV_MIME_TYPE = 'text/csv';
  VISIBLE_ITEMS = 5;


  /**
    loads text as data uri
  */
  downloadText = function (text, mime) {
      window.location.href = 'data:' + mime + ';charset=UTF-8,' + encodeURIComponent(text);
  };

  /**
    escapes double-quotes
    https://tools.ietf.org/html/rfc4180#section-2  Section 7
  */
  escapeDQuote = function(string){
    return string.replace(/"/g,'""');
  };

  /**
    Converts the given array of svalue-data items into an
    csv text
    values are quoted because there could be line breaks
    see https://tools.ietf.org/html/rfc4180
  */
  svalues2text = function (svalues) {
    text = '';
    $(svalues).each(function () {
      valuename = escapeDQuote($(this).children('.svalue-text').text());
      valuecount = $(this).children('.svalue-occurances').text();
      text += CSV_DOUBLEQUOTE + valuename + CSV_DOUBLEQUOTE;
      text += CSV_SEPARATOR;
      text += CSV_DOUBLEQUOTE + valuecount + CSV_DOUBLEQUOTE;
      text += CSV_LINEBREAK;
    });
    text += CSV_LINEBREAK;
    return text;
  };

  /**
    Starts the csv Download for the given row
  */
  $("#content").on("click", ".btn-download-csv", function(event){
      data = $(this).parent().next().children('.svalue');
      downloadText(svalues2text(data), CSV_MIME_TYPE);
  });

  /**
    load from document based on id
   */
  if (window.location.hash){

    $('#content').load(window.location.hash.substring(1) + ".html #data", function(){
      $(".btn-toogle-sannotation-dropdown").each(toggledropdown);
    });
  }


  /**
    Subcorpus navigation: hides subtree
   */
  $('.scorpus-item').click(function(e){
      e.stopPropagation();
      $(this).children('ul').toggle();
      $(this).toggleClass('minimized');
  });

  var toggleBox = function(event){
    var values = $('.svalue-text');
    $(values).toggleClass('boxed');
  };
  /**
    toggles .svalue items in a list with an index greater than VISIBLE_ITEMS
    */
  var toggledropdown = function(event){
    var buttons = $(this).parent().next().children('.svalue').slice(VISIBLE_ITEMS);
    if (buttons.length > 0) {
      buttons.toggle();
    }else{
      $(this).hide();
    }

  };
  // run the toggle during startup
  $( ".btn-toogle-sannotation-dropdown").each(toggledropdown);

  /**
    register click listener on placeholder
  */
  $("#content").on("click", ".btn-toogle-sannotation-dropdown", toggledropdown);
  $("#content").on("click", ".btn-toggle-box", toggleBox);


  /**
    loads the new content
  */
  $('.nav-link').click(function(){
    $('#content').load(this.href + " #data", function(){
      $(".btn-toogle-sannotation-dropdown").each(toggledropdown);
    });
    window.location.hash = this.getAttribute('data-id');
    return false;
  });

  /**
    places and toggles the tooltips
  */
  $( "#content").on( {
  click: function() {
    // alert($('#'+$(this).text()).text());

  }, mouseenter: function() {
    // $( this ).text( "inside" );
    $('#'+$(this).text()).fadeIn();
    var p = $(this).position();
    $('#'+$(this).text()).css('top', p.top + this.offsetHeight);
    $('#'+$(this).text()).css('left', p.left - 90 + this.offsetWidth / 2 );
  }, mouseleave: function() {
    // $( this ).text( "inside" );
     $('#'+$(this).text()).fadeOut();

  }
  },  ".data-entryName" );

  /**
   expands the navigation if document titles are to long
   */
  $( "#navigation").on( {
   mouseenter: function() {
    if(this.offsetWidth < this.scrollWidth){
      $(this).animate({
        width: this.scrollWidth + 15
      },150);
    }
  }, mouseleave: function() {
    $(this).animate({
        width: 250
      },150);
  }
  } );


});



