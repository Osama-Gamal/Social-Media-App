package com.wanjy.dannie.rivchat.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.*;
import android.support.v7.widget.*;
import android.app.*;
import java.util.*;

import com.wanjy.dannie.rivchat.Home;
import com.wanjy.dannie.rivchat.R;
import com.wanjy.dannie.rivchat.model.*;
import com.wanjy.dannie.rivchat.util.ImageUtils;

import android.content.*;
import android.util.*;
import android.widget.*;
import java.text.*;
import android.support.v4.widget.SwipeRefreshLayout;

public class FragmentDeveloper extends Fragment
{

    DatabaseReference databaseReference;


    private Context context;

    public TextView UserName,UserTitle,Telgeramlink;
    ImageView UserimageView;
    CardView UserCard;
    TextView communicateTxt;

    public FragmentDeveloper() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.developer_fragment, container, false);
        context = view.getContext();


        return inflater.inflate(R.layout.developer_fragment, container, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {



        UserName = (TextView) view.findViewById(R.id.USerNSme);
        //UserTitle = (TextView) view.findViewById(R.id.USerTitle);
        Telgeramlink = (TextView) view.findViewById(R.id.Telgeram);
        UserimageView = (ImageView)view.findViewById(R.id.UserImageView);
        UserCard = (CardView) view.findViewById(R.id.cardUser);


        if(Home.DeveloperUser != null) {
            UserName.setText(Home.DeveloperUser.getName());
            //UserTitle.setText(Home.DeveloperUser.getTitle());
            Telgeramlink.setText(Home.DeveloperUser.getTelegram());


            String img = Home.DeveloperUser.getAvata();
            final Bitmap src;
            if (img.equals("default")) {
                UserimageView.setImageResource(R.drawable.default_avata);
            } else {
                byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
                src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                UserimageView.setImageDrawable(ImageUtils.roundedImage(context, src));
            }


            UserCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Telgeramlink.getText().toString().length() > 3) {
                        GetTelegram(Telgeramlink.getText().toString());
                    } else {
                        Toast.makeText(context, "لا يمكن التواصل مع هذا الطالب", Toast.LENGTH_LONG).show();
                    }


                }
            });

        }



        super.onViewCreated(view, savedInstanceState);
    }



    public void GetTelegram(String idTelegram) {
        try {
            Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse(idTelegram));
            telegram.setPackage("org.telegram.messenger");
            context.startActivity(telegram);
        } catch (Exception e) {
            Toast.makeText(context, "Telegram app is not installed", Toast.LENGTH_LONG).show();
        }
    }




}
