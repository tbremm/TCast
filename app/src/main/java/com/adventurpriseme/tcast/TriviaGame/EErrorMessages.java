package com.adventurpriseme.tcast.TriviaGame;

/**
 * Created by Timothy on 1/2/2015.
 * Copyright 1/2/2015 adventurpriseme.com
 */
public enum EErrorMessages
	{
		// **************************
		// Enum values
		// **************************
		E_INVALID ("invalid"),
		// This must go first
		// Error message control strings
		MSG_ERROR ("error"),
		// Command "error"
		MSG_ERROR_MSG ("msg"),
		// error key
		// List of error messages
		MSG_ERROR_RECEIVED_EMPTY_MSG ("received empty message"),
		// Error message
		// Length of enum table
		NUM_ENUM ("Total messages");
	// **************************
	// Functionality to provite toString() ability to get string values for the enums
	// **************************
	private final String text;

	private EErrorMessages (final String text)
		{
		this.text = text;
		}

	/**
	 * Get the enumeration that matches the given string.
	 *
	 * @param str
	 * 	(required)  String whose enum we need to find
	 *
	 * @return EErrorMessages  The enum whose string value matches str
	 */
	public static EErrorMessages getEnumFromString (String str)
		{
		for (EErrorMessages eMsg : EErrorMessages.values ())
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
