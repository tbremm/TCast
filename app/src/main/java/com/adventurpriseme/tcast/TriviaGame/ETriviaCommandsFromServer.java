package com.adventurpriseme.tcast.TriviaGame;

/**
 * Contains all command strings expected to be received from the server.
 *
 * Created by Timothy on 1/2/2015.
 * Copyright 1/2/2015 adventurpriseme.com
 */
public enum ETriviaCommandsFromServer
	{
		// **************************
		// Enum values
		// **************************
		E_INVALID ("invalid"),
		// This must go first
		// Command separator (place between command and key-value pairs)
		MSG_SPLIT_DATA (String.valueOf ('|')),
		// Command strings
		MSG_CONNECTED ("connected"),
		MSG_HOST ("host"),
		MSG_HOSTED ("hosted"),
		MSG_REQUEST_CONFIG ("request config"),
		MSG_Q_AND_A ("qanda"),
		MSG_WIN ("win"),
		MSG_LOSE ("lose"),
		MSG_ERROR ("error"),
		// Length of enum table
		NUM_ENUM ("Total messages");
	// **************************
	// Functionality to provite toString() ability to get string values for the enums
	// **************************
	private final String text;

	private ETriviaCommandsFromServer (final String text)
		{
		this.text = text;
		}

	/**
	 * Get the enumeration that matches the given string.
	 *
	 * @param str
	 * 	(required)  String whose enum we need to find
	 *
	 * @return ETriviaMessagesToServer  The enum whose string value matches str
	 */
	public static ETriviaCommandsFromServer getEnumFromString (String str)
		{
		for (ETriviaCommandsFromServer eMsg : ETriviaCommandsFromServer.values ())
			{
			if (str.equals (eMsg.toString ()))
				{
				return eMsg;
				}
			}
		return E_INVALID;   // Return error if not found
		}

	@Override
	public String toString ()
		{
		return text;
		}
	}
