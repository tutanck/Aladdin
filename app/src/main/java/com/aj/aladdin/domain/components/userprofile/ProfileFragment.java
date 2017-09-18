package com.aj.aladdin.domain.components.userprofile;

import android.app.Activity;
import android.content.Context;
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
import com.aj.aladdin.tools.components.fragments.FormFieldFragment;
import com.aj.aladdin.tools.components.fragments.ImageFragment;
import com.aj.aladdin.tools.components.fragments.ItemDividerFragment;
import com.aj.aladdin.tools.components.fragments.RadioGroupFragment;
import com.aj.aladdin.tools.components.fragments.RatingControlFragment;


public class ProfileFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private String[] mSamples;


    private Listener mListener;

    private String[] mLabels;
    private String[] mIndics;


    private int mPage;

    public static ProfileFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
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

        final Activity activity = getActivity();


        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        ((AppCompatActivity) activity).getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.profile_image_layout, ImageFragment.newInstance(
                        mLabels[0], mIndics[0], 0, mSamples[0]
                ), "profile_image")
                .commit();

        ((AppCompatActivity) activity).getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.rating_layout, RatingControlFragment.newInstance(
                        mLabels[0], mIndics[0], 0, mSamples[0]
                ), "rating")
                .commit();

        ((AppCompatActivity) activity).getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.rating_control_layout, RatingControlFragment.newInstance(
                        mLabels[0], mIndics[0], 0, mSamples[0]
                ), "rating_control")
                .commit();

        ((AppCompatActivity) activity).getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.radio_group_layout, RadioGroupFragment.newInstance(
                        mLabels[0], mIndics[0], 0, mSamples[0]
                ), "radio_group")
                .commit();

        for (int i = 0; i < mLabels.length; i++) {
            ((AppCompatActivity) activity).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.form_layout, FormFieldFragment.newInstance(
                            mLabels[i], true, mSamples[i]
                    ), "form_field_" + i)
                    .commit();
            ((AppCompatActivity) activity).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.form_layout, ItemDividerFragment.newInstance(false), "item_divider" + i)
                    .commit();

        }

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof Listener)
            mListener = (Listener) context;
        else
            throw new RuntimeException(context.toString()
                    + " must implement Listener");*/

        final Resources resources = context.getResources();
        mLabels = resources.getStringArray(R.array.profile_form_field_labels);
        mIndics = resources.getStringArray(R.array.profile_form_field_indications);

        mSamples = resources.getStringArray(R.array.sample_contents);

        if (mLabels.length != mIndics.length)
            throw new RuntimeException("Form Fields resources are not consistent !");
    }

    public interface Listener {
    }
}