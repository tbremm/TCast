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
	private static final String[] Q_AND_A_STRING_ARRAY         = new String[5];   // TODO: Don't hardcode this size
	/**
	 * Strings used for communication with the web.
	 */
	// Command strings
	private final static String MSG_CONNECTED                = "connected";
	private final static String MSG_REQUEST_HOST             = "request host";
	private final static String MSG_HOST                     = "host";
	private final static String MSG_HOSTED                   = "hosted";
	private final static String MSG_BEGIN_ROUND              = "begin round";
	private final static String MSG_CONFIG                   = "config";
	private final static String MSG_Q_AND_A                  = "qanda";
	private final static String MSG_ERROR                    = "error";
	private final static String MSG_WIN                      = "win";
	private final static String MSG_LOSE                     = "lose";
	// Key strings
	private final static String MSG_ROUND_TIMER              = "round timer";
	private final static String MSG_POSTROUND_TIMER          = "postround timer";
	private final static String MSG_PLAYER_NAME              = "player name";
	private final static String MSG_KEY_ANSWER               = "answer";
	// Message separators
	private final static String MSG_SPLIT_KEY_VALUE          = String.valueOf ('=');
	private final static String MSG_SPLIT_DATA               = String.valueOf ('|');
	// Error messages
	private final static String MSG_ERROR_MSG                = "msg";
	private final static String MSG_ERROR_RECEIVED_EMPTY_MSG = "received empty message";
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
		// Break apart the message into its parts
		String[] inMsgSplit = m_strMsgIn.split (MSG_SPLIT_DATA);
		if (inMsgSplit.length < 1)
			{
			m_strMsgOut = new String[] {MSG_ERROR + MSG_SPLIT_DATA + MSG_ERROR_MSG + MSG_SPLIT_KEY_VALUE + MSG_ERROR_RECEIVED_EMPTY_MSG};
			m_activity.updateGame (TriviaGameState.ERROR, m_strMsgOut);
			return;
			}
		if (inMsgSplit[0].equals (MSG_CONNECTED))
			{
			m_strMsgOut[0] = MSG_CONNECTED;
			m_strMsgOut[1] = MSG_REQUEST_HOST;
			m_activity.updateGame (CONNECTED, m_strMsgOut);
			}
		else if (inMsgSplit[0].equals (MSG_HOST))
			{
			String enableRoundTimer = MSG_ROUND_TIMER + MSG_SPLIT_KEY_VALUE + String.valueOf (PreferenceManager.getDefaultSharedPreferences (m_activity)
				                                                                                  .getBoolean ("pref_debug_checkbox_enable_timer", true));
			String enablePostRoundTimer = MSG_POSTROUND_TIMER + MSG_SPLIT_KEY_VALUE +
			                              "true";  // TODO: Don't hardcode thisString playerName = MSG_PLAYER_NAME + MSG_SPLIT_KEY_VALUE + PreferenceManager.getDefaultSharedPreferences (m_activity).getString ("pref_player_name_text", "Player");
			m_strMsgOut[0] = MSG_BEGIN_ROUND + MSG_SPLIT_DATA + enableRoundTimer + MSG_SPLIT_DATA + enablePostRoundTimer + MSG_SPLIT_DATA + MSG_PLAYER_NAME;
			m_activity.updateGame (HOSTING, m_strMsgOut);
			}
		else if (inMsgSplit[0].equals (MSG_HOSTED))
			{
			m_strMsgOut[0] = MSG_CONFIG + MSG_SPLIT_DATA + MSG_PLAYER_NAME + MSG_SPLIT_KEY_VALUE + PreferenceManager.getDefaultSharedPreferences (m_activity)
				                                                                                       .getString ("pref_player_name_text", "Player");
			m_activity.updateGame (HOSTED, m_strMsgOut);
			}
		else if (inMsgSplit[0].equals (MSG_Q_AND_A))
			{
			if (inMsgSplit.length == 6)                         // command, question, answers 1-4
				{
				m_strMsgOut = Q_AND_A_STRING_ARRAY;             // Reset the array
				int answerIndex = 0;
				for (int i = 0; i < inMsgSplit.length; ++i)     // Get and strip the question and answer values
					{
					if (inMsgSplit[i].split (MSG_SPLIT_KEY_VALUE)[0].equals (MSG_KEY_ANSWER))       // Check for the message key
						{
						m_strMsgOut[answerIndex] = inMsgSplit[i].split (MSG_SPLIT_KEY_VALUE)[1];    // Get the answer value
						answerIndex++;  // Move to next answer slot
						}
					}
				}
			m_activity.updateGame (GOT_Q_AND_A, m_strMsgOut);
			}
		else if (inMsgSplit[0].equals (MSG_WIN))
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
		else if (inMsgSplit[0].equals (MSG_LOSE))
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
		else if (inMsgSplit[0].equals (MSG_CONFIG))
			{
			// TODO: Handle config messages
			}
		else
			{
			// TODO: Handle unexpected command
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
			QUIT,
			ERROR
		}
	}
