var NOTHING						= 	0;
var SPLASH_START				= 	1;
var SPLASH_DONE 				= 	2;
var WAITING_FOR_HOST_CONNECTION	=	3;

var screenState = 0;
var splashImg;
var wfhc_anim;

// This code assumes the following is defined elsewhere (should be moved into this file eventually maybe)
// background_canvas, foreground_canvas, debug_canvas
// background_ctx, foreground_ctx, debug_ctx
// DEFAULT_TIMER_RESOLUTION 
setSplashScreen = function() {
	screenState = SPLASH_START;
	draw();
}

setWaitingForHostConnection = function() {
	screenState = WAITING_FOR_HOST_CONNECTION;
	draw();
}

draw = function(isResize) {
	
	if (typeof (isResize) != "undefined") {
		if (isResize == true) {
			clearInterval(wfhc_anim);
		}
	}


	// draw stuff based on state
	// TODO check for undefined state
	switch (screenState) {
		case NOTHING:
			break;
		case SPLASH_START:
			splashImg = new Image();
			splashImg.onload = function() {
				splashFadeIn(2000);
			};
			splashImg.src = 'http://www.adventurpriseme.com/triviacast/MIcon.png';
			break;
		case SPLASH_DONE:
			background_ctx.drawImage	(splashImg, 	0, 0, splashImg.width, splashImg.height,
														0, 0, width, height);
			break;
		case WAITING_FOR_HOST_CONNECTION:
			background_ctx.drawImage	(splashImg, 	0, 0, splashImg.width, splashImg.height,
														0, 0, width, height);
			waitingForHostConnectionAnimation(1000);
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
				screenState = SPLASH_DONE;
			}
		}
		, DEFAULT_TIMER_RESOLUTION
	);
}


var wfhc_x; // 'waiting for host connection' x
var wfhc_y;
var wfhc_x_frac = 0.5;
var wfhc_y_frac = 0.9;
function waitingForHostConnectionAnimation(period_interval) {
	wfhc_x = foreground_canvas.width * wfhc_x_frac;
	wfhc_y = foreground_canvas.height * wfhc_y_frac;
	
	debug_ctx.strokeStyle="red";
	debug_ctx.moveTo(wfhc_x, 0);
	debug_ctx.lineTo(wfhc_x, debug_canvas.height);
	debug_ctx.stroke();
	 
	foreground_ctx.globalAlpha = 1.0;
	var start = $.now();
	var num_periods = 1;
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
			
			var disp_string = "Waiting for host connection";
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

	