package com.johnpepper.eeapp.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.WindowManager;

import com.johnpepper.eeapp.BuildConfig;
import com.johnpepper.eeapp.util.EEImageLoader;
import com.johnpepper.eeapp.util.EEPreferenceManager;
import com.johnpepper.eeapp.util.MessageUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by borysrosicky on 10/29/15.
 */
public class EEApplication extends Application {

    private static Context applicationContext;
    public static int mScreenWidth = 0;
    public static int mScreenHeight = 0;
    public static boolean mBPortrait = true;
    @Override
    public void onCreate() {
        super.onCreate();

        applicationContext = this.getApplicationContext();
        MessageUtil.initializeMesageUtil(applicationContext);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        EEPreferenceManager.initializePreferenceManager(sharedPreferences);

        // Initialize screen width and height

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;

        // Initialize Image Loader
        initImageLoader(applicationContext.getApplicationContext());
        new EEImageLoader();
        EEImageLoader.init();

        if (BuildConfig.BUILD_TYPE == "staging") {
            Constants.BASE_URL_STRING = "http://192.168.0.189/eeapp/index.php/api/example/";
        }
    }

    private void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}
