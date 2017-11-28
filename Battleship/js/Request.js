$.ajaxSetup({ cache: false });
sendGetReq("battleship", "q=newSession", updateStatus, true);


function sendGetReq(url, params, cFunction, async) {
	var xhttp;
	xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4) {
			cFunction(this);
		}
	};
	xhttp.open("GET", "" + url + "?" + params + "&ts=" + new Date().getTime(), (async == true));
	xhttp.send();
}

function sendPostReq(url, body, cFunction) {
	var xhttp;
	xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4) {
			cFunction(this);
		}
	};
	var time = new Date().getTime();
	xhttp.open("POST", url + "?ts=" + time, true);
	xhttp.send(body);
}

function updateStatus(xhttp) {
	//alert('' + xhttp.responseText);
}

function join(roomNum) {
	var roomNumber = ('0000' + roomNum).slice(-4);
	sendGetReq("battleship", "q=joinRoom" + roomNumber, fadeOut, true);
	$(".retry").remove();
}

function joinRandom() {
	sendGetReq("battleship", "q=joinRoom", fadeOut, true);
	$(".retry").remove();
}

function fadeOut(xhttp) {
	var response = xhttp.responseText;
	alert(response);
	if (!isNaN(response)) {
		$("#inputID").fadeOut(500);
		$("#join").fadeOut(500);
		$("#join-random").fadeOut(500);
		$("div").css("opacity", "1.0");
		$(".retry").remove();
		$("#roomNumber p").append(" " + Math.floor(parseInt(response, 10) / 10));
		confirm();
	}
	else {
		var p = document.createElement("p");
		$(p).addClass("retry").append((response === "invalidBoardSetUp") ? "Invalid Board Setup.": "Room Not Found.");
		document.getElementById("front").appendChild(p);
	}
}

function removeElement(idName) {
    var elem = document.getElementById(idName);
    elem.parentNode.removeChild(elem);
    return false;
}

function refreshBoards() {
	sendPostReq("battleship", "getHomeBoard", refreshHome);
	sendPostReq("battleship", "getEnemyBoard", refreshEnemy);
}

function refreshHome(xhttp) {
	var str = xhttp.responseText;
	if (!str) { return; }
	var json = JSON.parse(str);
	var board = json.board;
	for (var x = 0; x < 10; x++) {
		for (var y = 0; y < 10; y++) {
			var table = document.getElementById("one");
			var row = table.rows[y];
			var cell = row.cells[x];
			if (board[y][x] == 0) {

				$(cell).removeClass("missed");
				$(cell).removeClass("ship");	
				$(cell).removeClass("hit");

			} else if (board[y][x] == 1) {

				$(cell).addClass("missed");
				$(cell).removeClass("ship");	
				$(cell).removeClass("hit");

			} else if (board[y][x] == 2) {
				
				$(cell).removeClass("missed");
				$(cell).addClass("ship");	
				$(cell).removeClass("hit");

			} else if (board[y][x] == 3) {
				
				$(cell).removeClass("missed");
				$(cell).removeClass("ship");	
				$(cell).addClass("hit");

			} else {
			
				$(cell).removeClass("missed");
				$(cell).removeClass("ship");	
				$(cell).removeClass("hit");
			}
		}
	}
}

function refreshEnemy(xhttp) {
	var str = xhttp.responseText;
	if (!str) { return; }
	var json = JSON.parse(str);
	var board = json.board;
	for (var x = 0; x < 10; x++) {
		for (var y = 0; y < 10; y++) {
			var table = document.getElementById("two");
			var row = table.rows[y];
			var cell = row.cells[x];
			if (board[y][x] == 0) {

				$(cell).removeClass("missed");
				$(cell).removeClass("ship");	
				$(cell).removeClass("hit");

			} else if (board[y][x] == 1) {

				$(cell).addClass("missed");
				$(cell).removeClass("ship");	
				$(cell).removeClass("hit");

			} else if (board[y][x] == 2) {

				$(cell).removeClass("missed");
				$(cell).addClass("ship");	
				$(cell).removeClass("hit");

			} else if (board[y][x] == 3) {
				
				$(cell).removeClass("missed");
				$(cell).removeClass("ship");	
				$(cell).addClass("hit");
			} else {
			
				$(cell).removeClass("missed");
				$(cell).removeClass("ship");	
				$(cell).removeClass("hit");
			}
		}
	}
}

function refreshGame() {
	
	sendGetReq("battleship", "q=getGameStatus", analyseStatus, true);
	refreshBoards();
		
}

function analyseStatus(xhttp) {

	var status = xhttp.responseText;
	switch (status) {
		case "placingSingle":
			enemyCover(true);
			homeCover(false);
			break;
		case "placingShips":
			enemyCover(true);
			homeCover(false);
			break;
		case "waiting":
			enemyCover(true);
			homeCover(true);
			break;
		case "keepPlaying":
			sendGetReq("battleship", "q=isMyTurn", analyseTurn, true);
			break;
		case "youWon":
			enemyCover(true);
			homeCover(true);
			alert("You Won");
			break;
		case "youLost":
			enemyCover(true);
			homeCover(true);
			alert("You Lost");
			break;
		default:
			// enemyCover(true);
			// homeCover(true);
			break;
	}
}

function analyseTurn(xhttp) {
	var myTurn = xhttp.responseText;
	if (myTurn == "true") {
		enemyCover(false);
		homeCover(true);
	} else {
		enemyCover(true);
		homeCover(true);
	}
}

function homeCover(turn) {
	var cover1 = document.createElement('div');
	$(cover1).addClass('cover');
	if (turn) {
		if (document.getElementById("player1").querySelector(".cover") == null) {
			document.getElementById('player1').appendChild(cover1);
			$(cover1).show();
			$("#player1").css("opacity", "0.5");
		}
	} else { 
		$(".cover").remove(); 
		$("#player1").css("opacity", "1.0");
	}
}

function enemyCover(turn) {
	var cover2 = document.createElement('div');
	$(cover2).addClass('cover');
	if (turn) { 
		if (document.getElementById("player2").querySelector(".cover") == null) {
			document.getElementById('player2').appendChild(cover2);
			$(cover2).show();
			$("#player2").css("opacity", "0.5");
		}
	} else {
		$(".cover").remove();
		$("#player2").css("opacity", "1.0");
	}
}

function confirm() {
	sendGetReq("battleship", "q=finishPlacing", updateStatus, true);
}

function clickCell(tile) {
	var x = tile.parentNode.parentNode.rowIndex;
	var y = tile.parentNode.cellIndex;
	sendGetReq("battleship", "click=" + x + ',' + y, updateStatus, false);
	refreshBoards();
}

$(document).ready(function() {
	$(".battlefield-cell-content").attr('onclick', 'clickCell(this)');
});

setInterval(function() {
	refreshGame();
	}, 100);

