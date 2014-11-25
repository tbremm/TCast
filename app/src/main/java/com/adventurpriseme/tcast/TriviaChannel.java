package com.adventurpriseme.tcast;

import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;

/**
 * Created by Timothy on 11/18/2014.
 * Copyright 11/18/2014 adventurpriseme.com
 */
public class TriviaChannel extends PlayTriviaActivity implements Cast.MessageReceivedCallback
	{
	private final String TAG = "RPS Cast Channel";
	private TextView tv;

	public TriviaChannel (TextView _tv)
		{
		tv = _tv;
		}

	public String getNamespace ()
		{
		return "urn:x-cast:com.adventurpriseme.tcast";
		}

	@Override
	public void onMessageReceived (CastDevice castDevice, String namespace, String message)
		{
		Log.d (TAG, "onMessageReceived: " + message);

		// TODO: Do stuff here
		tv.setText (message);
		}
	}
