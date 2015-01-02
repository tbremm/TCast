package com.adventurpriseme.tcast.TriviaGame;

/**
 * Contains all Trivia-related communication string parts.
 * <p/>
 * Call toString() on the desired enum in order to get it's string value.
 * <p/>
 * Created by Timothy on 1/2/2015.
 * Copyright 1/2/2015 adventurpriseme.com
 */
public enum ETriviaMessagesToServer
	{
		// **************************
		// Enum values
		// **************************
		E_INVALID ("invalid"),
		// This must go first
		// Command strings
		MSG_CONFIG ("config"),
		MSG_BEGIN_ROUND ("begin round"),
		MSG_END_ROUND ("end round"),
		MSG_REQUEST_HOST ("request host"),
		MSG_CONNECTED ("connected"),
		MSG_HOST ("host"),
		MSG_HOSTED ("hosted"),
		MSG_REQUEST_CONFIG ("request config"),
		MSG_Q_AND_A ("qanda"),
		MSG_WIN ("win"),
		MSG_LOSE ("lose"),
		// Length of enum table
		NUM_ENUM ("Total messages");
	// **************************
	// Functionality to provite toString() ability to get string values for the enums
	// **************************
	private final String text;

	private ETriviaMessagesToServer (final String text)
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
	public static ETriviaMessagesToServer getEnumFromString (String str)
		{
		for (ETriviaMessagesToServer eMsg : ETriviaMessagesToServer.values ())
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
