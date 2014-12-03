package com.adventurpriseme.tcast;

/**
 * Created by Timothy on 11/21/2014.
 * Copyright 11/21/2014 adventurpriseme.com
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;

/**
 * helper for Prompt-Dialog creation
 */
public abstract class CPromptDlg extends AlertDialog.Builder implements OnClickListener
	{
	private final EditText input;

	/**
	 * Create a prompt dialog that gets a text string from the user.
	 *
	 * @param context
	 * @param title
	 * 		string resource id
	 * @param message
	 * 		string resource id
	 * @param okMsg
	 * 		string resource id
	 * @param okMsg
	 * 		string resource id
	 */
	public CPromptDlg (Context context, int title, int message, int okMsg, int cancelMsg)
		{
		super (context);
		setTitle (title);
		setMessage (message);

		input = new EditText (context);
		setView (input);

		setPositiveButton (okMsg, this);
		setNegativeButton (cancelMsg, this);
		}

	/**
	 * onClick listener for dialog buttons.
	 *
	 * This can catch the okay or cancel buttons.
	 *
	 * @param dialog (required)  Dialog interface
	 * @param which (required)  Determines what button was hit. 0-indexed.
	 */
	@Override
	public void onClick (DialogInterface dialog, int which)
		{
		if (which == DialogInterface.BUTTON_POSITIVE)
			{
			if (onOkClicked (input.getText ().toString ()))
				{
				dialog.dismiss ();
				}
			}
		else
			{
			onCancelClicked (dialog);
			}
		}

	/**
	 * Called when "ok" pressed.
	 *
	 * @param input
	 *
	 * @return true, if the dialog should be closed. false, if not.
	 */
	abstract public boolean onOkClicked (String input);

	/**
	 * Called when "cancel" is pressed.
	 * Closes the dialog.
	 * Can be overridden.
	 *
	 * @param dialog
	 */
	public void onCancelClicked (DialogInterface dialog)
		{
		dialog.dismiss ();
		}
	}
