var NOTHING	= 0;
var SPLASH	= 1;

var screenState = 0;
var splashImg;

// This code assumes the following is defined elsewhere (should be moved into this file eventually maybe)
// canvas
// ctx
// DEFAULT_TIMER_RESOLUTION 
setSplashScreen = function() {
	screenState = SPLASH;
	draw();
}

draw = function() {
	
	// draw stuff based on state
	// TODO check for undefined state 
	switch (screenState) {
		case NOTHING:
			break;
		case SPLASH:
			splashImg = new Image();
			splashImg.onload = function() {
				splashFadeIn(2000);
			};
			splashImg.src = 'http://www.adventurpriseme.com/triviacast/MIcon.png';
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
			ctx.globalAlpha = fractional_time_passed;

			// draw image with current alpha
			ctx.drawImage	(splashImg, 	0, 0, splashImg.width, splashImg.height,
											0, 0, width, height);
					
			if (fractional_time_passed >= 1.0) {
				clearInterval(splashFadeInVar);
			}
		}
		, DEFAULT_TIMER_RESOLUTION
	);
}
	