package com.wanjy.dannie.rivchat.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;

import com.google.firebase.database.*;
import android.support.v7.widget.*;
import android.app.*;
import java.util.*;

import com.wanjy.dannie.rivchat.R;
import com.wanjy.dannie.rivchat.model.*;
import android.content.*;
import android.util.*;
import android.widget.*;
import java.text.*;
import android.support.v4.widget.SwipeRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;
import android.graphics.Bitmap;
import com.wanjy.dannie.rivchat.util.ImageUtils;  
import android.util.Base64;
import android.graphics.BitmapFactory;
import com.google.android.gms.tasks.OnCompleteListener;
//import android.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import android.webkit.MimeTypeMap;
import android.net.Uri;
import java.io.IOException;
import android.provider.MediaStore;
import java.io.InputStream;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.google.android.gms.tasks.OnFailureListener;
import java.io.FileNotFoundException;

public class UserProfileFragment extends Fragment 
{

    TextView nameEdit,chaneName,emailEdit,passEdit,qrEdit,telegramTxt;
    ImageView UserImage;
    private Context context;
    private SharedPreferences sharedd;
    String imageUrl,group;
    CardView logout,changePass;
    int Image_Request_Code = 7;
    Uri FilePathUri;
    
    public UserProfileFragment() {  

    }  

    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
                             Bundle savedInstanceState) {  

        View view = inflater.inflate(com.wanjy.dannie.rivchat.R.layout.fragment_info, container, false);
        context = view.getContext();




        return inflater.inflate(R.layout.fragment_info, container, false);  

    }
    SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

           nameEdit = view.findViewById(R.id.nameEdit);
           emailEdit = view.findViewById(R.id.emailEdit);
           chaneName = view.findViewById(R.id.changeName);
           qrEdit = view.findViewById(R.id.qrEdit);
           passEdit = view.findViewById(R.id.passEdit);
           waitingDialog = new LovelyProgressDialog(context);
           logout = view.findViewById(R.id.logout);
           changePass = view.findViewById(R.id.changePass);
           UserImage = view.findViewById(R.id.userImage);
           telegramTxt = view.findViewById(R.id.telegramTxt);


        sharedd = context.getSharedPreferences("sharedUser", Activity.MODE_PRIVATE);
           

           checkImage();
           
           if(sharedd.getString("title","").equals("")||sharedd.getString("title","").isEmpty()||sharedd.getString("title","").length()<3){
               telegramTxt.setVisibility(View.GONE);
               telegramTxt.setEnabled(false);
            }else{
               telegramTxt.setVisibility(View.VISIBLE);
               telegramTxt.setEnabled(true);
           }


           telegramTxt.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   final AlertDialog dialog = new AlertDialog.Builder(context).create();
                   LayoutInflater inflater = getActivity().getLayoutInflater();
                   View convertView = (View) inflater.inflate(R.layout.dialog_edit, null);
                   dialog.setView(convertView);

                   final EditText nameEd = convertView.findViewById(R.id.ImageNameEditText);
                   final TextView questionTxt = convertView.findViewById(R.id.questionTxt);
                   final TextView titleTxtDialog = convertView.findViewById(R.id.titleTxtDialog);


                   Button accept = convertView.findViewById(R.id.acceptbtn);
                   Button close = convertView.findViewById(R.id.closebtn);

                   nameEd.setHint("ادخل معرف التيليجرام");
                   accept.setText("تغيرر المعرف");
                   questionTxt.setText("هل تريد اضافة رابط تواصل خاص بك ؟");
                   titleTxtDialog.setText("عرف التيليجرام");

                   accept.setOnClickListener(new View.OnClickListener(){
                       @Override
                       public void onClick(View p1)
                       {
                           String newName;
                           checkImage();
                           int length = nameEd.length();
                           String convert = String.valueOf(length);

                           if(length<3||!nameEd.getText().toString().contains("t.me")){
                               showMessage("ادخل رابط النيليجرام الخاص بك");
                           }else{
                               newName = nameEd.getText().toString();
                               DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                               mDatabase.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("telegram").setValue(newName);
                               sharedd.edit().putString("telegram", newName).commit();
                               showMessage("تم تغيير معرف التيليجرام");
                               dialog.dismiss();
                               checkImage();
                           }

                       }
                   });

                   close.setOnClickListener(new View.OnClickListener(){
                       @Override
                       public void onClick(View p1)
                       {
                           dialog.dismiss();
                       }
                   });



                   dialog.show();


               }
           });



        
        changePass.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1)
                {
                    final AlertDialog dialog = new AlertDialog.Builder(context).create();
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View convertView = (View) inflater.inflate(R.layout.dialog_password, null);
                    dialog.setView(convertView);

                    
                    Button accept = convertView.findViewById(R.id.acceptbtn);
                    Button close = convertView.findViewById(R.id.closebtn);


                    accept.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View p1)
                            {
                                dialog.dismiss();
                                FirebaseAuth.getInstance().sendPasswordResetEmail(emailEdit.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                showMessage("تفحص بريدك الالكتروني لتغيير كلمة المرور");
                                                
                                            }
                                        }
                                    });
                                checkImage();

                            }});

                    close.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View p1)
                            {
                                dialog.dismiss();
                            }                                                    
                        });



                    dialog.show();
                  
                                                
               }                             
            });  
                
                
            
            
        logout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1)
                {
                    final AlertDialog dialog = new AlertDialog.Builder(context).create();
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View convertView = (View) inflater.inflate(R.layout.dialog_logout, null);
                    dialog.setView(convertView);

                    
                    Button accept = convertView.findViewById(R.id.acceptbtn);
                    Button close = convertView.findViewById(R.id.closebtn);

                    accept.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View p1)
                            {
                                dialog.dismiss();
                                FirebaseAuth.getInstance().signOut();
                                sharedd.edit().putString("myid", "").commit();
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                getActivity().finish();

                            }});

                    close.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View p1)
                            {
                                dialog.dismiss();
                            }                                                    
                        });

                    dialog.show();


                }                             
            });
            
            
            
            
                
           
        chaneName.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1)
                {
                    
                    final AlertDialog dialog = new AlertDialog.Builder(context).create();
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View convertView = (View) inflater.inflate(R.layout.dialog_edit, null);
                    dialog.setView(convertView);

                    final EditText nameEd = convertView.findViewById(R.id.editTextName);
                    Button accept = convertView.findViewById(R.id.acceptbtn);
                    Button close = convertView.findViewById(R.id.closebtn);
                    
                    
                    accept.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View p1)
                            {
                              String newName;
                              int length = nameEd.length();

                                String convert = String.valueOf(length);
                              
                              if(length>20||length<5){
                                  showMessage("يجب ان لا يقل الاسم عن 5 احرف ولا يزيد عن 20 حرف");
                              }else{
                                  newName = nameEd.getText().toString();
                                  DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                  mDatabase.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("name").setValue(newName);
                                  sharedd.edit().putString("name", newName).commit();
                                  showMessage("تم تغيير الاسم بنجاح");
                                  dialog.dismiss();
                                  checkImage();
                              }
                                                            
                            }                                                
                           });
                    
                    close.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View p1)
                            {
                                dialog.dismiss();
                            }                                                    
                           });
                           
                           
                    
                    dialog.show();
                    
                 }});   
           
        UserImage.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1)
                {
                    new AlertDialog.Builder(context)
                        .setTitle("الصورة الشخصية")
                        .setMessage("هل انت متأكد أنك تريد تغيير الصورة")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_PICK);
                                startActivityForResult(Intent.createChooser(intent, "اختر الصورة"), Image_Request_Code);
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                }                               
               });
           
           
           
           
           
           
           
           
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("user");
        DatabaseReference uidRef = usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {


                    checkImage();                                  
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG", databaseError.getMessage());
            }
        };
        uidRef.addListenerForSingleValueEvent(eventListener);
        uidRef.keepSynced(true);
           
        
        
           

        super.onViewCreated(view, savedInstanceState);
    }

  public void checkImage(){
      nameEdit.setText(sharedd.getString("name",""));
      emailEdit.setText(sharedd.getString("email",""));
      passEdit.setText(sharedd.getString("password",""));
      qrEdit.setText(sharedd.getString("myid",""));//qrcode
      telegramTxt.setText(sharedd.getString("telegram",""));

      if(telegramTxt.getText().toString().isEmpty()){
          telegramTxt.setText("اضغط لاضافة معرف التيتلجرام");
      }else{
          telegramTxt.setText(sharedd.getString("telegram",""));

      }

      imageUrl = sharedd.getString("avata","");
      group = sharedd.getString("group","");

      if(imageUrl.equals("default")||imageUrl.equals("null")){
          UserImage.setImageResource(R.drawable.user_profile);
      }else{         
          final Bitmap src;         
          byte[] decodedString = Base64.decode(imageUrl, Base64.DEFAULT);
          src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
          UserImage.setImageDrawable(ImageUtils.roundedImage(context, src));          
              
      }
      
      
      
  }
    
    private void showMessage(String message) {
        //TODO: Make generic toast message
        Toast.makeText(context,message,Toast
                       .LENGTH_LONG).show();

    }
  
    private LovelyProgressDialog waitingDialog;
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            FilePathUri = data.getData();
         
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(data.getData());

                Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);
                imgBitmap = ImageUtils.cropToSquare(imgBitmap);
                InputStream is = ImageUtils.convertBitmapToInputStream(imgBitmap);
                final Bitmap liteImage = ImageUtils.makeImageLite(is,
                                                                  imgBitmap.getWidth(), imgBitmap.getHeight(),
                                                                  ImageUtils.AVATAR_WIDTH, ImageUtils.AVATAR_HEIGHT);

                String imageBase64 = ImageUtils.encodeBase64(liteImage);
                sharedd.edit().putString("avata", imageBase64).commit();

                waitingDialog.setCancelable(false)
                    .setTitle("جاري رفع الصوره.....")
                    .setTopColorRes(com.wanjy.dannie.rivchat.R.color.colorPrimary)
                    .show();

                    
                DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());                
                userDB.child("avata").setValue(imageBase64)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                waitingDialog.dismiss();                                
                                UserImage.setImageDrawable(ImageUtils.roundedImage(context, liteImage));
                                new LovelyInfoDialog(context)
                                    .setTopColorRes(com.wanjy.dannie.rivchat.R.color.colorPrimary)
                                    .setTitle("نجح")
                                    .setMessage("تم تغيير الصورة بنجاح")
                                    .show();
                                checkImage();

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waitingDialog.dismiss();
                            Log.d("Update Avatar", "failed");
                            new LovelyInfoDialog(context)
                                .setTopColorRes(com.wanjy.dannie.rivchat.R.color.colorAccent)
                                .setTitle("False")
                                .setMessage("False to update avatar")
                                .show();
                        }
                    });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            
            
    }

   }
    
    
    
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }
        
    
    
 }
