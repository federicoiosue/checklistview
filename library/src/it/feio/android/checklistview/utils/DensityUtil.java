package it.feio.android.checklistview.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class DensityUtil {
	/**
	 * This method converts dp unit to equivalent pixels, depending on device
	 * density.
	 * 
	 * @param dp
	 *            A value in dp (density independent pixels) unit. Which we need
	 *            to convert into pixels
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on
	 *         device density
	 */
//	public static int convertDpToPixel(float dp, Context context) {
//		Resources resources = context.getResources();
//		DisplayMetrics metrics = resources.getDisplayMetrics();
//		float px = dp * (metrics.densityDpi / 160f);
//		return (int)px;
//	}
	public static int dpToPx(int dp, Context context) {
	    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
	    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}

	/**
	 * This method converts device specific pixels to density independent
	 * pixels.
	 * 
	 * @param px
	 *            A value in px (pixels) unit. Which we need to convert into db
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
//	public static int convertPixelsToDp(float px, Context context) {
//		Resources resources = context.getResources();
//		DisplayMetrics metrics = resources.getDisplayMetrics();
//		float dp = px / (metrics.densityDpi / 160f);
//		return (int)dp;
//	}
	public static int pxToDp(int px, Context context) {
	    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
	    int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	    return dp;
	}
}
