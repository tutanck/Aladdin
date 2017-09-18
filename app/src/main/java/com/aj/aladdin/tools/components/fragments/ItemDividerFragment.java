package com.aj.aladdin.tools.components.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aj.aladdin.R;


public class ItemDividerFragment extends Fragment {

    private static final String VISIBLE = "VISIBLE";

    public static ItemDividerFragment newInstance(
            boolean visible
    ) {
        Bundle args = new Bundle();
        args.putBoolean(VISIBLE, visible);
        ItemDividerFragment fragment = new ItemDividerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        return getArguments().getBoolean(VISIBLE) ?
                inflater.inflate(R.layout.item_divider, container, false)
                :
                inflater.inflate(R.layout.item_divider_transparent, container, false);
    }

}