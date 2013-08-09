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

// Arrays for nodes and icons
var nodes			= new Array();;
var openNodes	= new Array();
var icons			= new Array(6);
var lastActiveThing= 0;

/**
 * MyFunction, to collapse tables
 * flips status of element id to hidden or visible
 **/
function collapseForm(id) {
			var treeNode = document.getElementById("treeNode_"+id);
			treeNode.style.color="gray";
			
			var el = document.getElementById(id);
			
			el.style.display='block';

			if (	(lastActiveThing!= 0)&&
					(lastActiveThing!= id))
			{
				var e2 = document.getElementById(lastActiveThing);
				e2.style.display='none';
				var oldTreeNode = document.getElementById("treeNode_"+lastActiveThing);
				oldTreeNode.style.color="black";
			}
			lastActiveThing=id;
         }

/**
 * Loads all icons that are used in the tree
 **/
function preloadIcons() {
	icons[0] = new Image();
	icons[0].src = "http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/plus.gif";
	icons[1] = new Image();
	icons[1].src = "http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/plusbottom.gif";
	icons[2] = new Image();
	icons[2].src = "http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/minus.gif";
	icons[3] = new Image();
	icons[3].src = "http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/minusbottom.gif";
	icons[4] = new Image();
	icons[4].src = "http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/folder.png";
	icons[5] = new Image();
	icons[5].src = "http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/folder_page_white.png";
}

/**
 * Create the tree
 **/
function createTree(arrName, startNode, openNode) {
	nodes = arrName;
	if (nodes.length > 0) {
		preloadIcons();
		if (startNode == null) startNode = 0;
		if (openNode != 0 || openNode != null) setOpenNodes(openNode);
	
		if (startNode !=0) {
			var nodeValues = nodes[getArrayId(startNode)].split("|");
			document.write("<a id=\"treeNode_"+nodeValues[3]+"\" href=\"javascript:void(0)\" onclick=\"collapseForm('"+nodeValues[3]+"')\" onmouseover=\"window.status='" + nodeValues[2] + "';return true;\" onmouseout=\"window.status=' ';return true;\" style=\"color:black\"><img src=\"http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/folder_page_white.png\" align=\"absbottom\" alt=\"\" />" + nodeValues[2] + "</a><br />");
		} else document.write("<img src=\"http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/chart_pie.png\" align=\"absbottom\" alt=\"\" /><br />");
	
		var recursedNodes = new Array();
		addNode(startNode, recursedNodes);
	}
}
/**
 * Returns the position of a node in the array
 **/
function getArrayId(node) {
	for (i=0; i<nodes.length; i++) {
		var nodeValues = nodes[i].split("|");
		if (nodeValues[0]==node) return i;
	}
}

/**
  * Puts in array nodes that will be open
  **/
function setOpenNodes(openNode) {
	for (i=0; i<nodes.length; i++) {
		var nodeValues = nodes[i].split("|");
		if (nodeValues[0]==openNode) {
			openNodes.push(nodeValues[0]);
			setOpenNodes(nodeValues[1]);
		}
	} 
}
/**
 * Checks if a node is open
 **/
function isNodeOpen(node) {
	for (i=0; i<openNodes.length; i++)
		if (openNodes[i]==node) return true;
	return false;
}
/**
 * Checks if a node has any children
 **/
function hasChildNode(parentNode) {
	for (i=0; i< nodes.length; i++) {
		var nodeValues = nodes[i].split("|");
		if (nodeValues[1] == parentNode) return true;
	}
	return false;
}
/**
 * Checks if a node is the last sibling
 **/
function lastSibling (node, parentNode) {
	var lastChild = 0;
	for (i=0; i< nodes.length; i++) {
		var nodeValues = nodes[i].split("|");
		if (nodeValues[1] == parentNode)
			lastChild = nodeValues[0];
	}
	if (lastChild==node) return true;
	return false;
}
/**
 * Adds a new node to the tree. Is called by createTree()
 */ 
