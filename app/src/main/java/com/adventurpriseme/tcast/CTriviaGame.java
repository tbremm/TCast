package com.adventurpriseme.tcast;

import android.app.Activity;
import android.preference.PreferenceManager;

import static com.adventurpriseme.tcast.CTriviaGame.TriviaGameState.CONNECTED;
import static com.adventurpriseme.tcast.CTriviaGame.TriviaGameState.GOT_Q_AND_A;
import static com.adventurpriseme.tcast.CTriviaGame.TriviaGameState.HOSTED;
import static com.adventurpriseme.tcast.CTriviaGame.TriviaGameState.HOSTING;
import static com.adventurpriseme.tcast.CTriviaGame.TriviaGameState.ROUND_LOSE;
import static com.adventurpriseme.tcast.CTriviaGame.TriviaGameState.ROUND_WIN;
import static com.adventurpriseme.tcast.CTriviaGame.TriviaGameState.WAITING;

/**
 * This represents a trivia game.
 * <p/>
 * This class maintains the game state machine and player control.
 * <p/>
 * Created by Timothy on 12/11/2014.
 * Copyright 12/11/2014 adventurpriseme.com
 */
public class CTriviaGame
	{
	private static final String[] Q_AND_A_STRING_ARRAY = new String[5];   // TODO: Don't hardcode this size
	// The caller's context
	private PlayTriviaActivity m_activity;
	private String             m_strMsgIn;
	private String[]           m_strMsgOut;
	private TriviaGameState    m_eTriviaGameState;

	public CTriviaGame (Activity activity)
		{
		m_strMsgOut = new String[4];    // TODO: Make this dependent on the type of question expected
		m_activity = (PlayTriviaActivity) activity;
		setGameState (WAITING);
		}

	public String[] getAllMessagesToSend ()
		{
		return m_strMsgOut;
		}

	public TriviaGameState getGameState ()
		{
		return m_eTriviaGameState;
		}

	private void setGameState (TriviaGameState state)
		{
		m_eTriviaGameState = state;
		}

	/**
	 * Handler for when we receive a message from the web.
	 * <p/>
	 * Preserves the message and sets the game state to process it.
	 *
	 * @param strMsgIn
	 * 	String content of the received message
	 */
	public void onMessageIn (String strMsgIn)
		{
		m_strMsgIn = strMsgIn.toLowerCase ();
		processMessage ();
		}

	/**
	 * Process messages received from the game server.
	 * <p/>
	 * Sets the response string, if any, based upon the received string.
	 * Sets the state according to the received message.
	 */
	private void processMessage ()
		{
		if (m_strMsgIn.equals ("connected"))
			{
			m_strMsgOut[0] = "connected";
			m_strMsgOut[1] = "request host";
			m_activity.updateGame (CONNECTED, m_strMsgOut);
			}
		else if (m_strMsgIn.equals ("host"))
			{
			String enableRoundTimer = "round timer=" + String.valueOf (PreferenceManager.getDefaultSharedPreferences (m_activity)
				                                                           .getBoolean ("pref_debug_checkbox_enable_timer", true));
			String enablePostRoundTimer = "postround timer=true";  // TODO: Don't hardcode this
			String playerName = "player name=" + PreferenceManager.getDefaultSharedPreferences (m_activity)
				                                     .getString ("pref_player_name_text", "Player");
			m_strMsgOut[0] = "begin round|" + enableRoundTimer + "|" + enablePostRoundTimer + "|" + playerName;
			m_activity.updateGame (HOSTING, m_strMsgOut);
			}
		else if (m_strMsgIn.equals ("hosted"))
			{
			m_strMsgOut[0] = "config|player name=" + PreferenceManager.getDefaultSharedPreferences (m_activity)
				                                         .getString ("pref_player_name_text", "Player");
			m_activity.updateGame (HOSTED, m_strMsgOut);
			}
		else if (m_strMsgIn.startsWith ("qanda|"))
			{
			// Expecting: "qanda|q=some question|a=answer1|a=answer2|...|a=answer"
			// Get a question k=v and answer k=v's
			String[] strings = m_strMsgIn.split ("\\|");
			// TODO: Pick what strings to take based off of game format (t/f, multi, open-ended, etc)
			if (strings.length == 6)    // command, question, answers 1-4
				{
				// Reset the array
				m_strMsgOut = Q_AND_A_STRING_ARRAY;
				// Get and strip the question and answer values
				for (int i = 1; i < strings.length; ++i)
					{
					m_strMsgOut[i - 1] = strings[i].split ("=")[1];
					}
				}
			m_activity.updateGame (GOT_Q_AND_A, m_strMsgOut);
			}
		else if (m_strMsgIn.equals ("win"))
			{
			if (m_activity.getTriviaPlayer ()
				    .getWillHost ())
				{
				String enableRoundTimer = "round timer=" + String.valueOf (PreferenceManager.getDefaultSharedPreferences (m_activity)
					                                                           .getBoolean ("pref_debug_checkbox_enable_timer", true));
				String enablePostRoundTimer = "postround timer=true";  // TODO: Don't hardcode this
				String playerName = "player name=" + PreferenceManager.getDefaultSharedPreferences (m_activity)
					                                     .getString ("pref_player_name_text", "Player");
				m_strMsgOut[0] = "begin round|" + enableRoundTimer;
				}
			m_activity.updateGame (ROUND_WIN, m_strMsgOut);
			}
		else if (m_strMsgIn.equals ("lose"))
			{
			if (m_activity.getTriviaPlayer ()
				    .getWillHost ())
				{
				String enableRoundTimer = "round timer=" + String.valueOf (PreferenceManager.getDefaultSharedPreferences (m_activity)
					                                                           .getBoolean ("pref_debug_checkbox_enable_timer", true));
				String enablePostRoundTimer = "postround timer=true";  // TODO: Don't hardcode this
				String playerName = "player name=" + PreferenceManager.getDefaultSharedPreferences (m_activity)
					                                     .getString ("pref_player_name_text", "Player");
				m_strMsgOut[0] = "begin round|" + enableRoundTimer;
				}
			m_activity.updateGame (ROUND_LOSE, m_strMsgOut);
			}
		}

	// Game state
	public static enum TriviaGameState
		{
			WAITING,
			CONNECTED,
			HOSTING,
			HOSTED,
			GOT_Q_AND_A,
			ROUND_WIN,
			ROUND_LOSE,
			QUIT
		}
	}
