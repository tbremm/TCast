package com.adventurpriseme.tcast.TriviaGame;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.adventurpriseme.tcast.R;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class TriviaPrefsActivity
	extends PreferenceActivity
	{
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	private static final boolean                               ALWAYS_SIMPLE_PREFS                   = true;
	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static       Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener ()
	{
	@Override
	public boolean onPreferenceChange (Preference preference, Object value)
		{
		String stringValue = value.toString ();
		// For all basic preferences, set the summary to the value's
		// simple string representation.
		preference.setSummary (stringValue);
		return true;
		}
	};

	@Override
	protected void onCreate (Bundle savedInstanceState)
		{
		super.onCreate (savedInstanceState);
		setupActionBar ();
		}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi (Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar ()
		{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			{
			// Show the Up button in the action bar.
			if (getActionBar () != null)
				{
				getActionBar ().setDisplayHomeAsUpEnabled (true);
				}
			}
		}

	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane ()
		{
		return isXLargeTablet (this) && !isSimplePreferences (this);
		}

	/** {@inheritDoc} */
	@Override
	@TargetApi (Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders (List<Header> target)
		{
		if (!isSimplePreferences (this))
			{
			loadHeadersFromResource (R.xml.pref_headers, target);
			}
		}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet (Context context)
		{
		return (context.getResources ()
			        .getConfiguration ().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
		}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences (Context context)
		{
		return ALWAYS_SIMPLE_PREFS || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || !isXLargeTablet (context);
		}

	@Override
	protected void onPostCreate (Bundle savedInstanceState)
		{
		super.onPostCreate (savedInstanceState);
		setupSimplePreferencesScreen ();
		}

	@Override
	public boolean onOptionsItemSelected (MenuItem item)
		{
		int id = item.getItemId ();
		if (id == android.R.id.home)
			{
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			NavUtils.navigateUpFromSameTask (this);
			return true;
			}
		return super.onOptionsItemSelected (item);
		}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	private void setupSimplePreferencesScreen ()
		{
		if (!isSimplePreferences (this))
			{
			return;
			}
		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.
		// Add 'general' preferences.
		addPreferencesFromResource (R.xml.pref_player);
		// Add 'host' preferences, and a corresponding header.
		PreferenceCategory fakeHeader = new PreferenceCategory (this);
		fakeHeader.setTitle (R.string.pref_header_host);
		getPreferenceScreen ().addPreference (fakeHeader);
		addPreferencesFromResource (R.xml.pref_host);
		// Add 'data and sync' preferences, and a corresponding header.
		fakeHeader = new PreferenceCategory (this);
		fakeHeader.setTitle (R.string.pref_header_debug);
		getPreferenceScreen ().addPreference (fakeHeader);
		addPreferencesFromResource (R.xml.pref_debug);
		// Bind the summaries of EditText preference to
		// its value. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.
		bindPreferenceSummaryToValue (findPreference ("pref_player_name_text"));
		}

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 *
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue (Preference preference)
		{
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener (sBindPreferenceSummaryToValueListener);
		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange (preference, PreferenceManager.getDefaultSharedPreferences (preference.getContext ())
			                                                                      .getString (preference.getKey (), ""));
		}
	}
