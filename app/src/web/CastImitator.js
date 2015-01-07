var canvas, ctx;
var debug_canvas, debug_ctx; // for displaying debug info
var console;
var p1;
function init () {
	document.onkeyup = keyuphandler;
	document.onkeydown = keydownhandler;
	ctr = 0;
	
	p1 = new Player();
	p1.id = "1";
	p1.name = "Gabe";
	
	canvas = document.getElementById('canvas');
	ctx = canvas.getContext('2d');
	
	debug_canvas = document.getElementById('debug_canvas');
	debug_ctx = debug_canvas.getContext('2d');
	
	// resize the canvas to fill browser window dynamically
    window.addEventListener('resize', resizeCanvas, false);
	function resizeCanvas() {
		canvas.width = window.innerWidth;
		canvas.height = window.innerHeight;
	
		debug_canvas.width = canvas.width;
		debug_canvas.height = canvas.height;
	
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

function keydownhandler(e) {
	if (e.keyCode == 16) {	// shift
		debug_canvas.style.opacity = 0.4;
	}
}
		
function keyuphandler(e) {
	if (e.keyCode == 16) { 	// shift
		debug_canvas.style.opacity = 0.0;
	} else if (e.keyCode == 32) {	// space
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
