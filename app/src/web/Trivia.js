
			var fadeStartTime = -1;
			var questionStartTime = -1;
			var fadeInVar;
			var questionTimerVar;

			triviaMessageReceived = function() {
			    // do stuff
			}

			triviaOnDisconnect = function() {
			    // do stuff
			}

			triviaOnConnect = function() {
			    // do stuff
			}

			triviaSendMessage = function() {
			    // do stuff
			}

            triviaWindowLoad = function () {
                // do stuff
            }

			doQuestion = function() {
				//alert("onload reached");
				$.ajax({
					url: 'GetQuestion.php', 
					type: 'post',
					data: '',
					success: function(data) {
						//alert(data);
					
						var split_data = data.split(':');
						var question = split_data[0];
						var answer = split_data[1];
						
						var qboxtext = document.getElementById("qboxtext");
						qboxtext.innerHTML = question;
						
						var aboxtext = document.getElementById("aboxtext");
						aboxtext.innerHTML = answer;
						
						fadeStartTime = $.now();
						fadeInVar = setInterval(fadeIn, 50);
					
					},
					error: function(xhr, desc, err) {
						console.log(xhr);
						console.log("Details: " + desc + "\nError: " + err);
					}
				});
			}
			
			function fadeIn () {
				var totalTime = 2000; // 2 second fade in
				var thisTime = $.now();
				
				// fadeStartTime better be set before this function gets called
				var timePassed = thisTime - fadeStartTime;
				
				var opacity = timePassed / totalTime;
				
				var qboxtext = document.getElementById("qbox");
				
				if (timePassed >= totalTime) {
					opacity = 1.0;
					clearInterval(fadeInVar);
					startQuestionTimer();
				}
				
				qboxtext.style.opacity = opacity;
			}
			
			var questionTime = 100000; // in ms
			function startQuestionTimer () {
				questionStartTime = $.now();
				questionTimerVar = setInterval(questionTimerFunc, 50);
			}
			
			function questionTimerFunc () {
				var thisTime = $.now();
				
				var timePassed = thisTime - questionStartTime;
				var timeLeft = questionTime - timePassed;
				timeLeft = Math.round(timeLeft/1000.0);
				
				var timer = document.getElementById("timer");
				timer.innerHTML = timeLeft;
			
			}
			