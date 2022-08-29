package com.wanjy.dannie.rivchat.ui;

import android.os.Bundle;  
import android.support.v4.app.Fragment;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;

import com.google.firebase.database.*;
import android.support.v7.widget.*;
import android.app.*;
import java.util.*;

import com.wanjy.dannie.rivchat.Home;
import com.wanjy.dannie.rivchat.R;
import com.wanjy.dannie.rivchat.model.*;
import android.content.*;
import android.util.*;
import android.widget.*;
import java.text.*;
import android.support.v4.widget.SwipeRefreshLayout;  

public class FragmentStudents extends Fragment
{




    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter ;
    ProgressDialog progressDialog;
    private Context context;
    User StudentUploadInfo;

    public FragmentStudents() {  

    }  

    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
                             Bundle savedInstanceState) {  

        View view = inflater.inflate(com.wanjy.dannie.rivchat.R.layout.fragment_students, container, false);
        context = view.getContext();




        return inflater.inflate(R.layout.fragment_students, container, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleListFriend);
        initData();




        super.onViewCreated(view, savedInstanceState);
    }




    public void initData(){



                    adapter = new UserViewAdabter(context, Home.listStudents);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.setHasFixedSize(true);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setReverseLayout(true);
                    layoutManager.setStackFromEnd(true);

                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 2);
                    recyclerView.setLayoutManager(mLayoutManager);


    }
    
    
	}
