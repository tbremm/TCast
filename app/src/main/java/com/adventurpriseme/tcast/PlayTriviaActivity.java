package com.adventurpriseme.tcast;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adventurpriseme.tcast.IChromeCast.IChromeCastMessage;
import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.LaunchOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This is the trivia game entry point.
 * <p/>
 * This activity is the initial page displayed to the user upon selecting to play trivia.
 * <p/>
 * This is where the user will connect to the chromecast.
 */
public class PlayTriviaActivity
	extends ActionBarActivity
	implements IChromeCastMessage, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Cast.MessageReceivedCallback
	{
	private static final String   TAG                = "Trivia Activity";
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	//	final Context context = this;
	private static MediaRouter.Callback m_MediaRouterCallback;
	/** Data members */
	private        CTriviaPlayer        m_cTriviaPlayer;
	private        CTriviaGame          m_cTriviaGame;
	private        MediaRouter          m_MediaRouter;
	private        MediaRouteSelector   m_MediaRouteSelector;
	private        GoogleApiClient      m_ApiClient;
	private boolean m_WaitingForReconnect = false;
	private boolean m_ApplicationStarted  = false;
	private CCastChannel      m_CCastChannel;
	private SharedPreferences m_sharedPreferences;
	private Context m_context = this;

    private String m_SessionId = "";


	/**
	 * Play Trivia Activity creator.
	 * <p/>
	 * This is the entry point for the activity. It is responsible for the Chromecast media router
	 * and its menu button. This will also initialize the player.
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate (Bundle savedInstanceState)
		{
		super.onCreate (savedInstanceState);
		// Show the Up button in the action bar.
		setupActionBar ();
		// Create the media router, selector, and callback for the chromecast
		m_MediaRouter = MediaRouter.getInstance (getApplicationContext ());
		m_MediaRouteSelector = new MediaRouteSelector.Builder ().addControlCategory (CastMediaControlIntent.categoryForCast ("53EAA363"))
			                       .build ();
		m_MediaRouterCallback = new MyMediaRouterCallback ();

        chooseActivityContentView();
		}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi (Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar ()
		{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			{
			if (getSupportActionBar () != null)
				{
				getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
				}
			}
		}

	@Override
	protected void onStop ()
		{
		m_MediaRouter.removeCallback (m_MediaRouterCallback);
        //m_ApiClient.disconnect();
		super.onStop ();
		}

	/**
	 * Called after {@link this.onCreate()} has finished.
	 *
	 * @param savedInstanceState
	 * 	(required)
	 */
	@Override
	protected void onPostCreate (Bundle savedInstanceState)
		{
		super.onPostCreate (savedInstanceState);
		// Create a new player
		m_sharedPreferences = PreferenceManager.getDefaultSharedPreferences (this);
		m_cTriviaPlayer = new CTriviaPlayer (m_sharedPreferences);
		m_cTriviaGame = new CTriviaGame (this);
		// TODO: We need to persist things like m_ApiClient in the onPause()/onStop() functions in order for this to really work
        //chooseActivityContentView();
		}

    private void chooseActivityContentView ()
        {
        // Set the activity layout dependant on our connected state
        if (m_ApiClient == null || !m_ApiClient.isConnected ())
            {
            setContentView (R.layout.activity_play_trivia_off);
            }
        else
            {
            setContentView (R.layout.activity_play_trivia_on);
            }
        }

	@Override
	public boolean onCreateOptionsMenu (Menu menu)
		{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater ().inflate (R.menu.menu_play_trivia, menu);
		MenuItem mediaRouteMenuItem = menu.findItem (R.id.media_route_menu_item);
		MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat.getActionProvider (mediaRouteMenuItem);
		mediaRouteActionProvider.setRouteSelector (m_MediaRouteSelector);
		return true;
		}

	@Override
	public boolean onOptionsItemSelected (MenuItem item)
		{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId ())
			{
			case R.id.action_settings:
			{
			onSettingsSelected ();
			return true;
			}
			case R.id.home:
			{
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask (this);
			return true;
			}
			case R.id.action_about:
			{
			// Show an about dialog box with version info
			CAboutDialog.Show (PlayTriviaActivity.this);
			}
			}
		return super.onOptionsItemSelected (item);
		}

	/**
	 * Action bar settings menu entry
	 * <p/>
	 * This will load the settings view.
	 */
	private void onSettingsSelected ()
		{
		Intent intent = new Intent (this, TriviaPrefsActivity.class);
		startActivity (intent);
		}

	@Override
	public void onConnected (Bundle bundle)
		{
		if (m_WaitingForReconnect)
			{
			m_WaitingForReconnect = false;
			reconnectChannels();
			}
		else
			{
			try
				{
				// Give the channel our message object
				m_CCastChannel = new CCastChannel (this);
                LaunchOptions m_LaunchOptions = new LaunchOptions();
                m_LaunchOptions.setRelaunchIfRunning(false);

				Cast.CastApi.launchApplication (m_ApiClient, "53EAA363", m_LaunchOptions)
					.setResultCallback (new ResultCallback<Cast.ApplicationConnectionResult> ()
					{
					@Override
					public void onResult (Cast.ApplicationConnectionResult result)
						{
						Status status = result.getStatus ();
						if (status.isSuccess ())
							{
							ApplicationMetadata applicationMetadata = result.getApplicationMetadata ();
							String sessionId = result.getSessionId ();
							String applicationStatus = result.getApplicationStatus ();
							boolean wasLaunched = result.getWasLaunched ();
							m_ApplicationStarted = true;
							try
								{
								Cast.CastApi.setMessageReceivedCallbacks (m_ApiClient, m_CCastChannel.getNamespace (), m_CCastChannel);
								//sendMessage("http://gnosm.net/missilecommand/sounds/524
								// .mp3");
                                }
							catch (IOException e)
                                {
                                Log.e(TAG, "Exception while creating channel", e);
                                }
							}
						}
					});
				}
			catch (Exception e)
                {
                Log.e(TAG, "Failed to launch application", e);
                }
			}
        chooseActivityContentView();
		}

    private void reconnectChannels() {

    }

	@Override
	public void onConnectionSuspended (int i)
		{
        // TODO
		m_WaitingForReconnect = true;
		}

	@Override
	public void onMessageReceived (CastDevice castDevice, String namespace, String message)
		{
		// TODO
		Log.d (TAG, "onMessageReceived: " + message);
		}

	@Override
	public void onConnectionFailed (ConnectionResult connectionResult)
		{
		// TODO
		chooseActivityContentView();
		Log.e (TAG, "onConnectionFailed...");
		}

	@Override
	protected void onPause ()
		{
		// TODO: Save activity/game state here
		if (isFinishing ())
			{
			m_MediaRouter.removeCallback (m_MediaRouterCallback);
			}
		super.onPause ();
		}

	@Override
	protected void onResume ()
		{
		// TODO: Restore activity/game state here
		super.onResume ();
		m_MediaRouter.addCallback (m_MediaRouteSelector, m_MediaRouterCallback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);

        m_MediaRouter = MediaRouter.getInstance (getApplicationContext ());

        // fixme - not really sure how to best save CC Connection info yet...
        // this feels hacky and gross -GN
        //if (m_ApiClientBuilt) {
        //    m_ApiClient = this.getApiClient();
        //}

		// TODO? chooseActivityContentView();
		}

	@Override
	protected void onStart ()
		{
		super.onStart ();
		// TODO: Should this be in onResume()?
		m_MediaRouter.addCallback (m_MediaRouteSelector, m_MediaRouterCallback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);

		// TODO chooseActivityContentView();
		}

	/**
	 * Callback for receiving a message from the chromecast.
	 * <p/>
	 * This function handles messages received from the chromecast.
	 * This will trigger a game action of some kind.
	 *
	 * @param strMsg
	 * 	(required)  The incoming message
	 */
	@Override
	public void onReceiveCallback (String strMsg)
		{
		m_cTriviaGame.onMessageIn (strMsg);
		}

	public void updateUI (CTriviaGame.TriviaGameState state)
		{
		// Get all of our GUI elements
		TextView tvPlayTitle = (TextView) findViewById (R.id.tvPlayTitle);
		TextView tvQuestion = (TextView) findViewById (R.id.tvQuestion);
		Button btnBeginNewRound = (Button) findViewById (R.id.btn_begin_new_round);
		RadioGroup rgAnswers = (RadioGroup) findViewById (R.id.radio_group_answers);
		// TODO Add an onCheckedChangeListener to handle button graphical state?
		switch (state)
			{
			case CONNECTED:
				// Clear the display of UI elements
				setAllUiElements_Visibility (View.INVISIBLE);
				if (m_cTriviaPlayer.getWillHost ())
					{
					// Create the "host game" button
					btnBeginNewRound.setText (getString (R.string.btn_text_host_game));
					btnBeginNewRound.setOnClickListener (new View.OnClickListener ()
					{
					@Override
					public void onClick (View view)
						{
                            m_cTriviaGame.requestHost();
						}
					});
					btnBeginNewRound.setVisibility (View.VISIBLE);
					}
				else
					{
					tvQuestion.setText (getString (R.string.waiting_for_host));
					tvQuestion.setVisibility (View.VISIBLE);
					}
				break;
			case HOSTING:
				// Clear the display of UI elements
				setAllUiElements_Visibility (View.INVISIBLE);
				m_cTriviaPlayer.setIsHosting (true);
				btnBeginNewRound.setText (getString (R.string.start_the_game));
				btnBeginNewRound.setOnClickListener (new View.OnClickListener ()
				{
				@Override
				public void onClick (View view)
					{
                    m_cTriviaGame.beginNewRound();
					}
				});
				btnBeginNewRound.setVisibility (View.VISIBLE);
				break;
			case HOSTED:
				// Clear the display of UI elements
				setAllUiElements_Visibility (View.INVISIBLE);
				m_cTriviaPlayer.setIsHosting (false);
				tvPlayTitle.setText (getString (R.string.waiting_on_host));
				tvPlayTitle.setVisibility (View.VISIBLE);
				break;
			case GOT_Q_AND_A:
				setAllUiElements_Visibility (View.INVISIBLE);       // Clear the display of UI elements
				tvPlayTitle.setText (R.string.true_or_false);       // Create the title text
				tvPlayTitle.setVisibility (View.VISIBLE);           // Make it visible
				tvQuestion.setText (m_cTriviaGame.getQuestion ());   // Create the question text
				tvQuestion.setVisibility (View.VISIBLE);            // Make it visible
				ArrayList<String> answers = m_cTriviaGame.getAnswers ();
				rgAnswers.removeAllViews ();                        // Remove any pre-existing radio buttons
				for (int i = 0; i < answers.size(); ++i)            // Add a radio button for each available answer
					{
					rgAnswers.addView (new CAnswerRadioButton (this, i, answers.get(i)));
					}
				rgAnswers.setVisibility (View.VISIBLE);         // Make sure we see the buttons
				if (m_cTriviaPlayer.isHosting ())               // Host gets to short-circuit the question
					{
					btnBeginNewRound.setText (getString (R.string.finish_round));
					btnBeginNewRound.setVisibility (View.VISIBLE);
					btnBeginNewRound.setOnClickListener (new View.OnClickListener ()    // TODO: Investigate if we have to remove old listeners here (possible leak?)
					{
					@Override
					public void onClick (View view)
						{
                        m_cTriviaGame.endRound();
						}
					});
					AddButtonLayout (btnBeginNewRound, RelativeLayout.ALIGN_BASELINE); // Put button at the beginning of the screen
					}
				break;
			case ROUND_WIN:
				// Clear the display of UI elements
				setAllUiElements_Visibility (View.INVISIBLE);
				tvPlayTitle.setText (getString (R.string.you_win));
				tvPlayTitle.setVisibility (View.VISIBLE);
				if (m_cTriviaPlayer.isHosting ())               // Host gets to short-circuit the question
					{
					btnBeginNewRound.setText (getString (R.string.finish_round));
					btnBeginNewRound.setVisibility (View.VISIBLE);
					btnBeginNewRound.setOnClickListener (new View.OnClickListener ()    // TODO: Investigate if we have to remove old listeners here (possible leak?)
					{
					@Override
					public void onClick (View view)
						{
                            m_cTriviaGame.beginNewRound();
						}
					});
					AddButtonLayout (btnBeginNewRound, RelativeLayout.ALIGN_BASELINE); // Put button at the bottom of the screen
					}
				break;
			case ROUND_LOSE:
				// Clear the display of UI elements
				setAllUiElements_Visibility (View.INVISIBLE);
				tvPlayTitle.setText (getString (R.string.you_lose));
				tvPlayTitle.setVisibility (View.VISIBLE);
				if (m_cTriviaPlayer.isHosting ())               // Host gets to short-circuit the question
					{
					btnBeginNewRound.setText (getString (R.string.finish_round));
					btnBeginNewRound.setVisibility (View.VISIBLE);
					btnBeginNewRound.setOnClickListener (new View.OnClickListener ()    // TODO: Investigate if we have to remove old listeners here (possible leak?)
					{
					@Override
					public void onClick (View view)
						{
                            m_cTriviaGame.beginNewRound();
						}
					});
					AddButtonLayout (btnBeginNewRound, RelativeLayout.ALIGN_BASELINE); // Put button at the bottom of the screen
					}
				break;
			case QUIT:
				break;
			case ERROR:
				// TODO: Error handling
				sendMessage ("error|msg=");
			default:
				break;
			}
		}

	/**
	 * Set the visibility of all children of the main relative layout.
	 *
	 * @param visibility
	 * 	(required)  This should be View.VISIBLE, INVISIBLE, or GONE
	 */
	private void setAllUiElements_Visibility (int visibility)
		{
		RelativeLayout relativeLayout = (RelativeLayout) findViewById (R.id.rl_trivia_main_on);
		int numViewElements = relativeLayout.getChildCount ();
		for (int i = 0; i < numViewElements; ++i)
			{
			relativeLayout.getChildAt (i)
				.setVisibility (visibility);
			}
		}

	/**
	 * Set layout of a button within a relative layout.
	 *
	 * @param button
	 * @param centerInParent
	 * 	RelativeLayout.ALIGN_PARENT_BOTTOM, etc
	 */
	private void AddButtonLayout (Button button, int centerInParent)
		{
		// Just call the other AddButtonLayout Method with Margin 0
		AddButtonLayout (button, centerInParent, 0, 0, 0, 0);
		}

	// public - so CTriviaGame can interact with this function
	public void sendMessage (final String message)
		{
		if (m_ApiClient != null && m_CCastChannel != null)
			{
			try
				{
				Cast.CastApi.sendMessage (m_ApiClient, m_CCastChannel.getNamespace (), message)
					.setResultCallback (new ResultCallback<Status> ()
					{
					@Override
					public void onResult (Status result)
						{
						if (!result.isSuccess ())
							{
							Log.e (TAG, "Sending message failed");
							}
						else
							{
							Log.e (TAG, "Sent message: " + message);
							}
						}
					});
				}
			catch (Exception e)
				{
				Log.e (TAG, "Exception while sending message", e);
				}
			}
		else if (m_ApiClient == null)
			{
			Log.e (TAG, "m_ApiClient is null!");
			}
		else
			{
			Log.e (TAG, "m_CCastChannel is null!");
			}
		}

	/**
	 * Apply a layout to a button within a relative layout.
	 *
	 * @param button
	 * @param centerInParent
	 * @param marginLeft
	 * @param marginTop
	 * @param marginRight
	 * @param marginBottom
	 */
	private void AddButtonLayout (Button button, int centerInParent, int marginLeft, int marginTop, int marginRight, int marginBottom)
		{
		// Defining the layout parameters of the Button
		RelativeLayout.LayoutParams buttonLayoutParameters = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		// Add Margin to the LayoutParameters
		buttonLayoutParameters.setMargins (marginLeft, marginTop, marginRight, marginBottom);
		// Add Rule to Layout
		buttonLayoutParameters.addRule (centerInParent);
		// Setting the parameters on the Button
		button.setLayoutParams (buttonLayoutParameters);
		}

	public String getPlayerName ()
		{
		return PreferenceManager.getDefaultSharedPreferences (this)
			       .getString ("pref_player_name_text", "Player");
		}

	public boolean getRoundTimerEnable ()
		{
        SharedPreferences temp = PreferenceManager.getDefaultSharedPreferences(this); // helpful for debug
		return PreferenceManager.getDefaultSharedPreferences (this)
			       .getBoolean ("pref_host_checkbox_round_timer", true);
		}

	public boolean getPostRoundTimerEnable ()
		{
		return PreferenceManager.getDefaultSharedPreferences (this)
			       .getBoolean ("pref_host_checkbox_postround_timer", true);
		}

	public CTriviaPlayer getTriviaPlayer ()
		{
		return m_cTriviaPlayer;
		}

	/**
	 * Creates a radio button customized for a trivia answer
	 */
	private class CAnswerRadioButton extends RadioButton
		{
		/**
		 * Constructor for the button.
		 * <p/>
		 * This sets the button's properties so that its text matches the correct answer,
		 * and has the "radio" selector hidden so it looks like the whole button is selected.
		 * Size is to wrap_content, and its onClick listener sets the player's answer and
		 * sends it to the game server.
		 *
		 * @param context
		 * 	(required)  Typically "this"
		 * @param index
		 * 	(required)  Index of the answer to assign this button to
		 */
		public CAnswerRadioButton (Context context, int index, String buttonText)
			{
			super (context);
			// Populate the button with our settings
			setId (index);
			setText (buttonText);
			setChecked (false);  // Default to no selection to remove bias
			// keep button visible for now. TODO. setButtonDrawable (R.drawable.null_selector);
			setVisibility (View.VISIBLE);
			setOnClickListener (new View.OnClickListener ()
			{
			@Override
			public void onClick (View view)
				{
				//TODO: Update graphics to let user know they've clicked something
				((RadioGroup) view.getParent ()).check (view.getId ());                     // Check the radio button
				m_cTriviaPlayer.setAnswer (((RadioButton) view).getText ()
					                           .toString ());    // Save off the answer

                m_cTriviaGame.sendAnswer(m_cTriviaPlayer.getAnswer());                          // Send the answer to the game server
				}
			});
			}
		}

	private final class MyMediaRouterCallback
		extends MediaRouter.Callback
		{
		private final String TAG = "My Media Router Callback";
		private CastDevice mSelectedDevice;
		private Cast.Listener mCastClientListener;

		@Override
		public void onRouteSelected (MediaRouter router, MediaRouter.RouteInfo info)
			{
			mSelectedDevice = CastDevice.getFromBundle (info.getExtras ());
			String routeId = info.getId ();
			mCastClientListener = new Cast.Listener ()
			{
			@Override
			public void onApplicationStatusChanged ()
				{
				if (m_ApiClient != null)
					{
					Log.d (TAG, "onApplicationStatusChanged: " + Cast.CastApi.getApplicationStatus (m_ApiClient));
					}
				}

			@Override
			public void onApplicationDisconnected (int errorCode)
				{
				Log.d (TAG, "Application Disconnected: " + errorCode);
				setContentView (R.layout.activity_play_trivia_off);
				// fixme teardown();
				}

			@Override
			public void onVolumeChanged ()
				{
				if (m_ApiClient != null)
					{
					Log.d (TAG, "onVolumeChanged: " + Cast.CastApi.getVolume (m_ApiClient));
					}
				}
			};
			Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions.builder (mSelectedDevice, mCastClientListener);
			m_ApiClient = new GoogleApiClient.Builder (PlayTriviaActivity.this).addApi (Cast.API, apiOptionsBuilder.build ())
				              .addConnectionCallbacks (PlayTriviaActivity.this)
				              .addOnConnectionFailedListener (PlayTriviaActivity.this)
				              .build ();
			m_ApiClient.connect ();
			}
		}

    public void onSaveInstanceState(Bundle savedInstanceState)
        {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean("fartbutt", true);
        // save all our important things
        /*savedInstanceState.put()


            private        CTriviaPlayer        m_cTriviaPlayer;
            private        CTriviaGame          m_cTriviaGame;
            private        MediaRouter          m_MediaRouter;
            private        MediaRouteSelector   m_MediaRouteSelector;
            private        GoogleApiClient      m_ApiClient;
            private boolean m_WaitingForReconnect = false;
            private boolean m_ApplicationStarted  = false;
            private CCastChannel      m_CCastChannel;
            private SharedPreferences m_sharedPreferences;
            private Context m_context = this;*/

        }

        private boolean m_test=false;
    public void onRestoreInstanceState(Bundle savedInstanceState)
        {
            super.onRestoreInstanceState(savedInstanceState);

            m_test = savedInstanceState.getBoolean("fartbutt");

        }
	}


