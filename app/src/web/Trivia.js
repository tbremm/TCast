			// **************************************
			// Constant assignments
			// **************************************

			// Game states
			var ERROR_DATABASE = -1;
            var GAME_PENDING = 0;   // game has not started
            var HOST_SELECTED = 1;	// host selected
            var ROUND = 2;  // question is up, timer ticking
            var POST_ROUND = 3;          // question is over, give a few seconds to review

            // commands from web -> phones
            var HOST_ACK = "host";				// let phone know they are the host
            var GAME_HOSTED = "hosted";			// let non-host phones know that host has been selected
            var WIN = "win";                    // Let player know they won a round
            var LOSE = "lose";                  // Let player know they lost a round
            var GET_CONFIG = "request config";  // Ask the player for their config info

			// commands from phones -> web
            var TRUE = "true";					    // for an answer of 'true'
            var FALSE = "false";				    // for an answer of 'false'
			var CONNECTED = "connected";		    // message to let us know phone connected
			var HOST_REQUEST = "request host";	    // phone requesting to be host
			var CONFIG = "config";				    // configuration message from host
			var BEGIN_ROUND = "begin round";	    // begin a round, sent from host
			var END_ROUND = "end round";		    // end a round, sent from host
			var ANSWER = "a";					    // answer from a phone

			// Configuration keys
			var CFG_ROUND_TIMER = "round timer";            // Configure the round timer
			var CFG_POST_ROUND_TIMER = "postround timer";   // Configure the post-round timer
			var CFG_PLAYER_NAME = "player name";            // Configure the player name

			// Configuration values
			var CFG_ENABLE = "true";      // Standard value to enable a binary option
			var CFG_DISABLE = "false";    // Standard value to disable a binary option

			// Default values
			var DEFAULT_POST_ROUND_TIMER = 5000;                // Default length of the post-round timer in ms
			var DEFAULT_ROUND_TIMER = 30000;                    // Default length of the round timer in ms
			var DEFAULT_TIMER_RESOLUTION = 50;                  // Default resolution for timer intervals
			var m_roundTime = DEFAULT_ROUND_TIMER;              // Round timer in ms
			var DEFAULT_FADE_IN_CFG_POST_ROUND_TIMER =  2000;   // Default length of the fade-in timer in ms

			// ***********************************
			// Global variables
			// ***********************************
			var m_gameState = 0;                    // Game state
			var m_round_timer_enable = true;        // Flag indicating state of round timer
            var m_postround_timer_enable = true;    // Flag indicating state of post-round timer
			var m_fadeStartTime = -1;               // Fade in length timer
			var m_roundStartTime = -1;              // Round timer starting value
			var m_postRoundStartTime = -1;          // Post round timer starting value
			var m_qboxFadeInVar;                    // Fade in interval object for question box
			var m_roundTimerVar;                    // Round timer interval object
			var m_players = [];                     // Player list
			var m_hostID = "";                      // ID of sender that is hosting
			var m_roundAnswer = "";                 // Answer for this round's trivia question
			var m_postRoundTimerVar;                // Round timer interval object
			var m_readyForMessages = true; // todo

			// *********************************
			// function triviaWindowLoad ()
			//
			// Called to initialize stuff
			// *********************************
            triviaWindowLoad = function ()
                {
                // Init game state
                resetRound ();
                setGamePending();
                }

			// *********************************
            // function resetRound ()
            //
            // Called to clear out round timers and to hide round-related UI elements.
            // *********************************
			resetRound = function() {

				// Clear timers
			    m_fadeStartTime = -1;
			    m_roundStartTime = -1;
			    m_postRoundStartTime = -1;
			    clearInterval (m_qboxFadeInVar);
                clearInterval (m_roundTimerVar);
                clearInterval (m_postRoundTimerVar);

				// Make text box disappear
				var qbox = document.getElementById("qbox");
				qbox.style.opacity = 0.0;

			}

			// ***********************************
			// function getPlayerIndexById (id)
			//
			// Returns the index of the player whose sender ID matches id. Returns -1 if no match is found.
			// ***********************************
			getPlayerIndexById = function(id)
				{
				var i;
				for (i = 0; i < m_players.length; i++)
					{
					if (m_players[i].id == id)
						{
						return i;
						}
					}
				return -1;
				}

			// Send a message to all players
			// Set bIncludeHost == true in order to send to the host as well
			sendToAllPlayers = function (message, bIncludeHost)
				{
				for (var i = 0; i < m_players.length; i++)
	                {
	                if (bIncludeHost == true || m_players[i].id != m_hostID)
	                    {
	                    triviaSendMessage(m_players[i].id, message);
	                    }
	                }
				}

			// **************************************
			// function triviaMessageReceived (id, data)
			//
			// Data packet is formatted like this:
			//  "command[|key_0=value_0[|key_1=value_1|...|key_n=value_n]]"
			//  where
			//      contents of "[]" brackets are optional,
			//      "command" is the operational command to handle, determines how key=value pairs are handled
			//      "key" is a parameter name that is associated with a value
			//      "value is the value of the parameter specified by the key
			// **************************************
			triviaMessageReceived = function(id, data)
				{
			    // do stuff
				var senderIndex = getPlayerIndexById(id);

				if (senderIndex > -1)
					{
					var data_split = data.split('|');           // Break up the data packet
					if (data_split.length > 0)
						{
						var command = data_split[0].toLowerCase();  // We currently issue case-insensitive commands
						switch (command)                            // Parse remaining data based on command statement
							{
							case ANSWER:
								{
								// Expecting "<ANSWER>|<player answer>"
								if (data_split.length == 2)
									{
									m_players[senderIndex].answer = data_split[1];
									}
								else
									{
									console.log ("Error: <triviaMessageReceived>  Bad answer packet length: " + data_split.length);
									}
								break;
								}
							case HOST_REQUEST:
								{
								if (m_gameState == GAME_PENDING)
									{
									// Assign host if not already assigned
									if (m_hostID == "" && m_players.length > senderIndex)
										{
										m_hostID = m_players[senderIndex].id;
										triviaSendMessage(m_hostID, HOST_ACK);
										sendToAllPlayers (GAME_HOSTED, false);  // Send to all but host that the game is ready
										m_gameState = HOST_SELECTED;
										}
									else
										{
										// Host is already assigned, let the player know
	                                    triviaSendMessage(m_players[senderIndex].id, GAME_HOSTED);
										}
									}
								break;
								}
							case BEGIN_ROUND:
								{
								// We are either starting a new round or another round
								if (m_gameState == HOST_SELECTED || m_gameState == POST_ROUND)
									{
									m_gameState = ROUND;

									// Check if we have config info
									if (data_split.length > 1)
										{
										for (var j = 1; j < data_split.length; j++)
											{
											configureTrivia (data_split[j], id);
											}
										}
									resetRound();
									doRound();
									}
								break;
								}
							case CONFIG:
								{
								// fixme todo : add in conditions so config can / cant change on the fly
								// If there are config options, process each of them in order
								if (data_split.length > 1)
									{
									var j;
									for (j = 1; j < data_split.length; j++)
										{
										configureTrivia(data_split[j], id);
										}
									}
								break;
								}
							case END_ROUND:
								{
								if (m_gameState == ROUND)
									{
									m_gameState = POST_ROUND;
									endRound();
									}
								}
							default:
								{
								console.log ("Trivia Message Received: \'" + data);
								break;
								}
							}
						}
					}
				}

			triviaOnDisconnect = function(id)
				{
				console.log ("Player disconnected: " + getPlayerIndexById (id));
			    // Remove player from player list
			    var ind = getPlayerIndexById (id);
			    if (ind > -1)
			        {
			        if (id == m_hostID)
			            {
			            // Host is disconnecting, reset the game
			            m_hostID = "";
			            }

			        m_players = m_players.splice (ind, 1);

			        // Reset game state if player list is now empty or if host disconnects
                    if (m_players.length == 0)
                        {
                        // TODO: No more players - disconnect after a timeout
                        resetRound ();
                        }
                    if (m_hostID == "")
                        {
                        resetRound ();
                        setGamePending();
                        }
			        }
				}

			// Player constructor
			function Player (id)
				{
				this.id = id;       // Assign the id to the new player
				this.score = 0;     // Initialize the player's score
				this.name = "";     // Initialize the player's name
				this.answer = "";   // Initialize the player's answer
				}

			triviaOnConnect = function(id)
				{
				// Don't allow duplicate ID's
				if (getPlayerIndexById (id) == -1)
					{
					m_players.push (new Player (id));        // Add player to players list

					// Add the player to the game depending on game state
					switch (m_gameState)
						{
						case GAME_PENDING:
							{
							// If this is the first player, initialize the game
							if (m_players.length == 1)
                                {
                                resetRound ();
                                setGamePending();
                                }
                            if (m_readyForMessages)
                                {
                                sendCastMessage(id, "connected");
                                }
							break;
							}
						case HOST_SELECTED:
							{
							if (m_readyForMessages)
	                            {
	                            sendCastMessage(id, GAME_HOSTED);   // Let player know they're not hosting. This will also cause them to send us their config info.
	                            }
							break;
							}
						case ROUND:
							{
							break;
							}
						case POST_ROUND:
							{
							break;
							}
						default:
							{
							console.log ("ERROR: Unknown gamestate - " + m_gameState);
							return;
							}
						}
	                }
	            else
                    {
                    console.log ("ERROR: Player with id \'" + id + "\' already connected...");
                    return;
                    }
				}

			triviaSendMessage = function(id, msg) {
			    // do stuff
				sendCastMessage(id, msg);
			}

			// *****************************************
			// function setGamePending ()
			//
			// Reset and initialize a new game
			// *****************************************
			setGamePending = function()
				{
			    m_gameState = GAME_PENDING;
		        var qbox = document.getElementById("qbox");
		        qbox.style.opacity = 1.0;
				qbox.innerHTML = "Game Pending...";
				}

			// *****************************************
			// function configureTrivia (cfg, senderId)
			//
			// This handles key=value pair strings received from the player.
			// cfg is a string with the format "key=value".
			// senderId is the id of the player to be configured.
			// *****************************************
			configureTrivia = function (cfg, senderId)
			    {
				var option_split = cfg.split('=');   // Break apart the key-value pair
				if (option_split.length == 2)
					{
					var switch_arg;
					key = option_split[0];      // Get the key
					value = option_split[1];    // Get the value
					switch (key)
						{
						case CFG_ROUND_TIMER:
							{
							m_round_timer_enable = (value == "true");
							if (!m_round_timer_enable)   // If timer is disabled, make it disappear
								{
								document.getElementById ("timer").style.opacity = 0;
								}
							break;
							}
						case CFG_POST_ROUND_TIMER:
							{
							m_postround_timer_enable = (value == "true");
							break;
							}
						case CFG_PLAYER_NAME:
							{
							var ind = getPlayerIndexById (senderId);
							m_players[ind].name = value;
							break;
							}
						default:
							{
							break;
							}
						}
					}
				else
					{
					// Handle a bad key=value pair
					console.log ("Error: <configureTrivia()>  Invalid key=value pair...");
					}
				}

			doRound = function()
				{
				$.ajax(
					{
					url: 'GetFourAnswerQ.php',
					type: 'post',
					data: '',
					success: function(data)
						{
						resetRound();
						m_gameState = ROUND;

						var split_data = data.split('|');

					    // sometimes whitespace seems to trickle in no matter
					    // how hard you try in the PHP code. so important
					    // to trim whitespace
						var question = $.trim(split_data[0]);
						var a1 = $.trim(split_data[1]);
						var a2 = $.trim(split_data[2]);
						var a3 = $.trim(split_data[3]);
						var a4 = $.trim(split_data[4]);
						m_roundAnswer = $.trim(split_data[5]);

						// Populate UI with question/answer choices
						var qbox = document.getElementById("qbox");
						var questionHTML = question + "<br>" + a1 + "<br>" + a2 + "<br>" + a3 + "<br>" + a4;
						qbox.innerHTML = questionHTML;

						// send Q&A to m_players
						var i;
						for (i = 0; i < m_players.length; i++)
							{
						    // fixme todo - will have to think about disconnects
						    // while looping over m_players
							var msg = 	"qanda" +
										"|q=" + question +
										"|a=" + a1 +
										"|a=" + a2 +
										"|a=" + a3 +
										"|a=" + a4;

						    triviaSendMessage(m_players[i].id, msg);
					        }

					    // Fade in question/answer box
						m_fadeStartTime = $.now();
						m_qboxFadeInVar = setInterval(fadeIn, DEFAULT_TIMER_RESOLUTION);
						},
					error: function(xhr, desc, err)
						{
						m_gameState = ERROR_DATABASE;
						console.log(xhr);
						console.log("Details: " + desc + "\nError: " + err);
						}
					});
				}

			endRound = function ()
				{
				var qbox = document.getElementById("qbox");
				var strWinners = "";    // Winners list to display
				var strScores = "";     // Scores list to display
			    // fixme think about player disconnects during loops
			    // fixme:   Could check game state during onConnect, and not allow entry until the proper game state.
			    // fixme:   We could put their name in a waiting list, then check it between rounds?
			    for (var i = 0; i < m_players.length; i++)
			        {
			        if (typeof (m_players[i].answer) == "undefined" || typeof (m_roundAnswer) == "undefined")
			            {
			            // Player loses if they don't provide an answer, or if there is no answer (bwahaha)
			            triviaSendMessage(m_players[i].id, LOSE);
			            }
			        // The player won
					else if (m_players[i].answer.toLowerCase() == m_roundAnswer.toLowerCase())
						{
						// Notify player of their win
						triviaSendMessage(m_players[i].id, WIN);

						// Build winners string to display on TV
						if (typeof (m_players[i].name) != "undefined")
							{
							strWinners += "<br>" + m_players[i].name;
							}
						else
							{
							strWinners += "<br>" + "Anonymous ";
							}

						// Handle player score
						if (typeof (m_players[i].score) == "undefined")
							{
							// Create initial score
							m_players[i].score = 1;
							}
						else
							{
							m_players[i].score++;
							}
						}
					else    // The player lost
						{
                        // Notify player of their loss
						triviaSendMessage(m_players[i].id, LOSE);
						// todo decrement scores sometimes?
						}

					// Reset player answers
					resetPlayerAnswers ();
			        }

			    qbox.innerHTML = (qbox.innerHTML + "<br><br>" + "Answer: " + m_roundAnswer + "<br><br>" + "Winners:" + strWinners + "<br><br>" + "Scores:<br>" + getScores ());
				startPostRoundTimer();
				}

			function resetPlayerAnswers ()
				{
				for (var i = 0; i < m_players.length; i++)
					{
					m_players[i].answer = "";
					}
				}

			function getScores ()
				{
				var strOut = "";
				for (var i = 0; i < m_players.length; i++)
					{
					// Make sure there's some kind of name
					if (typeof (m_players[i].name) == "undefined")
                        {
                        m_players[i].name = "Player" + i;
                        }

                    // Initialize the score if necessary
					if (typeof (m_players[i].score) == "undefined")
						{
						m_players[i].score = 0;
						}

					// Build the output string
					strOut += m_players[i].name + "&nbsp;&nbsp;" + m_players[i].score + "<br>";
					}
				return strOut;
				}

			function startPostRoundTimer ()
				{
				if (m_postround_timer_enable)
					{
					m_postRoundStartTime = $.now();
					m_postRoundTimerVar = setInterval (function ()
						{
						if ((($.now() - m_postRoundStartTime)) > DEFAULT_POST_ROUND_TIMER)
							{
							clearInterval(m_postRoundTimerVar);
							doRound();
							}
						}, DEFAULT_TIMER_RESOLUTION);
					}
				}

			function fadeIn ()
				{
				var qbox = document.getElementById("qbox"); // FIXME: Replace this with something not hardcoded

				// m_fadeStartTime must be set by now, if not then set opaque and return
                if (typeof m_fadeStartTime === "undefined" || m_fadeStartTime <= 0)
                    {
                    qbox.style.opacity = 1.0;
                    return;
                    }

				var totalTime = DEFAULT_FADE_IN_CFG_POST_ROUND_TIMER;
				var timePassed = $.now() - m_fadeStartTime;
				var opacity = 1.0;   // Default to opaque

				// If time is up, make sure that the object is opaque (and protect against divide-by-zero), otherwise increase opacity
				if (timePassed >= totalTime || typeof (totalTime) == "undefined" || totalTime == 0)
					{
					qbox.style.opacity = 1.0;
					clearInterval(m_qboxFadeInVar);
					startRoundTimer ();
					}
				else
					{
					// Keep on fading in
					qbox.style.opacity = timePassed / totalTime;
					}
				}

			function startRoundTimer ()
				{
				if (m_round_timer_enable)
					{
					m_roundStartTime = $.now();
					m_roundTimerVar = setInterval (roundTimerFunc, DEFAULT_TIMER_RESOLUTION);
					}
				}

			function roundTimerFunc ()
				{
				// Total time - time elapsed
				var timeLeft = m_roundTime - ($.now() - m_roundStartTime);
				timeLeft = Math.round (timeLeft/1000.0);    // Convert to seconds

				var timer = document.getElementById ("timer");
				timer.innerHTML = timeLeft + " seconds remaining..";
				timer.style.opacity = 1.0;

				if (timeLeft <= 0)
					{
					timer.innerHTML = 0;
					clearInterval (m_roundTimerVar);
					endRound(); // fixme todo - give phones time to give last-seond answer?
					}
				}
