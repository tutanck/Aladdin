package com.aj.aladdin.domain.components.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import com.aj.aladdin.R;
import com.aj.aladdin.db.colls.PROFILES;
import com.aj.aladdin.db.colls.USER_RATINGS;
import com.aj.aladdin.main.A;
import com.aj.aladdin.domain.components.keywords.UserKeywordsActivity;
import com.aj.aladdin.domain.components.keywords.UtherKeywordsActivity;
import com.aj.aladdin.domain.components.messages.MessagesActivity;
import com.aj.aladdin.tools.components.fragments.IDKeyFormField;
import com.aj.aladdin.tools.components.fragments.ImageFragment;
import com.aj.aladdin.tools.components.services.FormFieldKindTranslator;
import com.aj.aladdin.tools.regina.ack.VoidBAck;
import com.aj.aladdin.tools.utils.JSONServices;
import com.aj.aladdin.tools.utils.__;
import com.aj.aladdin.tools.regina.ack.UIAck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {

    private static final String EDITABLE = "EDITABLE";

    private boolean isEditable = false;

    private String _id = null;

    private JSONObject formParams;

    private Map<String, IDKeyFormField> formFields = new HashMap<>();

    private RadioGroup userTypeRG;

    public static ProfileFragment newInstance(
            boolean editable
    ) {
        Bundle args = new Bundle();
        args.putBoolean(EDITABLE, editable);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {

        _id = ((A) getActivity().getApplication()).getUser_id();

        final Bundle args = getArguments();

        isEditable = args.getBoolean(EDITABLE);

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);


        RatingBar userRating = view.findViewById(R.id.user_rating);

        USER_RATINGS.computeUserRating(_id, new UIAck(getActivity()) {
            @Override
            protected void onRes(Object res, JSONObject ctx) {
                JSONObject ratingDoc = ((JSONArray) res).optJSONObject(0);
                try {
                    userRating.setRating(ratingDoc != null ? ratingDoc.getInt(USER_RATINGS.reputationKey) : 0);
                } catch (JSONException e) {
                    __.fatal(e); //SNO : if a doc exist the reputation should exist too
                }
            }
        });


        RatingBar ratingControl = view.findViewById(R.id.rating_control);

        if (!isEditable) {
            ratingControl.setIsIndicator(false);
            USER_RATINGS.getUserRating(_id, "todo", new UIAck(getActivity()) {
                @Override
                protected void onRes(Object res, JSONObject ctx) {
                    JSONObject ratingDoc = ((JSONArray) res).optJSONObject(0);
                    try {
                        ratingControl.setRating(ratingDoc != null ? ratingDoc.getInt(USER_RATINGS.ratingKey) : 0);
                    } catch (JSONException e) {
                        __.fatal(e); //SNO : if a doc exist the reputation should exist too
                    }
                }
            });

            ratingControl.setOnRatingBarChangeListener(
                    new RatingBar.OnRatingBarChangeListener() {
                        public void onRatingChanged(
                                RatingBar ratingBar
                                , float rating
                                , boolean fromUser
                        ) {
                            if (fromUser)
                                USER_RATINGS.setUtherRating(rating, _id, "todo", new VoidBAck(getActivity()));
                        }
                    }
            );
        } else ratingControl.setVisibility(View.GONE);


        try {
            formParams = JSONServices.loadJsonFromAsset("form_params_user_profile.json", getContext());
            JSONArray orderedFieldsKeys = formParams.getJSONArray("ordered_fields_names");

            userTypeRG = view.findViewById(R.id.user_type_radio_group);

            for (int i = 0; i < userTypeRG.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) userTypeRG.getChildAt(i);
                radioButton.setEnabled(isEditable);
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int radioButtonID = userTypeRG.getCheckedRadioButtonId();
                        RadioButton radioButton = userTypeRG.findViewById(radioButtonID);
                        int index = userTypeRG.indexOfChild(radioButton);
                        PROFILES.setField(_id, PROFILES.typeKey, index, new VoidBAck(getActivity()));
                    }
                });
            }


            if (savedInstanceState == null) {//no duplicated fragments // TODO: 25/09/2017  check if frag only or else like listener on needSwitch

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

                fragmentTransaction.add(R.id.profile_image_layout, ImageFragment.newInstance(
                        _id, isEditable), "profile_image");

                for (int i = 0; i < orderedFieldsKeys.length(); i++) {
                    String key = orderedFieldsKeys.getString(i);
                    JSONObject fieldParam = formParams.getJSONObject(key);

                    IDKeyFormField formField = IDKeyFormField.newInstance
                            (i, _id, fieldParam.getString("label"), key
                                    , FormFieldKindTranslator.tr(fieldParam.getInt("kind")), isEditable);

                    fragmentTransaction.add(R.id.form_layout, formField, key);
                    formFields.put(key, formField);
                }
                fragmentTransaction.commit();
            }

        } catch (JSONException e) {
            __.fatal(e);
        }


        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (!isEditable) {
                    intent = new Intent(getContext(), UtherKeywordsActivity.class);
                    intent.putExtra(UtherKeywordsActivity.USER_ID, "todo"); //// TODO: 04/10/2017  deplaacer ds un start
                } else intent = new Intent(getContext(), UserKeywordsActivity.class);
                startActivity(intent);
            }
        });


        FloatingActionButton fabContact = view.findViewById(R.id.fabContact);
        if (!isEditable)
            fabContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MessagesActivity.start(getContext(),"todo");
                }
            });
        else fabContact.setVisibility(View.GONE);


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        PROFILES.getProfile(_id, new UIAck(getActivity()) {
            @Override
            protected void onRes(Object res, JSONObject ctx) {
                JSONArray jar = (JSONArray) res;
                try {
                    JSONObject profile = jar.getJSONObject(0);

                    //RadioGroup::userTypeRG
                    int selectedIndex = ((JSONArray) res).getJSONObject(0).optInt(PROFILES.typeKey, 0);
                    ((RadioButton) userTypeRG.getChildAt(selectedIndex)).setChecked(true);

                    //FormField::all
                    for (String key : formFields.keySet())
                        formFields.get(key).getTvContent().setText(profile.optString(key));

                } catch (JSONException e) {
                    __.fatal(e);
                }
            }
        });

    }
}