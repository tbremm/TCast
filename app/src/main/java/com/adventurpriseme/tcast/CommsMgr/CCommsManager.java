package com.adventurpriseme.tcast.CommsMgr;

import com.adventurpriseme.tcast.GamesManager.IComms2Games;

/**
 * Manages communication between an output device and a Games Manager.
 * <p/>
 * This class handles communications with the target device (eg. Chromecast), and must implement the functions required by the game manager.
 * <p/>
 * Created by Timothy on 12/27/2014.
 * Copyright 12/27/2014 adventurpriseme.com
 */
public class CCommsManager
	implements IGames2Comms // TODO: Implement interface functions
	{
	/** Data members */
	IComms2Games m_Comms2Games;    /** Communications manager */
	/**
	 * Initialize the manager with a connection object
	 * <p/>
	 * Call this after construction, and pass it an implementation of the {@link IGames2Comms} interface.
	 *
	 * @param iComms2Games
	 * 	(required)
	 * 	The communications manager that will be used
	 */
	public void Initialize (IComms2Games iComms2Games)
		{
		m_Comms2Games = iComms2Games;
		}
	}
