package com.bradlangel.gasbyme;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

/**
 * Created by bradlangel on 7/9/14.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity myActivity;
    private TextView myText;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myActivity = getActivity();
        myText =
                (TextView) myActivity
                        .findViewById(R.id.action_settings);
    }

}
