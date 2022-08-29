package com.wanjy.dannie.rivchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.wanjy.dannie.rivchat.service.*;
import android.os.*;

public class MyReciever extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {



    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = cm.getActiveNetworkInfo();
       if (info != null) {
        if (info.isConnected()) {

            Intent intent2 = new Intent(context, ServiceNotif.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.startForegroundService(intent2);
            } else {
                context.startService(intent2);
            }


        }
        else {

            Intent intent2 = new Intent(context, ServiceNotif.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //context.stopForegroundService(intent2);
            } else {
                context.stopService(intent2);
            }


        }
    }
}




	
	
	
}
