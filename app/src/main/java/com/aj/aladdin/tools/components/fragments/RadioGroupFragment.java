package com.aj.aladdin.tools.components.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.aj.aladdin.R;


public class RadioGroupFragment extends Fragment {
    private static final String RANK = "RANK";
    private static final String LABEL = "LABEL";
    private static final String HINT = "HINT";
    private static final String CONTENT = "CONTENT";

    private Listener mListener;


    public static RadioGroupFragment newInstance(
            String label
            , String hint
            , int rank
            , String content
    ) {
        Bundle args = new Bundle();
        args.putInt(RANK, rank);
        args.putString(LABEL, label);
        args.putString(HINT, hint);
        args.putString(CONTENT, content);
        RadioGroupFragment fragment = new RadioGroupFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_radio_group, container, false);

        final Bundle args = getArguments();

        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);

        final RadioButton radioCompany = (RadioButton) view.findViewById(R.id.radio_company);

        final RadioButton radioIndividual = (RadioButton) view.findViewById(R.id.radio_individual);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        };
        radioCompany.setOnClickListener(onClickListener);
        radioIndividual.setOnClickListener(onClickListener);


        return view;
    }


    private void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_company:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_individual:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof Listener)
            mListener = (Listener) context;
        else
            throw new RuntimeException(context.toString()
                    + " must implement Listener");*/
    }


    public interface Listener {
    }
}