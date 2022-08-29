package com.wanjy.dannie.rivchat.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wanjy.dannie.rivchat.R;
import com.wanjy.dannie.rivchat.model.Comment;


import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import com.google.android.gms.measurement.*;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.wanjy.dannie.rivchat.data.StaticConfig;

import com.wanjy.dannie.rivchat.util.ImageUtils;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.widget.Toast;
import org.ocpsoft.prettytime.PrettyTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import android.content.SharedPreferences;
import android.app.Activity;
import android.graphics.Color;
import android.widget.LinearLayout;

import java.io.IOException;
import android.util.Log;
import android.os.Handler;

import java.util.concurrent.TimeUnit;
import android.view.ViewGroup.LayoutParams;

import android.media.MediaPlayer.OnPreparedListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.graphics.drawable.GradientDrawable;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Intent;

import java.io.File;
import android.os.Environment;
import android.view.View.OnLongClickListener;
import android.view.View.OnClickListener;
import butterknife.OnLongClick;
import com.wanjy.dannie.rivchat.model.User;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mData;


    String soundUrl = "hjk";

    public ImagesAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder holder, int position) {



        holder.UrlImage.setText(mData.get(position).getImg());
        holder.TextImage.setText(mData.get(position).getContent());
        Glide.with(mContext).load(holder.UrlImage.getText().toString()).into(holder.ImageViewMain);

        holder.ImageViewMain.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View p1)
            {
                Intent go = new Intent(mContext,ImageViewerActivity.class);
                go.putExtra("Link",holder.UrlImage.getText().toString());
                go.putExtra("Text",holder.TextImage.getText().toString());
                mContext.startActivity(go);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageView ImageViewMain;
        TextView UrlImage,TextImage;

        public CommentViewHolder(View itemView) {
            super(itemView);

            ImageViewMain = itemView.findViewById(R.id.ImageViewMain);
            UrlImage = itemView.findViewById(R.id.UrlImage);
            TextImage = itemView.findViewById(R.id.TextImage);


        }
    }



}