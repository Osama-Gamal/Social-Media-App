package com.wanjy.dannie.rivchat.ui;

import android.os.Bundle;  
import android.support.v4.app.Fragment;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  

import com.google.firebase.database.*;
import android.support.v7.widget.*;
import java.util.*;
import android.content.*;
import android.app.*;

import com.wanjy.dannie.rivchat.R;
import com.wanjy.dannie.rivchat.model.*;
import java.text.*;


public class LectureFragment extends Fragment {  


    DatabaseReference lectureference;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter ;
    
    List<LectureModel> list = new ArrayList<>();
    private Context contextt;
    LectureModel lectureinfo;

    public LectureFragment() {  

    }  

    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
                             Bundle savedInstanceState) {  

        View view = inflater.inflate(com.wanjy.dannie.rivchat.R.layout.fragment_lecture, container, false);
        contextt = view.getContext();



        return inflater.inflate(R.layout.fragment_lecture, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleListLecture);


        lectureference = FirebaseDatabase.getInstance().getReference("SoundsLecture");
        lectureference.keepSynced(true);
        
        lectureference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    list.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                        lectureinfo = postSnapshot.getValue(LectureModel.class);
                        list.add(lectureinfo);

                    }

                    adapter = new LectureAdabter(contextt,list);
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
