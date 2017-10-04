package com.aj.aladdin.domain.components.needs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.aj.aladdin.R;
import com.aj.aladdin.db.colls.NEEDS;
import com.aj.aladdin.db.colls.itf.Coll;
import com.aj.aladdin.main.A;
import com.aj.aladdin.tools.components.fragments.ProgressBarFragment;
import com.aj.aladdin.tools.components.fragments.FormField;
import com.aj.aladdin.tools.components.services.FormFieldKindTranslator;
import com.aj.aladdin.tools.regina.ack.VoidBAck;
import com.aj.aladdin.tools.utils.JSONServices;
import com.aj.aladdin.tools.utils.__;
import com.aj.aladdin.tools.regina.ack.UIAck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UserNeedSaveActivity extends AppCompatActivity implements FormField.Listener {

    private final static String _ID = "_ID";
    private final static String SEARCH_TEXT = "SEARCH_TEXT";

    private String _id = null;

    private boolean isFormOpen = false;

    private Map<String, FormField> formFields = new HashMap<>();

    ArrayList<FormField> adFormFields = new ArrayList<>();

    private JSONObject formParams;

    private Switch needSwitch;

    private FloatingActionButton fab;

    ProgressBarFragment progressBarFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_need_save);

        _id = getIntent().getStringExtra(_ID);

        progressBarFragment = (ProgressBarFragment) getSupportFragmentManager().findFragmentById(R.id.waiter_modal_fragment);


        if (savedInstanceState == null) //no duplicated fragments // TODO: 25/09/2017  check if frag only or else like listener on needSwitch
            try {
                formParams = JSONServices.loadJsonFromAsset("form_params_user_need.json", this);
                JSONArray orderedFieldsKeys = formParams.getJSONArray("ordered_fields_names");

                for (int i = 0; i < orderedFieldsKeys.length(); i++) {
                    String key = orderedFieldsKeys.getString(i);

                    JSONObject fieldParam = formParams.getJSONObject(key);

                    FormField formField = FormField.newInstance(i,
                            fieldParam.getString("label"), key, FormFieldKindTranslator.tr(fieldParam.getInt("kind")));

                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.need_form_layout, formField, key)
                            .commit();

                    if (i > 1)
                        adFormFields.add(formField);

                    formFields.put(key, formField);
                }

            } catch (JSONException e) {
                __.fatal(e);
            }

        needSwitch = (Switch) findViewById(R.id.need_switch);
        needSwitch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stretchForm(true);
                    }
                }
        );

        fab = (FloatingActionButton) findViewById(R.id.fab_save_need);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFormOpen)
                    open();
                else if (validState(view))
                    NEEDS.saveNeed(_id, ((A) getApplication()).getUser_id()
                            , needSwitch.isChecked(), formFields, new UIAck(UserNeedSaveActivity.this) {
                                @Override
                                protected void onRes(Object res, JSONObject ctx) {
                                    if (_id == null) try {
                                        _id = ((JSONObject) res).getString(Coll._idKey);
                                    } catch (JSONException e) {
                                        __.fatal(e);
                                    }
                                    close();
                                    __.showShortToast(UserNeedSaveActivity.this, "Recherche enregistrée");
                                    finish();
                                }
                            });
            }
        });
    }


    @Override
    public void onFormFieldCreated(int id, FormField formField) {
        formField.setText("");

        if (id == 0)
            formField.setText(getIntent().getStringExtra(SEARCH_TEXT));

        if (id > 1)
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(formField)
                    .commit();
    }


    private void stretchForm(boolean fromUser) {
        if (needSwitch.isChecked()) {
            if (fromUser) open();
            for (FormField ff : adFormFields)
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .show(ff)
                        .commit();
        } else {
            if (fromUser) close();
            for (FormField ff : adFormFields)
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .hide(ff)
                        .commit();
            NEEDS.deactivateNeed(_id, new VoidBAck(this));
        }
    }


    public static void start(Context context, String _id, String searchText) {
        Intent intent = new Intent(context, UserNeedSaveActivity.class);
        intent.putExtra(_ID, _id);
        intent.putExtra(SEARCH_TEXT, searchText);
        context.startActivity(intent);
        //((Activity)context).finish();
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (_id != null) {
            fab.setVisibility(View.GONE);
            progressBarFragment.show();
            NEEDS.loadNeed(_id, new UIAck(UserNeedSaveActivity.this) {
                @Override
                protected void onRes(Object res, JSONObject ctx) {
                    JSONArray jar = (JSONArray) res;
                    try {
                        JSONObject need = jar.getJSONObject(0);
                        for (String key : formFields.keySet())
                            formFields.get(key).getTvContent().setText(need.getString(key));
                        needSwitch.setChecked(need.getBoolean("active"));
                        stretchForm(false);
                    } catch (JSONException e) {
                        __.fatal(e);
                    }
                    progressBarFragment.hide();
                    fab.setVisibility(View.VISIBLE);
                }
            });
        }
    }


    private boolean validState(View view) {
        if (TextUtils.isEmpty(formFields.get("title").getEtContent().getText())) {
            __.showShortSnack(view, "Le titre doit être renseigné pour enregistrer le besoin");
            return false;
        } else if (needSwitch.isChecked() && TextUtils.isEmpty(formFields.get("description").getEtContent().getText())) {
            __.showShortSnack(view, "La description doit être renseignée pour être contacté");
            return false;
        }
        return true;
    }


    private void open() {
        for (String key : formFields.keySet())
            if (!key.equals(NEEDS.searchKey))
                formFields.get(key).open();
        fab.setImageResource(R.drawable.ic_done_24dp);
        isFormOpen = true;
    }

    private void close() {
        for (String key : formFields.keySet())
            formFields.get(key).close();
        fab.setImageResource(R.drawable.ic_mode_edit_24dp);
        isFormOpen = false;
    }


    /**
     * Secure mode style for app exit
     */
    private Boolean exitMode = false;

    @Override
    public void onBackPressed() {
        if (!isFormOpen || exitMode)
            super.onBackPressed();
        else if (isFormOpen) {
            __.showShortToast(this, "Cliquez de nouveau : les modifications ne seront pas sauvegardées.");
            exitMode = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitMode = false;
                }
            }, 3 * 1000);
        }
    }
}
