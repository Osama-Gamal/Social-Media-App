package com.wanjy.dannie.rivchat.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import com.wanjy.dannie.rivchat.*;
import com.google.firebase.database.*;
import com.wanjy.dannie.rivchat.model.*;
import com.wanjy.dannie.rivchat.ui.RecyclerViewAdapter;

import android.icu.util.*;
import android.content.*;
import android.app.*;
import java.text.*;
import java.util.ArrayList;
import java.util.List;

import android.graphics.*;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

public class ServiceNotif extends Service
{

	private DatabaseReference databaseReference,databaseReference2;
	List<ImageUploadInfo> list = new ArrayList<>();
	List<NotificationModel> list2 = new ArrayList<>();
	private ImageUploadInfo imageUploadInfo;
	private NotificationModel NotificationInfo;


	@Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        
        final SharedPreferences notif = getApplicationContext().getSharedPreferences("notif", Activity.MODE_PRIVATE);

        databaseReference = FirebaseDatabase.getInstance().getReference("All_Image_Uploads_Database");
		databaseReference.keepSynced(true);


		databaseReference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {


				DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("All_Image_Uploads_Database");
				Query lastQuery = dbRef.orderByKey().limitToLast(1);
				lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						for(DataSnapshot data : dataSnapshot.getChildren())
						{
							imageUploadInfo = data.getValue(ImageUploadInfo.class);
							//Toast.makeText(ServiceNotif.this, "" + imageUploadInfo.getImageName(), Toast.LENGTH_LONG).show();

							String get=imageUploadInfo.getImageName();
							if(notif.getString("not","").equals(get)){}
							else {

								notif.edit().putString("not", get).commit();


								NotificationCompat.Builder builder = new NotificationCompat.Builder(ServiceNotif.this);
								builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
								Intent notificationIntent = new Intent(
										getApplicationContext(), Home.class);
								PendingIntent pendingIntent = PendingIntent.getActivity(
										getApplicationContext(),
										0,
										notificationIntent,
										Intent.FLAG_ACTIVITY_NEW_TASK);
								builder.setContentIntent(pendingIntent);
								builder.setDefaults(
										Notification.DEFAULT_SOUND
												| Notification.DEFAULT_VIBRATE);
								builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_alert));
								builder.setContentTitle("منشور جديد");
								builder.setContentText(imageUploadInfo.getImageName());
								builder.setSubText("اضغط لقراءة المزيد");

								NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

								notificationManager.notify(1, builder.build());


							}


						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}});


			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});












		final SharedPreferences notif2 = getApplicationContext().getSharedPreferences("notif2", Activity.MODE_PRIVATE);

		databaseReference2 = FirebaseDatabase.getInstance().getReference("Notification");
		databaseReference2.keepSynced(true);


		databaseReference2.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {

				if(snapshot.getChildrenCount() - notif2.getLong("notificationNum",0) >0) {
					notif2.edit().putLong("notificationNumHome", (snapshot.getChildrenCount() - notif2.getLong("notificationNum",0)) + notif2.getLong("notificationNumHome",0)).commit();
					notif2.edit().putLong("notificationNum", snapshot.getChildrenCount()).commit();
					//Toast.makeText(getApplicationContext(),"" + notif2.getLong("notificationNumHome",0),Toast.LENGTH_LONG).show();
				}

				DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Notification");
				Query lastQuery = dbRef.orderByKey().limitToLast(1);
				lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						for(DataSnapshot data : dataSnapshot.getChildren())
						{
							NotificationInfo = data.getValue(NotificationModel.class);

							String get2=NotificationInfo.getNotifText();
							if(notif2.getString("not2","").equals(get2)){}
							else {

								notif2.edit().putString("not2", get2).commit();


								NotificationCompat.Builder builder = new NotificationCompat.Builder(ServiceNotif.this);
								builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
								Intent notificationIntent = new Intent(
										getApplicationContext(), Home.class);
								PendingIntent pendingIntent = PendingIntent.getActivity(
										getApplicationContext(),
										0,
										notificationIntent,
										Intent.FLAG_ACTIVITY_NEW_TASK);
								builder.setContentIntent(pendingIntent);
								builder.setDefaults(
										Notification.DEFAULT_SOUND
												| Notification.DEFAULT_VIBRATE);
								builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_alert));
								builder.setContentTitle("اشعار جديد");
								builder.setContentText(NotificationInfo.getNotifText());
								builder.setSubText("اضغط لقراءة المزيد");

								NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

								notificationManager.notify(2, builder.build());


							}

						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}});


			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});








		return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
		restartServiceIntent.setPackage(getPackageName());
		startService(restartServiceIntent);
		super.onTaskRemoved(rootIntent);
	}
}
