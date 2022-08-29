package com.wanjy.dannie.rivchat.ui;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wanjy.dannie.rivchat.R;
import com.wanjy.dannie.rivchat.data.StaticConfig;
import com.wanjy.dannie.rivchat.model.NotificationModel;
import com.wanjy.dannie.rivchat.model.User;
import com.wanjy.dannie.rivchat.util.ImageUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationAdabter extends RecyclerView.Adapter<NotificationAdabter.ViewHolder> {

    Context context;
    List<NotificationModel> MainUserUploadInfoList;
    private DatabaseReference notification_seenref;

    public NotificationAdabter(Context context, List<NotificationModel> TempList) {

        this.MainUserUploadInfoList = TempList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final NotificationModel UploadInfo = MainUserUploadInfoList.get(position);


        holder.UserID.setText(UploadInfo.getSenderId());
        holder.ID.setText(UploadInfo.getNoifId());
        holder.NotificationText.setText(UploadInfo.getNotifText());


        String dateValue=UploadInfo.getNotifDate();
        PrettyTime p = new PrettyTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Date date = new Date();
        try {
            date = sdf.parse(dateValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date.getTime();
        String datetime = p.format(new Date(millis));
        holder.date.setText(datetime);





        notification_seenref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if(snapshot.child(holder.ID.getText().toString()).hasChild(StaticConfig.UID)){
                    //holder.like_btn.setImageResource(R.mipmap.action_like_accent);
                }
                else{
                    // holder.like_btn.setImageResource(R.mipmap.action_like_gray);
                    notification_seenref.child(holder.ID.getText().toString()).child(StaticConfig.UID).setValue("seen");
                }
                holder.seenTxt.setText(String.valueOf(snapshot.child(holder.ID.getText().toString()).getChildrenCount()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference NameGet = ref.child("user").child(holder.UserID.getText().toString());
        NameGet.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.UserName.setText(user.getName());
                holder.UserTitle.setText(user.getTitle());
                holder.UserGroup.setText(user.getGroup());

                String img = user.getAvata();
                final Bitmap src;
                if (img.equals("default")) {
                    holder.UserimageView.setImageResource(R.drawable.user_profile);
                } else {
                    byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
                    src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.UserimageView.setImageDrawable(ImageUtils.roundedImage(context, src));
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });





    }


    @Override
    public int getItemCount() {

        return MainUserUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView UserName, UserGroup, seenTxt,date,UserTitle,NotificationText;
        ImageView UserimageView;
        TextView ID,UserID;


        public ViewHolder(View itemView) {
            super(itemView);

            notification_seenref=FirebaseDatabase.getInstance().getReference().child("SeenNotifications");
            notification_seenref.keepSynced(true);

            UserID = (TextView) itemView.findViewById(R.id.UserID);
            ID = (TextView) itemView.findViewById(R.id.ID);
            NotificationText = (TextView) itemView.findViewById(R.id.notification_content);


            UserimageView = (ImageView) itemView.findViewById(R.id.UserImageView);
            seenTxt = (TextView) itemView.findViewById(R.id.Seen_num);
            UserName = (TextView) itemView.findViewById(R.id.notification_username);
            date = (TextView) itemView.findViewById(R.id.comment_date);
            UserGroup = (TextView) itemView.findViewById(R.id.groupTxt);
            UserTitle = (TextView) itemView.findViewById(R.id.titleTxt);


        }
    }



}
