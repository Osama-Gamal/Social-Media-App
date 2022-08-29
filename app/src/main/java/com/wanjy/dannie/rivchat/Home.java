package com.wanjy.dannie.rivchat;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.support.design.bottomappbar.BottomAppBar;

import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.view.GravityCompat;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.wanjy.dannie.rivchat.model.AppVersion;
import com.wanjy.dannie.rivchat.model.ChatFeatures;
import com.wanjy.dannie.rivchat.model.Comment;
import com.wanjy.dannie.rivchat.model.ImageUploadInfo;
import com.wanjy.dannie.rivchat.model.LectureModel;
import com.wanjy.dannie.rivchat.model.NotificationModel;
import com.wanjy.dannie.rivchat.service.ServiceNotif;
import com.wanjy.dannie.rivchat.ui.AddBook;
import com.wanjy.dannie.rivchat.ui.AddImage;
import com.wanjy.dannie.rivchat.ui.AddPost;
import com.wanjy.dannie.rivchat.ui.AddSound;
import com.wanjy.dannie.rivchat.ui.FragmentDeveloper;
import com.wanjy.dannie.rivchat.ui.FragmentImagesMedia;
import com.wanjy.dannie.rivchat.ui.FragmentLinkMedia;
import com.wanjy.dannie.rivchat.ui.FragmentManagers;
import com.wanjy.dannie.rivchat.ui.FragmentNotifications;
import com.wanjy.dannie.rivchat.ui.FragmentPdfMedia;
import com.wanjy.dannie.rivchat.ui.FragmentSoundsMedia;
import com.wanjy.dannie.rivchat.ui.UserProfileFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wanjy.dannie.rivchat.model.UserNumber;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import android.view.View;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.wanjy.dannie.rivchat.data.StaticConfig;
import com.wanjy.dannie.rivchat.ui.LoginActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import com.wanjy.dannie.rivchat.ui.FriendsFragment;
import com.wanjy.dannie.rivchat.ui.GroupFragment;

import android.support.v4.app.FragmentPagerAdapter;
import android.widget.RelativeLayout;
import android.widget.FrameLayout;
import android.content.SharedPreferences;

import com.wanjy.dannie.rivchat.model.User;
import com.wanjy.dannie.rivchat.ui.ChatAllFragment;
import com.wanjy.dannie.rivchat.ui.FragmentStudents;
import com.wanjy.dannie.rivchat.ui.LectureFragment;

import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.util.Base64;
import android.graphics.BitmapFactory;
import com.wanjy.dannie.rivchat.util.ImageUtils;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS;
import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;


public class Home extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener {
	String num=".....";
	
	private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
	List<UserNumber> list = new ArrayList<>();
	private DatabaseReference followers;
	private FrameLayout frame_container;
	private RelativeLayout content_main;
	private ViewPager viewPager;
    private TabLayout tabLayout = null;
   
