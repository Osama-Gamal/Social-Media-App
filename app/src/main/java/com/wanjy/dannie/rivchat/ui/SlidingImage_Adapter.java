package com.wanjy.dannie.rivchat.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wanjy.dannie.rivchat.Home;
import com.wanjy.dannie.rivchat.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Parsania Hardik on 23/04/2016.
 */
public class SlidingImage_Adapter extends PagerAdapter {


   // private String[] urls;
    private LayoutInflater inflater;
    private Context context;
    private List<String> listImages = new ArrayList<>();

    public SlidingImage_Adapter(Context context,  List<String> listImages) {
        this.context = context;
        this.listImages = listImages;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return listImages.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.image);


        Glide.with(context)
                .load(listImages.get(position))
                .into(imageView);

        view.addView(imageLayout, 0);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(context,ImageViewerActivity.class);
                go.putExtra("Link",listImages.get(position));
                go.putExtra("Text","");
                context.startActivity(go);

            }
        });



        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}