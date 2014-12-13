package com.adventurpriseme.tcast;

import android.content.SharedPreferences;

/**
 * This class defines a player for the trivia game.
 * <p/>
 * Created by Timothy on 11/19/2014.
 * Copyright 11/19/2014 adventurpriseme.com
 */
public class CTriviaPlayer
	implements IPerson
	{
	// Data members
	private String  m_strName   = "";
	private boolean m_bWillHost = true;
	private boolean m_bHosting  = false;
	private int     m_nScore    = 0;
	private String  m_strAnswer = "";

	/**
	 * Constructor
	 */
	public CTriviaPlayer (SharedPreferences preferences)
		{
		m_bWillHost = preferences.getBoolean ("pref_host_checkbox_will_host", false);
		m_strName = preferences.getString ("pref_player_name_text", "Player");
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
	 * 	(required)  The new player name
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
	 * 	(required)  True if player is willing to host, false otherwise.
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
	 * 	(optional) Sets the player's score.
	 */
	public void setScore (int nScore)
		{
		if (m_nScore != nScore)
			{
			m_nScore = nScore;
			}
		}

	public String getAnswer ()
		{
		return m_strAnswer;
		}

	public void setAnswer (String strAnswer)
		{
		m_strAnswer = strAnswer;
		}

	public boolean isHosting ()
		{
		return m_bHosting;
		}

	public void setIsHosting (boolean bHosting)
		{
		m_bHosting = bHosting;
		}
	}
