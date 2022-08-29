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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wanjy.dannie.rivchat.data.StaticConfig;
import com.wanjy.dannie.rivchat.R;

import android.content.SharedPreferences;
import android.app.Activity;


public class AddPost extends AppCompatActivity {

    // Folder path for Firebase Storage.
    String Storage_Path = "All_Image_Uploads/";

    // Root Database Name for Firebase Database.
    public static final String Database_Path = "All_Image_Uploads_Database";

    // Creating button.
    Button ChooseButton, UploadButton;

    // Creating EditText.
    EditText ImageName ;

    // Creating ImageView.
    ImageView SelectImage;

    // Creating URI.
    Uri FilePathUri;

    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;
    DatabaseReference databaseReference;

    // Image request code for onActivityResult() .
    int Image_Request_Code = 7;
    public ImagesModel images;

    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Assign FirebaseStorage instance to storageReference.
        storageReference = FirebaseStorage.getInstance().getReference();

        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        //Assign ID'S to button.
        ChooseButton = (Button)findViewById(R.id.ButtonChooseImage);
        UploadButton = (Button)findViewById(R.id.ButtonUploadImage);

		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("نشر موضوع");
		getSupportActionBar().setSubtitle("قم باختيار صوره مع موضوعك");
        

        // Assign ID's to EditText.
        ImageName = (EditText)findViewById(R.id.ImageNameEditText);

        // Assign ID'S to image view.
        SelectImage = (ImageView)findViewById(R.id.ShowImageView);

        // Assigning Id to ProgressDialog.
        progressDialog = new ProgressDialog(AddPost.this);

        // Adding click listener to Choose image button.
        ChooseButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					// Creating intent.
					Intent intent = new Intent();

					// Setting intent type as image to select image from phone storage.
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);

				}
			});


        // Adding click listener to Upload image button.
        UploadButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

                    // Calling method to upload selected image on Firebase storage.
                    UploadImageFileToFirebaseStorage();

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
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {

            // Setting progressDialog Title.
            progressDialog.setTitle("Image is Uploading...");

            // Showing progressDialog.
            progressDialog.show();

            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
				.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
					@Override
					public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

						// Getting image name from EditText and store into string variable.
						String TempImageName = ImageName.getText().toString().trim();

						// Hiding the progressDialog after done uploading.
						progressDialog.dismiss();

                        images = new ImagesModel();
						images.setImage1("1");
                        images.setImage2("2");
                        images.setImage3("3");


                        // Showing toast message after done uploading.
						Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
						Date date = new Date();
						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
						String dateVal = sdf.format(date);
						
						String ImageUploadId = databaseReference.push().getKey();
                        
						final SharedPreferences shared = getApplicationContext().getSharedPreferences("shared", Activity.MODE_PRIVATE);
						String myid=shared.getString("not","");
						@SuppressWarnings("VisibleForTests")
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(
							TempImageName,
							taskSnapshot.getDownloadUrl().toString(),
							ImageUploadId,
							dateVal,
							"",
							"",
							myid,
                                images);
						
																				  
						
						
						
						// Adding image upload id s child element into databaseReference.
						databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
						
					}
				})
				// If something goes wrong .
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception exception) {

						// Hiding the progressDialog.
						progressDialog.dismiss();

						// Showing exception erro message.
						Toast.makeText(AddPost.this, exception.getMessage(), Toast.LENGTH_LONG).show();
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

            Toast.makeText(AddPost.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }
	
	
}
