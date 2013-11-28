/**************************************************************************
  Copyright (c) 2001-2003 Geir Landrö (drop@destroydrop.com)
  JavaScript Tree - www.destroydrop.com/hjavascripts/tree/
  Version 0.96

  This script can be used freely as long as all copyright messages are
  intact.
**************************************************************************
This script was adopted by the SaltNPepper team see
http://korpling.german.hu-berlin.de/saltnpepper
**************************************************************************/
$(document).ready(function(){
VISIBLE_ITEMS = 5;
var infodiv = document.createElement('DIV');
infodiv.id = 'info';
document.getElementsByTagName('body')[0].appendChild(infodiv);
// tooltip-code
getScrollOffset = function ()          // Funktionskörper von quirksmode.org
  {
      var x = 0, y = 0;
      if (self.pageYOffset)          // all except Explorer
      {
          x = self.pageXOffset;
          y = self.pageYOffset;
      }
      else if (document.documentElement && document.documentElement.scrollTop)
               // Explorer 6 Strict
      {
          x = document.documentElement.scrollLeft;
          y = document.documentElement.scrollTop;
      }
      else if (document.body)           // all other Explorers
      {
          x = document.body.scrollLeft;
          y = document.body.scrollTop;
      }
      return new Array(x,y);
  };

showTip = function (ev) {
      if(!ev){
        ev = window.event;
      }

      var infotext = '';

      for(var n=0; n<this.childNodes.length; n++)
      {
          if(this.childNodes[n].tagName == "SPAN")
          {
              infotext = this.childNodes[n].innerHTML;
              break;
          }
      }

      if(infotext.length)
      {
          infodiv = document.getElementById('info');
          infodiv.innerHTML = infotext;

          infodiv.style.display = 'block';

          var scrollOffset = getScrollOffset();

          infodiv.style.top  = ev.clientY + scrollOffset[1] + 20 + 'px';
          infodiv.style.left = ev.clientX + scrollOffset[0] + 20 + 'px';
      }
  };

hideTip = function (){
   document.getElementById('info').style.display = 'none';
 };

   showone = function(){
         target = $(this).attr('href');
         // Hide all

         $('.data-view').hide();

        $(target).show();
        return false;
   };
   $('.scorpus-item a').click(showone);
   $('.sdocument-item a').click(showone);


   $('.values').click(function(){
        $(this.parentElement.parentElement.nextElementSibling).toggle();
   });

   // Subcorpus navigation: hides subtree
   $('.scorpus-item').click(function(){
        $(this.nextElementSibling).toggle();
        $(this).toggleClass('minimized');
   });

   toggleVisibilty = function(dataitems){
      if (dataitems.length > VISIBLE_ITEMS) {
          toState = '';
          if (dataitems[VISIBLE_ITEMS].style.display == 'none') {
          }else{
            toState = 'none';
          }
          for (var i = dataitems.length - 1; i >= VISIBLE_ITEMS; i--) {
            dataitems[i].style.display = toState;
          }
      }
      return toState;
   };
   buttons = $('.btn-toogle-sannotation-dropdown');
   for (var i = buttons.length - 1; i >= 0; i--) {
      dataitems = buttons[i].parentElement.nextSibling.getElementsByClassName('svalue');
      if (dataitems.length <= VISIBLE_ITEMS) {
        buttons[i].style.display = 'none';
      }else{
        toggleVisibilty(dataitems);
      }
   }
   buttons.click(function(){
        dataitems = this.parentElement.nextSibling.getElementsByClassName('svalue');
        state = toggleVisibilty(dataitems);
        if (state == 'none') {
          this.value = 'Show more';
        }else{
          this.value = 'Show less';
        }
   });

   // info button switch
   infoimgs = $('.infoimg');
   infoimgs.click(function(){
    $(this.nextSibling).toggle();
   });

   // Show the first 5
   $('.svalue-data').map(function(){
        // $(this).addClass('has-hidden-elements')
        for (var i = Math.min(this.childNodes.length,5) - 1; i >= 0; i--) {
          // $(this.childNodes[i]).show();
          $(this.childNodes[i]).toggleClass('preview');
        }
   });


   $('.tooltip').map(function(){
      this.onmousemove = showTip;
      this.onmouseout  = hideTip;
   });

});
// var images = document.getElementsByTagName('img')
// var arrowsrc2 = "http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/down_arrow_circle2.png"
// var arrowsrc1 = "http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/down_arrow_circle.png"
// for (var i = images.length - 1; i >= 0; i--) {
//    if(arrowsrc1 === images[i].src || arrowsrc2 === images[i].src){
//       images[i].addEventListener("click", switchArrow);
//    }
//  };
// })


