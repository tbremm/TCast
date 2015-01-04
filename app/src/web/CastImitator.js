var console;
function init () {
	document.onkeyup = keyuphandler;
	ctr = 0;

	
	

	console = new Object();
	console.log = function (msg) { };
}

function player () {
	this.id = "";
	this.name = "";
	
	this.connect = function () {
		triviaOnConnect(id);
		//triviaMessageReceived(this.id + "|config|player name=" + this.name);
	}
}

		
function keyuphandler(e) {
	if (e.keyCode == 32) {	// space
		advance_test();
	} else if (e.keyCode == 78) { // 'n'
		host_advance_game();
	} else if (e.keyCode == 67) { // 'c'
		connect_players();
	}
}

function host_advance_game () {

}

function connect_players() {

}

var ctr;
function advance_test() {
	if (ctr == 0) {
		triviaWindowLoad();
	} else if (ctr == 1) {
		triviaMessageReceived("1",HOST_REQUEST);
	} else if (ctr == 2) {
		triviaMessageReceived("1", BEGIN_ROUND);
	}
	ctr++;
}

function sendCastMessage (id, data) {
	//alert(id + " " + data);
}
