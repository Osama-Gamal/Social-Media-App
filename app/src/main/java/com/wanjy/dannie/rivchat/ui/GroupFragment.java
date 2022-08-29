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
  

public class GroupFragment extends Fragment {  
  
  
    DatabaseReference bookreference;
	RecyclerView recyclerView;
	RecyclerView.Adapter adapter ;
	
	List<ImageUploadInfo> list = new ArrayList<>();
	private Context contextt;
	ImageUploadInfo bookinfo;

    public GroupFragment() {  

    }  

    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
                             Bundle savedInstanceState) {  

		View view = inflater.inflate(com.wanjy.dannie.rivchat.R.layout.fragment_group, container, false);
        contextt = view.getContext();



        return inflater.inflate(R.layout.fragment_group, container, false);

    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{

		recyclerView = (RecyclerView) view.findViewById(R.id.recycleListGroup);



        
        bookreference = FirebaseDatabase.getInstance().getReference("Books_Uploads");
        bookreference.keepSynced(true);
        
        bookreference.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot snapshot) {
					list.clear();
					for (DataSnapshot postSnapshot : snapshot.getChildren()) {

						bookinfo = postSnapshot.getValue(ImageUploadInfo.class);
						list.add(bookinfo);

					}

					adapter = new BookViewAdapter(contextt,list);
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
