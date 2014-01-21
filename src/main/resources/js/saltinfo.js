
$(document).ready(function(){
  VISIBLE_ITEMS = 5;

  downloadText = function downloadText(text) {
  	 window.location.href = 'data:text/csv;charset=UTF-8,' + encodeURIComponent(text);
  }

  svalues2text = function svalues2text(svalues) {
  	text = '';
	$(svalues).each(function () {
		text += $(this).children('.svalue-text').text();
		text += '\t';
		text += $(this).children('.svalue-occurances').text();
		text += '\n';

  	});
	return text;
  }
  // load from document based on id
  if (window.location.hash){
    $('#content').load(window.location.hash.substring(1) + ".html #data");
  }


  // Subcorpus navigation: hides subtree
  $('.scorpus-item').click(function(e){
      e.stopPropagation();
      $(this).children('ul').toggle();
      $(this).toggleClass('minimized');
  });

  var toggledropdown = function(event){
    var buttons = $(this).parent().next().children('.svalue').slice(VISIBLE_ITEMS);
    if (buttons.length > 0) {
      buttons.toggle();
    }else{
      $(this).hide();
    }

  };
	
  $( ".btn-toogle-sannotation-dropdown").each(toggledropdown);
  // register click listener on placeholder
  $("#content").on("click", ".btn-toogle-sannotation-dropdown", toggledropdown);


  // loads the new content
  $('.nav-link').click(function(){
    $('#content').load(this.href + " #data", function(){
      $(".btn-toogle-sannotation-dropdown").each(toggledropdown);
    });
    window.location.hash = this.getAttribute('data-id');
    return false;
  });

  // places and toggles the tooltips
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

  // expands the navigation if document titles are to long
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



