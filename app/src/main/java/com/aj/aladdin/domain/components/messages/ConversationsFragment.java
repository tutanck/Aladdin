package com.aj.aladdin.domain.components.messages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aj.aladdin.R;
import com.aj.aladdin.db.colls.MESSAGES;
import com.aj.aladdin.db.colls.PROFILES;
import com.aj.aladdin.db.colls.itf.Coll;
import com.aj.aladdin.domain.components.profile.UserProfile;
import com.aj.aladdin.domain.components.profile.UserProfilesRecyclerAdapter;
import com.aj.aladdin.main.A;
import com.aj.aladdin.tools.regina.ack.UIAck;
import com.aj.aladdin.tools.utils.__;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ConversationsFragment extends Fragment {

    private ArrayList<UserProfile> mProfiles = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private UserProfilesRecyclerAdapter mAdapter;


    public static ConversationsFragment newInstance() {
        return new ConversationsFragment();
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.component_recycler_view, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new UserProfilesRecyclerAdapter(getContext(), mProfiles);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadContacts();
    }


    private void loadContacts() {
        MESSAGES.computeUserContacts(A.user_id(getActivity())
                , new UIAck(getActivity()) {
                    @Override
                    protected void onRes(Object res, JSONObject ctx) {
                        JSONArray jar = ((JSONArray) res);
                        if (jar.length() > 1)
                            __.fatal("loadContacts::MESSAGES.computeUserContacts::onRes : inconsistent result size. expected jar.length()==1");

                        if (jar.length() == 1) try {
                            PROFILES.getProfiles(jar.getJSONObject(0).getJSONArray("idList"), new UIAck(getActivity()) {
                                @Override
                                protected void onRes(Object res, JSONObject ctx) {
                                    try {
                                        JSONArray jar = (JSONArray) res;
                                        mProfiles.clear();
                                        for (int i = 0; i < jar.length(); i++) {
                                            JSONObject jo = jar.getJSONObject(i);
                                            mProfiles.add(new UserProfile(
                                                    jo.getString(Coll._idKey), jo.optString(PROFILES.usernameKey)//// TODO: 08/10/2017  passer en jo.getString(PROFILES.usernameKey) apres avoir trouvé un moy de forcer l'user à remplir au moins son username ou en ignorant les profils sans username
                                                    , 0, jo.getInt(PROFILES.availabilityKey)) //// TODO: 05/10/2017 : 0, true
                                            );
                                        }
                                        mAdapter.notifyDataSetChanged();
                                    } catch (JSONException e) {
                                        __.fatal(e); //SNO : if a doc exist the Need field should exist too
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            __.fatal(e);
                        }
                    }
                });
    }
}