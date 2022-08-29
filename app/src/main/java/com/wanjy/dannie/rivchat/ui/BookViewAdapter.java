package com.wanjy.dannie.rivchat.ui;
import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.wanjy.dannie.rivchat.util.ImageUtils;
import android.util.Base64;
import com.wanjy.dannie.rivchat.data.StaticConfig;
import android.app.AlertDialog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.app.ProgressDialog;
import java.io.File;
import android.os.Environment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
//import android.annotation.NonNull;
import com.google.firebase.storage.OnProgressListener;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.widget.PopupMenu;


public class BookViewAdapter extends RecyclerView.Adapter<BookViewAdapter.ViewHolder> {

    Context context;
    List<ImageUploadInfo> MainImageUploadInfoList;
	DatabaseReference book_seenref;

    PopupMenu popup;
    String post_key,book_url;

    public BookViewAdapter(Context context, List<ImageUploadInfo> TempList) {

        this.MainImageUploadInfoList = TempList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

		ImageUploadInfo UploadInfo = MainImageUploadInfoList.get(position);
        holder.book_name.setText(UploadInfo.getImageName());
		post_key=UploadInfo.get_key();
        
		book_url=UploadInfo.getPdf_url();
        
		holder.Link.setText(UploadInfo.getPdf_url());
        holder.ID.setText(UploadInfo.get_key());
        holder.UserID.setText(UploadInfo.getUser());
        
		String dateValue=UploadInfo.getDate();

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
		
		holder.des.setText(UploadInfo.getDes());

		
		
        
        
        holder.downloadBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1)
                {
                    downloadfile(holder.Link.getText().toString(),holder.book_name.getText().toString()+System.currentTimeMillis());
                                                
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
        
        
        
        
        book_seenref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    if(snapshot.child(holder.ID.getText().toString()).hasChild(StaticConfig.UID)){

                       /* final int color = Color.parseColor("#0288D1");
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.Seen_image.setColorFilter(color);
                            }
                        }, 2000);*/
                    }
                    else{
                        //holder.Seen_image.setImageResource(R.drawable.eye);
                        book_seenref.child(holder.ID.getText().toString()).child(StaticConfig.UID).setValue("seen");
                    }
                    holder.seenNum.setText(String.valueOf(snapshot.child(holder.ID.getText().toString()).getChildrenCount()));
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });







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
                                DatabaseReference deleteComment = FirebaseDatabase.getInstance().getReference().child("Books_Uploads").child(holder.ID.getText().toString());
                                deleteComment.removeValue();
                                break;
                            case R.id.copylink:

                                ClipboardManager clipboardd = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                                ClipData clipp = ClipData.newPlainText("labelo", holder.Link.getText().toString());
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
                                        mDatabase.child("Books_Uploads").child(holder.ID.getText().toString()).child("des").setValue(ImageNameEditText.getText().toString());
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






        
        
        
        
	}
    


    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView book_name,datev,des,seenNum,publiser_name;
		ImageView UserimageView,downloadBtn,moreImage,Seen_image;
        CardView main_book_post;
        
        TextView Link,ID,UserID,titleTxt;
        
        public ViewHolder(View itemView) {
            super(itemView);
            
            book_seenref=FirebaseDatabase.getInstance().getReference().child("SeenBooks");
			book_seenref.keepSynced(true);
            
            book_name = (TextView) itemView.findViewById(R.id.txtTitle);
			datev = (TextView) itemView.findViewById(R.id.date);
			des = (TextView) itemView.findViewById(R.id.txtAuthor);
            moreImage = (ImageView) itemView.findViewById(R.id.moreImage);
            Seen_image = (ImageView) itemView.findViewById(R.id.Seen_image);

            Link = (TextView) itemView.findViewById(R.id.Link);
            ID = (TextView) itemView.findViewById(R.id.ID);
            UserID = (TextView) itemView.findViewById(R.id.UserID);
            
            titleTxt = (TextView) itemView.findViewById(R.id.titleTxt);
            UserimageView = (ImageView) itemView.findViewById(R.id.blog_user_image);
            publiser_name = (TextView) itemView.findViewById(R.id.blog_user_namee);
            seenNum = (TextView) itemView.findViewById(R.id.Seen_num);

            main_book_post = itemView.findViewById(R.id.main_book_post);

            downloadBtn = itemView.findViewById(R.id.download_btn);

        }
    }


    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 12;

    private void downloadfile(String Link,String name) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(Link);
        Toast.makeText(context, "جاري التحميل", Toast.LENGTH_SHORT).show();
        
        
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

    
    
    
    
}
