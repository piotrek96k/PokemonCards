function buyCard(id) {
	var xhttp = new XMLHttpRequest();
	var url = "/buy/bought?id=" + id;
	xhttp.onreadystatechange = function() {
  		if (this.readyState == 4 && this.status == 200) {
			var data = JSON.parse(this.responseText);
			document.getElementById("coins").innerHTML = data.coins;
			document.getElementById("quantity" + id).innerHTML = data.quantity;
			disableButtonsIfNeed(parseInt(data.coins.replaceAll(" ", "")));
    }
  };
	xhttp.open("GET", url, true);
	xhttp.send();
}

function sellCard(id ,page, rarity, set, type, search) {
	var xhttp = new XMLHttpRequest();
	var path = "/sell";
	var url = path + "/sold?id=" + id;
	xhttp.onreadystatechange = function() {
  		if (this.readyState == 4 && this.status == 200) {
			var data = JSON.parse(this.responseText);
			if (data.quantity == "0")
				setCardsFragment(path, page, rarity, set, type, search);
			else {
				document.getElementById("coins").innerHTML = data.coins;
				document.getElementById("quantity" + id).innerHTML = data.quantity;	
			}		
    }
  };
	xhttp.open("GET", url, true);
	xhttp.send();
}

function setCardsFragment(path ,page, rarity, set, type, search) {
	var url = getUrl(path ,page, rarity, set, type, search);
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
  		if (this.readyState == 4 && this.status == 200) {
			var html = new DOMParser().parseFromString(this.responseText, "text/html");
			var cards = html.getElementsByName("sellbutton").length;
			if (cards > 0 || page == 1) {
				document.getElementById("cardpage").innerHTML = html.getElementById("cardpage").innerHTML;
				document.getElementById("coins").innerHTML = html.getElementById("coins").innerHTML;
				document.getElementById("pagination").innerHTML = html.getElementById("pagination").innerHTML;
				installTooltipListeners();
			}
			else if (page > 1)
				if (page == 2)
					window.location.href = getUrl(path, null, rarity, set, type, search).path;
				else
					window.location.href = getUrl(path, page-1, rarity, set, type, search).path;
		}
  	};
	xhttp.open("GET", url.path, true);
	xhttp.send();
}

function getUrl(path ,page, rarity, set, type, search) {
	var url = {
		path: path,
		added: false,
	};
	appendUrl(url, "page", page);
	appendUrl(url, "rarity", rarity);
	appendUrl(url, "set", set);
	appendUrl(url, "type", type);
	appendUrl(url, "search", search);
	return url;
}

function appendUrl(url, name, data) {
	if (data != null && data != "") {
		if (url.added)
			url.path += "&";
		else
			url.path += "?";
		var data = data.toString().replace("&", "%26");
		url.added = true;
		url.path += name;
		url.path += "=";
		url.path += data;
	}
}

function disableButtonsIfNeed(coins) {
	var buttons = document.getElementsByName("buybutton");
	for (let i = 0; i < buttons.length; i++){
		let id = buttons[i].id;
		let cost = parseInt(document.getElementById("cost" + id).innerHTML.replace(" ", ""));
		buttons[i].disabled = coins < cost;
	}
}

function installListeners(scroll) {
	installScrollListener(scroll);
	installExpandListeners();
	installTooltipListeners();
}

function installTooltipListeners() {
	var coll = document.getElementsByClassName("tool-tip");
	for (let i = 0; i<coll.length; i++) {
		coll[i].addEventListener("mouseenter", item=>{
			var height = coll[i].getElementsByTagName("img")[0].clientHeight;
			var element = coll[i].getElementsByClassName("tooltip-table")[0];
			element.style.height = height + "px";
			var location = coll[i].getBoundingClientRect();
			if (window.innerWidth > location.right + element.clientWidth + 25)
				element.style.marginLeft = "0px";
			else
				element.style.marginLeft =  - coll[i].clientWidth - element.clientWidth + "px";
		});
	}
}

function installScrollListener(scroll) {
	var verticalMenu = document.getElementById("verticalmenu");
	verticalMenu.scrollTop = scroll;
	verticalMenu.addEventListener("scroll", item=>{
		var scroll = verticalMenu.scrollTop;
		$.ajax({
			type:"post",
			data: {scroll : scroll},
			url:"/scroll",
			async: true,
			dataType: "text",
		});
	});
}

function installExpandListeners() {
	var coll = document.getElementsByClassName("vertical-menu-top");
	for (let i = 0; i < coll.length; i++) {
		let element = coll[i];
		element.addEventListener("click", item=> {
			var content = element.nextElementSibling;
			if (content.style.display === "none") {
				element.getElementsByTagName("img")[0].src = "images/hide.png";
				content.style.display = "block";
			} else {
				element.getElementsByTagName("img")[0].src = "images/expand.png";
				content.style.display = "none";
			}
			sendExpandData(element.getElementsByTagName("button")[0].id);
		});
	}
}

function sendExpandData(id) {
	$.ajax({
		type:"post",
		data: {expand : id},
		url:"/expand",
		async: true,
		dataType: "text",
	});
}

function changeDropdownColor() {
	document.getElementById("dropdown").style.backgroundColor = "#565656";
}

function returnDropdownColor() {
	document.getElementById("dropdown").style.backgroundColor = "#969390";
}

function eraseSearch() {
	document.getElementById("search").value = "";
}