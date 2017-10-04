package com.aj.aladdin.domain.components.needs;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aj.aladdin.R;
import com.aj.aladdin.db.colls.NEEDS;
import com.aj.aladdin.db.colls.itf.Coll;
import com.aj.aladdin.main.A;
import com.aj.aladdin.tools.components.others._Recycler;
import com.aj.aladdin.tools.utils.__;
import com.aj.aladdin.tools.regina.ack.UIAck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class UserNeedsFragment extends Fragment {

    private List<UserNeed> mUserNeeds = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private UserNeedsRecyclerAdapter mAdapter;

    public static UserNeedsFragment newInstance() {
        return new UserNeedsFragment();
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_user_needs, container, false);

        mRecyclerView = view.findViewById(R.id.needs_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new UserNeedsRecyclerAdapter(getContext(),mUserNeeds);
        mRecyclerView.setAdapter(mAdapter);

        setRecyclerViewItemTouchListener();

        FloatingActionButton fab = view.findViewById(R.id.fab_add_need);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserNeedNewSearchActivity.start(getContext());
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
                reloadList(res, mUserNeeds, mAdapter, getContext());
            }
        });
    }

    static void reloadList(Object res, List<UserNeed> userNeeds, UserNeedsRecyclerAdapter adapter, Context context) {
        try {
            JSONArray jar = (JSONArray) res;
            userNeeds.clear();
            for (int i = 0; i < jar.length(); i++) {
                JSONObject jo = jar.getJSONObject(i);
                userNeeds.add(new UserNeed(
                        jo.getString(Coll._idKey), jo.getString(NEEDS.titleKey)
                        , jo.getString(NEEDS.searchKey), jo.getBoolean(NEEDS.activeKey))
                );
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            __.fatal(e); //SNO : if a doc exist the Need field should exist too
        }
    }


    private void setRecyclerViewItemTouchListener() {
        mRecyclerView.addOnItemTouchListener(new _Recycler.ItemTouchListener(
                getContext(), mRecyclerView, new _Recycler.ClickListener() {

            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder, int position) {
                UserNeedAdActivity.start(getContext()
                        , ((UserNeedsRecyclerAdapter.ViewHolder) viewHolder).getUserNeed());
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder viewHolder, int position) {
                ((UserNeedsRecyclerAdapter.ViewHolder) viewHolder).deleteNeed(getActivity(),
                        ((A) getActivity().getApplication()).getUser_id(), mUserNeeds, mAdapter);
            }
        }));
    }


}