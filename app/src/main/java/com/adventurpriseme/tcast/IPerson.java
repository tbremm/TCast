package com.adventurpriseme.tcast;

/**
 * Interface IPerson - A generic interface for a person.
 * <p/>
 * This is intended to ensure that the basic common data for any class representing a person
 * can be guaranteed to exist. For example, a user or a game character.
 * <p/>
 * Created by Timothy on 11/19/2014.
 * Copyright 11/19/2014 adventurpriseme.com
 */
public interface IPerson
	{
	/**
	 * Get the person's name.
	 *
	 * @return a string containing the person's name.
	 */
	String getName ();

	/**
	 * Set the person's name.
	 *
	 * @param strName
	 * 		(required)  The new player name
	 */
	void setName (String strName);
	}