    SharedPreferences sharedd;
    private boolean isMembersVisible = false;
    private boolean isCommunicationVisible = false;
    NavigationView navigationView;
    AlertDialog dialog;
    double versionDouble;
    public static List<User> listStudents = new ArrayList<>();
    public static List<User> listManagers = new ArrayList<>();
    public static List<User> listManagersStablize = new ArrayList<>();
    public static List<User> listManagersSocial = new ArrayList<>();
    public static List<User> listManagersTechnical = new ArrayList<>();
    CurvedBottomNavigationView curvedBottomNavigationView;
    public static User DeveloperUser;
    public SharedPreferences notif2;
    AppVersion appVersion;
    User StudentUploadInfo;
    DatabaseReference databaseReference,CheckRef,updateRef,ModifyRef;
    Toolbar toolbar;
    BottomAppBar bottomAppBar;
    Badge BadgeNotif;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_NETWORK_STATE
    };

    private ViewPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        sharedd = getApplicationContext().getSharedPreferences("sharedUser", Activity.MODE_PRIVATE);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
		
        if(toolbar != null) {
		 setSupportActionBar(toolbar);
		 getSupportActionBar().setTitle("حاسبات بني سويف");
		 getSupportActionBar().setSubtitle("عدد الطلاب : "+num);
         //getSupportActionBar().setDisplayShowHomeEnabled(true);
         //getSupportActionBar().setIcon(R.drawable.home);
            
		 }

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


        updateRef = FirebaseDatabase.getInstance().getReference("versions");
        updateRef.keepSynced(true);




        curvedBottomNavigationView = findViewById(R.id.customBottomBar);
        curvedBottomNavigationView.inflateMenu(R.menu.bottom_nav_items);

        curvedBottomNavigationView.setSelectedItemId(R.id.action_home);

        notif2 = getApplicationContext().getSharedPreferences("notif2", Activity.MODE_PRIVATE);
        long NotificationNumbers = notif2.getLong("notificationNumHome",0);
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) curvedBottomNavigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(0);
        BadgeNotif = new QBadgeView(Home.this).bindTarget(v).setBadgeNumber((int)NotificationNumbers).setGravityOffset(60, 0, true);

        final int delay = 5000;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){

                long NotificationNumbers = notif2.getLong("notificationNumHome",0);
                BadgeNotif.setBadgeNumber((int) NotificationNumbers);


                handler.postDelayed(this, delay);
            }
        }, delay);



/*
        //right
        View v2 = curvedBottomNavigationView.getChildAt(0);
        new QBadgeView(this).bindTarget(v2).setBadgeNumber(9)
                .setBadgeGravity(Gravity.BOTTOM | Gravity.END)
                .stroke(0xff000000, 1, true);
*/






		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
			this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_main);
        navigationView.getMenu().setGroupVisible(R.id.media_group, false);



		View headerView = navigationView.getHeaderView(0);
        TextView emailTxt = (TextView) headerView.findViewById(R.id.emailTxtNav);
        TextView nameTxt = (TextView) headerView.findViewById(R.id.nameTxtNav);
        nameTxt.setText(sharedd.getString("name",""));
        ImageView userImage = (ImageView) headerView.findViewById(R.id.imageUserNav);
        nameTxt.setText(sharedd.getString("name",""));
        emailTxt.setText(sharedd.getString("email",""));

        String img = sharedd.getString("avata","");
        final Bitmap src;
        if (img.equals("default")) {
            userImage.setImageResource(R.drawable.user_profile);
        } else {
            byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
            src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            userImage.setImageDrawable(ImageUtils.roundedImage(this, src));
        }
        
        
        
		frame_container=(FrameLayout) findViewById(R.id.frame_container);
		content_main=(RelativeLayout) findViewById(R.id.content_main);
		viewPager = (ViewPager)findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorIndivateTab));
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        navigationView.getMenu().setGroupVisible(R.id.management, false);
        isCommunicationVisible = false;

		/*SharedPreferenceHelper preferenceHelper = SharedPreferenceHelper.getInstance(Home.this);
		Toast.makeText(Home.this,""+preferenceHelper.getUID(),Toast.LENGTH_LONG)
			.show();*/



		startService(new Intent(Home.this, ServiceNotif.class));

