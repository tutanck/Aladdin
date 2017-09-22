package com.aj.aladdin.tools.components.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aj.aladdin.R;

import java.util.ArrayList;


public class SimpleTextListViewFragment extends Fragment {

    private static final String DATA_LIST = "DATA_LIST";

    private ListView mListView;


    public static SimpleTextListViewFragment newInstance(
            ArrayList<String> stringArrayList
    ) {
        Bundle args = new Bundle();
        args.putStringArrayList(DATA_LIST, stringArrayList);
        SimpleTextListViewFragment fragment = new SimpleTextListViewFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_list_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> dataList = getArguments().getStringArrayList(DATA_LIST);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, dataList);

        mListView = view.findViewById(R.id.listview);
        mListView.setAdapter(adapter);
    }
}