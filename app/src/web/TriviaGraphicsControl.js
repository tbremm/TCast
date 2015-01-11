var GUI_NOTHING						= 	0;
var GUI_SPLASH_START				= 	1;
var GUI_SPLASH_DONE 				= 	2;
var GUI_WAITING_FOR_HOST_CONNECTION	=	3;
var GUI_WAITING_FOR_HOST_TO_START	=	4;
var GUI_QUESTION					=	5;

var screenState = 0;
var splashImg;
var wfhc_anim;

// This code assumes the following is defined elsewhere (should be moved into this file eventually maybe)
// background_canvas, foreground_canvas, debug_canvas
// background_ctx, foreground_ctx, debug_ctx
// DEFAULT_TIMER_RESOLUTION 
setSplashScreen = function() {
	screenState = GUI_SPLASH_START;
	draw();
}

setWaitingForHostConnection = function() {
	screenState = GUI_WAITING_FOR_HOST_CONNECTION;
	draw();
}

setWaitingForHostStart = function() {
	screenState = GUI_WAITING_FOR_HOST_TO_START;
	draw();
}

draw = function(isResize) {
	// draw stuff based on state
	// TODO check for undefined state
	switch (screenState) {
		case GUI_NOTHING:
			break;
		case GUI_SPLASH_START:
			splashImg = new Image();
			splashImg.onload = function() {
				splashFadeIn(2000);
			};
			splashImg.src = 'http://www.adventurpriseme.com/triviacast/MIcon.png';
			break;
		case GUI_SPLASH_DONE:
			background_ctx.drawImage	(splashImg, 	0, 0, splashImg.width, splashImg.height,
														0, 0, width, height);
			break;
		case GUI_WAITING_FOR_HOST_CONNECTION:
			background_ctx.drawImage	(splashImg, 	0, 0, splashImg.width, splashImg.height,
														0, 0, width, height);
			dotdotdotAnimation("Waiting for host connection", 1000);
			break;
		case GUI_WAITING_FOR_HOST_TO_START:
			background_ctx.drawImage	(splashImg, 	0, 0, splashImg.width, splashImg.height,
														0, 0, width, height);
			dotdotdotAnimation("Waiting for host to begin game", 1000); // TODO: Include host's name here
			break;
		case GUI_QUESTION:
			clearInterval(wfhc_anim);
			background_ctx.fillStyle = "gray";
			background_ctx.fillRect (0, 0, background_canvas.width, background_canvas.height);
			
			foreground_ctx.clearRect (0, 0, foreground_canvas.width, foreground_canvas.height);
			alert(1);
			foreground_ctx.clearRect (0, 0, foreground_canvas.width, foreground_canvas.height);
			foreground_ctx.fillStyle = "purple";
			foreground_ctx.textAlign = "center";
			
			foreground_ctx.font = "40px Arial"; // TODO: Parameterize, resize, etc
			foreground_ctx.fillText(q, foreground_canvas.width * 0.5, foreground_canvas.height * 0.1);
			
			foreground_ctx.font = "20px Arial"; // TODO: Parameterize, resize, etc
			foreground_ctx.fillText(a1, foreground_canvas.width * 0.5, foreground_canvas.height * 0.4);
			
			foreground_ctx.font = "20px Arial"; // TODO: Parameterize, resize, etc
			foreground_ctx.fillText(a2, foreground_canvas.width * 0.5, foreground_canvas.height * 0.5);
			
			foreground_ctx.font = "20px Arial"; // TODO: Parameterize, resize, etc
			foreground_ctx.fillText(a3, foreground_canvas.width * 0.5, foreground_canvas.height * 0.6);
			
			foreground_ctx.font = "20px Arial"; // TODO: Parameterize, resize, etc
			foreground_ctx.fillText(a4, foreground_canvas.width * 0.5, foreground_canvas.height * 0.7);
			
			break;
	}
}

function splashFadeIn(total_time) {
	var start = $.now();
	var time_passed;
	var fractional_time_passed;
	
	var splashFadeInVar = setInterval(
		function () {
			time_passed = ($.now() - start);
			fractional_time_passed = time_passed / total_time;
			
			if (fractional_time_passed > 1.0) fractional_time_passed = 1.0;
		
			// set alpha
			background_ctx.globalAlpha = fractional_time_passed;

			// draw image with current alpha
			background_ctx.drawImage	(splashImg, 	0, 0, splashImg.width, splashImg.height,
														0, 0, width, height);
					
			if (fractional_time_passed >= 1.0) {
				clearInterval(splashFadeInVar);
				screenState = GUI_WAITING_FOR_HOST_CONNECTION;
				draw();
			}
		}
		, DEFAULT_TIMER_RESOLUTION
	);
}


var wfhc_x; // 'waiting for host connection' x
var wfhc_y;
var wfhc_x_frac = 0.5;
var wfhc_y_frac = 0.9;
function dotdotdotAnimation(str, period_interval) {
	wfhc_x = foreground_canvas.width * wfhc_x_frac;
	wfhc_y = foreground_canvas.height * wfhc_y_frac;
	
	debug_ctx.strokeStyle="white";
	debug_ctx.moveTo(wfhc_x, 0);
	debug_ctx.lineTo(wfhc_x, debug_canvas.height);
	debug_ctx.stroke();
	 
	foreground_ctx.globalAlpha = 1.0;
	var start = $.now();
	var num_periods = 1;
	clearInterval(wfhc_anim);
	wfhc_anim = setInterval(
		function () {
			var time_passed = ($.now() - start);
			if (time_passed >= period_interval) {
				start = start + period_interval;
				num_periods++;
				if (num_periods > 3) {
					num_periods = 1;
				}
			}
			
			var disp_string = str;
			for (var i = 0; i < num_periods; i++) {
				disp_string += ".";
			}
			
			foreground_ctx.clearRect (0, 0, foreground_canvas.width, foreground_canvas.height);
			foreground_ctx.font = "20px Arial"; // TODO: Parameterize, resize, etc
			foreground_ctx.fillStyle = "red";
			foreground_ctx.textAlign = "left";
			foreground_ctx.fillText(disp_string, wfhc_x, wfhc_y);
		}
		, DEFAULT_TIMER_RESOLUTION
	);
	
}

var q;
var a1, a2, a3, a4;
function showQuestion(question, ans1, ans2, ans3, ans4) {
	q = question;
	a1 = ans1;
	a2 = ans2;
	a3 = ans3;
	a4 = ans4;
	
	screenState = GUI_QUESTION;
	draw();
}