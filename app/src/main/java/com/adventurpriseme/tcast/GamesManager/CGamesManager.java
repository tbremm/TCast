package com.adventurpriseme.tcast.GamesManager;

import com.adventurpriseme.tcast.CommsMgr.IGames2Comms;

/**
 * Manages game-related objects and implements communication with the cast interface.
 * <p/>
 * Created by Timothy on 12/27/2014.
 * Copyright 12/27/2014 adventurpriseme.com
 */
public class CGamesManager
	implements IComms2Games // TODO: Implement interface functions
	{
	/** Data members */
	IGames2Comms m_iGames2Comms;    /** Communications manager */
	/**
	 * Initialize the manager with a communications manager object
	 * <p/>
	 * Call this after construction, and pass it an implementation of the {@link com.adventurpriseme.tcast.GamesManager.IComms2Games} interface. This allows the manager to make
	 * outbound calls to the communications API.
	 *
	 * @param iGames2CommsMgr
	 * 	(required)
	 * 	The communications interface used to make outbound calls to the communications API
	 */
	public void Initialize (IGames2Comms iGames2CommsMgr)
		{
		m_iGames2Comms = iGames2CommsMgr;
		}
	}
