package com.johnpepper.eeapp.util;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.app.EEApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by borysrosicky on 11/2/15.
 */
public class EEImageLoader {
    public static class IMG_TYPE {
        public static int NORMAL = 0;
        public static int MALE = 1;
        public static int FEMALE = 2;
        public static int CAMERA = 3;
    }

    private static ImageLoader instance = ImageLoader.getInstance();

    public static DisplayImageOptions normal_options;
    public static DisplayImageOptions male_options;
    public static DisplayImageOptions female_options;
    public static DisplayImageOptions camera_options;

    public static void init() {
        normal_options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();
        male_options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();
        female_options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();
        camera_options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();
    }

    public static void clearCache() {
        if (instance != null) {
            instance.clearDiscCache();
            instance.clearMemoryCache();
        }
    }
    public static void clearImageFromCache(String imageURL) {
        MemoryCacheUtils.removeFromCache(imageURL, instance.getMemoryCache());
        DiskCacheUtils.removeFromCache(imageURL, instance.getDiskCache());
    }

    public static void stop() {
        if (instance != null) {
            instance.stop();
        }
    }

    public static void showImage(ImageView imgView, String url) {
        if (TextUtils.isEmpty(url))
            return;

        DisplayImageOptions option = normal_options;

        try {
            instance.displayImage(url, imgView, option, animateFirstListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void showScaledImage(ImageView imgView, String url, int type) {
        if (TextUtils.isEmpty(url))
            return;

        DisplayImageOptions option = normal_options;
        if (type == IMG_TYPE.MALE) {
            option = male_options;
        } else if (type == IMG_TYPE.FEMALE) {
            option = female_options;
        } else if (type == IMG_TYPE.CAMERA) {
            option = camera_options;
        }

        try {
            instance.displayImage(url, imgView, option, scaledFirstListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isScaledImagedLoaded(String url) {
        return ((ScaledFirstDisplayListener)scaledFirstListener).isImageLoaded(url);
    }

    public static Bitmap getScaledBitmap(Bitmap orgBmp, double retWidth) {
        if (orgBmp == null || retWidth <= 0)
            return null;

        Bitmap retBmp = null;
        try {
            int orgWidth = orgBmp.getWidth();
            int orgHeight = orgBmp.getHeight();
            double ratio = (double)retWidth / (double)orgWidth;
            retBmp = Bitmap.createScaledBitmap(orgBmp, (int)(orgWidth*ratio), (int)(orgHeight*ratio), false);
        }
        catch(Exception e) {
            e.printStackTrace();
            retBmp = null;
        }

        return retBmp;
    }

    private static ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    private static ImageLoadingListener scaledFirstListener = new ScaledFirstDisplayListener();
    private static class ScaledFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedScaledImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
            if (loadedImage != null) {
                String strTag = (String) view.getTag();
                if(!strTag.equalsIgnoreCase(imageUri))
                    return;

                final ImageView imageView = (ImageView) view;
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                if (EEApplication.mBPortrait) {
                    params.width = EEApplication.mScreenWidth;
                    int setheight = (int)(EEApplication.mScreenWidth * (((double)loadedImage.getHeight()) / ((double)loadedImage.getWidth())));
                    if (params.height != setheight) {
                        params.height = setheight;
                        imageView.setLayoutParams(params);
                    }
                }
                else {
                    params.width = EEApplication.mScreenHeight;
                    int setheight = (int)(EEApplication.mScreenHeight * (((double)loadedImage.getHeight()) / ((double)loadedImage.getWidth())));
                    if (params.height != setheight) {
                        params.height = setheight;
                        imageView.setLayoutParams(params);
                    }
                }
                FadeInBitmapDisplayer.animate(imageView, 500);
                displayedScaledImages.add(imageUri);
                //imageView.requestLayout();
                //imageView.setImageBitmap(loadedImage);
                //imageView.setBackground(new BitmapDrawable(view.getResources(), loadedImage));
            }
        }

        public boolean isImageLoaded(String imageUri) {
            return displayedScaledImages.contains(imageUri);
        }
    }
}
