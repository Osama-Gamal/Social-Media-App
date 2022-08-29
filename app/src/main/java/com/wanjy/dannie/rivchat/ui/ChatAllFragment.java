package com.wanjy.dannie.rivchat.ui;

import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;  
import android.view.ViewGroup;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.Intent;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import java.io.IOException;
import com.google.firebase.database.FirebaseDatabase;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.support.v7.widget.RecyclerView;
import java.util.List;

import com.wanjy.dannie.rivchat.BuildConfig;
import com.wanjy.dannie.rivchat.Home;
import com.wanjy.dannie.rivchat.R;
import com.wanjy.dannie.rivchat.model.AppVersion;
import com.wanjy.dannie.rivchat.model.ChatFeatures;
import com.wanjy.dannie.rivchat.model.Comment;
import android.widget.ImageView;
import com.google.firebase.storage.StorageReference;
import android.net.Uri;
import android.app.ProgressDialog;
//import android.support.v7.appcompat.R;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.android.gms.tasks.OnSuccessListener;
import android.app.Activity;
import com.google.android.gms.tasks.OnFailureListener;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import java.util.ArrayList;
import com.google.firebase.database.DatabaseError;
import android.content.ContentResolver;
import android.webkit.MimeTypeMap;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.OnProgressListener;
import com.wanjy.dannie.rivchat.model.User;

import android.view.View.OnClickListener;
import java.io.File;
import android.database.Cursor;
import android.provider.OpenableColumns;
import android.widget.TextView;
import android.support.v4.content.CursorLoader;
import android.media.MediaPlayer;
import android.widget.SeekBar;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import java.util.concurrent.TimeUnit;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.MotionEvent;
import android.media.MediaScannerConnection;
import android.util.Log;
import android.content.ContentValues;
import android.media.AudioManager;
//import com.google.android.gms.measurement.R;




public class ChatAllFragment extends Fragment {  


    EditText editTextComment;
    ImageView btnAddMedia;
    ImageView btnAddComment,sound_play,voice_record;
    TextView pdf_name,pdf_size,sound_name,sound_size,sound_finalText,sound_startText;
    FirebaseDatabase firebaseDatabase;
    LinearLayout media,imagePlace,pdfPlace,soundPlace;
    RecyclerView RvComment;
    CommentAdapter commentAdapter;

    public static List<Comment> listComment;


    static String COMMENT_KEY = "Comment" ;
    private String RoomKey = "ChatAll";
    private ImageView addImage,imageViewer,addPdf,addSound;
    private int c =0;
    int Image_Request_Code = 7;
    int Pdf_Request_Code = 8;
    int Souund_Request_Code = 9;
    boolean image_exist=false;
    boolean pdf_exist=false;
    boolean sound_exist=false;
    SeekBar sound_progress;
    StorageReference storageReference;
    private Uri FilePathUri = null;
	private Uri FilePathUriBook = null;
    private Uri FilePathUriSound = null;
    ProgressDialog progressDialog ;
    String Storage_Path = "Comment_Images/";
    int finalTime;
    int startTime;
    TextView cancel_sound,cancel_pdf,cancel_image;
    SharedPreferences shared,sharedd;
    LinearLayout layoutMedia,layoutRecord,layoutSend;
	private Context contextt;
    TextView chatTimeTxt;
    long ChatTimeLong = 60;
    Handler handler;

    DatabaseReference commentReference,commentRef,chatFeatureRef;
    ChatFeatures chatFeatures;
    LinearLayout add_image_layout,add_sound_layout,add_book_layout;
    TextView noticeText;

