var canvas, ctx;
var console;
var p1;
function init () {
	document.onkeyup = keyuphandler;
	ctr = 0;
	
	p1 = new Player();
	p1.id = "1";
	p1.name = "Gabe";
	
	canvas = document.getElementById('canvas');
	ctx = canvas.getContext('2d');
	
	// resize the canvas to fill browser window dynamically
    window.addEventListener('resize', resizeCanvas, false);
	function resizeCanvas() {
		canvas.width = window.innerWidth;
		canvas.height = window.innerHeight;
	
		width = canvas.width;
		height = canvas.height;	
		
		draw();
	}
	resizeCanvas();
	
	
	console = new Object();
	console.log = function (msg) { };
}

function testPhone () {
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
		triviaOnConnect(p1.id);
	}
}

function host_advance_game () {

}

function connect_players() {

}

var ctr;
function advance_test() {

	canvas = document.getElementById("canvas");
	ctx = canvas.getContext("2d");
	if (ctr == 0) {
		setSplashScreen();
		//triviaWindowLoad();
	} else if (ctr == 1) {
		triviaMessageReceived("1",HOST_REQUEST);
	} else if (ctr == 2) {
		triviaMessageReceived("1", BEGIN_ROUND);
	} else if (ctr == 3) {

	}
	
	ctr++;
}

function sendCastMessage (id, data) {
	alert(id + " " + data);
}
