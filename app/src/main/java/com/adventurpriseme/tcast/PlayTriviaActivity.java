package com.adventurpriseme.tcast;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adventurpriseme.tcast.IChromeCast.IChromeCastMessage;
import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;

/**
 * This is the trivia game entry point.
 * <p/>
 * This activity is the initial page displayed to the user upon selecting to play trivia.
 * <p/>
 * This is where the user will connect to the chromecast.
 */
public class PlayTriviaActivity extends ActionBarActivity implements IChromeCastMessage, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Cast.MessageReceivedCallback
	{

	private static final String TAG = "Trivia Activity";
	private static MediaRouter.Callback m_MediaRouterCallback;
	final Context context = this;
	/** Data members */
	private CTriviaPlayer      m_cTriviaPlayer;
	private MediaRouter        m_MediaRouter;
	private MediaRouteSelector m_MediaRouteSelector;
	private GoogleApiClient    m_ApiClient;
	private boolean m_WaitingForReconnect = false;
	private boolean m_ApplicationStarted  = false;
	private CCastChannel       m_CCastChannel;

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
		// Spawn the activity layout
		setContentView (R.layout.activity_play_trivia_offline);

		// Get the main layout to set its properties
		RelativeLayout relativeLayout = (RelativeLayout) findViewById (R.id.rl_trivia_main_offline);
		relativeLayout.setBackgroundResource (R.drawable.blue_orange_background);

		// Get the image view used for delivering question content and display its decorations
		ImageView imgvThoughtBubble = (ImageView) findViewById (R.id.img_thought_bubble);
		imgvThoughtBubble.setBackgroundResource (R.drawable.pd_thought_bubble_outline_half_long);

		// Show the Up button in the action bar.
		setupActionBar ();

		// Create the media router, selector, and callback for the chromecast
		m_MediaRouter = MediaRouter.getInstance (getApplicationContext ());
		m_MediaRouteSelector = new MediaRouteSelector.Builder ().addControlCategory (CastMediaControlIntent.categoryForCast ("53EAA363")).build ();
		m_MediaRouterCallback = new MyMediaRouterCallback ();

		// Create a new player
		m_cTriviaPlayer = new CTriviaPlayer ();

		// Initialize the willingness to host checkbox
		((CheckBox) findViewById (R.id.checkbox_willHost)).setChecked (m_cTriviaPlayer.getWillHost ());
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
		super.onStop ();
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
				return true;
			case R.id.home:
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

		return super.onOptionsItemSelected (item);
		}

	/**
	 * onClick handler for any checkboxes we might have here.
	 * <p/>
	 * This determines if the user is willing to host the trivia round
	 *
	 * @param view
	 * 		The view owning the checkbox
	 */
	public void onCheckBoxClicked (View view)
		{
		// Is the view (checkbox) checked?
		boolean bChecked = ((CheckBox) view).isChecked ();

		// Operate on the specific checkbox
		switch (view.getId ())
			{
			case R.id.checkbox_willHost:    // Is player willing to host?
				m_cTriviaPlayer.setWillHost (bChecked);
				break;
			default:
				// Should never get here
				break;
			}
		}

	@Override
	public void onConnected (Bundle bundle)
		{
		if (m_WaitingForReconnect)
			{
			m_WaitingForReconnect = false;
			//reconnectChannels();
			}
		else
			{
			try
				{
				// Give the channel our message object
				m_CCastChannel = new CCastChannel (this);

				Cast.CastApi.launchApplication (m_ApiClient, "53EAA363", false).setResultCallback (new ResultCallback<Cast.ApplicationConnectionResult> ()
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
							Log.e (TAG, "Exception while creating channel", e);
							}
						}
					}
				});
				}
			catch (Exception e)
				{
				Log.e (TAG, "Failed to launch application", e);
				}
			}
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
		Log.e (TAG, "onConnectionFailed...");
		}

	private void sendMessage (String message)
		{
		if (m_ApiClient != null && m_CCastChannel != null)
			{
			try
				{
				Cast.CastApi.sendMessage (m_ApiClient, m_CCastChannel.getNamespace (), message).setResultCallback (new ResultCallback<Status> ()
				{
				@Override
				public void onResult (Status result)
					{
					if (!result.isSuccess ())
						{
						Log.e (TAG, "Sending message failed");
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

	@Override
	protected void onPause ()
		{
		if (isFinishing ())
			{
			m_MediaRouter.removeCallback (m_MediaRouterCallback);
			}
		super.onPause ();
		}

	@Override
	protected void onResume ()
		{
		super.onResume ();
		m_MediaRouter.addCallback (m_MediaRouteSelector, m_MediaRouterCallback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
		}

	@Override
	protected void onStart ()
		{
		super.onStart ();
		m_MediaRouter.addCallback (m_MediaRouteSelector, m_MediaRouterCallback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);

/*	FIXME - We need to figure out how to scale/load bitmaps into memory efficiently to avoid visual lag and overrunning our memory resources.

		Bitmap bmBackground = CBitmapLoader.decodeSampledBitmapFromResource (getResources (), R.drawable.pd_thought_bubble_outline_half_long,
		findViewById (R.id.rl_trivia_main_offline).getWidth (), findViewById (R.id.rl_trivia_main_offline).getHeight ());

		imgvThoughtBubble.setImageBitmap (bmBackground);
*/
		//	TODO: Deal with the player's name. I'm not sure how to wait for the dialog to complete correctly.
		//		if (m_cTriviaPlayer.getName ().isEmpty () /** or if player chooses to change name*/)
		//			{
		//			CPromptDlg userNamePrompt = new CPromptDlg (context, R.string.alert_whoAreYou, R.string.enter_name, R.string.okay_button, R.string.cancel)
		//			{
		//			/**
		//			 * Called when "ok" pressed.
		//			 *
		//			 * @param input
		//			 *
		//			 * @return true, if the dialog should be closed. false, if not.
		//			 */
		//			@Override
		//			public boolean onOkClicked (String input)
		//				{
		//				m_cTriviaPlayer.setName (input);
		//				return true;
		//				}
		//			};
		//
		//			userNamePrompt.create ();
		//			userNamePrompt.show ();
		//			}
		//
		//		if (!m_cTriviaPlayer.getName ().isEmpty ())
		//			{
		//			// Create a welcome message for the user
		//			((TextView) findViewById (R.id.tvPlayTitle)).setText ("Hi, " + m_cTriviaPlayer.getName () + ", welcome to Trivia!");
		//			}
		}

	/**
	 * Callback for receiving a message from the chromecast.
	 * <p/>
	 * This function handles messages received from the chromecast.
	 * This will trigger a game action of some kind.
	 *
	 * @param strMsg (required)  The incoming message
	 */
	@Override
	public void onReceiveCallback (String strMsg)
		{
		TextView textView = (TextView) findViewById (R.id.tvQuestion);
		textView.setText (strMsg);

		if (strMsg.equals ("timeout"))
			{
			sendMessage ("done");
			}
		}

	private final class MyMediaRouterCallback extends MediaRouter.Callback
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
	}
