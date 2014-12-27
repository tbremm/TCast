package com.adventurpriseme.tcast.CommsMgr;

/**
 * Base class for a communications manager
 * <p/>
 * This base class provides basic functionality for connecting, disconnecting, sending, and receiving messages through a communications channel.
 * <p/>
 * TODO: Flesh out this class
 * <p/>
 * Created by Timothy on 12/17/2014.
 * Copyright 12/17/2014 adventurpriseme.com
 */
public class CBaseCommsMgr
	implements IBaseCommsMgr
	{
	/**
	 * Send a string message through a communications channel
	 *
	 * @param strMessage
	 * 	(required)  The message to send
	 *
	 * @return boolean  Returns true if the message successfully sent, false otherwise
	 */
	@Override
	public boolean send (String strMessage)
		{
		return false;
		}

	/**
	 * Callback to receive messages from the communications layer
	 *
	 * @return String  The message received from the communications layer
	 */
	@Override
	public String onReceive ()
		{
		return null;
		}

	/**
	 * Connect to the target communications channel
	 * <p/>
	 * TODO: Determine what makes sense to pass in as a connection object so we know what to connect to
	 *
	 * @return boolean  True on success, false otherwise
	 */
	@Override
	public boolean connect ()
		{
		return false;
		}

	/**
	 * Callback that should be called once communications are connected
	 */
	@Override
	public void onConnect ()
		{
		}

	/**
	 * Disconnect from the communications channel
	 *
	 * @return boolean  True on success, false otherwise
	 */
	@Override
	public boolean disconnect ()
		{
		return false;
		}

	/**
	 * Callback that should be called when communications are disconnected
	 */
	@Override
	public void onDisconnect ()
		{
		}
	}
