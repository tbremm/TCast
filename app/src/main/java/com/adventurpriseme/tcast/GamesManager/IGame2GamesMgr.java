package com.adventurpriseme.tcast.GamesManager;

import android.app.Activity;

/**
 * Allows interaction from the Game Instance into the Games Manager.
 * <p/>
 * Implement this interface in the Game Manager to handle calls from the Game Instance.
 * <p/>
 * Created by Timothy on 12/27/2014.
 * Copyright 12/27/2014 adventurpriseme.com
 */
public interface IGame2GamesMgr
	{
	Activity getActivity ();
	}
