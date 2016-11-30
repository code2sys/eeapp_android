package com.johnpepper.eeapp.ui.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.johnpepper.eeapp.R;

/**
 * Created by Dragon on 12/14/15.
 */
public abstract class EEActionBarBaseActivity extends AppCompatActivity{

    protected static String TAG;
    public boolean mTouchEnabled = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TAG = getClass().getSimpleName();

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.view_actionbar_title);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View customView = actionBar.getCustomView();

        TextView titleView = (TextView) customView.findViewById(R.id.tv_title);
        titleView.setText(title);
    }

    public void setRightOptionsItemStatus(boolean exist) {
        // We don't have to call this function if exist == true

        RelativeLayout titleView = (RelativeLayout) getSupportActionBar().getCustomView();
        View titleViewComponent = titleView.getChildAt(0);
        if (exist == false)
            titleViewComponent.setPadding(0, 0, (int) getResources().getDimension(R.dimen.actionbar_button_size), 0);
        else
            titleViewComponent.setPadding(0, 0, 0, 0);
    }

    /**
     * **************************************** Options Menu ******************************************
     */

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            popIntent();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