function addNode(parentNode, recursedNodes) {
	for (var i = 0; i < nodes.length; i++) {

		var nodeValues = nodes[i].split("|");
		if (nodeValues[1] == parentNode) {
			
			var ls	= lastSibling(nodeValues[0], nodeValues[1]);
			var hcn	= hasChildNode(nodeValues[0]);
			//var ino = isNodeOpen(nodeValues[0]);
			var ino = true;

			// Write out line & empty icons
			for (g=0; g<recursedNodes.length; g++) {
				if (recursedNodes[g] == 1) document.write("<img src=\"http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/line.gif\" align=\"absbottom\" alt=\"\" />");
				else  document.write("<img src=\"http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/empty.gif\" align=\"absbottom\" alt=\"\" />");
			}

			// put in array line & empty icons
			if (ls) recursedNodes.push(0);
			else recursedNodes.push(1);

			// Write out join icons
			if (hcn) {
				if (ls) {
					document.write("<a href=\"javascript: openCloseNode(" + nodeValues[0] + ", 1);\"><img id=\"join" + nodeValues[0] + "\" src=\"http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/");
					 	if (ino) document.write("minus");
						else document.write("plus");
					document.write("bottom.gif\" align=\"absbottom\" alt=\"Open/Close node\" /></a>");
				} else {
					document.write("<a href=\"javascript: openCloseNode(" + nodeValues[0] + ", 0);\"><img id=\"join" + nodeValues[0] + "\" src=\"http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/");
						if (ino) document.write("minus");
						else document.write("plus");
					document.write(".gif\" align=\"absbottom\" alt=\"Open/Close node\" /></a>");
				}
			} else {
				if (ls) document.write("<img src=\"http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/joinbottom.gif\" align=\"absbottom\" alt=\"\" />");
				else document.write("<img src=\"http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/join.gif\" align=\"absbottom\" alt=\"\" />");
			}

			//alert("id: "+ "id=\"treeNode_"+nodeValues[3]+"\"");
			// Start link
			document.write("<a id=\"treeNode_"+nodeValues[3]+"\" href=\"javascript:void(0)\" onclick=\"collapseForm('"+nodeValues[3]+"')\" onmouseover=\"window.status='" + nodeValues[2] + "';return true;\" onmouseout=\"window.status=' ';return true;\" style=\"color:black\">");
			
			// Write out folder & page icons
			if (hcn) {
				document.write("<img id=\"icon" + nodeValues[0] + "\" src=\"http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/folder")
					if (ino) document.write("open");
				document.write(".png\" align=\"absbottom\" alt=\"Folder\" />");
			} else document.write("<img id=\"icon" + nodeValues[0] + "\" src=\"http://korpling.german.hu-berlin.de/saltnpepper/salt/info/info-10/img/page_white_text.png\" align=\"absbottom\" alt=\"Page\" />");
			
			// Write out node name
			document.write(nodeValues[2]);

			// End link
			document.write("</a><br />");
			
			// If node has children write out divs and go deeper
			if (hcn) {
				document.write("<div id=\"div" + nodeValues[0] + "\"");
					if (!ino) document.write(" style=\"display: none;\"");
				document.write(">");
				addNode(nodeValues[0], recursedNodes);
				document.write("</div>");
			}
			
			// remove last line or empty icon 
			recursedNodes.pop();
		}
	}
}

/**
  * Opens or closes a node
  */
function openCloseNode(node, bottom) {
	var theDiv = document.getElementById("div" + node);
	var theJoin	= document.getElementById("join" + node);
	var theIcon = document.getElementById("icon" + node);
	
	if (theDiv.style.display == 'none') {
		if (bottom==1) theJoin.src = icons[3].src;
		else theJoin.src = icons[2].src;
		theIcon.src = icons[5].src;
		theDiv.style.display = '';
	} else {
		if (bottom==1) theJoin.src = icons[1].src;
		else theJoin.src = icons[0].src;
		theIcon.src = icons[4].src;
		theDiv.style.display = 'none';
	}
}
// Push and pop not implemented in IE
if(!Array.prototype.push) {
	function array_push() {
		for(var i=0;i<arguments.length;i++)
			this[this.length]=arguments[i];
		return this.length;
	}
	Array.prototype.push = array_push;
}
if(!Array.prototype.pop) {
	function array_pop(){
		lastElement = this[this.length-1];
		this.length = Math.max(this.length-1,0);
		return lastElement;
	}
	Array.prototype.pop = array_pop;
}


// tooltip-code
function showTip(ev)
  {
      if(!ev) { ev = window.event; };

      var infotext = '';

      for(var n=0; n<this.childNodes.length; n++)
      {
          if(this.childNodes[n].tagName == "SPAN")
          {
              infotext = this.childNodes[n].innerHTML; break;
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
  }
  
function hideTip()
  {   document.getElementById('info').style.display = 'none';   }
  
function initTooltips()
  {
      var infodiv = document.createElement('DIV');
      infodiv.id = 'info';
      document.getElementsByTagName('body')[0].appendChild(infodiv);

      var liste = document.getElementsByTagName('A');
    
      for (var n=0; n<liste.length; n++)
      {
          if(liste[n].className.match( /tooltip/ ))
          {
              liste[n].onmousemove = showTip;
              liste[n].onmouseout  = hideTip;
          }
      }
  }
  
function getScrollOffset()          // Funktionskörper von quirksmode.org
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
      };
      return new Array(x,y);
  }

//   collapsible/ expandible rows
function toggle_visibility(id) {
       var e = document.getElementById(id);
       if(e.style.display == 'block')
          e.style.display = 'none';
       else
          e.style.display = 'block';
    }

// everything for more than one collapsible row
function getElementsByClassName(strClassName){
  var arrHelp = new Array();
 
  // Suchpattern
  var strPattern = eval("/"+strClassName+"/");
 
  // Alle Elemente über Wildcard ermitteln
  var arrTags = (navigator.userAgent.toLowerCase().indexOf("msie") != -1) ? document.all : document.getElementsByTagName('*');
 
  // Alle Elemente durchlaufen
  for(varEntry in arrTags){
    // Fall eine Klasse existiert UND dem Suchkriterium entspricht
    if((arrTags[varEntry].className) && (arrTags[varEntry].className.match(strPattern))){
      // In Klassenarray schreiben
      arrHelp[arrHelp.length] = arrTags[varEntry];
    }
  }
 
  return arrHelp;
}
function toggle_visibility2(item){
  var e=getElementsByClassName(item);
  //alert(e.length);
  if(!e)return true;
  for (var i = 0; i < e.length; i++){
    with (e[i].style){
      if(visibility == 'hidden' || display=='none'){
        display = '';
        visibility = 'visible';
      }else{
        display = 'none';
        visibility = 'hidden';
      }
    }
  }
  return true;
}