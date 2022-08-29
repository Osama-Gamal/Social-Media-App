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
import com.wanjy.dannie.rivchat.R;

import android.content.SharedPreferences;
import android.app.Activity;


public class AddBook extends AppCompatActivity {

    // Folder path for Firebase Storage.
    String Storage_Path = "Books_Uploads/";

    // Root Database Name for Firebase Database.
    public static final String Database_Path = "Books_Uploads";

    // Creating button.
    Button ChooseButton, UploadButton
	,ChooseBook;

    // Creating EditText.
    EditText ImageName, DesEditText,link_book;
	String Book_Url;

    public ImagesModel images;

    StorageReference storageReference;
    DatabaseReference databaseReference;


	int Book_Request_Code = 16;
	
    ProgressDialog progressDialog ;

	private Uri FilePathUriBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);



        
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
		
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("نشر مجلد");
		getSupportActionBar().setSubtitle("قم باختيار مجلد من الهاتف");
		
        
        UploadButton = (Button)findViewById(R.id.upload_book);
		ChooseBook = (Button)findViewById(R.id.pick_Book);
		
        ImageName = (EditText)findViewById(R.id.name_book);
		DesEditText = (EditText)findViewById(R.id.des_book);
		link_book = (EditText)findViewById(R.id.link_book);
        
        progressDialog = new ProgressDialog(AddBook.this);

        
        ChooseBook.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					
					Intent intent2 = new Intent();
                    // Setting intent type as image to select image from phone storage.
                    intent2.setType("application/pdf");
                    intent2.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent2, "من فضلك إختر الكتاب"), Book_Request_Code);
                    
				}
			});


		
			
        // Adding click listener to Upload image button.
        UploadButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

                    if(DesEditText.getText().toString().equals("")||ImageName.getText().toString().equals("")){
						
						Toast.makeText(getApplicationContext(),"أدخل وصف وإسم الكتاب",Toast.LENGTH_LONG)
						.show();
					}else{
                        if(link_book.getText().toString().isEmpty() && FilePathUriBook == null){
                            Toast.makeText(getApplicationContext(),"يجب عليك اختيار المجلد او وضع رابط صحيح",Toast.LENGTH_LONG).show(); 
                            
                           }else{
                               if(FilePathUriBook != null){
                                   UploadBookFileToFirebaseStorage();
                               }else{

                                   if(link_book.getText().toString().contains("firebasestorage")) {

                                       String TempImageName = ImageName.getText().toString().trim();
                                       Date date = new Date();
                                       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                                       String dateVal = sdf.format(date);

                                       final SharedPreferences shared = getApplicationContext().getSharedPreferences("shared", Activity.MODE_PRIVATE);
                                       String myid = shared.getString("not", "");

                                       String BookUploadId = databaseReference.push().getKey();

                                       ImageUploadInfo BooksUploadInfo = new ImageUploadInfo(
                                               TempImageName,
                                               "",
                                               BookUploadId,
                                               dateVal,
                                               link_book.getText().toString(),
                                               DesEditText.getText().toString(),
                                               myid,
                                               images);


                                       databaseReference.child(BookUploadId).setValue(BooksUploadInfo);
                                       Intent intent = new Intent(getApplicationContext(), Home.class);
                                       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                       startActivity(intent);
                                       finish();


                                   }else{
                                       Toast.makeText(getApplicationContext(), "يجب أن يكون الرابط من داخل التطبيق", Toast.LENGTH_LONG).show();

                                   }

                               }
                               
                           }
                    
                    
                           
					}
				}
			});


        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        		
		if (requestCode == Book_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            FilePathUriBook = data.getData();			
			Toast.makeText(AddBook.this,"تم إختيار الكتاب",Toast.LENGTH_LONG)
			.show();
        }
		
		
		
		
		
    }

	
	
	
	
	
	
	
	
	
	
    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        
		return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }
public String link;
    

	
	
	
	
	
	
	
	
	
	public void UploadBookFileToFirebaseStorage() {

        
        if (FilePathUriBook !=null) {

            progressDialog.setTitle("Book is Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            final StorageReference storageReference22nd = storageReference.child(Storage_Path + System.currentTimeMillis() +  ".pdf");
			
            storageReference22nd.putFile(FilePathUriBook)
				.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
					@Override
					public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

						
						String TempImageName = ImageName.getText().toString().trim();
						progressDialog.dismiss();

						Toast.makeText(getApplicationContext(), "Book Uploaded Successfully ", Toast.LENGTH_LONG).show();
						@SuppressWarnings("VisibleForTests")
						
                        Date date = new Date();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
						String dateVal = sdf.format(date);
						
						final SharedPreferences shared = getApplicationContext().getSharedPreferences("shared", Activity.MODE_PRIVATE);
						String myid=shared.getString("not","");
                        
						String BookUploadId = databaseReference.push().getKey();
                        
						ImageUploadInfo BooksUploadInfo = new ImageUploadInfo(
						TempImageName,
						"",
						BookUploadId,
						dateVal,
						taskSnapshot.getDownloadUrl().toString(),
						DesEditText.getText().toString(),
						myid,
                                images);
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
						
						
						databaseReference.child(BookUploadId).setValue(BooksUploadInfo);
					
						}
				})
				
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception exception) {
						
						progressDialog.dismiss();
						Toast.makeText(AddBook.this, exception.getMessage(), Toast.LENGTH_LONG).show();
					}
				})
				.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
					@Override
					public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
						double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        
						progressDialog.setTitle("Book is Uploading.."+(int) progress);

					}
				});
        }
        else {

            Toast.makeText(AddBook.this, "Select Book", Toast.LENGTH_LONG).show();

        }
    } 
	
	
	
	
	
	
	
}
