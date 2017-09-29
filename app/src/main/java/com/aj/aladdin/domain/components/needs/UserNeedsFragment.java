package com.aj.aladdin.domain.components.needs;

import android.content.Intent;
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
import com.aj.aladdin.tools.oths.db.IO;
import com.aj.aladdin.tools.oths.db.DB;
import com.aj.aladdin.tools.oths.utils.__;
import com.aj.aladdin.tools.regina.Regina;
import com.aj.aladdin.tools.utils.UIAck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class UserNeedsFragment extends Fragment {

    public final static String coll = DB.USER_NEEDS;

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
        loadNeeds();
    }

    private void loadNeeds() {
        try {
            IO.r.find(
                    coll
                    , __.jo().put("ownerID", "joan").put("deleted", false) //// TODO: 24/09/2017 joan
                    , __.jo().put("sort", __.jo().put("active", -1).put("title", 1))
                    , __.jo()
                    , new UIAck(getActivity()) {
                        @Override
                        protected void onRes(Object res, JSONObject ctx) {
                            try {
                                JSONArray jar = (JSONArray) res;
                                mUserNeeds.clear();
                                for (int i = 0; i < jar.length(); i++) {
                                    JSONObject jo = jar.getJSONObject(i);
                                    mUserNeeds.add(new UserNeed(jo.getString("_id"), jo.getString("title"), jo.getString("search"), jo.getBoolean("active"), getContext()));
                                }
                                mAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                __.fatalError(e); //SNO : if a doc exist the Need field should exist too
                            }
                        }
                    }
            );
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatalError(e);
        }
    }


    private void setRecyclerViewItemTouchListener() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                ((UserNeedsRecyclerAdapter.ViewHolder) viewHolder).deleteNeed();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
}