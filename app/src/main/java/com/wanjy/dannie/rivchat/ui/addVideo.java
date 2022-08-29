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


public class addVideo extends AppCompatActivity {

    // Folder path for Firebase Storage.
    String Storage_Path = "Video_Uploads/";

    // Root Database Name for Firebase Database.
    public static final String Database_Path = "Video_Uploads";

    // Creating button.
    Button ChooseButton, UploadButton
	,ChooseBook;

    // Creating EditText.
    EditText ImageName, DesEditText;
	String img,Book_Url;

    ImageView SelectImage;


    Uri FilePathUri;


    StorageReference storageReference;
    DatabaseReference databaseReference;


    int Image_Request_Code = 7;
	int Book_Request_Code = 16;
	public ImagesModel images;

    ProgressDialog progressDialog ;

	private Uri FilePathUriBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);


        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);



        ChooseButton = (Button)findViewById(R.id.ButtonChooseImage);
        UploadButton = (Button)findViewById(R.id.ButtonUploadImage);
		//ChooseBook = (Button)findViewById(R.id.ButtonChooseBook);

        ImageName = (EditText)findViewById(R.id.ImageNameEditText);
		//DesEditText = (EditText)findViewById(R.id.ImageDesEditText);

        SelectImage = (ImageView)findViewById(R.id.ShowImageView);


        progressDialog = new ProgressDialog(addVideo.this);


        ChooseButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {


					Intent intent = new Intent();


					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);

				}
			});


		ChooseBook.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					// Creating intent.
					Intent intent2 = new Intent();

					// Setting intent type as image to select image from phone storage.
					intent2.setType("video/*");
					intent2.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent2, "من فضلك اختر الفيديو"), Book_Request_Code);

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
						UploadImageFileToFirebaseStorage();
					}
				}
			});



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);

                // Setting up bitmap selected image into ImageView.
                SelectImage.setImageBitmap(bitmap);

                // After selecting image change choose button above text.
                ChooseButton.setText("Image Selected");

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }


		if (requestCode == Book_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUriBook = data.getData();

			Toast.makeText(addVideo.this,"تم اختيار الفيديو",Toast.LENGTH_LONG)
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
    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {

            progressDialog.setTitle("Image is Uploading...");
            progressDialog.show();

            // Creating second StorageReference.
            final StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

            storageReference2nd.putFile(FilePathUri)
				.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
					@Override
					public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


						progressDialog.dismiss();


						@SuppressWarnings("VisibleForTests")


							String iimg =taskSnapshot.getDownloadUrl().toString();

						UploadBookFileToFirebaseStorage(iimg);

					}
				})
				// If something goes wrong .
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception exception) {

						// Hiding the progressDialog.
						progressDialog.dismiss();

						// Showing exception erro message.
						Toast.makeText(addVideo.this, exception.getMessage(), Toast.LENGTH_LONG).show();
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
        else {

            Toast.makeText(addVideo.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }











	public void UploadBookFileToFirebaseStorage(final String url) {


        if (FilePathUriBook !=null) {

            progressDialog.setTitle("جاري رفع الفيديو");
            progressDialog.show();
///////////
            final StorageReference storageReference22nd = storageReference.child(Storage_Path + System.currentTimeMillis() +  "_video");

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
						
						ImageUploadInfo BooksUploadInfo = new ImageUploadInfo(
						TempImageName,
						url,
						databaseReference.child(databaseReference.push().getKey()).push().getKey(),
						dateVal,
						taskSnapshot.getDownloadUrl().toString(),
						DesEditText.getText().toString(),
						myid,
								images);


						String BookUploadId = databaseReference.push().getKey();
						databaseReference.child(BookUploadId).setValue(BooksUploadInfo);

					}
				})

				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception exception) {

						progressDialog.dismiss();
						Toast.makeText(addVideo.this, exception.getMessage(), Toast.LENGTH_LONG).show();
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

            Toast.makeText(addVideo.this, "Select Book", Toast.LENGTH_LONG).show();

        }
    } 







}
