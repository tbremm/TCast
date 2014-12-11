package com.adventurpriseme.tcast;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Vanilla "About" dialog box.
 * <p/>
 * To use this class, simply call:
 * &nbsp;&nbsp;AboutDialog.Show (activity.this);
 * <p/>
 * Created by Timothy on 12/10/2014.
 * Copyright 12/10/2014 adventurpriseme.com
 */
public class CAboutDialog
	{

	/**
	 * Get the Version Name of the app
	 *
	 * @param context
	 * 		(required)  Context of the desired version
	 *
	 * @return Version Name of the app
	 */
	static String getVersionName (Context context)
		{
		try
			{
			return context.getPackageManager ().getPackageInfo (context.getPackageName (), 0).versionName;
			}
		catch (PackageManager.NameNotFoundException e)
			{
			return "Unknown";
			}
		}

	/**
	 * Get the Version Code of the app
	 *
	 * @param context
	 * 		(required)  Context of the desired version
	 *
	 * @return int  Version Code of the app
	 */
	static int getVersionCode (Context context)
		{
		try
			{
			return context.getPackageManager ().getPackageInfo (context.getPackageName (), 0).versionCode;
			}
		catch (PackageManager.NameNotFoundException e)
			{
			return -1;
			}
		}

	/**
	 * Display an "About" dialog box.
	 * <p/>
	 * Displays an "About" dialog box with the calling app's version name.
	 * To show the box, simply call AboutDialog.Show (Activity.this).
	 * This expects the calling activity to have the resource:
	 * &nbsp;&nbsp;R.string.about
	 *
	 * @param callingActivity
	 * 		(required)  The activity that will host the "About" dialog box.
	 */
	public static void Show (Activity callingActivity)
		{
		// Use a Spannable to allow for links highlighting
		SpannableString aboutText = new SpannableString ("Version " + getVersionName (callingActivity) + "\n\n" + callingActivity.getString (R.string.about));
		// Generate views to pass to AlertDialog.Builder and to set the text
		View about;
		TextView tvAbout;
		try
			{
			//Inflate the custom view
			LayoutInflater inflater = callingActivity.getLayoutInflater ();
			about = inflater.inflate (R.layout.aboutbox, (ViewGroup) callingActivity.findViewById (R.id.aboutView));
			tvAbout = (TextView) about.findViewById (R.id.aboutText);
			}
		catch (InflateException e)
			{
			// Inflater can throw exception, unlikely but default to TextView if it occurs
			about = tvAbout = new TextView (callingActivity);
			}
		// Set the about text
		tvAbout.setText (aboutText);
		// Now Linkify the text
		Linkify.addLinks (tvAbout, Linkify.ALL);
		// Build and show the dialog
		new AlertDialog.Builder (callingActivity).setTitle ("About " + callingActivity.getString (R.string.app_name))
		                                         .setCancelable (true)
		                                         .setIcon (R.drawable.ic_launcher)
		                                         .setPositiveButton ("OK", null)
		                                         .setView (about)
		                                         .show ();    // Builder method returns allow for method chaining
		}
	}
