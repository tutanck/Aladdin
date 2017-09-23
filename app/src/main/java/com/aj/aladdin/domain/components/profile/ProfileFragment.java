package com.aj.aladdin.domain.components.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aj.aladdin.R;
import com.aj.aladdin.domain.components.keywords.UserKeywordsActivity;
import com.aj.aladdin.domain.components.keywords.UtherKeywordsActivity;
import com.aj.aladdin.tools.components.fragments.autonomous.AutoRatingBar;
import com.aj.aladdin.tools.components.fragments.autonomous.QUBIFormField;
import com.aj.aladdin.tools.components.fragments.ImageFragment;
import com.aj.aladdin.tools.components.fragments.ItemDividerFragment;
import com.aj.aladdin.tools.components.fragments.autonomous.QUBIRadioGroup;
import com.aj.aladdin.tools.components.fragments.autonomous.QUBIRatingBar;
import com.aj.aladdin.tools.oths.db.Colls;


public class ProfileFragment extends Fragment {

    private static final String EDITABLE = "EDITABLE";

    private final String coll = Colls.USER_PROFILE;
    private final String _id = "59c13a29457ba52f74884c89";

    AppCompatActivity activity;


    //rg : radio_group
    private String[] rgKeys;
    private String[] rgLabels;

    //ff : form_field
    private String[] ffLabels;
    private String[] ffKeys;
    private int[] ffTypes;
    private String[] ffIndics; // TODO: 19/09/2017 See what to do


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
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = (AppCompatActivity) getActivity();

        final Resources resources = context.getResources();

        rgKeys = resources.getStringArray(R.array.profile_radio_group_keys);
        rgLabels = resources.getStringArray(R.array.profile_radio_group_labels);


        ffLabels = resources.getStringArray(R.array.profile_form_field_labels);
        ffKeys = resources.getStringArray(R.array.profile_form_field_keys);
        ffTypes = resources.getIntArray(R.array.profile_form_field_type);
        ffIndics = resources.getStringArray(R.array.profile_form_field_indications);
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        final Bundle args = getArguments();

        final boolean isEditable = args.getBoolean(EDITABLE);


        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.profile_image_layout, ImageFragment.newInstance(
                        ffLabels[0], ffIndics[0], 0, "" //todo
                ), "profile_image")
                .commit();

        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.rating_layout, AutoRatingBar.newInstance(
                        Colls.USER_RATING, "fictivID"
                ), "rating")
                .commit();

        if (!isEditable)
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.rating_control_layout, QUBIRatingBar.newInstance(
                            Colls.USER_RATING, _id, "fictivID"
                    ), "rating_control")
                    .commit();

        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.radio_group_layout, QUBIRadioGroup.newInstance(
                        coll, _id, rgKeys[0], rgLabels, isEditable
                ), "radio_group")
                .commit();


        for (int i = 0; i < ffLabels.length; i++) {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.form_layout, QUBIFormField.newInstance(
                            coll, _id, ffKeys[i], ffLabels[i], R.layout.fragment_form_field_multiline, isEditable
                    ), "form_field_" + i)//// TODO: 23/09/2017
                    .commit();

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.form_layout, ItemDividerFragment.newInstance(false), "item_divider" + i)
                    .commit();
        }


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (isEditable) {
                    intent = new Intent(getContext(), UserKeywordsActivity.class);
                    intent.putExtra(UserKeywordsActivity.USERID, "joan");
                } else {
                    intent = new Intent(getContext(), UtherKeywordsActivity.class);
                    intent.putExtra(UtherKeywordsActivity.USERID, "joan");
                }
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                startActivity(intent);

            }
        });

        return view;
    }
}