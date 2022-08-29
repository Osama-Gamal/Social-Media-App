package com.wanjy.dannie.rivchat.ui;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import com.wanjy.dannie.rivchat.model.*;
import com.wanjy.dannie.rivchat.*;
import java.util.*;
import java.text.*;
import com.wanjy.dannie.rivchat.data.StaticConfig;
import android.content.SharedPreferences;
import android.app.Activity;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.LinearLayout;
import android.os.Handler;
import java.util.concurrent.TimeUnit;
import java.io.File;
import android.media.MediaPlayer;


public class AddSound extends AppCompatActivity {


    String Storage_Path = "SoundLecture/";
    public static final String Database_Path = "SoundsLecture";
    Button UploadButton
    ,ChooseSound;
    
    
    
    Uri FilePathUri;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    int Sound_Request_Code = 16;
    ProgressDialog progressDialog ;
    public List<String> images;

    EditText lecture_des,lecture_link;
    TextView sound_name,sound_size,sound_startText,sound_finalText;
    ImageView sound_play;
    SeekBar sound_progress;
    LinearLayout sound_place;
    boolean sound_exist = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_sound_lecture);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("نشر محاضرة");
        getSupportActionBar().setSubtitle("قم باختيار المحاضره مع نص توضيحي لمحتواها");
        
        
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        ChooseSound = (Button)findViewById(R.id.pick_lecture);
        UploadButton = (Button)findViewById(R.id.upload_lecture);
        
        progressDialog = new ProgressDialog(AddSound.this);
        
        
        
        lecture_des = (EditText) findViewById(R.id.des_lecture);
        lecture_link = (EditText) findViewById(R.id.link_lecture);
        
        sound_name=(TextView) findViewById(R.id.sound_name);
        sound_size=(TextView) findViewById(R.id.sound_size);        
        
        sound_play=(ImageView) findViewById(R.id.sound_play);
        sound_startText=(TextView) findViewById(R.id.sound_startText);
        sound_finalText=(TextView) findViewById(R.id.sound_finalText);
        sound_progress=(SeekBar) findViewById(R.id.sound_progress);
        sound_place=(LinearLayout) findViewById(R.id.sound_place);
        
        sound_place.setVisibility(View.GONE);
        
        
        
        
        
        
        
        
        
        
        
        ChooseSound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();
                    intent.setType("audio/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Please Select Sound"), Sound_Request_Code);
                    
                }
            });



        // Adding click listener to Upload image button.
        UploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(lecture_des.getText().toString().equals("")){

                        Toast.makeText(getApplicationContext(),"ادخل وصف المحاضرة",Toast.LENGTH_LONG)
                            .show();
                    }else{
                        if(FilePathUri == null && lecture_link.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(),"يجب عليك اختيار المحاضرة او وضع رابط صحيح",Toast.LENGTH_LONG).show();
                        }else{

                            if(FilePathUri !=null){
                                UploadSoundFileToFirebaseStorage();
                            }else {
                                if (lecture_link.getText().toString().contains("firebasestorage")) {

                                    progressDialog.setTitle("Sound is Uploading...");
                                    progressDialog.show();


                                    final SharedPreferences shared = getApplicationContext().getSharedPreferences("shared", Activity.MODE_PRIVATE);
                                    String myid = shared.getString("not", "");

                                    String desLec = lecture_des.getText().toString();

                                    Date date = new Date();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");


                                    String comment_id = databaseReference.push().getKey();
                                    String dateval = sdf.format(date);
                                    LectureModel lectureUp = new LectureModel("", desLec, dateval, lecture_link.getText().toString(), comment_id, myid, sound_size.getText().toString());

                                    databaseReference.child(comment_id).setValue(lectureUp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Sound Added", Toast.LENGTH_LONG)
                                                    .show();
                                            lecture_des.setText("");
                                            sound_exist = false;
                                            sound_place.setVisibility(View.GONE);
                                            Intent intent = new Intent(getApplicationContext(), Home.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "fail to add comment : " + e.getMessage(), Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    });


                                }else{
                                    Toast.makeText(getApplicationContext(), "يجب أن يكون الرابط من داخل التطبيق", Toast.LENGTH_LONG).show();
                                }



                            }
                            
                            
                        }
                        
                        
                        
                        
                    }
                }
            });



    }
    
    
    
    int finalTime;
    int startTime;
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Sound_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            if (requestCode == Sound_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

                FilePathUri = data.getData();
                sound_place.setVisibility(View.VISIBLE);
                
                sound_exist=true;
                Toast.makeText(AddSound.this,""+FilePathUri,Toast.LENGTH_LONG).show();


                Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
                String path = myFile.getAbsolutePath();
                String displayName = null;


                final MediaPlayer filePlayer = MediaPlayer.create(AddSound.this, data.getData());

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
                                Toast.makeText(getApplicationContext(), "Empty", 1000).show();
                            }

                        }           
                    });


                sound_name.setText("");
                sound_size.setText("");
                                
                                
                                
                                

}



    }


}





    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }


   
    public String link;
    public void UploadSoundFileToFirebaseStorage() {


        if ( FilePathUri!= null) {
            progressDialog.setTitle("Sound is Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));


            storageReference2nd.putFile(FilePathUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        progressDialog.dismiss();
                        // Showing toast message after done uploading.
                        Toast.makeText(getApplicationContext(), "Sound Uploaded Successfully ", Toast.LENGTH_LONG).show();


                        final SharedPreferences shared = getApplicationContext().getSharedPreferences("shared", Activity.MODE_PRIVATE);
                        String myid=shared.getString("not","");
                        @SuppressWarnings("VisibleForTests")

                       
                        
                        
                        String desLec = lecture_des.getText().toString();
                        
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");


                        String comment_id=databaseReference.push().getKey();
                        String dateval=sdf.format(date);
                        LectureModel lectureUp = new LectureModel("",desLec,dateval,taskSnapshot.getDownloadUrl().toString(),comment_id,myid,sound_size.getText().toString());

                        databaseReference.child(comment_id).setValue(lectureUp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(),"Sound Added",Toast.LENGTH_LONG)
                                        .show();
                                    lecture_des.setText("");
                                    sound_exist=false;
                                    sound_place.setVisibility(View.GONE);
                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();

                                    
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"fail to add comment : "+e.getMessage(),Toast.LENGTH_LONG)
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
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })

                // On progress change upload time.
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        final double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setTitle("Sound is Uploading..." +progress);

                    }
                });
        }
    }


}  
