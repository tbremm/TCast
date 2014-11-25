package com.adventurpriseme.tcast;

/**
 * This class defines a player for the trivia game.
 * <p/>
 * Created by Timothy on 11/19/2014.
 * Copyright 11/19/2014 adventurpriseme.com
 */
public class CTriviaPlayer implements IPerson
	{
	// Data members
	private String  m_strName   = "";
	private boolean m_bWillHost = true;
	private int     m_nScore    = 0;

	/**
	 * Constructor
	 */
	public CTriviaPlayer ()
		{
		}

	/**
	 * Get the person's name.
	 *
	 * @return a string containing the person's name.
	 */
	@Override
	public String getName ()
		{
		return m_strName;
		}

	/**
	 * Set the person's name.
	 *
	 * @param strName
	 * 		(required)  The new player name
	 */
	@Override
	public void setName (String strName)
		{
		m_strName = strName;
		}

	/**
	 * Get the player's willingness to host a game.
	 *
	 * @return True if player is willing to host, false otherwise.
	 */
	public boolean getWillHost ()
		{
		return m_bWillHost;
		}

	/**
	 * Set the player's willingness to host a game.
	 *
	 * @param bWillHost
	 * 		(required)  True if player is willing to host, false otherwise.
	 */
	public void setWillHost (boolean bWillHost)
		{
		if (m_bWillHost != bWillHost)
			{
			m_bWillHost = bWillHost;
			}
		}

	/**
	 * Gets the player's score.
	 *
	 * @return The player's score.
	 */
	public int getScore ()
		{
		return m_nScore;
		}

	/**
	 * Sets the player's score.
	 *
	 * @param nScore
	 * 		(optional) Sets the player's score.
	 */
	public void setScore (int nScore)
		{
		if (m_nScore != nScore)
			{
			m_nScore = nScore;
			}
		}
	}
