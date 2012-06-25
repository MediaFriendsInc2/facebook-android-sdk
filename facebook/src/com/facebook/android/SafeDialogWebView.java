package com.facebook.android;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

/**
 * This is a workaround for the onWindowFocusChanged bug that occurs in froyo devices.
 * 
 * http://www.zubha-labs.com/workaround-for-null-pointer-excpetion-in-webv
 * 
 */
public class SafeDialogWebView extends WebView
{

    private static final String TAG = SafeDialogWebView.class.getSimpleName();

    public SafeDialogWebView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public SafeDialogWebView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public SafeDialogWebView(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus)
    {
        try
        {
            super.onWindowFocusChanged(hasWindowFocus);
        } catch (NullPointerException e)
        {
            Log.d(TAG, "onWindowFocusChanged bug", e);
        }
    }

}
