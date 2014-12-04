
			var fadeStartTime = -1;
			var questionStartTime = -1;
			var fadeInVar;
			var questionTimerVar;
			var players = [];
			var answerIsTrue;
			var questionEndTimerVar;
            var questionEndStartTime;

			var gameState;
			// possibilities for game state
			var GAME_PENDING = 0;   // game hasnt started
			var Q_IN_PROGRESS = 1;  // question is up, timer ticking
			var Q_END = 2;          // question is over, give a few seconds to review

            triviaWindowLoad = function () {
                setGamePending();
                // do stuff
            }

			resetQuestion = function() {
			    fadeStartTime = -1;
			    questionStartTime = -1;
                clearInterval(questionTimerVar);
                clearInterval(fadeInVar);

				var qboxtext = document.getElementById("qbox");
				qboxtext.style.opacity = 0.0;

			}

            // list of commands
            var TRUE = "true";
            var FALSE = "false";
            var CONTINUE = "continue";
            var WIN = "win";
            var LOSE = "lose";


			triviaMessageReceived = function(id, data) {
			    // do stuff
				var senderIndex = players.indexOf(id);
				if (senderIndex > -1) {
				    // fixme todo - lots of message parsing that we need to do!!

				    if (data == TRUE || data == FALSE) {
				        players[senderIndex].answer = data;
				    } else if (data == CONTINUE) {
				        if (gameState == GAME_PENDING) {
    				        doQuestion();
    				    } else
    				        endQuestion();
    				    }
				    }
			    }
	        }

			triviaOnDisconnect = function(id) {
			    // do stuff
			    var ind = players.indexOf(id);
			    players.splice(ind, 1);
			    if (players.length == 0) {
			        setGamePending();
			    }
			}

			triviaOnConnect = function(id) {
			    // do stuff
                // temp
                setGamePending();
				players.push(id);
			    //doQuestion();
			}

			triviaSendMessage = function(id, msg) {
			    // do stuff
				sendCastMessage(id, msg);
			}


			setGamePending = function() {
			    gameState = GAME_PENDING;
		        var qbox = document.getElementById("qbox");
		        qbox.style.opacity = 1.0;
				qbox.innerHTML = "Game Pending...";
			}

			doQuestion = function() {
				//alert("onload reached");
				gameState = Q_IN_PROGRESS;
				$.ajax({
					url: 'GetTrueFalseQuestion.php',
					type: 'post',
					data: '',
					success: function(data) {
						//alert(data);
					
						var split_data = data.split(':');
						var question = split_data[0];
						var answer = split_data[1];
						answerIsTrue = split_data[2];
						
						var qboxtext = document.getElementById("qboxtext");
						question = [question, "<br>", answer, "<br>", "True or False?"];
						qboxtext.innerHTML = question;
						
						//var aboxtext = document.getElementById("aboxtext");
						//aboxtext.innerHTML = answer;

						// send Q&A to phones
						var i;
						for (i = 0; i < players.length; i++) {
						    // fixme todo - will have to think about disconnects
						    // while looping over players
						    triviaSendMessage(players[i], ["Q: ", question]);
						    triviaSendMessage(players[i], ["A: ", answer]);
					    }
						fadeStartTime = $.now();
						fadeInVar = setInterval(fadeIn, 50);
					
					},
					error: function(xhr, desc, err) {
						console.log(xhr);
						console.log("Details: " + desc + "\nError: " + err);
					}
				});
			}

			endQuestion = function () {
				var qboxtext = document.getElementById("qboxtext");
				if (answerIsTrue) {
				    qboxtext.innerHTML = [qboxtext.innerHTML, "<br>", "It's True!"];
		        } else {
		            qboxtext.innerHTML = [qboxtext.innerHTML, "<br>", "It's False!"];
		        }
                gameState = Q_END;

			    var i;
			    // fixme think about player disconnects during loops
			    for (i = 0; i < players.length; i++) {
			        if (    (answerIsTrue && (players[i].answer = TRUE)) ||
			                (!answerIsTrue && (players[i].answer == FALSE))) {
			            triviaSendMessage(players[i], WIN);
			        } else {
			            triviaSendMessage(players[i], LOSE);
			        }
			    }

                questionEndStartTime = $.now;
				questionEndTimerVar = setInterval(function () {
                    if (($.now - questionEndStartTimer) > 10000) { // 10 seconds
                        clearInterval(questionEndTimerVar);
                        resetQuestion();
                        doQuestion();
                    }
				}, 50);
			}

			function fadeIn () {
				var totalTime = 2000; // 2 second fade in
				var thisTime = $.now();
				
				// fadeStartTime better be set before this function gets called
				var timePassed = thisTime - fadeStartTime;
				
				var opacity = timePassed / totalTime;
				
				var qbox = document.getElementById("qbox");
				
				if (timePassed >= totalTime) {
					opacity = 1.0;
					clearInterval(fadeInVar);
					startQuestionTimer();
				}
				
				qbox.style.opacity = opacity;
			}
			
			var questionTime = 30000; // in ms
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
				
				if (timeLeft < 0) {
					clearInterval(questionTimerVar);
					var i;
					for (i = 0; i < players.length; i++) {
					    triviaSendMessage(players[i], "timeout");
					}
					endQuestion(); // fixme todo - give phones time to give last-seond answer?
				}
			}
			