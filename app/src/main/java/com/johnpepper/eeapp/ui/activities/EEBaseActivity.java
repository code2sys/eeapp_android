package com.johnpepper.eeapp.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.johnpepper.eeapp.R;

/**
 * Created by Dragon on 12/14/15.
 */
public abstract class EEBaseActivity extends FragmentActivity {

    protected static String TAG;
    public boolean mTouchEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TAG = getClass().getSimpleName();

        super.onCreate(savedInstanceState);
    }

    /**
     * **************************************** Options Menu ******************************************
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                popIntent();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setContentView(int layoutResId) {
        View contentView = View.inflate(this, layoutResId, null);

        setContentView(contentView);
    }

    public void setContentView(View contentView) {
        setContentView(contentView, null);
    }

    public void setContentView(View contentView, ViewGroup.LayoutParams params) {
        View rootView = contentView;

        if (params != null) {
            super.setContentView(rootView, params);
        } else {
            super.setContentView(rootView);
        }

        _initUI();
    }

    private void _initUI() {
        initUI();
    }

    /**
     * **************************************** Abstract Interface ******************************************
     */
    protected abstract void initUI();

    /**
     * **************************************** Touch Event ******************************************
     */
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mTouchEnabled == true)
            return super.dispatchTouchEvent(ev);
        return true;
    }

    public boolean getTouchEnabled() {
        return mTouchEnabled;
    }

    public void setTouchEnabled(boolean enabled) {
        mTouchEnabled = enabled;
    }


    public void pushIntent(Intent intent) {
        pushIntent(intent, false);
    }

    public void pushIntent(Intent intent, boolean finish) {
        startActivity(intent);
        if (finish) finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void popIntent() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
