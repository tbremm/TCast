
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
			
			var GAME_PENDING = 0;   // game has not started
			var HOST_SELECTED = 1;	// host selected
			var Q_IN_PROGRESS = 2;  // question is up, timer ticking
			var QUESTION_REVIEW = 3;          // question is over, give a few seconds to review

            triviaWindowLoad = function () {
                setGamePending();
                // do stuff
            }

			resetQuestion = function() {
			    fadeStartTime = -1;
			    questionStartTime = -1;
                clearInterval(questionTimerVar);
                clearInterval(fadeInVar);

				var qbox = document.getElementById("qbox");
				qbox.style.opacity = 0.0;

			}

            // list of commands
            var TRUE = "true";
            var FALSE = "false";
			var CONNECTED = "connected"
			var HOST_REQUEST = "request host";
			var HOST_ACK = "host";
			var GAME_HOSTED = "hosted";
			var CONFIG = "config";
			
			var CONTINUE = "continue";
			var PLAY_GAME = "PLAY"
			
            var WIN = "win";
            var LOSE = "lose";


			triviaMessageReceived = function(id, data)
				{
			    // do stuff
				var senderIndex = players.indexOf(id);
				data_arr = data.split(':');
				
				if (senderIndex > -1)
					{
					var data_split = data.split(',');
					var command = data[0].toLowerCase();
					
				    // fixme todo - lots of message parsing that we need to do!!
					switch (command)    // Filter case for simplicity
						{
						case TRUE:
						case FALSE:
							{
								players[senderIndex].answer = data;
								break;
							}
						case HOST_REQUEST:
							{
							if (gameState == GAME_PENDING) 
								{
								// select host - first person who gets here
								hostID = players[senderIndex];
								gameState = HOST_SELECTED;
								
								var i;
								for (i = 0; i < players.length; i++) 
									{
										if (players[i] == hostID) {
											triviaSendMessage(players[i], HOST_ACK);
										} else {
											triviaSendMessage(players[i], GAME_HOSTED);
										}
									}
								}
								break;
							}
						case PLAY_GAME:
							{
							if (gameState == HOST_SELECTED) 
								{
									// fixme todo : do configuration here, in data_splits[1]
									doQuestion();
								}
								break;
							}
						case CONFIG:
							{	
									// fixme todo : do configuration here, in data_splits[1]
								break;
							}
						case CONTINUE:
							{
							//gameState == GAME_PENDING ? doQuestion () : endQuestion ();
							//break;
							
								if (timer_disabled) {
									if (gameState == Q_IN_PROGRESS) {
										endQuestion();
									} else if (gameState == QUESTION_REVIEW) {
										resetQuestion();
										doQuestion();
									}
								}
									
							
								break;
							}
						default:
							{
							console.log ("Trivia Message Received: \'" + data);
								break;
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

			var readyForMessages = true; // todo
			triviaOnConnect = function(id) {
			    // do stuff
                // temp
				if (readyForMessages) {
					sendCastMessage(event.senderId, "connected");
				}
				if (players.length == 0) {
					setGamePending();
				}
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
			
			var timer_disable = false;
			configureTrivia = function(cfg) {
				// todo: configuration options?
				if (cfg == "1") {
					timer_disable = true;
				} else {
					timer_disable = false;
				}
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

						var qbox = document.getElementById("qbox");
						var questionHTML = question + "<br>" + answer + "<br>" + "True or False?";
						qbox.innerHTML = questionHTML;

						//var aboxtext = document.getElementById("aboxtext");
						//aboxtext.innerHTML = answer;

						// send Q&A to phones
						var i;
						for (i = 0; i < players.length; i++) {
						    // fixme todo - will have to think about disconnects
						    // while looping over players
						    triviaSendMessage(players[i], ("Q: " +  question));
						    triviaSendMessage(players[i], ("A: " +  answer));
					    }
						if (!timer_disabled) {
							fadeStartTime = $.now();
							fadeInVar = setInterval(fadeIn, 50);
						}
					},
					error: function(xhr, desc, err) {
						console.log(xhr);
						console.log("Details: " + desc + "\nError: " + err);
					}
				});
			}

			endQuestion = function () {
				var qbox = document.getElementById("qbox");
				if (answerIsTrue) {
				    qbox.innerHTML = (qbox.innerHTML + "<br>" + "It's True!");
		        } else {
		            qbox.innerHTML = (qbox.innerHTML + "<br>" + "It's False!");
		        }
                gameState = QUESTION_REVIEW;

			    var i;
			    // fixme think about player disconnects during loops
			    for (i = 0; i < players.length; i++) {
			        if (    (answerIsTrue && (players[i].answer == TRUE)) ||
			                (!answerIsTrue && (players[i].answer == FALSE))) {
			            triviaSendMessage(players[i], WIN);
			        } else {
			            triviaSendMessage(players[i], LOSE);
			        }
			    }

				if (!timer_disable) {
					questionEndStartTime = $.now();
					questionEndTimerVar = setInterval(function () {
						if (($.now() - questionEndStartTime) > 5000) { // 10 seconds
							clearInterval(questionEndTimerVar);
							resetQuestion();
							doQuestion();
						}
					}, 50);
				} 
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
