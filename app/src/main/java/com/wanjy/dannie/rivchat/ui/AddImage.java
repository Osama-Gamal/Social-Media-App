package com.wanjy.dannie.rivchat.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wanjy.dannie.rivchat.Home;
import com.wanjy.dannie.rivchat.R;
import com.wanjy.dannie.rivchat.model.ImageUploadInfo;
import com.wanjy.dannie.rivchat.model.ImagesModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.nashapp.androidsummernote.Summernote;

public class AddImage extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private Button mSelectBtn,UploadBtn;
    private RecyclerView mUploadList;

    private List<String> fileNameList;
    private List<String> fileDoneList;

    ProgressDialog progressDialog ;

    DatabaseReference databaseReference;
    public static final String Database_Path = "All_Image_Uploads_Database";


    int count = 0;
    EditText imageName;
    private UploadListAdapter uploadListAdapter;
    private StorageReference mStorage;
    String fileName;
    Uri fileUri;
    int finalI;
    int totalItemsSelected;
    Intent da = new Intent();
    public ImagesModel images;
    int chekUpload = 0;
    Summernote summernote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("نشر صور");
        getSupportActionBar().setSubtitle("قم باختيار الصور مع نص توضيحي لمحتواها");



        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        mStorage = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(AddImage.this);

        mSelectBtn = (Button) findViewById(R.id.select_btn);
        UploadBtn = (Button) findViewById(R.id.upload_btn);
        imageName = (EditText) findViewById(R.id.ImageNameEditText);
        mUploadList = (RecyclerView) findViewById(R.id.upload_list);

        summernote = (Summernote) findViewById(R.id.summernote);
        summernote.setRequestCodeforFilepicker(5);


        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();

        uploadListAdapter = new UploadListAdapter(fileNameList, fileDoneList);

        //RecyclerView

        mUploadList.setLayoutManager(new LinearLayoutManager(this));
        mUploadList.setHasFixedSize(true);
        mUploadList.setAdapter(uploadListAdapter);


        UploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                images = new ImagesModel();
                progressDialog.setTitle("جاري رفع الصور.....");
                progressDialog.setCancelable(false);
                progressDialog.show();

                if(chekUpload == 2) {
                    fileNameList.clear();
                    fileDoneList.clear();

                    for (int i = 0; i < totalItemsSelected; i++) {
                        fileUri = da.getClipData().getItemAt(i).getUri();
                        fileName = getFileName(fileUri);


                        fileNameList.add(fileName);
                        fileDoneList.add("uploading");
                        uploadListAdapter.notifyDataSetChanged();

                        finalI = i;
                        StorageReference fileToUpload = mStorage.child("Images").child(fileName);
                        final int finalI = i;
                        fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                fileDoneList.remove(finalI);
                                fileDoneList.add(finalI, "done" + taskSnapshot.getDownloadUrl().toString());
                                uploadListAdapter.notifyDataSetChanged();


                                if (images.getImage1() == null) {
                                    images.setImage1(taskSnapshot.getDownloadUrl().toString());
                                } else {
                                    if (images.getImage2() == null) {
                                        images.setImage2(taskSnapshot.getDownloadUrl().toString());
                                    } else {
                                        if (images.getImage3() == null) {
                                            images.setImage3(taskSnapshot.getDownloadUrl().toString());
                                        } else {
                                            if (images.getImage4() == null) {
                                                images.setImage4(taskSnapshot.getDownloadUrl().toString());
                                            } else {
                                                if (images.getImage5() == null) {
                                                    images.setImage5(taskSnapshot.getDownloadUrl().toString());
                                                } else {
                                                    if (images.getImage6() == null) {
                                                        images.setImage6(taskSnapshot.getDownloadUrl().toString());
                                                    } else {
                                                        if (images.getImage7() == null) {
                                                            images.setImage7(taskSnapshot.getDownloadUrl().toString());
                                                        } else {
                                                            if (images.getImage8() == null) {
                                                                images.setImage8(taskSnapshot.getDownloadUrl().toString());
                                                            } else {
                                                                if (images.getImage9() == null) {
                                                                    images.setImage9(taskSnapshot.getDownloadUrl().toString());
                                                                } else {
                                                                    if (images.getImage10() == null) {
                                                                        images.setImage10(taskSnapshot.getDownloadUrl().toString());
                                                                    } else {
                                                                        if (images.getImage11() == null) {
                                                                            images.setImage11(taskSnapshot.getDownloadUrl().toString());
                                                                        } else {
                                                                            if (images.getImage12() == null) {
                                                                                images.setImage12(taskSnapshot.getDownloadUrl().toString());
                                                                            } else {
                                                                                if (images.getImage13() == null) {
                                                                                    images.setImage13(taskSnapshot.getDownloadUrl().toString());
                                                                                } else {
                                                                                    if (images.getImage14() == null) {
                                                                                        images.setImage14(taskSnapshot.getDownloadUrl().toString());
                                                                                    } else {
                                                                                        if (images.getImage15() == null) {
                                                                                            images.setImage15(taskSnapshot.getDownloadUrl().toString());
                                                                                        } else {
                                                                                            if (images.getImage16() == null) {
                                                                                                images.setImage16(taskSnapshot.getDownloadUrl().toString());
                                                                                            } else {
                                                                                                if (images.getImage17() == null) {
                                                                                                    images.setImage17(taskSnapshot.getDownloadUrl().toString());
                                                                                                } else {
                                                                                                    if (images.getImage18() == null) {
                                                                                                        images.setImage18(taskSnapshot.getDownloadUrl().toString());
                                                                                                    } else {
                                                                                                        if (images.getImage19() == null) {
                                                                                                            images.setImage19(taskSnapshot.getDownloadUrl().toString());
                                                                                                        } else {
                                                                                                            if (images.getImage20() == null) {
                                                                                                                images.setImage20(taskSnapshot.getDownloadUrl().toString());
                                                                                                            } else {

                                                                                                            }

                                                                                                        }

                                                                                                    }

                                                                                                }

                                                                                            }

                                                                                        }

                                                                                    }

                                                                                }

                                                                            }

                                                                        }

                                                                    }

                                                                }

                                                            }

                                                        }

                                                    }

                                                }

                                            }

                                        }

                                    }

                                }

                                count++;
                                if (count == totalItemsSelected) {


                                    //String TempImageName = imageName.getText().toString().trim();
                                    String TempImageName = summernote.getText();


                                    Date date = new Date();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                                    String dateVal = sdf.format(date);

                                    progressDialog.dismiss();
                                    String ImageUploadId = databaseReference.push().getKey();
                                    final SharedPreferences shared = getApplicationContext().getSharedPreferences("shared", Activity.MODE_PRIVATE);
                                    String myid = shared.getString("not", "");
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

                                    databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();

                                }


                            }
                        });
                    }


                }else {
                    if (chekUpload == 1) {


                        StorageReference fileToUpload = mStorage.child("Images").child(fileName);

                        fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                images.setImage1(taskSnapshot.getDownloadUrl().toString());

                                //String TempImageName = imageName.getText().toString().trim();
                                String TempImageName = summernote.getText();

                                Date date = new Date();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                                String dateVal = sdf.format(date);

                                progressDialog.dismiss();
                                String ImageUploadId = databaseReference.push().getKey();
                                final SharedPreferences shared = getApplicationContext().getSharedPreferences("shared", Activity.MODE_PRIVATE);
                                String myid = shared.getString("not", "");
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

                                databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                                Intent intent = new Intent(getApplicationContext(), Home.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            }
                        });
                    }
                    else{
                        if(chekUpload == 0){

                            //String TempImageName = imageName.getText().toString().trim();
                            String TempImageName = summernote.getText();

                            Date date = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                            String dateVal = sdf.format(date);

                            progressDialog.dismiss();
                            String ImageUploadId = databaseReference.push().getKey();
                            final SharedPreferences shared = getApplicationContext().getSharedPreferences("shared", Activity.MODE_PRIVATE);
                            String myid = shared.getString("not", "");
                            @SuppressWarnings("VisibleForTests")
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(
                                    TempImageName,
                                    "0",
                                    ImageUploadId,
                                    dateVal,
                                    "",
                                    "",
                                    myid,
                                    images);

                            databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                            Intent intent = new Intent(getApplicationContext(), Home.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }



                }


                }








            }
            });




        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                da= data;
                totalItemsSelected = data.getClipData().getItemCount();

                for (int i = 0; i < totalItemsSelected; i++) {
                    fileUri = data.getClipData().getItemAt(i).getUri();
                    fileName = getFileName(fileUri);


                    fileNameList.add(fileName);
                    fileDoneList.add("uploading");

                    uploadListAdapter.notifyDataSetChanged();
                    chekUpload = 2;



                }

                //Toast.makeText(MainActivity.this, "Selected Multiple Files", Toast.LENGTH_SHORT).show();

            } else if (data.getData() != null) {

                chekUpload = 1;

                da= data;
                totalItemsSelected = 1;
                for (int i = 0; i < totalItemsSelected; i++) {
                    fileUri = data.getData();
                    fileName = getFileName(fileUri);

                    fileNameList.add(fileName);
                    fileDoneList.add("uploading");

                    uploadListAdapter.notifyDataSetChanged();

                }



            }

        }
    }




    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }











}











