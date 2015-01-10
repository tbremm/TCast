var background_canvas, background_ctx;
var foreground_canvas, foreground_ctx;
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
	
	background_canvas = document.getElementById('background_canvas');
	background_ctx = background_canvas.getContext('2d');
	
	foreground_canvas = document.getElementById('foreground_canvas');
	foreground_ctx = foreground_canvas.getContext('2d');
	
	debug_canvas = document.getElementById('debug_canvas');
	debug_ctx = debug_canvas.getContext('2d');
	
	// resize the canvas to fill browser window dynamically
    window.addEventListener('resize', resizeCanvas, false);
	function resizeCanvas() {
		background_canvas.width = window.innerWidth;
		background_canvas.height = window.innerHeight;
		
		// reusing background size - in case of rapid quick resizing,
		// at least all of our canvases will remain aligned
		foreground_canvas.width = background_canvas.width;
		foreground_canvas.height = background_canvas.height;
	
		debug_canvas.width = background_canvas.width;
		debug_canvas.height = background_canvas.height;
	
		width = background_canvas.width;
		height = background_canvas.height;	
		
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
	if (ctr == 0) {
		write_to_debug_screen ("Setting splash screen");
		setSplashScreen();
		//triviaWindowLoad();
	} else if (ctr == 1) {
		write_to_debug_screen ("Setting \"waiting for host connection\"");
		setWaitingForHostConnection();
		//triviaMessageReceived("1",HOST_REQUEST);
	} else if (ctr == 2) {
		triviaMessageReceived("1", BEGIN_ROUND);
	} else if (ctr >= 3) {
		write_to_debug_screen ("test asdgg " + ctr);
	}
	
	ctr++;
}

function sendCastMessage (id, data) {
	alert(id + " " + data);
}

var debug_txt_x_frac = 0.1;
var debug_txt_y_frac = 0.1;
function write_to_debug_screen (str) {
	var debug_x_loc = debug_canvas.width * debug_txt_x_frac;
	var debug_y_loc = debug_canvas.height * debug_txt_y_frac;

	var imageData = debug_ctx.getImageData(0, 0, debug_canvas.width, debug_canvas.height);
	debug_ctx.clearRect(0, 0, debug_canvas.width, debug_canvas.height);
	debug_ctx.putImageData(imageData, 0, 35);
	
	debug_ctx.font = "30px Arial"; // TODO: Parameterize, resize, etc
	debug_ctx.fillStyle = "red";
	debug_ctx.textAlign = "left";
	debug_ctx.fillText(str, debug_x_loc, debug_y_loc);
}
