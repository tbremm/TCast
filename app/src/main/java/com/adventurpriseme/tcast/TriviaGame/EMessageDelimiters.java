package com.adventurpriseme.tcast.TriviaGame;

/**
 * Created by Timothy on 1/2/2015.
 * Copyright 1/2/2015 adventurpriseme.com
 */
public enum EMessageDelimiters
	{
		MSG_SPLIT_KEY_VALUE (String.valueOf ('=')),
		// Key-value separator
		MSG_SPLIT_DATA (String.valueOf ('|')),
		// Command separator (place between command and key-value pairs)
		E_INVALID ("invalid");                                  // Used when looking for a non-existent entry
	// **************************
	// Functionality to provite toString() ability to get string values for the enums
	// **************************
	private final String text;

	private EMessageDelimiters (final String text)
		{
		this.text = text;
		}

	/**
	 * Get the enumeration that matches the given string.
	 *
	 * @param str
	 * 	(required)  String whose enum we need to find
	 *
	 * @return EMessageDelimiters  The enum whose string value matches str
	 */
	public static EMessageDelimiters getEnumFromString (String str)
		{
		for (EMessageDelimiters eMsg : EMessageDelimiters.values ())
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
