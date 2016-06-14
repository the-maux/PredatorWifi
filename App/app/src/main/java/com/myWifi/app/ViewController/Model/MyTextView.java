package com.myWifi.app.ViewController.Model;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 *
 * TextView avec police choisie
 *
 */
public class            MyTextView extends TextView {
    public              MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public              MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public              MyTextView(Context context) {
        super(context);
        init();
    }

    public void         init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/montserrat_regular.ttf");
        setTypeface(tf ,1);
    }
}
