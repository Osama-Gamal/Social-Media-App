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

import com.wanjy.dannie.rivchat.R;
import com.wanjy.dannie.rivchat.model.*;
import android.content.*;
import android.util.*;
import android.widget.*;
import java.text.*;
import android.support.v4.widget.SwipeRefreshLayout;  
  
public class FriendsFragment extends Fragment
{

	
	
	
DatabaseReference databaseReference;
	RecyclerView recyclerView;
	RecyclerView.Adapter adapter ;
	ProgressDialog progressDialog;
	List<ImageUploadInfo> list = new ArrayList<>();
	private Context context;
ImageUploadInfo imageUploadInfo;
	
    public FriendsFragment() {  
        
    }  
  
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
                             Bundle savedInstanceState) {  
        
		View view = inflater.inflate(com.wanjy.dannie.rivchat.R.layout.fragment_people, container, false);
        context = view.getContext();


        return inflater.inflate(R.layout.fragment_people, container, false);
		
    }

	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		
		recyclerView = (RecyclerView) view.findViewById(R.id.recycleListFriend);
		initData();


		
		
		
		super.onViewCreated(view, savedInstanceState);
	}



	public void initData(){
		
		databaseReference = FirebaseDatabase.getInstance().getReference("All_Image_Uploads_Database");
        databaseReference.keepSynced(true);
        
        databaseReference.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot snapshot) {
					list.clear();
					for (DataSnapshot postSnapshot : snapshot.getChildren()) {


						imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);
						list.add(imageUploadInfo);


					}

					adapter = new RecyclerViewAdapter(context, list);
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

				}
			});
	}
	
  
	

    
    
    
    
		
			
}  
