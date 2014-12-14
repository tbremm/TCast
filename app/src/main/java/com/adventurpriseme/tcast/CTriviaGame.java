package com.adventurpriseme.tcast;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Arrays;

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
public class CTriviaGame {
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
    private final static String MSG_END_ROUND                = "end round";
	private final static String MSG_CONFIG                   = "config";
	private final static String MSG_Q_AND_A                  = "qanda";
	private final static String MSG_ERROR                    = "error";
	private final static String MSG_WIN                      = "win";
	private final static String MSG_LOSE                     = "lose";
	// Key strings
	private final static String MSG_ROUND_TIMER              = "round timer";
	private final static String MSG_POSTROUND_TIMER          = "postround timer";
	private final static String MSG_PLAYER_NAME              = "player name";
    private final static String MSG_KEY_QUESTION             = "q";
	private final static String MSG_KEY_ANSWER               = "a";
	// Message separators
	private final static String MSG_SPLIT_KEY_VALUE          = String.valueOf ('=');
	private final static String MSG_SPLIT_DATA               = String.valueOf ('|');
	// Error messages
	private final static String MSG_ERROR_MSG                = "msg";
	private final static String MSG_ERROR_RECEIVED_EMPTY_MSG = "received empty message";
	// The caller's context
	private PlayTriviaActivity m_activity;
	private String             m_strMsgIn;
    private ArrayList<String>  m_strMsgOut;
    private String             m_question;
    private ArrayList<String>  m_answers;
	private TriviaGameState    m_eTriviaGameState;

	public CTriviaGame (Activity activity)
		{
		m_strMsgOut = new ArrayList<String>();    // TODO: Make this dependent on the type of question expected
		m_activity = (PlayTriviaActivity) activity;
        m_question = "";
        m_answers = new ArrayList<String>();
		setGameState (WAITING);
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
		m_strMsgIn = strMsgIn.toLowerCase ();   // TODO: Move this to just-in-time conditioning, in case we want to preserve case on a string that's displayed directly
		processMessage (strMsgIn);
		}

	/**
	 * Process messages received from the game server.
	 * <p/>
	 * Sets the response string, if any, based upon the received string.
	 * Sets the state according to the received message.
	 */
	private void processMessage (String strMsgIn)
		{
		// Break apart the message into its parts

        ArrayList<String> inMsgSplit =  new ArrayList<String>(
                                            Arrays.asList(strMsgIn.split("\\" + MSG_SPLIT_DATA))
                                        );
        ArrayList<String> messageOut = new ArrayList<String>();
        messageOut.clear();
		if (inMsgSplit.size() < 1)
			{
			messageOut.add(MSG_ERROR);
            messageOut.add(MSG_ERROR_MSG + MSG_SPLIT_KEY_VALUE + MSG_ERROR_RECEIVED_EMPTY_MSG);

            m_activity.sendMessage (formatMessage(messageOut));
            m_activity.updateUI(TriviaGameState.ERROR);
			return;
			}
		if (inMsgSplit.get(0).equals(MSG_CONNECTED))
			{
            messageOut.add(MSG_CONNECTED);

            m_activity.sendMessage (formatMessage(messageOut));
			m_activity.updateUI(CONNECTED);
			}
		else if (inMsgSplit.get(0).equals (MSG_HOST))
			{
			m_activity.updateUI (HOSTING);
			}
		else if (inMsgSplit.get(0).equals (MSG_HOSTED))
			{
            messageOut.add(MSG_CONFIG);
            messageOut.add(MSG_PLAYER_NAME + MSG_SPLIT_KEY_VALUE + m_activity.getPlayerName()
                          );
			m_activity.updateUI (HOSTED);
			}
		else if (inMsgSplit.get(0).equals (MSG_Q_AND_A))
			{
			if (inMsgSplit.size() == 6)                         // command, question, answers 1-4
				{
                m_question = "";    // clear question
                m_answers.clear();  // clear answers
				int answerIndex = 0;
				for (int i = 0; i < inMsgSplit.size(); ++i)     // Get and strip the question and answer values
					{
                    String[] key_value_split = inMsgSplit.get(i).split(MSG_SPLIT_KEY_VALUE);
					if (key_value_split[0].equals (MSG_KEY_ANSWER))       // Check for the message key
						{
                        m_answers.add(inMsgSplit.get(i).split(MSG_SPLIT_KEY_VALUE)[1]);    // Get the answer value
						}
                    else if (key_value_split[0].equals (MSG_KEY_QUESTION))
                        {
                        m_question = inMsgSplit.get(i).split(MSG_SPLIT_KEY_VALUE)[1];
                        }
					}
				}
			m_activity.updateUI (GOT_Q_AND_A);
			}
		else if (inMsgSplit.get(0).equals (MSG_WIN))
			{
			m_activity.updateUI (ROUND_WIN);
			}
		else if (inMsgSplit.get(0).equals (MSG_LOSE))
			{
			m_activity.updateUI(ROUND_LOSE);
			}
		else if (inMsgSplit.get(0).equals(MSG_CONFIG))
			{
			// TODO: Handle config messages
			}
		else
			{
			// TODO: Handle unexpected command
			}
		}

private String formatMessage (ArrayList<String> list)
	{
	String ret = "";
	for (int i = 0; i < list.size (); i++)
		{
		ret += list.get (i);
		if (i < (list.size () - 1))
			{
			ret += MSG_SPLIT_DATA;
			}
		}
	return ret;
	}

        // TODO: same function call for beginning a game as starting a new round.
        // may want to separate calls (ie. one with config data - one without)
    public void beginNewRound ()
        {
            // TODO: Should wrap all configuration into a single object to be passed around -GN
            String enableRoundTimer = MSG_ROUND_TIMER + MSG_SPLIT_KEY_VALUE +
                    String.valueOf(m_activity.getRoundTimerEnable());

            String enablePostRoundTimer = MSG_POSTROUND_TIMER + MSG_SPLIT_KEY_VALUE +
                    String.valueOf(m_activity.getPostRoundTimerEnable());

            String playerName = MSG_PLAYER_NAME + MSG_SPLIT_KEY_VALUE +
                    String.valueOf(m_activity.getPlayerName());
        m_activity.sendMessage (MSG_BEGIN_ROUND + MSG_SPLIT_DATA + enableRoundTimer +
                                MSG_SPLIT_DATA + enablePostRoundTimer +
                                MSG_SPLIT_DATA + playerName);
        }

    public void endRound()
    {
        m_activity.sendMessage (MSG_END_ROUND);
    }

public void requestHost ()
	{
	m_activity.sendMessage ("request host");
	}

public String getQuestion ()
	{
	return m_question;
	}

public ArrayList<String> getAnswers ()
	{
	return m_answers;
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
