package com.adventurpriseme.tcast.CustomGridView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.adventurpriseme.tcast.R;

/**
 * Created by Timothy on 12/27/2014.
 * Copyright 12/27/2014 adventurpriseme.com
 */
public class CustomGridAdapter
	extends BaseAdapter
	{
	private final String[] gridValues;
	private       Context  context;

	//Constructor to initialize values
	public CustomGridAdapter (Context context, String[] gridValues)
		{
		this.context = context;
		this.gridValues = gridValues;
		}

	@Override
	public int getCount ()
		{
		// Number of times getView method call depends upon gridValues.length
		return gridValues.length;
		}

	@Override
	public Object getItem (int i)
		{
		return null;
		}

	@Override
	public long getItemId (int i)
		{
		return 0;
		}

	@Override
	// Number of times getView method call depends upon gridValues.length
	public View getView (int position, View convertView, ViewGroup parent)
		{
		// LayoutInflator to call external grid_item.xml file
		LayoutInflater inflater = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
		View gridView;
		if (convertView == null)
			{
			gridView = new View (context);
			// get layout from grid_item.xml ( Defined Below )
			gridView = inflater.inflate (R.layout.grid_item, null);
			// set value into textview
			TextView textView = (TextView) gridView.findViewById (R.id.grid_item_label);
			textView.setText (gridValues[position]);
			// set image based on selected text
			ImageView imageView = (ImageView) gridView.findViewById (R.id.grid_item_image);
			String arrLabel = gridValues[position];
			if (arrLabel.equals ("Trivia"))
				{
				imageView.setImageResource (R.drawable.logo_triviacast);
				}
			else
				{
				imageView.setImageResource (R.drawable.ic_castme_logo);    // FIXME: Make a better resource
				}
			}
		else
			{
			gridView = (View) convertView;
			}
		return gridView;
		}
	}
