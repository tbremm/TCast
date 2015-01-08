package com.adventurpriseme.tcast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.adventurpriseme.tcast.CustomGridView.CustomGridAdapter;
import com.adventurpriseme.tcast.TriviaGame.TriviaPrefsActivity;

public class MainActivity
	extends Activity
	{
	public static final  String   TAG       = "CastMe Main Activity";
	private static final String[] GRID_DATA = new String[] {"Trivia"};
	private GridView m_gridView;

	@Override
	protected void onCreate (Bundle savedInstanceState)
		{
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);
		if (getActionBar () != null)
			{
			getActionBar ().setDisplayHomeAsUpEnabled (true);
			}
		// Get the gridview object from xml
		m_gridView = (GridView) findViewById (R.id.main_layout);
		// Set gridview custom adapter
		m_gridView.setAdapter (new CustomGridAdapter (this, GRID_DATA));
		m_gridView.setOnItemClickListener (new AdapterView.OnItemClickListener ()
		{
		@Override
		public void onItemClick (AdapterView<?> parent, View view, int pos, long id)
			{
			switch (pos)
				{
				case 0:
					playGame_Trivia ();
					break;
				default:
					Log.e (TAG, "ERROR: Unhandled grid selection...");
					break;
				}
			}
		});
		}

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

	public void playGame_Trivia ()
		{
		Intent intent = new Intent (this, PlayTriviaActivity.class);
		startActivity (intent);
		}

	// onClick handler for the playgame button
	// This launches the trivia game entry intent
	public void onPlayGame_Trivia (View view)
		{
		playGame_Trivia ();
		}
	}
