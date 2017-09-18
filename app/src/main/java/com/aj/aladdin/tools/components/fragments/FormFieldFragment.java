package com.aj.aladdin.tools.components.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aj.aladdin.R;
import com.aj.aladdin.tools.components.model.AutonomousDBFragment;
import com.aj.aladdin.tools.components.services.ComponentsServices;
import com.aj.aladdin.tools.oths.KeyboardServices;
import com.aj.aladdin.tools.regina.Regina;

import org.json.JSONException;

import io.socket.client.Ack;


public class FormFieldFragment extends AutonomousDBFragment {

    private static final String OPEN = "OPEN";
    private static final String LABEL = "LABEL";
    private static final String INDIC = "INDIC";
    private static final String CONTENT = "CONTENT";

    private Listener mListener;

    private boolean openStatus = false;

    public static FormFieldFragment newInstance(
            String label
            , boolean open
            , String content
    ) {
        Bundle args = new Bundle();
        args.putBoolean(OPEN, open);
        args.putString(LABEL, label);
        args.putString(CONTENT, content);
        FormFieldFragment fragment = new FormFieldFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_form_field, container, false);

        final ImageView ivIndication = (ImageView) view.findViewById(R.id.ivIndication);

        final TextView tvContent = (TextView) view.findViewById(R.id.tvContent);

        final EditText etContent = (EditText) view.findViewById(R.id.etContent);

        final TextInputLayout textInputLayout = (TextInputLayout) view.findViewById(R.id.text_input_layout);

        final TextView tvDescription = (TextView) view.findViewById(R.id.tvDescription);


        final Bundle args = getArguments();

        String label = args.getString(LABEL);
        //String indic = args.getString(INDIC);

        if (args.getBoolean(OPEN))
            ComponentsServices.setSelectable(
                    getContext(), view.findViewById(R.id.form_field_layout), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!openStatus) { //open it as input
                                etContent.setText(tvContent.getText());
                                etContent.setVisibility(View.VISIBLE);
                                tvContent.setVisibility(View.GONE);
                                tvDescription.setVisibility(View.GONE);
                            } else { //close it as selectable view
                                tvContent.setText(etContent.getText());
                                etContent.setVisibility(View.GONE);
                                tvContent.setVisibility(View.VISIBLE);
                                tvDescription.setVisibility(View.VISIBLE);
                                KeyboardServices.dismiss(getContext(), etContent);
                                try {
                                    saveState(tvContent.getText());
                                } catch (InvalidStateException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Regina.NullRequiredParameterException e) {
                                    e.printStackTrace();
                                } catch (AutonomousDBFragmentNotInitializedException e) {
                                    e.printStackTrace();
                                }
                            }
                            openStatus = !openStatus;
                        }
                    });

        tvContent.setText(args.getString(CONTENT));
        textInputLayout.setHint(label);
        tvDescription.setText(label);

        etContent.setVisibility(View.GONE);

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
    }

    @Override
    protected Ack loadStateAck() {
        return null; //todo
    }


    public interface Listener {
    }
}