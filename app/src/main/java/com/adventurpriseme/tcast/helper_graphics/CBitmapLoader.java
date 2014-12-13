package com.adventurpriseme.tcast.helper_graphics;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * This class is intended to assist in loading bitmaps.
 * <p/>
 * This will help to load bitmaps efficiently, ensuring that they are scaled/formatted correctly.
 * <p/>
 *
 * @author Timothy Bremm on 11/23/2014.
 *         Copyright 11/23/2014 adventurpriseme.com
 */
public class CBitmapLoader
	{
	/**
	 * This decodes a bitmap from a resource at a sample rate constrained by the provided dimension requirements.
	 *
	 * @param res
	 * 	(required)
	 * 	The resource corresponding with the resource ID, resId, that the bitmap will be decoded from.
	 * @param resId
	 * 	(required)
	 * 	Resource ID of the image to be decoded.
	 * @param reqWidth
	 * 	(required)
	 * 	The max width required by the caller.
	 * @param reqHeight
	 * 	(required)
	 * 	The max height required by the caller.
	 *
	 * @return Bitmap that was decoded from a resource within the given dimension constraints.
	 */
	public static Bitmap decodeSampledBitmapFromResource (Resources res, int resId, int reqWidth, int reqHeight)
		{
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options ();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource (res, resId, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize (options, reqWidth, reqHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource (res, resId, options);
		}

	/**
	 * Internal helper function, but may be useful on it's own as well.
	 * <p/>
	 * Calculates and returns the sample size used in {@link BitmapFactory::decodeResource()}
	 * by decoding the image's bounds without decoding the image itself.
	 *
	 * @param options
	 * 	(required)
	 * 	Provides the raw dimensions of the object.
	 * 	Set this first by setting {@link android.graphics.BitmapFactory.Options::inJustDecodeBounds}=true,
	 * 	and then calling {@link android.graphics.BitmapFactory::decodeResource()}.
	 * @param reqWidth
	 * 	(required)
	 * 	The max width required by the caller, in pixels
	 * @param reqHeight
	 * 	(required)
	 * 	The max height required by the caller, in pixels
	 *
	 * @return the inSampleSize that corresponds with the given dimension requirements
	 */
	public static int calculateInSampleSize (BitmapFactory.Options options, int reqWidth, int reqHeight)
		{
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth)
			{
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth)
				{
				inSampleSize *= 2;
				}
			}
		return inSampleSize;
		}
	}
