package com.example.crowdfireassign.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.crowdfireassign.R;

import java.util.ArrayList;

/**
 * Created by chitra on 16/1/18.
 */

public class ViewPageAdapter extends PagerAdapter {

    private ArrayList<Bitmap> bitmapList;
    private Activity context;
    private LayoutInflater inflater;

    public ViewPageAdapter(ArrayList<Bitmap> bitmapList, Activity context) {
        this.bitmapList = bitmapList;

        this.context = context;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }



    @Override
    public int getCount() {
        return bitmapList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.content_main_activity, container, false);

        ImageView imageView = view.findViewById(R.id.my_image_view);
        //imageView.setImageBitmap(bitmapList.get(position));

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.error(R.mipmap.ic_launcher);
        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .asBitmap()
                .load(bitmapList.get(position))
                .into(imageView);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
