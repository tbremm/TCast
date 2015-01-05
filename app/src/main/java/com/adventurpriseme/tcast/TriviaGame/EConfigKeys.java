package com.adventurpriseme.tcast.TriviaGame;

/**
 * Created by Timothy on 1/2/2015.
 * Copyright 1/2/2015 adventurpriseme.com
 */
public enum EConfigKeys
	{
		// **************************
		// Enum values
		// **************************
		E_INVALID ("invalid"),
		// This must go first
		// Key strings
		MSG_KEY_UID ("uid"),
		MSG_KEY_ROUND_TIMER ("round timer"),
		MSG_KEY_POSTROUND_TIMER ("postround timer"),
		MSG_KEY_PLAYER_NAME ("player name"),
		MSG_KEY_PLAYER_SCORE ("player score"),
		MSG_KEY_PLAYER_IS_HOSTING ("player host"),
		MSG_KEY_PLAYER_WILL_HOST ("player will host"),
		MSG_KEY_QUESTION ("q"),
		MSG_KEY_ANSWER ("a");
	// **************************
	// Functionality to provite toString() ability to get string values for the enums
	// **************************
	private final String text;

	private EConfigKeys (final String text)
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
	public static EConfigKeys getEnumFromString (String str)
		{
		for (EConfigKeys eMsg : EConfigKeys.values ())
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
