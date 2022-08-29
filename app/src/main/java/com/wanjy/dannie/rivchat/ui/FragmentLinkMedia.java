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

public class FragmentLinkMedia extends Fragment
{

    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter ;

    CommentAdapter commentAdapter;
    //public static List<Comment> listComment;
    private Context context;
    Comment comment;

    public FragmentLinkMedia() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_link_media, container, false);
        context = view.getContext();



        return inflater.inflate(R.layout.fragment_link_media, container, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleListFriend);


        adapter = new CommentAdapter(context, ChatAllFragment.listLinks);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 1);
        recyclerView.setLayoutManager(mLayoutManager);






        super.onViewCreated(view, savedInstanceState);
    }





}
