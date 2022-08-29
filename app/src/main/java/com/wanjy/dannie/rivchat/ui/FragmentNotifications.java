package com.wanjy.dannie.rivchat.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.*;
import java.util.*;
import android.content.*;
import android.app.*;
import android.widget.Toast;

import com.wanjy.dannie.rivchat.R;
import com.wanjy.dannie.rivchat.model.*;
import java.text.*;
import com.google.firebase.database.*;
import android.support.v7.widget.*;
import java.util.*;
import android.content.*;
import android.app.*;

public class FragmentNotifications extends Fragment
{

    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter ;
    List<NotificationModel> list = new ArrayList<>();
    private Context context;
    NotificationModel notificationModel;

    public FragmentNotifications() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        context = view.getContext();


        return inflater.inflate(R.layout.fragment_notification, container, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleListFriend);
        databaseReference = FirebaseDatabase.getInstance().getReference("Notification");
        databaseReference.keepSynced(true);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    notificationModel = postSnapshot.getValue(NotificationModel.class);
                    list.add(notificationModel);

                }

                adapter = new NotificationAdabter(context,list);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(layoutManager);




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.

            }
        });




        super.onViewCreated(view, savedInstanceState);
    }





}

