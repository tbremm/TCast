package com.adventurpriseme.tcast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity
	extends Activity
	{
	@Override
	protected void onCreate (Bundle savedInstanceState)
		{
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);
		if (getActionBar () != null)
			{
			getActionBar ().setDisplayHomeAsUpEnabled (true);
			}
		String str = "test";
		changeString (str);
		Log.e ("Pointer Test", str);
		}

	private void changeString (String str) {str = "I'm a new string!";}

	@Override
	public boolean onCreateOptionsMenu (Menu menu)
		{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater ().inflate (R.menu.menu_main, menu);
		return true;
		}

	@Override
	public boolean onOptionsItemSelected (MenuItem item)
		{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId ();
		//noinspection SimplifiableIfStatement
		switch (id)
			{
			case R.id.action_settings:
			{
			onSettingsSelected ();
			return true;
			}
			case R.id.action_about:
			{
			// Show an about dialog box with version info
			CAboutDialog.Show (MainActivity.this);
			}
			}
		return super.onOptionsItemSelected (item);
		}

	/**
	 * Action bar settings menu entry
	 * <p/>
	 * This will load the settings view.
	 */
	private void onSettingsSelected ()
		{
		Intent intent = new Intent (this, TriviaPrefsActivity.class);
		startActivity (intent);
		}

	// onClick handler for the playgame button
	// This launches the trivia game entry intent
	public void onPlayGame (View view)
		{
		Intent intent = new Intent (this, PlayTriviaActivity.class);
		startActivity (intent);
		}
	}
