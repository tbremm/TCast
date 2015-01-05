package com.adventurpriseme.tcast;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.adventurpriseme.tcast.TriviaGame.EConfigKeys;

import java.util.HashMap;
import java.util.Map;

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
	// TODO: Note, when adding a new attribute/config setting, update the ctor and getPlayerInfo() accordingly!
	private String  m_strName               = "";
	private boolean m_bWillHost             = true;
	private boolean m_bHosting              = false;
	private boolean m_bEnableRoundTimer     = true;
	private boolean m_bEnablePostRoundTimer = true;
	private int     m_nScore                = 0;
	private String  m_strAnswer             = "";
	private Map    m_mapPlayerInfo = new HashMap ();
	private String m_UID           = "";
	private Activity m_activity;
	// TODO: migrate all config settings into the map

	/**
	 * Constructor
	 */
	public CTriviaPlayer (Activity activity)
		{
		m_activity = activity;
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (m_activity);
		_InitPlayerInfoMap (preferences);
		setWillHost (preferences.getBoolean ("pref_host_checkbox_will_host", false));               // Player's willingness to host
		setName (preferences.getString ("pref_player_name_text", ""));                              // Player's name
		setEnablePostRoundTimer (preferences.getBoolean ("pref_host_checkbox_round_timer", true));  // Post round timer enable
		setEnableRoundTimer (preferences.getBoolean (m_activity.getString (R.string.config_host_postround_timer_enable), true));  // Round timer enable
		setScore (0);
		}

	private void _InitPlayerInfoMap (SharedPreferences preferences)
		{
		// Clear out the map in case it's already been initialized
		m_mapPlayerInfo.clear ();
		for (EConfigKeys key : EConfigKeys.values ())
			{
			if (key != EConfigKeys.E_INVALID)
				{
				// Create the map of attributes that will be populated later
				m_mapPlayerInfo.put (key, null);
				}
			}
		}

	/**
	 * Provides access to the complete set of player info as an array list of string key=value pairs.
	 *
	 * @return ArrayList<String>  Key=Value pairs corresponding to player data items.
	 */
	public Map getPlayerInfoMap ()
		{
		return m_mapPlayerInfo;
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
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences (m_activity)
			                                  .edit ();
		editor.putString ("config_player_name", m_strName);
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
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences (m_activity)
				                                  .edit ();
			editor.putBoolean ("pref_host_checkbox_will_host", false);
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
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences (m_activity)
				                                  .edit ();
			editor.putInt ("config_player_score", m_nScore);
			}
		}

	public String getAnswer ()
		{
		return m_strAnswer;
		}

	public void setAnswer (String strAnswer)
		{
		m_strAnswer = strAnswer;
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences (m_activity)
			                                  .edit ();
		editor.putString ("config_player_answer", m_strAnswer);
		}

	public boolean isHosting ()
		{
		return m_bHosting;
		}

	public void setIsHosting (boolean bHosting)
		{
		m_bHosting = bHosting;
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences (m_activity)
			                                  .edit ();
		editor.putBoolean ("config_player_hosting", m_bHosting);
		}

	public boolean getEnableRoundTimer ()
		{
		return m_bEnableRoundTimer;
		}

	public void setEnableRoundTimer (boolean bEnableRoundTimer)
		{
		m_bEnableRoundTimer = bEnableRoundTimer;
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences (m_activity)
			                                  .edit ();
		editor.putBoolean ("pref_host_checkbox_round_timer", m_bEnableRoundTimer);
		}

	public boolean getEnablePostRoundTimer ()
		{
		return m_bEnablePostRoundTimer;
		}

	public void setEnablePostRoundTimer (boolean bEnablePostRoundTimer)
		{
		m_bEnablePostRoundTimer = bEnablePostRoundTimer;
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences (m_activity)
			                                  .edit ();
		editor.putBoolean (m_activity.getString (R.string.config_host_postround_timer_enable), m_bEnablePostRoundTimer);
		}

	public String getUID ()
		{
		return m_UID;
		}

	public void setUID (String UID)
		{
		m_UID = UID;
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences (m_activity)
			                                  .edit ();
		editor.putString ("config_player_uid", m_UID);
		}
	}