    public static boolean switch_image,switch_sound,switch_book;


    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp3";
    private static final String AUDIO_RECORDER_FOLDER = "FCAI Beni Suef/Records";
    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4,             MediaRecorder.OutputFormat.THREE_GPP };
    private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP }; 
    /** Called when the activity is first created. */


    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();
    long timeCounter;
    

    public ChatAllFragment() {  

    }  

    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
                             Bundle savedInstanceState) {  

        View view = inflater.inflate(com.wanjy.dannie.rivchat.R.layout.fragment_chatall, container, false);
        contextt = view.getContext();










        return inflater.inflate(R.layout.fragment_chatall, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        commentReference = firebaseDatabase.getInstance().getReference(COMMENT_KEY);
        commentRef = firebaseDatabase.getInstance().getReference(COMMENT_KEY).child(RoomKey);

        commentReference.keepSynced(true);
        commentRef.keepSynced(true);


        shared = contextt.getSharedPreferences("shared", Activity.MODE_PRIVATE);
        sharedd = contextt.getSharedPreferences("sharedUser", Activity.MODE_PRIVATE);

        editTextComment = (EditText) view.findViewById(R.id.editWriteMessage);
        btnAddComment = (ImageView) view.findViewById(R.id.btnSend);
        RvComment=(RecyclerView)view.findViewById(R.id.recyclerChat);
        
        pdfPlace=(LinearLayout) view.findViewById(R.id.pdf_chossed);
        media=(LinearLayout) view.findViewById(R.id.media);
        btnAddMedia=(ImageView) view.findViewById(R.id.add);
        addImage=(ImageView)view.findViewById(R.id.add_image);
        imageViewer=(ImageView)view.findViewById(R.id.image_viewer);
        imagePlace=(LinearLayout) view.findViewById(R.id.image_place);
        soundPlace=(LinearLayout) view.findViewById(R.id.sound_place);
        sound_play=(ImageView)view.findViewById(R.id.sound_play);
        addPdf=(ImageView)view.findViewById(R.id.add_pdf);
        addSound=(ImageView)view.findViewById(R.id.add_sound);
        voice_record=(ImageView) view.findViewById(R.id.voice_record);
        pdf_name=(TextView)view.findViewById(R.id.pdf_name);
        pdf_size=(TextView)view.findViewById(R.id.pdf_size);
        sound_name=(TextView)view.findViewById(R.id.sound_name);
        sound_size=(TextView)view.findViewById(R.id.sound_size);
        sound_progress=(SeekBar)view.findViewById(R.id.sound_progress);
        sound_finalText=(TextView)view.findViewById(R.id.sound_finalText);
        sound_startText=(TextView)view.findViewById(R.id.sound_startText);

        chatTimeTxt=(TextView)view.findViewById(R.id.chatTimeTxt);
        layoutMedia = view.findViewById(R.id.layoutMedia);
        layoutRecord = view.findViewById(R.id.layoutRecord);
        layoutSend = view.findViewById(R.id.layoutSend);

        cancel_sound=(TextView)view.findViewById(R.id.cancel_sound);
        cancel_pdf=(TextView)view.findViewById(R.id.cancel_pdf);
        cancel_image=(TextView)view.findViewById(R.id.cancel_image);

        add_image_layout = view.findViewById(R.id.add_image_layout);
        add_sound_layout = view.findViewById(R.id.add_sound_layout);
        add_book_layout = view.findViewById(R.id.add_book_layout);
        noticeText = view.findViewById(R.id.noticeText);

        progressDialog = new ProgressDialog(contextt);
        storageReference = FirebaseStorage.getInstance().getReference();


        iniRvComment();

        soundPlace.setVisibility(View.GONE);
        pdfPlace.setVisibility(View.GONE);
        media.setVisibility(View.GONE);
        imagePlace.setVisibility(View.GONE);


        btnAddMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(c==0){
                        c=1;
                        media.setVisibility(View.VISIBLE);
                    }else{
                        c=0;
                        media.setVisibility(View.GONE);
                    }
                }
            });


        layoutRecord.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startRecording();
                        Toast.makeText(contextt,"جار التسجيل",Toast.LENGTH_LONG).show();
                       MediaPlayer mp = MediaPlayer.create(contextt, R.raw.recordbtn);
                        mp.start();
                        return true;
                    case MotionEvent.ACTION_UP:
                        stopRecording();
                        break;
                }
                return false;

            }
        });


        voice_record.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            startRecording();
                            Toast.makeText(contextt,"جار التسجيل",Toast.LENGTH_LONG).show();
                            MediaPlayer mp = MediaPlayer.create(contextt, R.raw.recordbtn);
                            mp.start();
                            return true;
                        case MotionEvent.ACTION_UP:
                            stopRecording();
                            break;
                    }
                    return false;
                }
            });
            
        addImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);

                }
            });
        addPdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();
                    intent.setType("application/pdf");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Please Select PDF"), Pdf_Request_Code);

                }
            });
        addSound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();
                    intent.setType("audio/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Please Select Sound"), Souund_Request_Code);

                }
            });
            
        cancel_sound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sound_exist = false;
                    FilePathUriSound = null;
                    soundPlace.setVisibility(View.GONE);
                    sound_play.setImageResource(R.drawable.play_audio);
                    if(filePlayer.isPlaying()) {
                        filePlayer.stop();
                    }
                    
                    
                    
                }
            });
            
        cancel_pdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pdf_exist = false;
                    FilePathUriBook = null;
                    pdfPlace.setVisibility(View.GONE);

                }
            });
            
        cancel_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    image_exist = false;
                    FilePathUri = null;
                    imagePlace.setVisibility(View.GONE);

                }
            });
            
            
            
            
            
       checkChatTime();
       
        btnAddComment.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View p1)
                {

                    if(editTextComment.getText().toString().equals("")&image_exist==false&pdf_exist==false&sound_exist==false){
                        Toast.makeText(contextt,"أدخل تعليقا من فضلك",Toast.LENGTH_LONG)
                            .show();
                    }else{

                        if(image_exist==true){
                            UploadImageFileToFirebaseStorage();
                        }else{
                             if(pdf_exist==true){
                                 UploadPdfFileToFirebaseStorage();
                             }else{
                                 if(sound_exist==true){
                                     UploadSoundFileToFirebaseStorage();
                                    }else{

                                     commentReference = firebaseDatabase.getInstance().getReference(COMMENT_KEY).child(RoomKey).push();

                            String comment_content = editTextComment.getText().toString();

                            String myid=shared.getString("not","");

                            Date date = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");


                            String comment_id=commentReference.getKey();
                            String dateval=sdf.format(date);
                                 Comment comment = new Comment(comment_content,myid,comment_id,dateval,"0","0","0","0","0","0","0");

                            commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        editTextComment.setText("");
                                        btnAddComment.setVisibility(View.VISIBLE);
                                        sharedd.edit().putLong("chatTime", System.currentTimeMillis()).commit();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(contextt,"fail to add comment : "+e.getMessage(),Toast.LENGTH_LONG)
                                            .show();
                                    }
                                });
}
                           }
                        }

                    }

                }
            });
       
       
      
		


        super.onViewCreated(view, savedInstanceState);
    }





    public void checkChatTime(){

        final int delay = 500;
        handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){




                if(System.currentTimeMillis()-sharedd.getLong("chatTime",0)>=60000){
                    chatTimeTxt.setVisibility(View.GONE);
                    layoutMedia.setVisibility(View.VISIBLE);
                    layoutRecord.setVisibility(View.VISIBLE);
                    layoutSend.setVisibility(View.VISIBLE);
                    editTextComment.setVisibility(View.VISIBLE);

                    if(image_exist==false&pdf_exist==false&sound_exist==false){
                        layoutRecord.setVisibility(View.VISIBLE);
                        layoutMedia.setVisibility(View.VISIBLE);
                    }else{
                        layoutRecord.setVisibility(View.GONE);
                        layoutMedia.setVisibility(View.GONE);
                    }


                }else{

                    chatTimeTxt.setVisibility(View.VISIBLE);
                    layoutMedia.setVisibility(View.GONE);
                    layoutRecord.setVisibility(View.GONE);
                    layoutSend.setVisibility(View.GONE);
                    editTextComment.setVisibility(View.GONE);
                    ChatTimeLong = System.currentTimeMillis()-sharedd.getLong("chatTime",0);

                    chatTimeTxt.setText(ChatTimeLong/1000+" / 60");

                }




                handler.postDelayed(this, delay);
            }
        }, delay);


    }



    public static List<Comment> listImages;
    public static List<Comment> listSounds;
    public static List<Comment> listPdfs;
    public static List<Comment> listLinks;

    private void iniRvComment() {

        RvComment.setLayoutManager(new LinearLayoutManager(contextt));
        noticeText.setVisibility(View.GONE);

        commentRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listComment = new ArrayList<>();
                    listImages = new ArrayList<>();
                    listSounds = new ArrayList<>();
                    listPdfs = new ArrayList<>();
                    listLinks = new ArrayList<>();

                    for (DataSnapshot snap:dataSnapshot.getChildren()) {
                        Comment comment = snap.getValue(Comment.class);
                        listComment.add(comment);



                        if(!comment.getImg().equals("0")){
                            listImages.add(comment);
                        }
                        if(!comment.getSound().equals("0")){
                            listSounds.add(comment);
                        }
                        if(!comment.getPdf().equals("0")){
                            listPdfs.add(comment);
                        }
                        if(comment.getContent().contains("http")||comment.getContent().contains("www")){
                            listLinks.add(comment);
                        }

                    }


                    commentAdapter = new CommentAdapter(contextt,listComment);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(contextt);
                    layoutManager.setStackFromEnd(true);//true
                    RvComment.setLayoutManager(layoutManager);
                    RvComment.setAdapter(commentAdapter);
                    RvComment.smoothScrollToPosition(commentAdapter.getItemCount());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        // Check if Chat features had been closed or not
        chatFeatureRef = FirebaseDatabase.getInstance().getReference("chatFeature");
        chatFeatureRef.keepSynced(true);

        chatFeatureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    chatFeatures = postSnapshot.getValue(ChatFeatures.class);

                    if(chatFeatures.getImage() == true){
                        add_image_layout.setAlpha(1f);
                        addImage.setEnabled(true);
                        noticeText.setVisibility(View.GONE);
                        switch_image = true;
                    }else{
                        add_image_layout.setAlpha(0.5f);
                        addImage.setEnabled(false);
                        noticeText.setVisibility(View.VISIBLE);
                        switch_image = false;
                    }

                    if(chatFeatures.getSound() == true){
                        add_sound_layout.setAlpha(1f);
                        addSound.setEnabled(true);
                        noticeText.setVisibility(View.GONE);
                        layoutRecord.setAlpha(1f);
                        layoutRecord.setEnabled(true);
                        voice_record.setEnabled(true);
                        switch_sound = true;
                    }else{
                        add_sound_layout.setAlpha(0.5f);
                        addSound.setEnabled(false);
                        noticeText.setVisibility(View.VISIBLE);
                        layoutRecord.setAlpha(0.5f);
                        layoutRecord.setEnabled(false);
                        voice_record.setEnabled(false);
                        switch_sound = false;
                    }

                    if(chatFeatures.getBook() == true){
                        add_book_layout.setAlpha(1f);
                        addPdf.setEnabled(true);
                        noticeText.setVisibility(View.GONE);
                        switch_book = true;
                    }else{
                        add_book_layout.setAlpha(0.5f);
                        addPdf.setEnabled(false);
                        noticeText.setVisibility(View.VISIBLE);
                        switch_book = false;
                    }
                }



            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







    }
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), FilePathUri);

                // Setting up bitmap selected image into ImageView.
                imageViewer.setImageBitmap(bitmap);

                image_exist=true;
                media.setVisibility(View.GONE);
                imagePlace.setVisibility(View.VISIBLE);

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
        
        if (requestCode == Souund_Request_Code && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {

            FilePathUriSound = data.getData();
            soundPlace.setVisibility(View.VISIBLE);
            media.setVisibility(View.GONE);
            sound_exist=true;
            //Toast.makeText(contextt,""+FilePathUriSound,Toast.LENGTH_LONG).show();
            
            
            Uri uri = data.getData();
            String uriString = uri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            String displayName = null;

            filePlayer =new MediaPlayer();
            filePlayer = MediaPlayer.create(contextt, data.getData());
            
            final int duration = filePlayer.getDuration();
            
            finalTime = filePlayer.getDuration();
            startTime = filePlayer.getCurrentPosition();
            sound_finalText.setText(String.format("%d min, %d sec",
                                      TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                                      TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                      TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) 
                                                                                                 finalTime)))
                        );

            sound_startText.setText(String.format("%d min, %d sec",
                                      TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                      TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                      TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) 
                                                                                                 startTime)))
                        );
                        
            sound_progress.setMax(duration/1000);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
            public void run(){         
                if (filePlayer != null) {
                    int mCurrentPosition = filePlayer.getCurrentPosition() / 1000;
                    sound_progress.setProgress(mCurrentPosition);
                    
                    startTime = filePlayer.getCurrentPosition();
                    sound_startText.setText(String.format("%d min, %d sec",
                                                          TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                                          TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                                          TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                                                     toMinutes((long) startTime))));
                }                                            
                handler.postDelayed(this, 100);
                }
                }, 100);
                
                
                
                
            sound_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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
           
            
            sound_play.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View p1)
                    {
                        if (filePlayer != null){
                            if(filePlayer.isPlaying()){
                            filePlayer.pause();
                            sound_play.setImageResource(R.drawable.play_audio);
                            }else{
                                filePlayer.start();
                                sound_play.setImageResource(R.drawable.pause_audio);
                                }
                           }
                        else{
                            Toast.makeText(contextt, "Empty", 1000).show();
                        }
                        
                    }           
                  });
            
            
            sound_name.setText("");
            sound_size.setText("");
        }
        
        
        if (requestCode == Pdf_Request_Code && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            FilePathUriBook = data.getData();

            Toast.makeText(contextt,"تم اختيار المجلد",Toast.LENGTH_LONG)
                .show();
            pdfPlace.setVisibility(View.VISIBLE);
            media.setVisibility(View.GONE);
            pdf_exist=true;
                
            Uri uri = data.getData();
            String uriString = uri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            String displayName = null;

            if (uriString.startsWith("content://")) {                   
                Cursor cursor = null;
                try {                           
                    cursor = getActivity().getContentResolver().query(uri, null, null, null, null);                         
                    if (cursor != null && cursor.moveToFirst()) {                               
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                        pdf_name.setText(displayName.toString());
                        Toast.makeText(contextt,""+sizeIndex,Toast.LENGTH_LONG)
                            .show();
                        pdf_size.setText(""+sizeIndex+" Mg");
                    }
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {           
                displayName = myFile.getName();
                pdf_name.setText(displayName);
            }
        
                
                
                
                
        }
        
        
        
    }
    
    
    
    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getActivity().getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }
    
    private String getFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);
        
        if(!file.exists()){
            file.mkdirs();
        }
        RecorderPath = file+ "/" + System.currentTimeMillis() + file_exts[currentFormat];
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
    }
    String RecorderPath;
    private void startRecording(){

        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run(){
                        timeCounter++;
                    }
                });
            }
        };
        mTimer1.schedule(mTt1, 1, 1000);

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(output_formats[currentFormat]);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(getFilename());
            recorder.setOnErrorListener(errorListener);
            recorder.setOnInfoListener(infoListener);
        
       /* Uri fille = Uri.fromFile(new File(getFilename()));
        Toast.makeText(contextt,""+fille,Toast.LENGTH_LONG).show();
        */


        try {
                recorder.prepare();
                recorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



    }





    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
           // AppLog.logString("Error: " + what + ", " + extra);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            //AppLog.logString("Warning: " + what + ", " + extra);
        }
    };
    
    
    MediaPlayer filePlayer;
    private void stopRecording(){

        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }

        if(null != recorder&&timeCounter>2){
            recorder.stop();
            recorder.reset();
            recorder.release();
            if(filePlayer!=null) {
                filePlayer.stop();
            }




            soundPlace.setVisibility(View.VISIBLE);
            media.setVisibility(View.GONE);
            sound_play.setImageResource(R.drawable.pause);
            sound_exist=true;
            
            Uri uri = Uri.fromFile(new File(RecorderPath));
            String uriString = uri.toString();
            //Toast.makeText(contextt,""+uriString,Toast.LENGTH_LONG).show();
            File myFile = new File(uriString);
           
            FilePathUriSound = uri;

            //final MediaPlayer filePlayer = MediaPlayer.create(contextt, data.getData());

            filePlayer =new MediaPlayer();
            try {
                filePlayer.setDataSource(RecorderPath);
                filePlayer.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }
            filePlayer.start();
            if(filePlayer.isPlaying()){
                //Toast.makeText(contextt,""+RecorderPath+"  Dura ; "+filePlayer.getDuration(),Toast.LENGTH_LONG).show();

            }
            
            
            final int duration = filePlayer.getDuration();

            finalTime = filePlayer.getDuration();
            startTime = filePlayer.getCurrentPosition();
            sound_finalText.setText(String.format("%d min, %d sec",
                                                  TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                                                  TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                                  TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) 
                                                                                                             finalTime)))
                                    );

            sound_startText.setText(String.format("%d min, %d sec",
                                                  TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                                  TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                                  TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) 
                                                                                                             startTime)))
                                    );

            sound_progress.setMax(duration/1000);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                    public void run(){         
                        if (filePlayer != null) {
                            int mCurrentPosition = filePlayer.getCurrentPosition() / 1000;
                            sound_progress.setProgress(mCurrentPosition);

                            startTime = filePlayer.getCurrentPosition();
                            sound_startText.setText(String.format("%d min, %d sec",
                                                                  TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                                                  TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                                                  TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                                                             toMinutes((long) startTime))));
                        }
                        handler.postDelayed(this, 100);
                    }
                }, 100);




            sound_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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

            sound_play.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View p1)
                    {
                        if (filePlayer != null){
                            if(filePlayer.isPlaying()){
                                filePlayer.pause();
                                sound_play.setImageResource(R.drawable.play_audio);
                            }else{
                                filePlayer.start();
                                sound_play.setImageResource(R.drawable.pause_audio);
                            }
                        }
                        else{
                            Toast.makeText(contextt, "Empty", 1000).show();
                        }

                    }           
                });


            
                sound_name.setText("");
                sound_size.setText("");

            
            recorder = null;

        }else{
            recorder.stop();
            recorder.reset();
            Toast.makeText(contextt,"يجب ان لا يقل التسجيل عن ثانيتين",Toast.LENGTH_LONG).show();
        }


    }
    
    
    
    public void UploadImageFileToFirebaseStorage() {


        if (FilePathUri != null) {
            progressDialog.setTitle("Image is Uploading...");
            progressDialog.show();

            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));


            storageReference2nd.putFile(FilePathUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        progressDialog.dismiss();
                        // Showing toast message after done uploading.
                        Toast.makeText(contextt, "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();


                        final SharedPreferences shared = contextt.getSharedPreferences("shared", Activity.MODE_PRIVATE);
                        String myid=shared.getString("not","");
                        @SuppressWarnings("VisibleForTests")

                            DatabaseReference commentReference = firebaseDatabase.getInstance().getReference(COMMENT_KEY).child(RoomKey).push();
                        String comment_content = editTextComment.getText().toString();
                        String uid = myid;

                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");


                        String comment_id=commentReference.getKey();
                        String dateval=sdf.format(date);
                        Comment comment = new Comment(comment_content,uid,comment_id,dateval,taskSnapshot.getDownloadUrl().toString(),"0","0","0","0","0","0");

                        commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(contextt,"Comment Added",Toast.LENGTH_LONG)
                                        .show();
                                    editTextComment.setText("");
                                    image_exist=false;
                                    imagePlace.setVisibility(View.GONE);
                                    sharedd.edit().putLong("chatTime", System.currentTimeMillis()).commit();

                                    btnAddComment.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(contextt,"fail to add comment : "+e.getMessage(),Toast.LENGTH_LONG)
                                        .show();
                                }
                            });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        // Hiding the progressDialog.
                        progressDialog.dismiss();

                        // Showing exception erro message.
                        Toast.makeText(contextt, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })

                // On progress change upload time.
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        // Setting progressDialog Title.
                        progressDialog.setTitle("Image is Uploading...");

                    }
                });
        }
    }


    public void UploadPdfFileToFirebaseStorage() {


        if ( FilePathUriBook!= null) {
            progressDialog.setTitle("Pdf is Uploading...");
            progressDialog.show();

            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUriBook));


            storageReference2nd.putFile(FilePathUriBook)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        progressDialog.dismiss();
                        // Showing toast message after done uploading.
                        Toast.makeText(contextt, "Pdf Uploaded Successfully ", Toast.LENGTH_LONG).show();


                        final SharedPreferences shared = contextt.getSharedPreferences("shared", Activity.MODE_PRIVATE);
                        String myid=shared.getString("not","");
                        @SuppressWarnings("VisibleForTests")

                            DatabaseReference commentReference = firebaseDatabase.getInstance().getReference(COMMENT_KEY).child(RoomKey).push();
                        String comment_content = editTextComment.getText().toString();
                        String uid = myid;

                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");


                        String comment_id=commentReference.getKey();
                        String dateval=sdf.format(date);
                        Comment comment = new Comment(comment_content,uid,comment_id,dateval,"0",taskSnapshot.getDownloadUrl().toString(),pdf_name.getText().toString(),pdf_size.getText().toString(),"0","0","0");

                        commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(contextt,"Comment Added",Toast.LENGTH_LONG)
                                        .show();
                                    editTextComment.setText("");
                                    pdf_exist=false;
                                    pdfPlace.setVisibility(View.GONE);
                                    sharedd.edit().putLong("chatTime", System.currentTimeMillis()).commit();

                                    btnAddComment.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(contextt,"fail to add comment : "+e.getMessage(),Toast.LENGTH_LONG)
                                        .show();
                                }
                            });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        // Hiding the progressDialog.
                        progressDialog.dismiss();

                        // Showing exception erro message.
                        Toast.makeText(contextt, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })

                // On progress change upload time.
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        // Setting progressDialog Title.
                        progressDialog.setTitle("Image is Uploading...");

                    }
                });
        }
    }
                        
    
    public void UploadSoundFileToFirebaseStorage() {


        if ( FilePathUriSound!= null) {
            progressDialog.setTitle("Sound is Uploading...");
            progressDialog.show();

            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUriSound));


            storageReference2nd.putFile(FilePathUriSound)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        progressDialog.dismiss();
                        // Showing toast message after done uploading.
                        Toast.makeText(contextt, "Sound Uploaded Successfully ", Toast.LENGTH_LONG).show();


                        final SharedPreferences shared = contextt.getSharedPreferences("shared", Activity.MODE_PRIVATE);
                        String myid=shared.getString("not","");
                        @SuppressWarnings("VisibleForTests")

                        DatabaseReference commentReference = firebaseDatabase.getInstance().getReference(COMMENT_KEY).child(RoomKey).push();
                        String comment_content = editTextComment.getText().toString();
                        String uid = myid;

                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

                        
                        String comment_id=commentReference.getKey();
                        String dateval=sdf.format(date);
                        Comment comment = new Comment(comment_content,uid,comment_id,dateval,"0","0","0","0",taskSnapshot.getDownloadUrl().toString(),sound_name.getText().toString(),sound_size.getText().toString());

                        commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(contextt,"Comment Added",Toast.LENGTH_LONG)
                                        .show();
                                    editTextComment.setText("");
                                    sound_exist=false;
                                    soundPlace.setVisibility(View.GONE);
                                    sharedd.edit().putLong("chatTime", System.currentTimeMillis()).commit();
                                    filePlayer.stop();
                                    btnAddComment.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(contextt,"fail to add comment : "+e.getMessage(),Toast.LENGTH_LONG)
                                        .show();
                                }
                            });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        // Hiding the progressDialog.
                        progressDialog.dismiss();

                        // Showing exception erro message.
                        Toast.makeText(contextt, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })

                // On progress change upload time.
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        // Setting progressDialog Title.
                        progressDialog.setTitle("Sound is Uploading...");

                    }
                });
        }
    }
    

}  
