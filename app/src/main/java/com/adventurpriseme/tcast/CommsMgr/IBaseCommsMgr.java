package com.adventurpriseme.tcast.CommsMgr;

/**
 * Base interface to the communications manager.
 * <p/>
 * Created by Timothy on 12/17/2014.
 * Copyright 12/17/2014 adventurpriseme.com
 */
public interface IBaseCommsMgr
	{
	/**
	 * Send a string message through a communications channel
	 *
	 * @param strMessage
	 * 	(required)  The message to send
	 *
	 * @return boolean  Returns true if the message successfully sent, false otherwise
	 */
	public boolean send (String strMessage);
	/**
	 * Callback to receive messages from the communications layer
	 *
	 * @return String  The message received from the communications layer
	 */
	public String onReceive ();
	/**
	 * Connect to the target communications channel
	 * <p/>
	 * TODO: Determine what makes sense to pass in as a connection object so we know what to connect to
	 *
	 * @return boolean  True on success, false otherwise
	 */
	public boolean connect ();
	/**
	 * Callback that should be called once communications are connected
	 */
	public void onConnect ();
	/**
	 * Disconnect from the communications channel
	 *
	 * @return boolean  True on success, false otherwise
	 */
	public boolean disconnect ();
	/**
	 * Callback that should be called when communications are disconnected
	 */
	public void onDisconnect ();
	}
