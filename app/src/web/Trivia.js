
			var fadeStartTime = -1;
			var roundStartTime = -1;
			var fadeInVar;
			var roundTimerVar;
			var players = [];
			var answerIsTrue;
			var postRoundTimerVar;
            var postRoundStartTime;

			var gameState;
			// possibilities for game state
			
			var GAME_PENDING = 0;   // game has not started
			var HOST_SELECTED = 1;	// host selected
			var ROUND = 2;  // question is up, timer ticking
			var POST_ROUND = 3;          // question is over, give a few seconds to review

            triviaWindowLoad = function () {
                setGamePending();
                // do stuff
            }

			resetRound = function() {
			    fadeStartTime = -1;
			    roundStartTime = -1;
                clearInterval(roundTimerVar);
                clearInterval(fadeInVar);

				var qbox = document.getElementById("qbox");
				qbox.style.opacity = 0.0;

			}

            // list of commands
			// commands from phones -> web
            var TRUE = "true";					// for an answer of 'true'
            var FALSE = "false";				// for an answer of 'false'
			var CONNECTED = "connected";		// message to let us know phone connected - also echoed back
			var HOST_REQUEST = "request host";	// phone requesting to be host
			var CONFIG = "config";				// configuration message from host
			var BEGIN_ROUND = "begin round";	// begin a round, sent from host
			var END_ROUND = "end round";		// end a round, sent from host
			
			// commands from web -> phones
			var HOST_ACK = "host";				// let phone know they are the host	
			var GAME_HOSTED = "hosted";			// let non-host phones know that host has been selected
			
            var WIN = "win";
            var LOSE = "lose";

			getPlayerIndexById = function(id) 
				{
				var i;
				for (i = 0; i < players.length; i++) 
					{
					if (players[i].id = id) 
						{
						return i;
						}
					}
				}
				
			triviaMessageReceived = function(id, data)
				{
			    // do stuff
				var senderIndex = getPlayerIndexById(id);
				
				//players.indexOf(id);
				
				if (senderIndex > -1)
					{
					var data_split = data.split(',');
					var command = data_split[0].toLowerCase();
					
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
								hostID = players[senderIndex].id;
								gameState = HOST_SELECTED;
								
								var i;
								for (i = 0; i < players.length; i++) 
									{
										if (players[i].id == hostID) {
											triviaSendMessage(players[i].id, HOST_ACK);
										} else {
											triviaSendMessage(players[i].id, GAME_HOSTED);
										}
									}
								}
								break;
							}
						case BEGIN_ROUND:
							{
							if (gameState == HOST_SELECTED) 
								{
								// fixme - this is awkward as fuck
								var j;
								var arg = "";
								for (j = 0; j < (data_split.length - 1); j++) {
									arg += data_split[j+1];
								}
								configureTrivia(arg, id); // dont allow in questions? fixme todo
							
								doRound();
								}
							break;
							}
						case CONFIG:
							{	
							// fixme todo : do configuration here, in data_splits[1]
							// fixme todo : add in conditions so config can / cant change on the fly
							// fixme - this is awkward as fuck
							var j;
							var arg = "";
							for (j = 0; j < (data_split.length - 1); j++) {
								arg += data_split[j+1];
							}
							configureTrivia(arg, id); // dont allow in questions? fixme todo
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
			    var ind = getPlayerIndexById(id);//players.indexOf(id);
			    // fixme todo players.splice(ind, 1);
			    //if (players.length == 0) {
			    //    setGamePending();
			    //}
			}

			var readyForMessages = true; // todo
			triviaOnConnect = function(id) {
			    // do stuff
                // temp
				if (readyForMessages) {
					sendCastMessage(id, "connected");
				}
				if (players.length == 0) {
					setGamePending();
				}
				
				var _new_player = new Object();
				_new_player.id = id;
				
				players.push(_new_player);
			    //doRound();
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
			
			var round_timer_enable = true;
			var postround_timer_enable = true;
			configureTrivia = function(cfg, senderId) {
				// todo: configuration options?
				var cfg_options = cfg.split('|');
				var i;
				for (i = 0; i < cfg_options.length; i++){
					var option = cfg_options[i];
	
					var option_split = option.split('=');
					var switch_arg;
					if (option_split.length > 1 ) {
						switch_arg = option_split[0];
					} else {
						switch_arg = option;
					}
					
					switch (switch_arg) {
						case "round timer": {
							if (option_split[1] == "true") {
								round_timer_enable = true;
							} else if (option_split[1] == "false") {
								round_timer_enable = false;
							}
							break;
						} case "postround timer": {
							// fixme todo - should functionize common things like this true/false
							if (option_split[1] == "true") {
								postround_timer_enable = true;
							} else if (option_split[1] == "false") {
								postround_timer_enable = false;
							}
						} case "my name": {
							var ind = getPlayerIndexById(senderId);
							players[ind].name = option_split[1];
						}	
						
					}
				}
					
			}

			doRound = function() {
				//alert("onload reached");
				gameState = ROUND;
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
						    triviaSendMessage(players[i].id, ("Q: " +  question));
						    triviaSendMessage(players[i].id, ("A: " +  answer));
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

			endRound = function () {
				var qbox = document.getElementById("qbox");
				if (answerIsTrue) {
				    qbox.innerHTML = (qbox.innerHTML + "<br>" + "It's True!");
		        } else {
		            qbox.innerHTML = (qbox.innerHTML + "<br>" + "It's False!");
		        }
                gameState = POST_ROUND;

			    var i;
			    // fixme think about player disconnects during loops
			    for (i = 0; i < players.length; i++) {
			        if (    (answerIsTrue && (players[i].answer == TRUE)) ||
			                (!answerIsTrue && (players[i].answer == FALSE))) {
			            triviaSendMessage(players[i].id, WIN);
			        } else {
			            triviaSendMessage(players[i].id, LOSE);
			        }
			    }

				if (postround_timer_enable) {
					postRoundStartTime = $.now();
					postRoundTimerVar = setInterval(function () {
						if (!postround_timer_enable || (($.now() - postRoundStartTime)) > 5000) { // 5 seconds
							clearInterval(postRoundTimerVar);
							resetRound();
							doRound();
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
					startRoundTimer();
				}

				qbox.style.opacity = opacity;
			}

			var roundTime = 30000; // in ms
			function startRoundTimer () {
				if (round_timer_enable) {
					roundStartTime = $.now();
					roundTimerVar = setInterval(roundTimerFunc, 50);
				}
			}

			function roundTimerFunc () {
				if (round_timer_enable) {
					var thisTime = $.now();

					var timePassed = thisTime - roundStartTime;
					var timeLeft = roundTime - timePassed;
					timeLeft = Math.round(timeLeft/1000.0);

					var timer = document.getElementById("timer");
					timer.innerHTML = timeLeft;

					if (timeLeft < 0) {
						timer.innerHTML = 0;
						clearInterval(roundTimerVar);
						//var i;
						//for (i = 0; i < players.length; i++) {
							// no need to send timeout message triviaSendMessage(players[i].id, "timeout");
						//} no need for this for loop - win/loss message will make it clear the round is over
						endRound(); // fixme todo - give phones time to give last-seond answer?
					}
				} else {
					timer.innerHTML = 0;
					endRound();
				}
			}
