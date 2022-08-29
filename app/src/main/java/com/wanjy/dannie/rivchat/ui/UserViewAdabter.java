package com.wanjy.dannie.rivchat.ui;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.*;
import android.content.*;

import com.wanjy.dannie.rivchat.R;
import com.wanjy.dannie.rivchat.model.*;
import java.util.*;
import com.google.firebase.database.*;
import android.view.*;
import com.bumptech.glide.*;
import android.widget.*;
import org.ocpsoft.prettytime.*;
import java.text.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.wanjy.dannie.rivchat.util.ImageUtils;
import android.util.Base64;
import com.wanjy.dannie.rivchat.data.StaticConfig;
import android.app.AlertDialog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.app.ProgressDialog;
import java.io.File;
import android.os.Environment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
//import android.annotation.NonNull;
import com.google.firebase.storage.OnProgressListener;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;


public class UserViewAdabter extends RecyclerView.Adapter<UserViewAdabter.ViewHolder> {

    Context context;
    List<User> MainUserUploadInfoList;


    public UserViewAdabter(Context context, List<User> TempList) {

        this.MainUserUploadInfoList = TempList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final User UploadInfo = MainUserUploadInfoList.get(position);


        holder.UserID.setText(UploadInfo.getMyId());
        holder.UserName.setText(UploadInfo.getName());
        holder.UserTitle.setText(UploadInfo.getTitle());
        holder.UserGroup.setText(UploadInfo.getGroup());
        holder.Telgeramlink.setText(UploadInfo.getTelegram());

        if(holder.UserTitle.getText().toString().equals("")||holder.UserTitle.getText().toString().isEmpty()||holder.UserTitle.getText().toString().length()<3){
            holder.communicateTxt.setVisibility(View.INVISIBLE);
            holder.UserCard.setEnabled(false);
        }else{
            holder.communicateTxt.setVisibility(View.VISIBLE);
            holder.UserCard.setEnabled(true);
        }


        holder.UserCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.Telgeramlink.getText().toString().length()>3) {
                    GetTelegram(holder.Telgeramlink.getText().toString());
                }else{
                    Toast.makeText(context,"لا يمكن التواصل مع هذا الطالب",Toast.LENGTH_LONG).show();
                }


            }
        });


        String img = UploadInfo.getAvata();
        final Bitmap src;
        if (img.equals("default")) {
            holder.UserimageView.setImageResource(R.drawable.default_avata);
        } else {
            byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
            src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            holder.UserimageView.setImageDrawable(ImageUtils.roundedImage(context, src));
        }








    }


    @Override
    public int getItemCount() {

        return MainUserUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView UserName,UserGroup,UserTitle,Telgeramlink;
        ImageView UserimageView;
        CardView UserCard;
        TextView UserID,communicateTxt;

        public ViewHolder(View itemView) {
            super(itemView);

            UserID = (TextView) itemView.findViewById(R.id.UserId);
            UserName = (TextView) itemView.findViewById(R.id.USerNSme);
            UserTitle = (TextView) itemView.findViewById(R.id.USerTitle);
            UserGroup = (TextView) itemView.findViewById(R.id.USerGroup);
            communicateTxt = (TextView) itemView.findViewById(R.id.communicateTxt);
            Telgeramlink = (TextView) itemView.findViewById(R.id.Telgeram);
            UserimageView = (ImageView) itemView.findViewById(R.id.UserImageView);
            UserCard = (CardView) itemView.findViewById(R.id.cardUser);


        }
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
