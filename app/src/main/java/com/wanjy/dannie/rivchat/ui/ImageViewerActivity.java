package com.wanjy.dannie.rivchat.ui;

import android.app.*;
import android.net.Uri;
import android.os.*;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.util.FloatMath;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.webkit.WebView;
import android.widget.TextView;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.ClipboardManager;
import android.content.ClipData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.android.gms.tasks.OnFailureListener;
import com.wanjy.dannie.rivchat.Home;
import com.wanjy.dannie.rivchat.R;

import android.widget.PopupMenu;
import android.view.MenuInflater;

public class ImageViewerActivity extends AppCompatActivity 
{
    WebView imageViewer;
    TextView textViewer;
    String Link,Text;
    ImageView moreBtn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageview_viewer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("صور");
        getSupportActionBar().setSubtitle(getIntent().getExtras().getString("Text"));


        imageViewer = (WebView) findViewById(R.id.imageview_viewerWebview);
        textViewer = (TextView) findViewById(R.id.imageviewviewerTextView1);
        moreBtn = (ImageView) findViewById(R.id.morebtn);
        
        imageViewer.requestFocus();
        imageViewer.getSettings().setLightTouchEnabled(true);
        imageViewer.getSettings().setBuiltInZoomControls(true);
        imageViewer.getSettings().setUseWideViewPort(true);
        imageViewer.setInitialScale(1);

        Link = getIntent().getExtras().getString("Link");
        Text = getIntent().getExtras().getString("Text");

        imageViewer.loadUrl(Link);
        textViewer.setText(Text);
        
        
        moreBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1)
                {
                    PopupMenu popup = new PopupMenu(getApplicationContext(), p1);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.menu_image, popup.getMenu());
                    popup.show();
                    
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.save:
                                        downloadfile(Link,"Image"+System.currentTimeMillis());                
                                        return true;
                                    case R.id.copy:
                                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
                                        ClipData clip = ClipData.newPlainText("label", Text);
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(getApplicationContext(),"تم نسخ الرابط",Toast.LENGTH_LONG).show();                

                                        return true;
                                    default:
                                        return false;
                                }  
                            }
                        });

                    popup.show();
                    
                }                            
            });
        
        
        
            
            
    }

   
    private void downloadfile(String Link,String name) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(Link);
        
        final File rootPath = new File(Environment.getExternalStorageDirectory(), "FCAI Beni Suef/Images");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath, name+".jpg");
        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener <FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.e("firebase ", ";local tem file created  created " + localFile.toString());

                    if (localFile.canRead()){


                    }

                    Toast.makeText(getApplicationContext(), "تم الحفظ", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(context, "Internal storage/MADBO/Nature.jpg", Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                    Toast.makeText(getApplicationContext(), "Download Incompleted" + "//" + exception, Toast.LENGTH_LONG).show();
                }

            });
            }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_activity, menu);


        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.copy) {
            ClipboardManager clipboardd = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipp = ClipData.newPlainText("labell", Text);
            clipboardd.setPrimaryClip(clipp);
            Toast.makeText(ImageViewerActivity.this,"تم نسخ الصورة",Toast.LENGTH_LONG).show();
            return true;
        }

        if(id==R.id.download){
            downloadfile(Link,"Image"+System.currentTimeMillis());
            return true;
        }





        return super.onOptionsItemSelected(item);
    }


}
