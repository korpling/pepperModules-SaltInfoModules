
$(document).ready(function(){
  VISIBLE_ITEMS = 5;

  // load from document based on id
  if (window.location.hash){
    $('#content').load(window.location.hash.substring(1) + ".html #data");
  }
  // showone = function(){
  //      target = $(this).attr('href');
  //      // Hide all

  //      $('.data-view').hide();

  //     $(target).show();
  //     return false;
  // };
  // $('.scorpus-item a').click(showone);
  // $('.sdocument-item a').click(showone);


  $('.values').click(function(){
      $(this.parentElement.parentElement.nextElementSibling).toggle();
  });

  // Subcorpus navigation: hides subtree
  $('.scorpus-item').click(function(e){
      e.stopPropagation();
      $(this).children('ul').toggle();
      $(this).toggleClass('minimized');
  });

  // toggleVisibilty = function(dataitems){
  //   if (dataitems.length > VISIBLE_ITEMS) {
  //       toState = '';
  //       if (dataitems[VISIBLE_ITEMS].style.display == 'none') {
  //       }else{
  //         toState = 'none';
  //       }
  //       for (var i = dataitems.length - 1; i >= VISIBLE_ITEMS; i--) {
  //         dataitems[i].style.display = toState;
  //       }
  //   }
  //   return toState;
  // };
  // buttons = $('.btn-toogle-sannotation-dropdown');
  // for (var i = buttons.length - 1; i >= 0; i--) {
  //   dataitems = buttons[i].parentElement.nextSibling.getElementsByClassName('svalue');
  //   if (dataitems.length <= VISIBLE_ITEMS) {
  //     buttons[i].style.display = 'none';
  //   }else{
  //     toggleVisibilty(dataitems);
  //   }
  // }
  // buttons.click(function(){
  //     dataitems = this.parentElement.nextSibling.getElementsByClassName('svalue');
  //     state = toggleVisibilty(dataitems);
  //     if (state == 'none') {
  //       this.value = 'Show more';
  //     }else{
  //       this.value = 'Show less';
  //     }
  // });

  // info button switch
  infoimgs = $('.infoimg');
  infoimgs.click(function(){
  $(this.nextSibling).toggle();
  });

  // // Show the first 5
  // $('.svalue-data').map(function(){
  //     // $(this).addClass('has-hidden-elements')
  //     for (var i = Math.min(this.childNodes.length,5) - 1; i >= 0; i--) {
  //       // $(this.childNodes[i]).show();
  //       $(this.childNodes[i]).toggleClass('preview');
  //     }
  // });

  $("#content").on("click", ".btn-toogle-sannotation-dropdown", function(event){
    var svalue = $(this).parent().next().children('.svalue').toggle();
    for (var i = VISIBLE_ITEMS; i >= 0; i--) {
      svalue = svalue.toArray().shift();
    }
  svalue.toggle();
  });


  // loads the new content
  $('.nav-link').click(function(){
    $('#content').load(this.href + " #data");
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



