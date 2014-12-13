        // **********************************************
        // Globals for casting
        // **********************************************

        // **********************************************
        // Main window onLoad - Receiver app entry point
        // **********************************************
        window.onload = function () {
            cast.receiver.logger.setLevelValue (cast.receiver.LoggerLevel.DEBUG);
            window.castReceiverManager = cast.receiver.CastReceiverManager.getInstance ();
            displayDebug ('Starting Receiver Manager');

            displayDebug ("onload");

            // CALL INTO TRIVIA
            triviaWindowLoad();

            // **********************************************
            // handler for the 'ready' event
            // **********************************************
            castReceiverManager.onReady = function (event) {
                displayDebug ('Received Ready event: ' + JSON.stringify (event.data));
                window.castReceiverManager.setApplicationState ("Application status is ready...");
                displayDebug ("onReady");
            };

            // **********************************************
            // handler for 'senderconnected' event
            // **********************************************
            castReceiverManager.onSenderConnected = function (event) {
                displayDebug ('Received Sender Connected event: ' + event.data);
                displayDebug (window.castReceiverManager.getSender (event.data).userAgent);
                displayDebug ("onSenderConnected");

                // CALL INTO TRIVIA
                triviaOnConnect(event.senderId);
            };

            // **********************************************
            // handler for 'senderdisconnected' event
            // **********************************************
            castReceiverManager.onSenderDisconnected = function (event) {
                displayDebug ('Received Sender Disconnected event: ' + event.data);
                if (window.castReceiverManager.getSenders ().length == 0) {
                    window.close ();

                }
                displayDebug ("onSenderDisconnected");

				// TODO: STUFF ON DISCONNECT HERE

                // CALL INTO TRIVIA
                triviaOnDisconnect(event.senderId);
            };

            // **********************************************
            // handler for 'systemvolumechanged' event
            // **********************************************
            castReceiverManager.onSystemVolumeChanged = function (event) {
                displayDebug ('Received System Volume Changed event: ' + event.data['level'] + ' ' + event.data['muted']);
                displayDebug ("onSystemVolumeChanged");
            };

            // create a CastMessageBus to handle messages for a custom namespace
            window.messageBus = window.castReceiverManager.getCastMessageBus ('urn:x-cast:com.adventurpriseme.tcast');

            // **********************************************
            // handler for the CastMessageBus message event
            // This is where the magic begins!!
            // **********************************************
            window.messageBus.onMessage = function (event) {
                // inform all senders on the CastMessageBus of the incoming message event
                // sender message listener will be invoked
                displayDebug ("onMessage");

                // TODO: Remove debugging code
                displayDebug ('Message [' + event.senderId + ']: ' + event.data);    // Output debug info
                displayMessage (event.data);                                         // Output raw message to screen for debug

				// TODO: DO GAME STUFF HERE

                // CALL INTO TRIVIA
                triviaMessageReceived(event.senderId, event.data);
            }

            // initialize the CastReceiverManager with an application status message
            window.castReceiverManager.start ({statusText: "Application is starting"});
            displayDebug ('Receiver Manager started');
            };  // onLoad ()


        sendCastMessage = function(id, msg) {
            window.messageBus.send(id, msg);
        }

        // **********************************************
        // utility function wrapper to display text in a given id
        // **********************************************
        function displayText (text, elementID) {
            console.log ("Message: " + text + "; elementID: " + elementID);
            // todo document.getElementById (elementID).innerHTML=text;
            window.castReceiverManager.setApplicationState ("displayText");
        }

        // **********************************************
        // utility function to display the received text in the input field
        // **********************************************
        function displayMessage (text) {
            console.log (text);
            // todo document.getElementById ("message").innerHTML=text;
            window.castReceiverManager.setApplicationState ("displayMessage");
        }

        // **********************************************
        // utility function to display the debug text in the input field
        // **********************************************
        function displayDebug (text) {
            console.log (text);
            // todo document.getElementById ("debug").innerHTML=text;
            window.castReceiverManager.setApplicationState ("displayDebug");
        }
