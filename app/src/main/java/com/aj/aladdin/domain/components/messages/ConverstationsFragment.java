package com.aj.aladdin.domain.components.messages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aj.aladdin.R;
import com.aj.aladdin.domain.components.profile.UserProfile;
import com.aj.aladdin.domain.components.profile.UserProfilesRecyclerAdapter;

import java.util.ArrayList;


public class ConverstationsFragment extends Fragment {

    private ArrayList<UserProfile> mProfiles = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private UserProfilesRecyclerAdapter mAdapter;


    public static ConverstationsFragment newInstance() {
        ConverstationsFragment fragment = new ConverstationsFragment();
        return fragment;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.component_recycler_view, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mAdapter = new UserProfilesRecyclerAdapter(getContext(), mProfiles);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadMessages();
    }


    private void loadMessages() {
        //// TODO: 26/09/2017
    }
}