package com.aj.aladdin.domain.components.needs;

import android.content.Context;
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
import com.aj.aladdin.domain.components.keywords.UserKeywordsActivity;
import com.aj.aladdin.domain.components.keywords.UtherKeywordsActivity;
import com.aj.aladdin.tools.components.services.IO;
import com.aj.aladdin.tools.oths.db.Colls;
import com.aj.aladdin.tools.oths.utils.__;
import com.aj.aladdin.tools.regina.Regina;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;


public class UserNeedsFragment extends Fragment {

    private final String _id = "59c13a29457ba52f74884c89";

    public final static String coll = Colls.USER_NEEDS;

    public final static String USERID = "userID";

    private ArrayList<UserNeed> mUserNeeds = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private UserNeedsRecyclerAdapter mAdapter;


    public static UserNeedsFragment newInstance(
    ) {
        UserNeedsFragment fragment = new UserNeedsFragment();
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
                Intent intent = new Intent(getContext(), UserNeedActivity.class);
                intent.putExtra(UserNeedActivity._ID, "joan");
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                startActivity(intent);
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
                    , __.jo().put(USERID, _id).put("deleted", false)
                    , __.jo().put("sort", __.jo().put("active", 1).put("title", 1)) // TODO: 22/09/2017 check
                    , __.jo()
                    , new Ack() {
                        @Override
                        public void call(Object... args) {
                            getActivity().runOnUiThread(new Runnable() { //mandatory to modify an activity's ui view
                                @Override
                                public void run() {
                                    if (args[0] != null) {
                                        __.showLongToast(getActivity(), "Une erreur s'est produite");
                                    } else try {
                                        JSONArray jar = (JSONArray) args[1];
                                        mUserNeeds.clear();
                                        for (int i = 0; i < jar.length(); i++) {
                                            JSONObject jo = jar.getJSONObject(i);
                                            mUserNeeds.add(new UserNeed(jo.getString("_id"), jo.getString("title"), jo.getBoolean("active")));
                                        }
                                        mAdapter.notifyDataSetChanged();
                                    } catch (JSONException e) {
                                        __.fatalError(e); //SNO : if a doc exist the Need field should exist too
                                    }
                                }
                            });
                        }
                    }
            );
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatalError(e);
        }
    }


    void setNeedStatus(String _id, boolean active, boolean deleted) {
        try {
            IO.r.update(coll
                    , __.jo().put("_id", _id)
                    , __.jo().put("active", active).put("deleted", deleted)
                    , __.jo()
                    , __.jo()
                    , new Ack() {
                        @Override
                        public void call(Object... args) {
                            getActivity().runOnUiThread(new Runnable() { //mandatory to modify an activity's ui view
                                @Override
                                public void run() {
                                    if (args[0] != null) {
                                        __.showLongToast(getActivity(), "Une erreur s'est produite");
                                    } else {
                                        loadNeeds();
                                    }
                                }
                            });
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