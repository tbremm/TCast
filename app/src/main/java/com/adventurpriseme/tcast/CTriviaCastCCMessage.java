package com.adventurpriseme.tcast;

import com.adventurpriseme.tcast.IChromeCast.ITriviaCastPlayerCCM;

/**
 * This class is used to interface with the receiver app via {@link CCastChannel}
 * <p/>
 * Created by Timothy on 11/24/2014.
 * Copyright 11/24/2014 adventurpriseme.com
 */
public class CTriviaCastCCMessage
	implements ITriviaCastPlayerCCM
	{
	/**
	 * Callback for receiving a message from the chromecast.
	 * <p/>
	 * Implement this function to handle a message string coming in from the chromecast.
	 *
	 * @param strMsg
	 */
	@Override
	public void onReceiveCallback (String strMsg)
		{
		}
	// TODO - This class
	}
