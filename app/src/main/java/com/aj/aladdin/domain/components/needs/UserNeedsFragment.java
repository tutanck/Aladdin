package com.aj.aladdin.domain.components.needs;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aj.aladdin.R;
import com.aj.aladdin.db.colls.NEEDS;
import com.aj.aladdin.db.colls.itf.Coll;
import com.aj.aladdin.main.A;
import com.aj.aladdin.utils.__;
import com.aj.aladdin.tools.regina.ack.UIAck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class UserNeedsFragment extends Fragment {

    public final static String coll = "NEEDS";

    private List<UserNeed> mUserNeeds = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private UserNeedsRecyclerAdapter mAdapter;


    public static UserNeedsFragment newInstance() {
        UserNeedsFragment fragment = new UserNeedsFragment();
        return fragment;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_user_needs, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.needs_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mAdapter = new UserNeedsRecyclerAdapter(mUserNeeds);
        mRecyclerView.setAdapter(mAdapter);

        setRecyclerViewItemTouchListener();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_add_need);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserNeedActivity.start(getContext());
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        NEEDS.loadNeeds(((A) getActivity().getApplication()).getUser_id(), new UIAck(getActivity()) {
            @Override
            protected void onRes(Object res, JSONObject ctx) {
                reloadList(res,mUserNeeds,mAdapter,getContext());
            }
        });
    }

    static void reloadList(Object res, List<UserNeed> userNeeds, UserNeedsRecyclerAdapter adapter, Context context){
        try {
            JSONArray jar = (JSONArray) res;
            userNeeds.clear();
            for (int i = 0; i < jar.length(); i++) {
                JSONObject jo = jar.getJSONObject(i);
                userNeeds.add(new UserNeed(jo.getString(Coll._idKey), jo.getString("title"), jo.getString("search"), jo.getBoolean("active"), context));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            __.fatal(e); //SNO : if a doc exist the Need field should exist too
        }
    }



    private void setRecyclerViewItemTouchListener() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0,  ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                ((UserNeedsRecyclerAdapter.ViewHolder) viewHolder).deleteNeed
                        (((A)getActivity().getApplication()).getUser_id(),mUserNeeds,mAdapter);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
}