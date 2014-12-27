package com.adventurpriseme.tcast.CommsMgr;

import android.util.Log;

import com.adventurpriseme.tcast.ChromeCastMgr.CChromeCastMgr;
import com.adventurpriseme.tcast.CommInstanceMgr.ICommsMgr2Comm;
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
	implements IGames2Comms, IComm2CommsMgr
	{
	/** Data members */
	private static final String TAG = "Communications Manager";
	private IComms2Games   m_Comms2Games;   // Game manager
	private ICommsMgr2Comm m_commInstance;  // Communications channel instance

	/**
	 * Constructor.
	 * <p/>
	 * Set the communication type on construction.
	 *
	 * @param eType
	 * 	(required)
	 * 	The type of communications channel to create.
	 */
	public CCommsManager (ECommChannelTypes eType)
		{
		// Set our communications device
		switch (eType)
			{
			case CHROMECAST:
				m_commInstance = new CChromeCastMgr ();
				break;
			default:
				Log.e (TAG, "ERROR: Invalid communications type selected: " + eType.toString ());
				break;
			}
		}

	/**
	 * initCommunications the manager with a connection object.
	 * <p/>
	 * Call this after construction, and pass it an implementation of the {@link IGames2Comms} interface, and the type of communications channel to create.
	 *
	 * @param iComms2Games
	 * 	(required)
	 * 	The communications manager that will be used.
	 */
	public void Initialize (IComms2Games iComms2Games)
		{
		// Set our game interface
		m_Comms2Games = iComms2Games;
		}
	}
