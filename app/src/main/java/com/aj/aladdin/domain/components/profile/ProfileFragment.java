package com.aj.aladdin.domain.components.profile;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.aj.aladdin.tools.components.fragments.simple.FormField;
import com.aj.aladdin.tools.components.services.FormFieldKindTranslator;
import com.aj.aladdin.tools.oths.db.DB;
import com.aj.aladdin.tools.oths.utils.JSONServices;
import com.aj.aladdin.tools.oths.utils.__;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ProfileFragment extends Fragment {

    private static final String EDITABLE = "EDITABLE";

    private final String coll = DB.USER_PROFILE;
    private final String _id = "59c13a29457ba52f74884c89";

    private JSONObject formParams;

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
        final Bundle args = getArguments();

        final boolean isEditable = args.getBoolean(EDITABLE);

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();


        if (savedInstanceState == null) //no duplicated fragments // TODO: 25/09/2017  check if frag only or else like listener on needSwitch
            try {

                formParams = JSONServices.loadJsonFromAsset("form_params_user_profile.json", getContext());
                JSONArray orderedFieldsKeys = formParams.getJSONArray("ordered_fields_names");

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.add(R.id.profile_image_layout, ImageFragment.newInstance(
                        _id, isEditable), "profile_image");

                String typeKey = "type";
                JSONArray typeLabels = formParams.getJSONObject(typeKey).getJSONArray("label");

                fragmentTransaction.add(R.id.radio_group_layout, QUBIRadioGroup.newInstance(
                        coll, _id, typeKey, new String[]
                                {typeLabels.getString(0), typeLabels.getString(1)}, isEditable
                ), "type");

                fragmentTransaction.add(R.id.rating_layout, AutoRatingBar.newInstance(
                        DB.USER_RATING, "fictivID" //// TODO: 01/10/2017
                ), "rating");

                if (!isEditable)
                    fragmentTransaction.add(R.id.rating_control_layout, QUBIRatingBar.newInstance(
                            DB.USER_RATING, _id, "fictivID" //// TODO: 01/10/2017
                    ), "rating_control");

                for (int i = 0; i < orderedFieldsKeys.length(); i++) {
                    String key = orderedFieldsKeys.getString(i);
                    JSONObject fieldParam = formParams.getJSONObject(key);

                    QUBIFormField qubiFormField = QUBIFormField.newInstance(
                            coll, _id, key, fieldParam.getString("label")
                            , FormFieldKindTranslator.tr(fieldParam.getInt("kind"))
                            , isEditable);

                    fragmentTransaction.add(R.id.form_layout, qubiFormField, key);
                }
                fragmentTransaction.commit();

            } catch (JSONException e) {
                __.fatalError(e);
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