$(document).ready(function(){
   var switchArrow = function () {
   this.style.WebkitTransform = this.style.WebkitTransform == "rotate(180deg)" ? '' : "rotate(180deg)";
}
var images = document.getElementsByTagName('img')
var arrowsrc2 = "http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/down_arrow_circle2.png"
var arrowsrc1 = "http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/down_arrow_circle.png"
for (var i = images.length - 1; i >= 0; i--) {
   if(arrowsrc1 === images[i].src || arrowsrc2 === images[i].src){
      images[i].addEventListener("click", switchArrow);
   }
 };
})