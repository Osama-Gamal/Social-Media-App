package com.wanjy.dannie.rivchat.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.Query;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.wanjy.dannie.rivchat.data.StaticConfig;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
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
import android.media.MediaPlayer;
import java.io.IOException;
import android.util.Log;
import android.os.Handler;
import android.widget.SeekBar;
import java.util.concurrent.TimeUnit;
import android.view.ViewGroup.LayoutParams;
import android.media.AudioManager;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.graphics.drawable.GradientDrawable;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Intent;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import android.os.Environment;
import com.google.firebase.storage.FileDownloadTask;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.OnProgressListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnClickListener;
import butterknife.OnLongClick;
import com.wanjy.dannie.rivchat.model.User;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mData;
    private List<String> postKey;
    DatabaseReference comment_likeref,comment_seenref;
	private DatabaseReference userDB;
    private FirebaseAuth mAuth;
	boolean likechecker=false;
	String comment_key;
	PopupMenu popup;
    int finalTime,startTime;
    long clickDoqn,clickUp;
    
    String soundUrl="hjk";
    
    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.chat_row,parent,false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder holder, int position) {
		
        popup = new PopupMenu(mContext, holder.chat_place);
		popup.inflate(R.menu.menu_option);
		
        
		
		
        holder.tv_content.setText(mData.get(position).getContent());
        String date=mData.get(position).getDate();
        holder.user_id.setText(mData.get(position).getUid());
        
		
        holder.ID.setText(mData.get(position).getCommentId());
        holder.UserID.setText(mData.get(position).getUid());
        holder.Link1.setText(mData.get(position).getImg());
        holder.Link2.setText(mData.get(position).getPdf());
        holder.Link3.setText(mData.get(position).getSound());
        

        
		holder.comment_id.setText(mData.get(position).getCommentId());
		
		if(holder.Link1.getText().toString().equals("0")){
			holder.comment_image.setVisibility(View.GONE);
		}else{
			holder.comment_image.setVisibility(View.VISIBLE);
			Glide.with(mContext).load(holder.Link1.getText().toString()).into(holder.comment_image);	
            
            holder.comment_image.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View p1)
                    {
                        Intent go = new Intent(mContext,ImageViewerActivity.class);
                        go.putExtra("Link",holder.Link1.getText().toString());
                        go.putExtra("Text",holder.tv_content.getText().toString());
                        mContext.startActivity(go);
                    }                            
                });
		}
        
        
        if(holder.Link2.getText().toString().equals("0")){
            holder.pdf_shower.setVisibility(View.GONE);
        }else{
            holder.pdf_shower.setVisibility(View.VISIBLE);
            holder.pdf_name.setText(mData.get(position).getPdfName());
            holder.pdf_size.setText(mData.get(position).getPdfSize());
            
            holder.downloadBtn2.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View p1)
                    {
                        downloadfile(holder.Link2.getText().toString(),"pdf"+System.currentTimeMillis());
                    }                                    
                });
            
            
		}        
        
        if(holder.Link3.getText().toString().equals("0")){
            holder.sound_shower.setVisibility(View.GONE);
        }else{
            //holder.sound_place.getLayoutParams().width = LayoutParams.MATCH_PARENT;   
            holder.sound_shower.setVisibility(View.VISIBLE);
            holder.sound_name.setText(mData.get(position).getSound());
            holder.sound_size.setText(mData.get(position).getSoundSize());
            
            holder.downloadBtn1.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View p1)
                    {
                        downloadfile2(holder.Link3.getText().toString(),"Sound"+System.currentTimeMillis());
                    }                                    
                });
          /*  
            filePlayer = new MediaPlayer();
            filePlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
             
            try {
                filePlayer.setDataSource(holder.sound_name.getText().toString());
                
                filePlayer.setOnPreparedListener(new OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            
                            if(mp.isPlaying()){
                                mp.pause();
                                holder.sound_play.setImageResource(R.drawable.play_audio);
                            }else{
                                mp.start();
                                holder.sound_play.setImageResource(R.drawable.pause_audio);
                            }
                          }});  
                filePlayer.prepareAsync();
                
            } catch (IOException e) {
                Log.e("", "prepare() failed");
            }
            */
            
            
            
            final MediaPlayer filePlayer = new MediaPlayer();
            filePlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                filePlayer.setDataSource(holder.Link3.getText().toString());

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
                        
                       /* filePlayer = new MediaPlayer();
                        filePlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        */
                        
                        
                       /* if(soundUrl.equals(holder.sound_name.getText().toString())){                        
                        
                                                     
                               if(filePlayer.isPlaying()){
                                   filePlayer.pause();
                                   holder.sound_play.setImageResource(R.drawable.play_audio);
                               }else{
                                   filePlayer.start();
                                   holder.sound_play.setImageResource(R.drawable.pause_audio);
                               }


                           
                       
                       
                    }else{
                        try {
                            filePlayer.setDataSource(holder.sound_name.getText().toString());
                            //filePlayer.prepare();
                            filePlayer.setOnPreparedListener(new OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                      //  mp.start();
                                                                    
                                            if(mp.isPlaying()){
                                                mp.pause();
                                                holder.sound_play.setImageResource(R.drawable.play_audio);
                                            }else{
                                                mp.start();
                                                holder.sound_play.setImageResource(R.drawable.pause_audio);
                                            }
                                            
                                            
                                        soundUrl = holder.sound_name.getText().toString();
                                        Toast.makeText(mContext,""+soundUrl,Toast.LENGTH_LONG).show();
                                        
                                        
                                        
                                        
                                        
                                        
                                        
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
                                                
                                        
                                        
                                        
                                        
                                        
                                        
                                        
                                        
                                        
                                        
                                        
                                        
                                    }
                                });
                            filePlayer.prepareAsync();


                        } catch (IOException e) {
                            Log.e("", "prepare() failed");
                        }
                        
                    }    
                        
                        */    
                           
                          
                             
                
                        
                            
                        
                        
                        


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
                    
            }





        final SharedPreferences shared = mContext.getSharedPreferences("shared", Activity.MODE_PRIVATE);
        if(holder.UserID.getText().toString().equals(shared.getString("not",""))){

            GradientDrawable bgShape = (GradientDrawable)holder.chat_place.getBackground();
            bgShape.setStroke(2 ,Color.parseColor("#03A9F4"));
        }else{
            GradientDrawable bgShape = (GradientDrawable)holder.chat_place.getBackground();
            bgShape.setStroke(0 ,Color.parseColor("#03A9F4"));
        }

        holder.chat_place.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP){                        
                        GradientDrawable bgShape = (GradientDrawable)holder.chat_place.getBackground();
                        bgShape.setColor(Color.parseColor("#FFFFFFFF"));
                        
                        
                        clickUp=System.currentTimeMillis();
                        if(clickUp-clickDoqn>=300){
                           SharedPreferences sharedd = mContext.getSharedPreferences("sharedUser", Activity.MODE_PRIVATE);
                            
                            
                         if(holder.UserID.getText().toString().equals(sharedd.getString("myid",""))&&holder.Link1.getText().toString().equals("0")&&holder.Link2.getText().toString().equals("0")&&holder.Link3.getText().toString().equals("0")){
                            popup = new PopupMenu(mContext, holder.chat_place);
                            popup.inflate(R.menu.chat_user);
                            popup.show();                               
                           }else{
                             if(holder.UserID.getText().toString().equals(sharedd.getString("myid",""))&&!holder.Link1.getText().toString().equals("0")||!holder.Link2.getText().toString().equals("0")||!holder.Link3.getText().toString().equals("0")){
                                 popup = new PopupMenu(mContext, holder.chat_place);
                                 popup.inflate(R.menu.chat_user_link);
                                 popup.show();
                             }
                             else{
                                 if(!holder.UserID.getText().toString().equals(sharedd.getString("myid",""))&&holder.Link1.getText().toString().equals("0")&&holder.Link2.getText().toString().equals("0")&&holder.Link3.getText().toString().equals("0")) {
                                     ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
                                     ClipData clip = ClipData.newPlainText("label", holder.tv_content.getText().toString());
                                     clipboard.setPrimaryClip(clip);
                                     Toast.makeText(mContext,"تم النسخ",Toast.LENGTH_LONG).show();
                                 }else{
                                     if(!holder.UserID.getText().toString().equals(sharedd.getString("myid",""))&&!holder.Link1.getText().toString().equals("0")||!holder.Link2.getText().toString().equals("0")||!holder.Link3.getText().toString().equals("0")) {
                                         popup = new PopupMenu(mContext, holder.chat_place);
                                         popup.inflate(R.menu.chat_strange);
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
                                                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE); 
                                                ClipData clip = ClipData.newPlainText("label", holder.tv_content.getText().toString());
                                                clipboard.setPrimaryClip(clip);
                                                Toast.makeText(mContext,"تم النسخ",Toast.LENGTH_LONG).show();

                                                break;
                                            case R.id.delete:

                                                DatabaseReference deleteComment = FirebaseDatabase.getInstance().getReference().child("Comment").child("ChatAll").child(holder.ID.getText().toString());
                                                deleteComment.removeValue();

                                                break;
                                            case R.id.copylink:
                                                if(!holder.Link1.getText().toString().equals("0")){
                                                ClipboardManager clipboardd = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE); 
                                                ClipData clipp = ClipData.newPlainText("labell", holder.Link1.getText().toString());
                                                clipboardd.setPrimaryClip(clipp);
                                                Toast.makeText(mContext,"تم نسخ الرابط",Toast.LENGTH_LONG).show();
                                                }else{
                                                    if(!holder.Link2.getText().toString().equals("0")){
                                                        ClipboardManager clipboardd = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE); 
                                                        ClipData clipp = ClipData.newPlainText("labell", holder.Link2.getText().toString());
                                                        clipboardd.setPrimaryClip(clipp);
                                                        Toast.makeText(mContext,"تم نسخ الرابط",Toast.LENGTH_LONG).show();
                                                    }else{
                                                        ClipboardManager clipboardd = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE); 
                                                        ClipData clipp = ClipData.newPlainText("labell", holder.Link3.getText().toString());
                                                        clipboardd.setPrimaryClip(clipp);
                                                        Toast.makeText(mContext,"تم نسخ الرابط",Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                                
                                                                                                
                                                
                                                break;
                                                
                                                
                                                
                                        }
                                        return false;
                                    }
                                   
                                });
                                
                                                                                                                
                          
                          
                            
                           } 
                        
                        return true;
                    }
                    if(event.getAction() == MotionEvent.ACTION_DOWN){                        
                        GradientDrawable bgShape = (GradientDrawable)holder.chat_place.getBackground();
                        bgShape.setColor(Color.parseColor("#FFE6E6E6"));
                        clickDoqn=System.currentTimeMillis();
                        
                        
                        return true;
                    }
                    if(event.getAction() == MotionEvent.ACTION_CANCEL){                        
                        GradientDrawable bgShape = (GradientDrawable)holder.chat_place.getBackground();
                        bgShape.setColor(Color.parseColor("#FFFFFFFF"));

                        return true;
                    }
                    
                    
                    return false;
                }
            });
            
        
		
        

        

        
        
        
        
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference NameGet = ref.child("user").child(holder.UserID.getText().toString());
        NameGet.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    holder.tv_name.setText(user.getName());
                    holder.groupTxt.setText(user.getGroup());
                    holder.titleTxt.setText(user.getTitle());
                    
                    String img = user.getAvata();
                    final Bitmap src;
                    if (img.equals("default")) {
                        holder.img_user.setImageResource(R.drawable.user_profile);
                    } else {
                        byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
                        src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        holder.img_user.setImageDrawable(ImageUtils.roundedImage(mContext, src));
					}
                    
                    
                    
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
			});
        
        
     
            
            
            
		PrettyTime p = new PrettyTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Date datee = new Date();
        try {
            datee = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long millis = datee.getTime();
        String datetime = p.format(new Date(millis));
        holder.tv_date.setText(datetime);
			
			
		
		comment_seenref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    comment_key=holder.ID.getText().toString();

                    if(snapshot.child(comment_key).hasChild(StaticConfig.UID)){
                        //holder.like_btn.setImageResource(R.mipmap.action_like_accent);
                    }
                    else{
                       // holder.like_btn.setImageResource(R.mipmap.action_like_gray);
                        comment_seenref.child(comment_key).child(StaticConfig.UID).setValue("seen");
                    }
                    holder.seen_num.setText(String.valueOf(snapshot.child(comment_key).getChildrenCount()));
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
			});
		
		comment_likeref.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot snapshot) {
					comment_key=holder.ID.getText().toString();
					
					if(snapshot.child(comment_key).hasChild(StaticConfig.UID)){
						holder.like_btn.setImageResource(R.mipmap.action_like_accent);
					}
					else{
						holder.like_btn.setImageResource(R.mipmap.action_like_gray);
					}
					holder.tv_likenum.setText(String.valueOf(snapshot.child(comment_key).getChildrenCount()+" Likes"));
				}
				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});
		
		
		
		
		holder.like_btn.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					likechecker=true;

					comment_key=holder.ID.getText().toString();

					comment_likeref.addValueEventListener(new ValueEventListener() {
							@Override
							public void onDataChange(DataSnapshot snapshot) {

								if(likechecker){

									if(snapshot.child(comment_key).hasChild(StaticConfig.UID)){
										comment_likeref.child(comment_key).child(StaticConfig.UID).removeValue();
										likechecker=false;
									}
									else{
										comment_likeref.child(comment_key).child(StaticConfig.UID).setValue("liked");
										likechecker=false;
									}}
							}
							@Override
							public void onCancelled(DatabaseError databaseError) {

							}
						});

				}});
		
		
		
		
		
		
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        ImageView img_user,more_img,comment_image,like_btn,sound_play;
        TextView tv_name,tv_content,tv_date,tv_commentnum,tv_likenum,comment_id,user_id;
        TextView pdf_name,pdf_size,seen_num,sound_name,sound_size,sound_finalText,sound_startText;
        LinearLayout pdf_shower,sound_shower;
        LinearLayout sound_place,chat_place;
        SeekBar sound_progress;
        TextView Link1,Link2,Link3,ID,UserID,groupTxt,titleTxt;
        ImageView downloadBtn1,downloadBtn2;
        
        public CommentViewHolder(View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.comment_user_img);
            tv_name = itemView.findViewById(R.id.comment_username);
            tv_content = itemView.findViewById(R.id.comment_content);
            //tv_content.setMovementMethod(LinkMovementMethod.getInstance());

            tv_date = itemView.findViewById(R.id.comment_date);
			tv_commentnum=itemView.findViewById(R.id.comment_commentnum);
			tv_likenum=itemView.findViewById(R.id.comment_like_count);
			comment_image=itemView.findViewById(R.id.commentt_rowImageView);
			comment_id=itemView.findViewById(R.id.comment_id);
			like_btn=itemView.findViewById(R.id.comment_like_btn);
            pdf_shower=itemView.findViewById(R.id.pdf_shower);
            pdf_name=itemView.findViewById(R.id.pdf_name);
            pdf_size=itemView.findViewById(R.id.pdf_size);
            sound_shower=itemView.findViewById(R.id.sound_place);
            sound_name=itemView.findViewById(R.id.sound_name);
            sound_size=itemView.findViewById(R.id.sound_size);
            seen_num=itemView.findViewById(R.id.Seen_num);
            user_id=itemView.findViewById(R.id.user_id);
			sound_play=itemView.findViewById(R.id.sound_play);
            sound_startText=itemView.findViewById(R.id.sound_startText);
            sound_finalText=itemView.findViewById(R.id.sound_finalText);
            sound_progress=itemView.findViewById(R.id.sound_progress);
            sound_place=itemView.findViewById(R.id.sound_place);
            chat_place=itemView.findViewById(R.id.chat_place);
            groupTxt = (TextView) itemView.findViewById(R.id.groupTxt);
            titleTxt = (TextView) itemView.findViewById(R.id.titleTxt);
            
            Link1 = (TextView) itemView.findViewById(R.id.Link1);
            Link2 = (TextView) itemView.findViewById(R.id.Link2);
            Link3 = (TextView) itemView.findViewById(R.id.Link3);
            ID = (TextView) itemView.findViewById(R.id.ID);
            UserID = (TextView) itemView.findViewById(R.id.UserID);
            
            downloadBtn1 = (ImageView) itemView.findViewById(R.id.download_btn1);
            downloadBtn2 = (ImageView) itemView.findViewById(R.id.download_btn2);
            
            
			//more_img = itemView.findViewById(R.id.morebtn);
			comment_image.setVisibility(View.GONE);
			
			
			final SharedPreferences shared = mContext.getSharedPreferences("shared", Activity.MODE_PRIVATE);
			StaticConfig.UID=shared.getString("not","");
			
			
			comment_likeref=FirebaseDatabase.getInstance().getReference().child("LikesComments");
			comment_likeref.keepSynced(true);
            
			comment_seenref=FirebaseDatabase.getInstance().getReference().child("SeenComments");
			comment_seenref.keepSynced(true);
            
            
            
            

            
            
            
            
            
            
            
            
            
			
			}
    }

	
	
	


    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm",calendar).toString();
        return date;


    }
    
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 78;

    private void downloadfile(String Link,String name) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(Link);
        Toast.makeText(mContext, "جاري التحميل", Toast.LENGTH_SHORT).show();


        final File rootPath = new File(Environment.getExternalStorageDirectory(), "FCAI Beni Suef/Pdf");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath, name+".pdf");
        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener <FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.e("firebase ", ";local tem file created  created " + localFile.toString());

                    if (localFile.canRead()){

                    }

                    Toast.makeText(mContext, "تم التحميل", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(context, "Internal storage/MADBO/Nature.jpg", Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                    Toast.makeText(mContext, "Download Incompleted" + "//" + exception, Toast.LENGTH_LONG).show();
                }

            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    final double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder = new NotificationCompat.Builder(mContext);
                    mBuilder.setContentTitle("تحميل الملف")
                        .setContentText("جاري التحميل...")
                        .setSmallIcon(R.drawable.pdf_icon)
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
    
    private NotificationManager mNotifyManager2;
    private NotificationCompat.Builder mBuilder2;
    int id2 = 167;

    private void downloadfile2(String Link,String name) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(Link);
        Toast.makeText(mContext, "جاري التحميل", Toast.LENGTH_SHORT).show();


        final File rootPath = new File(Environment.getExternalStorageDirectory(), "FCAI Beni Suef/Records");
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

                    Toast.makeText(mContext, "تم التحميل", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(context, "Internal storage/MADBO/Nature.jpg", Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                    Toast.makeText(mContext, "Download Incompleted" + "//" + exception, Toast.LENGTH_LONG).show();
                }

            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    final double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    mNotifyManager2 = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder2 = new NotificationCompat.Builder(mContext);
                    mBuilder2.setContentTitle("تحميل الصوت")
                        .setContentText("جاري التحميل...")
                        .setSmallIcon(R.drawable.voicesearch)
                        .setOngoing(true);

                    new Thread(
                        new Runnable() {
                            @Override
                            public void run() {

                                mBuilder2.setProgress(100, (int) progress, false);                                    
                                mNotifyManager2.notify(id2, mBuilder2.build());
                                try {
                                    // Sleep for 1 second
                                    Thread.sleep(1*1000);
                                } catch (InterruptedException e) {
                                    Log.d("TAG", "sleep failure");
                                }

                                if((int)progress==100){
                                    mBuilder2.setContentText("تم التحميل")                                
                                        .setProgress(0,0,false).setOngoing(false);

                                    mNotifyManager2.notify(id2, mBuilder2.build());
                                }
                            }
                        }
                    ).start();

                }
            });
    }
    
    

}
