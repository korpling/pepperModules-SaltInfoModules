$(document).ready(function(){
   // var switchArrow = function () {
   // this.style.WebkitTransform = this.style.WebkitTransform == "rotate(180deg)" ? '' : "rotate(180deg)";

   // $('.data-view').hide();
   showone = function(){
   		target = $(this).attr('href');
   		// Hide all

   		$('.data-view').hide();

   		$(target).show();
   		// $(target).fadeIn('fast');
   		//Return false to avoid the scrolling
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

   $('.btn-toogle-sannotation-dropdown').click(function(){
   		dataitems = this.parentElement.nextElementSibling.childNodes;
   		$(dataitems).toggle();
   		$(dataitems).toggleClass('has-hidden-elements')

   });

   // Show the first 5 
   $('.svalue-data').map(function(){
   		// $(this).addClass('has-hidden-elements')
   		for (var i = Math.min(this.childNodes.length,5) - 1; i >= 0; i--) {
   			$(this.childNodes[i]).show();
   		};
   })

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

