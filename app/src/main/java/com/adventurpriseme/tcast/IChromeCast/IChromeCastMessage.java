package com.adventurpriseme.tcast.IChromeCast;

/**
 * Implement this class to interface with the chromecast receiver.
 *
 * Created by Timothy on 11/24/2014.
 * Copyright 11/24/2014 adventurpriseme.com
 */
public interface IChromeCastMessage
	{
	/**
	 * Callback for receiving a message from the chromecast.
	 *
	 * Implement this function to handle a message string coming in from the chromecast.
	 *
	 * @param strMessage (required)  Content of the message received from the chromecast
	 */
	public void onReceiveCallback (String strMsg);
	}
