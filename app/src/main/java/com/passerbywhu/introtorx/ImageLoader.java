package com.passerbywhu.introtorx;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

/**
 * Created by hzwuwenchao on 16/1/14.
 */
public class ImageLoader {
    private static volatile  ImageLoader mInstance;
    public static ImageLoader getInstance(){
        if(mInstance == null)
            synchronized (ImageLoader.class) {
                if (mInstance == null){
                    mInstance = new ImageLoader();
                 }
            }
        return mInstance;
    }

    private Context context;
    private Picasso picasso;
    private static final int MIN_DISK_CACHE_SIZE = 10 * 1024 * 1024; //10MB
    private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; //50MB

    private ImageLoader(){
        this.context = MyApplication.getInstance().getApplicationContext();
        picasso = Picasso.with(context);
    }

    public void loadImage(String url, ImageView imageView, boolean fit, boolean centerCrop, Transformation transformation, Callback callback, Drawable placeholderDrawable, Drawable errorDrawable) {
        if(TextUtils.isEmpty(url)) {
            imageView.setImageDrawable(errorDrawable);
            return;
        }
        RequestCreator requestCreator = picasso.load(url);
        if(fit)
            requestCreator.fit();
        if(centerCrop)
            requestCreator.centerCrop();
        if(transformation != null)
            requestCreator.transform(transformation);
        if (placeholderDrawable != null) {
            requestCreator.placeholder(placeholderDrawable);
        }
        if (errorDrawable != null) {
            requestCreator.error(errorDrawable);
        }
        if(callback != null)
            requestCreator.into(imageView, callback);
        else
            requestCreator.into(imageView);
    }


    public void loadImage(String url, ImageView imageView) {
        loadImage(url, imageView, true, true, null, null ,null, null);
    }

    public void loadImage(String url, ImageView imageView, boolean fit, boolean crop) {
        loadImage(url, imageView, fit, crop, null, null ,null, null);
    }

    public void loadImage(String url, ImageView imageView, Drawable placeHolderDrawable, Drawable errorDrawable) {
        loadImage(url, imageView, true, true, null, null, placeHolderDrawable, errorDrawable);
    }

    public void loadImage(String url, ImageView imageView, int placeHolderDrawableId, int errorDrawableId) {
        loadImage(url, imageView, context.getResources().getDrawable(placeHolderDrawableId), context.getResources().getDrawable(errorDrawableId));
    }

    public void loadImage(String url, ImageView imageView, Drawable drawable, Transformation transformation, Callback callback) {
        loadImage(url, imageView, true, true, transformation, callback, drawable, drawable);
    }

    public void loadImage(String url, ImageView imageView, Drawable drawable) {
        loadImage(url, imageView, drawable, drawable);
    }

    public void loadImage(String url, ImageView imageView, int drawableId) {
        loadImage(url, imageView, context.getResources().getDrawable(drawableId));
    }

    public void loadImage(String url, ImageView imageView, int drawableId, Transformation transformation, Callback callback) {
        loadImage(url, imageView, context.getResources().getDrawable(drawableId), transformation, callback);
    }

    public void loadImage(String url, ImageView imageView, int drawableId, Transformation transformation){
        loadImage(url, imageView, drawableId, transformation, null);
    }

    public void loadImage(String url, ImageView imageView, int drawableId, Callback callback){
        loadImage(url, imageView, drawableId, null, callback);
    }

    public void clearCache(String url){
        picasso.invalidate(url);
    }
}