/*
        Intent myIntent = new Intent();
        myIntent.setAction(Intent.ACTION_VIEW);
        myIntent.setData(Uri.parse("https://drive.google.com/file/d/1x2DhzikQ12cR_gBRmPfke_uYo_0SVTP4/view?usp=drivesdk"));
        startActivity(myIntent);
*/

        //"https://drive.google.com/uc?id=139jBj_GUfmFi_pZN38SS9RMB5wNXMEy9"


        dialog = new AlertDialog.Builder(Home.this).create();

        if(sharedd.getString("myid","").equals("")){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();           
        }else{

            final SharedPreferences shared = getApplicationContext().getSharedPreferences("shared", Activity.MODE_PRIVATE);
            shared.edit().putString("not",sharedd.getString("myid","")).commit();
            StaticConfig.UID=shared.getString("not","");



            try {
                PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
                versionDouble = Double.valueOf(pInfo.versionName);
                //Toast.makeText(getApplicationContext(),""+ versionDouble,Toast.LENGTH_LONG).show();


                CheckRef = FirebaseDatabase.getInstance().getReference("versions");
                CheckRef.keepSynced(true);

                CheckRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                            appVersion = postSnapshot.getValue(AppVersion.class);

                            if (appVersion != null) {
                                if (versionDouble < appVersion.getVersion()) {

                                    LayoutInflater inflater = getLayoutInflater();
                                    View convertView = (View) inflater.inflate(R.layout.dialog_update, null);
                                    dialog.setView(convertView);
                                    dialog.setCancelable(false);

                                    Button accept = convertView.findViewById(R.id.acceptbtn);
                                    Button close = convertView.findViewById(R.id.closebtn);
                                    final TextView progressTxt = convertView.findViewById(R.id.progressTxt);
                                    final ProgressBar downloadProgress = convertView.findViewById(R.id.downloadProgress);
                                    downloadProgress.setVisibility(View.GONE);
                                    progressTxt.setVisibility(View.GONE);

                                    accept.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View p1) {

                                            if (appVersion.getLink().contains("drive.google")){

                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW , Uri.parse(appVersion.getLink()));
                                                startActivity(browserIntent);



                                            }else{

                                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                                StorageReference storageRef = storage.getReferenceFromUrl(appVersion.getLink());
                                                final File rootPath = new File(Environment.getExternalStorageDirectory(), "FCAI Beni Suef/Versions");
                                                if (!rootPath.exists()) {
                                                    rootPath.mkdirs();
                                                }
                                                final File localFile = new File(rootPath, "FCAIBeniSuef" + appVersion.getVersion() + ".apk");
                                                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                        Log.e("firebase ", ";local tem file created  created " + localFile.toString());
                                                        downloadProgress.setVisibility(View.GONE);
                                                        progressTxt.setVisibility(View.GONE);
                                                        if (localFile.canRead()) {
                                                            //Toast.makeText(Home.this,""+localFile,Toast.LENGTH_LONG).show();


                                                            try {
                                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                Uri urii = FileProvider.getUriForFile(Home.this, BuildConfig.APPLICATION_ID + ".provider", localFile);
                                                                intent.setDataAndType(urii, "application/vnd.android.package-archive");
                                                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                startActivity(intent);
                                                            } catch (ActivityNotFoundException e) {
                                                                // no Activity to handle this kind of files
                                                            }

                                                        }

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                                                        Toast.makeText(Home.this, "Download Incompleted" + "//" + exception, Toast.LENGTH_LONG).show();
                                                        downloadProgress.setVisibility(View.GONE);
                                                        progressTxt.setVisibility(View.GONE);
                                                    }

                                                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                        //calculating progress percentage
                                                        final double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                                        downloadProgress.setProgress((int) progress);
                                                        progressTxt.setText((int) progress + " % ");
                                                        downloadProgress.setVisibility(View.VISIBLE);
                                                        progressTxt.setVisibility(View.VISIBLE);


                                                    }
                                                });
                                            }
                                        }
                                    });


                                    close.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View p1) {
                                            finish();
                                            dialog.dismiss();
                                        }
                                    });

                                    dialog.show();

                                } else {
                                    dialog.dismiss();
                                }

                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException(); // don't ignore errors
                    }
                });

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


       /* appVersion = new AppVersion(1.0,"j");
        updateRef.child(updateRef.getKey()).setValue(appVersion);
*/



            // Check if Chat features had been closed or not

            ModifyRef = FirebaseDatabase.getInstance().getReference("Modify");
            ModifyRef.keepSynced(true);

          /*  Map ModifyState = new HashMap();
            ModifyState.put("ModifyState", "true");
            ModifyRef.push().setValue(ModifyState);
*/
            ModifyRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        //Toast.makeText(Home.this,"" + postSnapshot.child("ModifyState").getValue(String.class),Toast.LENGTH_LONG).show();
                        if(postSnapshot.child("ModifyState").getValue(String.class).equals("true")) {

                        final AlertDialog dialog = new AlertDialog.Builder(Home.this).create();
                        LayoutInflater inflater = getLayoutInflater();
                        View convertView = (View) inflater.inflate(R.layout.modify_close, null);
                        dialog.setView(convertView);
                        dialog.setCancelable(false);

                        Button close = (Button) convertView.findViewById(R.id.closebtn);

                        close.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View v){
                                dialog.dismiss();
                                finish();
                            }
                        });

                        dialog.show();

                        }else{
                            dialog.dismiss();
                        }

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child("user")
                    .orderByChild("myId")
                    .equalTo(sharedd.getString("myid",""));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getChildrenCount()>0) {

                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference usersRef = rootRef.child("user");
                        DatabaseReference uidRef = usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                        ValueEventListener eventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {

                                    sharedd.edit().putString("name", dataSnapshot.child("name").getValue(String.class)).commit();
                                    sharedd.edit().putString("email", dataSnapshot.child("email").getValue(String.class)).commit();
                                    sharedd.edit().putString("password", dataSnapshot.child("password").getValue(String.class)).commit();
                                    sharedd.edit().putString("avata", dataSnapshot.child("avata").getValue(String.class)).commit();
                                    sharedd.edit().putString("myid", dataSnapshot.child("myId").getValue(String.class)).commit();
                                    sharedd.edit().putString("qrcode", dataSnapshot.child("qrcode").getValue(String.class)).commit();
                                    sharedd.edit().putString("group", dataSnapshot.child("group").getValue(String.class)).commit();
                                    sharedd.edit().putString("title", dataSnapshot.child("title").getValue(String.class)).commit();
                                    sharedd.edit().putString("telegram", dataSnapshot.child("telegram").getValue(String.class)).commit();
                                    //Toast.makeText(getApplicationContext(),""+sharedd.getString("name",""),Toast.LENGTH_LONG).show();

                                }


                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("TAG", databaseError.getMessage());
                            }
                        };
                        uidRef.addListenerForSingleValueEvent(eventListener);
                        uidRef.keepSynced(true);


                        listStudents = new ArrayList<>();
                        listManagers = new ArrayList<>();
                        listManagersStablize = new ArrayList<>();
                        listManagersSocial = new ArrayList<>();
                        listManagersTechnical = new ArrayList<>();


                        databaseReference = FirebaseDatabase.getInstance().getReference("user");
                        databaseReference.keepSynced(true);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                listStudents.clear();
                                listManagers.clear();
                                listManagersStablize.clear();
                                listManagersSocial.clear();
                                listManagersTechnical.clear();

                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                                    StudentUploadInfo = postSnapshot.getValue(User.class);

                                    if(StudentUploadInfo.getTitle().equals("")){
                                        listStudents.add(StudentUploadInfo);
                                        getSupportActionBar().setSubtitle("عدد الطلاب : "+listStudents.size());
                                    }
                                    if(StudentUploadInfo.getTitle().equals("مشرف")){
                                        listManagers.add(StudentUploadInfo);
                                    }
                                    if(StudentUploadInfo.getTitle().equals("المبرمج")){
                                        DeveloperUser = StudentUploadInfo;
                                    }
                                    if(StudentUploadInfo.getTitle().equals("منظم")){
                                        listManagersStablize.add(StudentUploadInfo);
                                    }
                                    if(StudentUploadInfo.getTitle().equals("داعم اجتماعي")){
                                        listManagersSocial.add(StudentUploadInfo);
                                    }
                                    if(StudentUploadInfo.getTitle().equals("داعم تقني")){
                                        listManagersTechnical.add(StudentUploadInfo);
                                    }




                                }

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }else{
                        // username not found
                        Toast.makeText(getApplicationContext(),"تم حذف حسابك بسبب مخالفة للقوانين",Toast.LENGTH_LONG).show();
                        sharedd.edit().putString("myid", "").commit();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });







        }




		
		
		initFirebase();
        onBottomNavigationClicked();
		
		
		
		}
		
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(sharedd.getString("title","").contains("مندوب")||sharedd.getString("title","").contains("مشرف")||sharedd.getString("title","").contains("المبرمج")
                ||sharedd.getString("title","").contains("داعم تقني")||sharedd.getString("title","").contains("داعم اجتماعي")){
            getMenuInflater().inflate(R.menu.menu_main_manager, menu);
        }else{
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }



        return super.onCreateOptionsMenu(menu);
            }




    DatabaseReference chatFeatureRef;
    ChatFeatures chatFeatures;
    boolean image_state,sound_state,book_state;

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            Toast.makeText(this, "حاسبات بني سويف", Toast.LENGTH_LONG).show();
            return true;
        }

		if(id==R.id.share){

			String apk = "أخبار";
			String uri = ("com.wanjy.dannie.rivchat.ui");

			try {
				android.content.pm.PackageInfo pi = getPackageManager().getPackageInfo(uri, android.content.pm.PackageManager.GET_ACTIVITIES);

				apk = pi.applicationInfo.publicSourceDir;
			} catch (Exception e) {

			}
			Intent iten = new Intent(Intent.ACTION_SEND);
			iten.setType("*/*");
			iten.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new java.io.File(apk)));

			startActivity(Intent.createChooser(iten, "Send APK"));
			return true;
		}

        if (id == R.id.logout) {

            final AlertDialog dialog = new AlertDialog.Builder(Home.this).create();
            LayoutInflater inflater = getLayoutInflater();
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
                    Intent intent = new Intent(Home.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }});
            close.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1)
                {
                    dialog.dismiss();
                }
            });
            dialog.show();

            return true;
        }

        if (id == R.id.add) {

            final AlertDialog dialog = new AlertDialog.Builder(Home.this).create();
            LayoutInflater inflater = getLayoutInflater();
            View convertView = (View) inflater.inflate(R.layout.custom_dialog, null);
            dialog.setView(convertView);

            chatFeatureRef = FirebaseDatabase.getInstance().getReference("chatFeature");
            chatFeatureRef.keepSynced(true);

            Button addpost = (Button) convertView.findViewById(R.id.custom_addpost);
            Button addbook = (Button) convertView.findViewById(R.id.custom_addbook);
            Button addvideo = (Button) convertView.findViewById(R.id.custom_addvideo);
            Button sendNotification = (Button) convertView.findViewById(R.id.custom_addNotif);


             final Switch switch_image = (Switch) convertView.findViewById(R.id.switch_image);
             final Switch switch_sound = (Switch) convertView.findViewById(R.id.switch_sound);
             final Switch switch_book = (Switch) convertView.findViewById(R.id.switch_book);


             switch_image.setChecked(ChatAllFragment.switch_image);
             switch_sound.setChecked(ChatAllFragment.switch_sound);
             switch_book.setChecked(ChatAllFragment.switch_book);



             switch_image.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    chatFeatureRef.child(chatFeatureRef.getKey()).child("image").setValue(isChecked);
                }
            });

            switch_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    chatFeatureRef.child(chatFeatureRef.getKey()).child("sound").setValue(isChecked);
                }
            });

            switch_book.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    chatFeatureRef.child(chatFeatureRef.getKey()).child("book").setValue(isChecked);
                }
            });



            sendNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();

                    final AlertDialog dialogNotif = new AlertDialog.Builder(Home.this).create();
                    LayoutInflater inflater = getLayoutInflater();
                    View convertView = (View) inflater.inflate(R.layout.dialog_send_notif, null);
                    dialogNotif.setView(convertView);

                    final EditText editNotif = convertView.findViewById(R.id.NotifEditText);
                    final Button sendNotif = convertView.findViewById(R.id.acceptbtn);
                    Button closeNotif = convertView.findViewById(R.id.closebtn);

                    final DatabaseReference NotifReference = FirebaseDatabase.getInstance().getReference("Notification");

                    dialogNotif.show();

                    sendNotif.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            sendNotif.setEnabled(false);
                            Toast.makeText(getApplicationContext(), "جاري الارسال", Toast.LENGTH_LONG)
                                    .show();

                            final SharedPreferences shared = getApplicationContext().getSharedPreferences("shared", Activity.MODE_PRIVATE);
                            String myid = shared.getString("not", "");

                            Date date = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

                            String Notif_id = databaseReference.push().getKey();
                            String dateval = sdf.format(date);
                            NotificationModel lectureUp = new NotificationModel(myid,editNotif.getText().toString(),Notif_id,dateval);


                            NotifReference.child(Notif_id).setValue(lectureUp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "تم الارسال", Toast.LENGTH_LONG)
                                            .show();
                                    dialogNotif.dismiss();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "fail to add comment : " + e.getMessage(), Toast.LENGTH_LONG)
                                            .show();
                                }
                            });



                        }
                    });

                    closeNotif.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogNotif.dismiss();
                        }
                    });





                }
            });

            addpost.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    dialog.dismiss();

                    Intent k=new Intent(Home.this, AddImage.class);
                    startActivity(k);
                }
            });

            addbook.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    dialog.dismiss();

                    Intent k=new Intent(Home.this, AddBook.class);
                    startActivity(k);
                }
            });

            addvideo.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    dialog.dismiss();

                    Intent k=new Intent(Home.this, AddSound.class);
                    startActivity(k);
                }
            });

            dialog.show();

            return true;
        }










        return super.onOptionsItemSelected(item);
    }
	




    int Souund_Request_Code = 19;
    int frag = 0;
    public static String mangagerSort;

   public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

    Fragment fragment = null;
    if (id == R.id.nav_main) {
        frag = 0;
        frame_container.setVisibility(View.GONE);
		tabLayout.setVisibility(View.VISIBLE);
		viewPager.setVisibility(View.VISIBLE);
		content_main.setVisibility(View.VISIBLE);

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(SCROLL_FLAG_SCROLL | SCROLL_FLAG_ENTER_ALWAYS);

        drawer.closeDrawer(GravityCompat.START);
        navigationView.getMenu().setGroupVisible(R.id.media_group, false);
        isMembersVisible = false;
        navigationView.getMenu().setGroupVisible(R.id.management, false);
        isCommunicationVisible = false;

    } else if (id == R.id.nav_students) {
        frag = 1;

        drawer.closeDrawer(GravityCompat.START);
        navigationView.getMenu().setGroupVisible(R.id.media_group, false);
        isMembersVisible = false;
        navigationView.getMenu().setGroupVisible(R.id.management, false);
        isCommunicationVisible = false;

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);


        frame_container.setVisibility(View.VISIBLE);
        fragment = new FragmentStudents();
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        content_main.setVisibility(View.GONE);

	
		
    } else if (id == R.id.nav_account) {
        drawer.closeDrawer(GravityCompat.START);
        navigationView.getMenu().setGroupVisible(R.id.media_group, false);
        isMembersVisible = false;
        navigationView.getMenu().setGroupVisible(R.id.management, false);
        isCommunicationVisible = false;
        frag = 1;

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

		frame_container.setVisibility(View.VISIBLE);
		fragment = new UserProfileFragment();
		tabLayout.setVisibility(View.GONE);
		viewPager.setVisibility(View.GONE);
		content_main.setVisibility(View.GONE);
		
    } else if (id == R.id.nav_media) {

        if (!isMembersVisible) {
            navigationView.getMenu().setGroupVisible(R.id.media_group, true);
            isMembersVisible = true;
        } else {
            navigationView.getMenu().setGroupVisible(R.id.media_group, false);
            isMembersVisible = false;
        }
        
        
    } else if (id == R.id.nav_communicate) {


        if (!isCommunicationVisible) {
            navigationView.getMenu().setGroupVisible(R.id.management, true);
            isCommunicationVisible = true;
        } else {
            navigationView.getMenu().setGroupVisible(R.id.management, false);
            isCommunicationVisible = false;
        }

        /*drawer.closeDrawer(GravityCompat.START);
        navigationView.getMenu().setGroupVisible(R.id.media_group, false);
        isMembersVisible = false;
        frag = 1;

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

        frame_container.setVisibility(View.VISIBLE);
        fragment = new FragmentManagers(); //StudentsFragment
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
		content_main.setVisibility(View.GONE);*/

    } else if (id == R.id.nav_developer) {
        frag = 1;

        drawer.closeDrawer(GravityCompat.START);
        navigationView.getMenu().setGroupVisible(R.id.media_group, false);
        isMembersVisible = false;
        navigationView.getMenu().setGroupVisible(R.id.management, false);
        isCommunicationVisible = false;

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);


        frame_container.setVisibility(View.VISIBLE);
        fragment = new FragmentDeveloper();
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        content_main.setVisibility(View.GONE);


    }else if (id == R.id.photo) {
        drawer.closeDrawer(GravityCompat.START);
        frag = 1;
        navigationView.getMenu().setGroupVisible(R.id.management, false);
        isCommunicationVisible = false;

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

        frame_container.setVisibility(View.VISIBLE);
        fragment = new FragmentImagesMedia();
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        content_main.setVisibility(View.GONE);

    }else if (id == R.id.sounds) {
        drawer.closeDrawer(GravityCompat.START);
        frag = 1;
        navigationView.getMenu().setGroupVisible(R.id.management, false);
        isCommunicationVisible = false;

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);


        frame_container.setVisibility(View.VISIBLE);
        fragment = new FragmentSoundsMedia();
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        content_main.setVisibility(View.GONE);

    }else if (id == R.id.pdfs) {
        drawer.closeDrawer(GravityCompat.START);
        frag = 1;
        navigationView.getMenu().setGroupVisible(R.id.management, false);
        isCommunicationVisible = false;

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);


        frame_container.setVisibility(View.VISIBLE);
        fragment = new FragmentPdfMedia();
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        content_main.setVisibility(View.GONE);

    }else if (id == R.id.links) {
        drawer.closeDrawer(GravityCompat.START);
        frag = 1;
        navigationView.getMenu().setGroupVisible(R.id.management, false);
        isCommunicationVisible = false;

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);


        frame_container.setVisibility(View.VISIBLE);
        fragment = new FragmentLinkMedia();
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        content_main.setVisibility(View.GONE);

    }
    else if (id == R.id.management_admins) {

        mangagerSort = "مشرف";
        drawer.closeDrawer(GravityCompat.START);
        navigationView.getMenu().setGroupVisible(R.id.media_group, false);
        isMembersVisible = false;
        frag = 1;

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

        frame_container.setVisibility(View.VISIBLE);
        fragment = new FragmentManagers(); //StudentsFragment
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        content_main.setVisibility(View.GONE);

    }
    else if (id == R.id.management_stablize) {

        mangagerSort = "منظم";
        drawer.closeDrawer(GravityCompat.START);
        navigationView.getMenu().setGroupVisible(R.id.media_group, false);
        isMembersVisible = false;
        frag = 1;

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

        frame_container.setVisibility(View.VISIBLE);
        fragment = new FragmentManagers(); //StudentsFragment
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        content_main.setVisibility(View.GONE);

    }
    else if (id == R.id.management_social) {

        mangagerSort = "الدعم الاجتماعي";
        drawer.closeDrawer(GravityCompat.START);
        navigationView.getMenu().setGroupVisible(R.id.media_group, false);
        isMembersVisible = false;
        frag = 1;

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

        frame_container.setVisibility(View.VISIBLE);
        fragment = new FragmentManagers(); //StudentsFragment
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        content_main.setVisibility(View.GONE);

    }
    else if (id == R.id.management_technical) {

        mangagerSort = "الدعم التقني";
        drawer.closeDrawer(GravityCompat.START);
        navigationView.getMenu().setGroupVisible(R.id.media_group, false);
        isMembersVisible = false;
        frag = 1;

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

        frame_container.setVisibility(View.VISIBLE);
        fragment = new FragmentManagers(); //StudentsFragment
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        content_main.setVisibility(View.GONE);

    }
    else if (id == R.id.main_page) {


    }



    if (fragment != null) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment).commit();

    }

    return true;

}

	private void initFirebase() {
        //Khoi tao thanh phan de dang nhap, dang ky
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {

			private String TAG;
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    
					/*final SharedPreferences shared = getApplicationContext().getSharedPreferences("shared", Activity.MODE_PRIVATE);
					shared.edit().putString("not", StaticConfig.UID).commit();
					String getid=shared.getString("not","");*/



                } else {
                    Home.this.finish();
                    // User is signed in
                    startActivity(new Intent(Home.this, LoginActivity.class));
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if(frag == 1){
            frag = 0;
            frame_container.setVisibility(View.GONE);
            tabLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
            content_main.setVisibility(View.VISIBLE);

            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            params.setScrollFlags(SCROLL_FLAG_SCROLL | SCROLL_FLAG_ENTER_ALWAYS);

            navigationView.getMenu().setGroupVisible(R.id.media_group, false);
            isMembersVisible = false;
            navigationView.getMenu().setGroupVisible(R.id.management, false);
            isCommunicationVisible = false;

        }else{

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
        }






    }




    private void setupTabIcons() {
        int[] tabIcons = {
			R.drawable.home,
			R.drawable.speechbubble,
			R.drawable.voicesearch,
            R.drawable.files
        };

       /* tabLayout.getTabAt(0).setText("الصفحة الرئيسية").setIcon(0);
        tabLayout.getTabAt(1).setText("المراسلة").setIcon(1);
		tabLayout.getTabAt(2).setText("الطلاب").setIcon(2);
        */
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        
       // tabLayout.getTabAt(3).setText("ملفك");//setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());//getsupportfragmentmanager
        adapter.addFrag(new FriendsFragment(),"الصفحة الرئيسية" );
        adapter.addFrag(new ChatAllFragment(), "المراسلة");//GroupFrag
		adapter.addFrag(new LectureFragment(), "محاضرات");//VideoFrag
        adapter.addFrag(new GroupFragment(), "مجلدات");
        

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				@Override
				public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

				}

				@Override
				public void onPageSelected(int position) {
					//ServiceUtils.stopServiceFriendChat(Home.this.Act, false);
					if (adapter.getItem(position) instanceof FriendsFragment) {


					} else if (adapter.getItem(position) instanceof ChatAllFragment) {


					} else {

					}
				}

				@Override
				public void onPageScrollStateChanged(int state) {

				}
			});
    }

	

    
	
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            // return null to display only the icon
            return mFragmentTitleList.get(position);
        }
    }

    public static boolean hasPermissions(Context context, String[] permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void onBottomNavigationClicked(){

        curvedBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment = null;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.notifications:

                        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
                        params.setScrollFlags(0);

                        frame_container.setVisibility(View.VISIBLE);
                        fragment = new FragmentNotifications();
                        tabLayout.setVisibility(View.GONE);
                        viewPager.setVisibility(View.GONE);
                        content_main.setVisibility(View.GONE);
                        notif2.edit().putLong("notificationNumHome", 0).commit();
                        BadgeNotif.hide(true);

                        break;
                    case R.id.action_home:

                        frame_container.setVisibility(View.GONE);
                        tabLayout.setVisibility(View.VISIBLE);
                        viewPager.setVisibility(View.VISIBLE);
                        content_main.setVisibility(View.VISIBLE);

                        AppBarLayout.LayoutParams params2 = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
                        params2.setScrollFlags(SCROLL_FLAG_SCROLL | SCROLL_FLAG_ENTER_ALWAYS);

                        break;
                    case R.id.communicateUs:

                        break;


                }
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, fragment).commit();

                }
                return true;
            }
        });

    }




    
    }
