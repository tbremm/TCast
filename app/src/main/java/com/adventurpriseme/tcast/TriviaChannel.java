package com.adventurpriseme.tcast;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;

/**
 * Created by Timothy on 11/18/2014.
 * Copyright 11/18/2014 adventurpriseme.com
 */
public class TriviaChannel extends PlayTriviaActivity implements Cast.MessageReceivedCallback
	{
	private final String TAG = "RPS Cast Channel";
	private Context m_context;
	private CTriviaCastCCMessage m_cTriviaCastCCMessage;

	public TriviaChannel () {}

	public TriviaChannel (CTriviaCastCCMessage CCMsg)
		{

		}

	public TriviaChannel (Context context)
		{
		m_context = context;
		}


	public String getNamespace ()
		{
		return "urn:x-cast:com.adventurpriseme.tcast";
		}

	@Override
	public void onMessageReceived (CastDevice castDevice, String namespace, String message)
		{
		Log.d (TAG, "onMessageReceived: " + message);

		// TODO: set an IChromeCastMessage here to be read in the parent
		}
	}
