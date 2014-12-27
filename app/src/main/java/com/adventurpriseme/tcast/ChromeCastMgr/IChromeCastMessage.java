package com.adventurpriseme.tcast.ChromeCastMgr;

/**
 * Implement this class to interface with the chromecast receiver.
 * <p/>
 * Created by Timothy on 11/24/2014.
 * Copyright 11/24/2014 adventurpriseme.com
 */
public interface IChromeCastMessage
	{
	/**
	 * Callback for receiving a message from the chromecast.
	 * <p/>
	 * Implement this function to handle a message string coming in from the chromecast.
	 *
	 * @param strMsg
	 * 	(required)  Content of the message received from the chromecast
	 */
	public void onReceiveCallback (String strMsg);
	}
