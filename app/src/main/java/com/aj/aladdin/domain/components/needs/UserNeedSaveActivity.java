package com.aj.aladdin.domain.components.needs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.aj.aladdin.R;
import com.aj.aladdin.db.colls.NEEDS;

import com.aj.aladdin.db.colls.itf.Coll;
import com.aj.aladdin.main.A;
import com.aj.aladdin.tools.components.fragments.ProgressBarFragment;
import com.aj.aladdin.tools.components.fragments.FormField;
import com.aj.aladdin.tools.components.services.ComponentsServices;
import com.aj.aladdin.tools.components.services.FormFieldKindTranslator;

import com.aj.aladdin.tools.utils.JSONServices;
import com.aj.aladdin.tools.utils.__;
import com.aj.aladdin.tools.regina.ack.UIAck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class UserNeedSaveActivity extends AppCompatActivity implements FormField.Listener {

    ProgressBarFragment progressBarFragment;

    private final static String _ID = "_ID";
    private final static String SEARCH_TEXT = "SEARCH_TEXT";

    private String _id = null;

    private boolean isFormOpen = false;

    private Map<String, FormField> formFields = new HashMap<>();

    private Switch needSwitch;

    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_need_save);

        _id = getIntent().getStringExtra(_ID);

        progressBarFragment = (ProgressBarFragment) getSupportFragmentManager().findFragmentById(R.id.waiter_modal_fragment);

        //no duplicated fragments // TODO: 25/09/2017  check if frag only or else like listener on needSwitch
        if (savedInstanceState == null) try {
            JSONObject formParams = JSONServices.loadJsonFromAsset("form_params_user_need.json", this);
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

                formFields.put(key, formField);
            }

        } catch (JSONException e) {
            __.fatal(e);
        }


        needSwitch = (Switch) findViewById(R.id.need_switch);
        needSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setVisibility(View.VISIBLE);
            }
        });


        fab = (FloatingActionButton) findViewById(R.id.fab_save_need);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validState())
                    NEEDS.saveNeed(_id, A.user_id(UserNeedSaveActivity.this)
                            , needSwitch.isChecked(), formFields, new UIAck(UserNeedSaveActivity.this) {
                                @Override
                                protected void onRes(Object res, JSONObject ctx) {
                                    if (_id == null) try { //robust code
                                        _id = ((JSONObject) res).getString(Coll._idKey);
                                    } catch (JSONException e) {
                                        __.fatal(e);
                                    }
                                    close();
                                    __.showShortToast(UserNeedSaveActivity.this, "Mise à jour réussie !");
                                    //finish(); // TODO: 04/10/2017
                                }
                            });
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (_id == null) {
            needSwitch.setChecked(true);
            formFields.get(NEEDS.searchKey).setText(getIntent().getStringExtra(SEARCH_TEXT));
            open();
        } else {
            progressBarFragment.show();
            NEEDS.loadNeed(_id, new UIAck(UserNeedSaveActivity.this) {
                @Override
                protected void onRes(Object res, JSONObject ctx) {
                    try {
                        JSONObject need = ((JSONArray) res).getJSONObject(0);
                        for (String key : formFields.keySet())
                            formFields.get(key).setText(need.getString(key));
                        needSwitch.setChecked(need.getBoolean(NEEDS.activeKey));
                    } catch (JSONException e) {
                        __.fatal(e);
                    }
                    progressBarFragment.hide();
                }
            });
        }
    }


    public static void start(Activity activity, String str, boolean update) {
        Intent intent = new Intent(activity, UserNeedSaveActivity.class);
        intent.putExtra(update ? _ID : SEARCH_TEXT, str);
        activity.startActivity(intent);
        if (!update) activity.finish();
    }


    private void open() {
        for (String key : formFields.keySet())
            if (!key.equals(NEEDS.searchKey))
                formFields.get(key).open();
        fab.setVisibility(View.VISIBLE);
        isFormOpen = true;
    }

    private void close() {
        for (String key : formFields.keySet())
            formFields.get(key).close();
        fab.setVisibility(View.GONE);
        isFormOpen = false;
    }


    private boolean validState() {
        EditText titleET = formFields.get(NEEDS.titleKey).getEtContent();
        EditText descriptionET = formFields.get(NEEDS.descriptionKey).getEtContent();
        if (TextUtils.isEmpty(titleET.getText())) {
            titleET.setError("Le titre doit être renseigné !");
            return false;
        }
        titleET.setError(null);
        if (TextUtils.isEmpty(descriptionET.getText())) {
            descriptionET.setError("La description doit être renseignée !");
            return false;
        }
        descriptionET.setError(null);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (fab.getVisibility() == View.VISIBLE)
            Snackbar.make(fab, "Les modifications seront perdues !", Snackbar.LENGTH_SHORT)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserNeedSaveActivity.super.onBackPressed();
                    }
                }).show();
        else super.onBackPressed();
    }


    @Override
    public void onFormFieldCreated(int id, FormField formField) {
        ComponentsServices.setSelectable(
                this, formField.getLayout(), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isFormOpen) open();
                    }
                }
        );
    }
}
