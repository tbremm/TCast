package com.adventurpriseme.tcast.TriviaGame;

/**
 * Base enumeration for enumerating strings.
 * <p/>
 * Created by Timothy on 1/3/2015.
 * Copyright 1/3/2015 adventurpriseme.com
 */
public enum EBaseStringEnum
	{
		E_INVALID ("invalid");  // This must go first
	// **************************
	// Functionality to provite toString() ability to get string values for the enums
	// **************************
	private final String text;

	private EBaseStringEnum (final String text)
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
	public static EBaseStringEnum getEnumFromString (String str)
		{
		for (EBaseStringEnum eMsg : EBaseStringEnum.values ())
			{
			if (str.equals (eMsg.toString ()))
				{
				return eMsg;
				}
			}
		return null;
		}

	@Override
	public String toString ()
		{
		return text;
		}
	}
