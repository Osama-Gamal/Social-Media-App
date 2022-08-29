package com.wanjy.dannie.rivchat.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
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
import android.os.Handler;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.media.MediaPlayer.OnPreparedListener;
import java.io.IOException;
import android.util.Log;
import java.util.concurrent.TimeUnit;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.wanjy.dannie.rivchat.util.ImageUtils;
import android.util.Base64;
import com.wanjy.dannie.rivchat.data.StaticConfig;
import android.app.NotificationManager;
//import android.support.v7.app.NotificationCompat;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.app.ProgressDialog;
import java.io.File;
import android.os.Environment;
import android.widget.PopupMenu;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.OnProgressListener;
//import android.annotation.NonNull;


public class LectureAdabter extends RecyclerView.Adapter<LectureAdabter.ViewHolder> {

    Context context;
    List<LectureModel> MainLectureUploadInfoList;
    int finalTime,startTime;
    private DatabaseReference lecture_seenref;
    PopupMenu popup;

    String post_key,lecture_url,publisherId;

    public LectureAdabter(Context context, List<LectureModel> TempList) {

        this.MainLectureUploadInfoList = TempList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sound_lecture, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        LectureModel UploadInfo = MainLectureUploadInfoList.get(position);
        
        holder.lectureName.setText(UploadInfo.getLecture());
        post_key=UploadInfo.getLectureId();
        publisherId = UploadInfo.getPublisherId();
        holder.des.setText(UploadInfo.getDesLecture());
        holder.lectureSize.setText(UploadInfo.getLectureSize());
        
        holder.Link.setText(UploadInfo.getLecture());
        holder.ID.setText(UploadInfo.getLectureId());
        holder.UserID.setText(UploadInfo.getPublisherId());
        
        
        String dateValue=UploadInfo.getDateLecture();
        lecture_url = UploadInfo.getLecture();
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
        holder.datev.setText(datetime);






        final MediaPlayer filePlayer = new MediaPlayer();
        filePlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            filePlayer.setDataSource(holder.lectureName.getText().toString());
            filePlayer.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //mp.start();


                }});
            filePlayer.prepareAsync();

        } catch (IOException e) {
            Log.e("", "prepare() failed");
        }



        holder.sound_play.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1)
                {


                    if(filePlayer.isPlaying()){
                        filePlayer.pause();
                        holder.sound_play.setImageResource(R.drawable.play_audio);
                    }else{
                        filePlayer.start();
                        holder.sound_play.setImageResource(R.drawable.pause_audio);
                    }
                        
                    final int duration = filePlayer.getDuration();

                    finalTime = filePlayer.getDuration();
                    startTime = filePlayer.getCurrentPosition();
                    holder.sound_finalText.setText(String.format("%d min, %d sec",
                                                                 TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                                                                 TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                                                 TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) 
                                                                                                                            finalTime)))
                                                   );

                    holder.sound_startText.setText(String.format("%d min, %d sec",
                                                                 TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                                                 TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                                                 TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) 
                                                                                                                            startTime)))
                                                   );

                    holder.sound_progress.setMax(duration/1000);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable(){
                            public void run(){         
                                if (filePlayer != null) {
                                    int mCurrentPosition = filePlayer.getCurrentPosition() / 1000;
                                    holder.sound_progress.setProgress(mCurrentPosition);

                                    startTime = filePlayer.getCurrentPosition();
                                    holder.sound_startText.setText(String.format("%d min, %d sec",
                                                                                 TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                                                                 TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                                                                 TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                                                                            toMinutes((long) startTime))));
                                }                                            
                                handler.postDelayed(this, 100);
                            }
                        }, 100);




                    holder.sound_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {                
                                if(filePlayer != null && fromUser){
                                    filePlayer.seekTo(progress * 1000);
                                }
                            }
                        });



                }});





        holder.moreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final SharedPreferences sharedd = context.getSharedPreferences("sharedUser", Activity.MODE_PRIVATE);

                if ((sharedd.getString("title", "").contains("مندوب") || sharedd.getString("title", "").contains("مندوبة") || sharedd.getString("title", "").contains("المبرمج") || sharedd.getString("title", "").contains("المشرف") || sharedd.getString("title", "").contains("المشرفة")) && holder.Link.getText().toString().equals("0")) {
                  /*  popup = new PopupMenu(context, holder.moreImage);
                    popup.inflate(R.menu.post_manager);
                    popup.show();
*/
                } else {
                    if ((sharedd.getString("title", "").contains("مندوب") || sharedd.getString("title", "").contains("مندوبة") || sharedd.getString("title", "").contains("المبرمج") || sharedd.getString("title", "").contains("المشرف") || sharedd.getString("title", "").contains("المشرفة")) && !holder.Link.getText().toString().equals("0")) {
                        popup = new PopupMenu(context, holder.moreImage);
                        popup.inflate(R.menu.media_menu_manager);
                        popup.show();

                    } else {
                        if ((!sharedd.getString("title", "").contains("مندوب") || !sharedd.getString("title", "").contains("مندوبة") || !sharedd.getString("title", "").contains("المبرمج") || !sharedd.getString("title", "").contains("المشرف") || !sharedd.getString("title", "").contains("المشرفة")) && holder.Link.getText().toString().equals("0")) {
                          /*  ClipboardManager clipboardd = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                            ClipData clipp = ClipData.newPlainText("labell", holder.imageNameTextView.getText().toString());
                            clipboardd.setPrimaryClip(clipp);
                            Toast.makeText(context, "تم نسخ الصورة", Toast.LENGTH_LONG).show();
*/
                        } else {
                            if ((!sharedd.getString("title", "").contains("مندوب") || !sharedd.getString("title", "").contains("مندوبة") || !sharedd.getString("title", "").contains("المبرمج") || !sharedd.getString("title", "").contains("المشرف") || !sharedd.getString("title", "").contains("المشرفة")) && !holder.Link.getText().toString().equals("0")) {
                                popup = new PopupMenu(context, holder.moreImage);
                                popup.inflate(R.menu.media_menu_strange);
                                popup.show();


                            }

                        }
                    }
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.copy:
                                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("labelo", holder.des.getText().toString());
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(context, "تم النسخ", Toast.LENGTH_LONG).show();

                                break;
                            case R.id.delete:
                                DatabaseReference deleteComment = FirebaseDatabase.getInstance().getReference().child("SoundsLecture").child(holder.ID.getText().toString());
                                deleteComment.removeValue();
                                break;
                            case R.id.copylink:

                                ClipboardManager clipboardd = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                                ClipData clipp = ClipData.newPlainText("labelo", holder.lectureName.getText().toString());
                                clipboardd.setPrimaryClip(clipp);
                                Toast.makeText(context, "تم النسخ", Toast.LENGTH_LONG).show();

                                break;
                            case R.id.change:


                                final AlertDialog dialog = new AlertDialog.Builder(context).create();
                                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View convertView = (View) inflater.inflate(R.layout.dialog_edit_post, null);
                                dialog.setView(convertView);
                                dialog.setCancelable(false);

                                final EditText ImageNameEditText = convertView.findViewById(R.id.ImageNameEditText);
                                Button accept = convertView.findViewById(R.id.acceptbtn);
                                Button close = convertView.findViewById(R.id.closebtn);

                                ImageNameEditText.setText(holder.des.getText().toString());

                                accept.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View p1)
                                    {
                                        dialog.dismiss();

                                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                        mDatabase.child("SoundsLecture").child(holder.ID.getText().toString()).child("desLecture").setValue(ImageNameEditText.getText().toString());
                                        holder.des.setText(ImageNameEditText.getText().toString());
                                        Toast.makeText(context,"تم تغيير النص بنجاح",Toast.LENGTH_LONG).show();

                                    }});

                                close.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View p1)
                                    {
                                        dialog.dismiss();


                                    }
                                });

                                dialog.show();


                                break;


                        }
                        return false;
                    }
                });




            }
        });







        lecture_seenref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    
                    if(snapshot.child(holder.ID.getText().toString()).hasChild(StaticConfig.UID)){
                        //holder.like_btn.setImageResource(R.mipmap.action_like_accent);
                    }
                    else{
                        // holder.like_btn.setImageResource(R.mipmap.action_like_gray);
                        lecture_seenref.child(holder.ID.getText().toString()).child(StaticConfig.UID).setValue("seen");
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
                    holder.publiser_name.setText(user.getName());                    
                    holder.titleTxt.setText(user.getTitle());

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
        
        
        
        
        
        
        holder.downloadBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1)
                {
                    downloadfile(lecture_url,"محاضرة"+System.currentTimeMillis());

                }                            
            });
        
        

    }


    @Override
    public int getItemCount() {

        return MainLectureUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView UserimageView,sound_play,downloadBtn,moreImage;
        public TextView lectureName,datev,des,seenTxt,sound_startText,sound_finalText,lectureSize,publiser_name;
        SeekBar sound_progress;
        
        TextView Link,ID,UserID,titleTxt;
        
        public ViewHolder(View itemView) {
            super(itemView);

            
            lecture_seenref=FirebaseDatabase.getInstance().getReference().child("SeenComments");
			lecture_seenref.keepSynced(true);
            
            Link = (TextView) itemView.findViewById(R.id.Link);
            ID = (TextView) itemView.findViewById(R.id.ID);
            UserID = (TextView) itemView.findViewById(R.id.UserID);
            
            UserimageView = (ImageView) itemView.findViewById(R.id.blog_user_image);
            moreImage = (ImageView) itemView.findViewById(R.id.moreImage);

            lectureName = (TextView) itemView.findViewById(R.id.sound_name);
            datev = (TextView) itemView.findViewById(R.id.date);
            des = (TextView) itemView.findViewById(R.id.desLecture);
            publiser_name = (TextView) itemView.findViewById(R.id.blog_user_namee);
            seenTxt = (TextView) itemView.findViewById(R.id.Seen_num);
            titleTxt = (TextView) itemView.findViewById(R.id.titleTxt);
            
            sound_play=itemView.findViewById(R.id.sound_play);
            sound_startText=itemView.findViewById(R.id.sound_startText);
            sound_finalText=itemView.findViewById(R.id.sound_finalText);
            sound_progress=itemView.findViewById(R.id.sound_progress);
            lectureSize=itemView.findViewById(R.id.sound_size);

            downloadBtn = itemView.findViewById(R.id.download_btn);
            
            
            
        }
    }


    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 16;

    private void downloadfile(String Link,String name) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(Link);
        Toast.makeText(context, "جاري التحميل", Toast.LENGTH_SHORT).show();
        
        
        final File rootPath = new File(Environment.getExternalStorageDirectory(), "FCAI Beni Suef/Sounds");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath, name+".mp3");
        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener <FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.e("firebase ", ";local tem file created  created " + localFile.toString());

                    if (localFile.canRead()){

                        
                    }

                    Toast.makeText(context, "تم التحميل", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(context, "Internal storage/MADBO/Nature.jpg", Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                    Toast.makeText(context, "Download Incompleted" + "//" + exception, Toast.LENGTH_LONG).show();
                }

            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    final double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    
                    mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder = new NotificationCompat.Builder(context);
                    mBuilder.setContentTitle("تحميل المحاضرة")
                        .setContentText("جاري التحميل...")
                        .setSmallIcon(R.drawable.voicesearch)
                        .setOngoing(true);

                    new Thread(
                        new Runnable() {
                            @Override
                            public void run() {

                                mBuilder.setProgress(100, (int) progress, false);                                    
                                mNotifyManager.notify(id, mBuilder.build());
                                try {
                                    // Sleep for 1 second
                                    Thread.sleep(1*1000);
                                } catch (InterruptedException e) {
                                    Log.d("TAG", "sleep failure");
                                }

                                if((int)progress==100){
                                mBuilder.setContentText("تم التحميل")                                
                                    .setProgress(0,0,false).setOngoing(false);

                                mNotifyManager.notify(id, mBuilder.build());
                                }
                            }
                        }
                    ).start();

                }
            });
    }
  
    
    
    


}
