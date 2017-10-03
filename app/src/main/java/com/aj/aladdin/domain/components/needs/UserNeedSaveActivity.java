package com.aj.aladdin.domain.components.needs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Switch;

import com.aj.aladdin.R;
import com.aj.aladdin.db.itf.MongoColl;
import com.aj.aladdin.tools.components.fragments.ProgressBarFragment;
import com.aj.aladdin.tools.components.fragments.FormField;
import com.aj.aladdin.tools.components.services.FormFieldKindTranslator;
import com.aj.aladdin.db.IO;
import com.aj.aladdin.utils.JSONServices;
import com.aj.aladdin.utils.__;
import com.aj.aladdin.tools.regina.Regina;
import com.aj.aladdin.tools.regina.ack.UIAck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UserNeedSaveActivity extends AppCompatActivity implements FormField.Listener {

    private final String coll = "NEEDS";

    public final static String _ID = "_ID";

    private String _id = null;

    private UserNeedSaveActivity self = this;

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
                            fieldParam.getString("label"),"resume", FormFieldKindTranslator.tr(fieldParam.getInt("kind")));

                    FragmentManager fragmentManager = getSupportFragmentManager();

                    fragmentManager
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
                if (!isFormOpen) open();
                else if (validState(view)) saveState();
            }
        });
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
            deactivateNeed();
        }
    }


    @Override
    public void onFormFieldCreated(int id, FormField formField) {
        if (id > 1)
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(formField)
                    .commit();
    }


    public static void start(Context context, String _id) {
        Intent intent = new Intent(context, UserNeedSaveActivity.class);
        intent.putExtra(_ID, _id);
        context.startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (_id != null) {
            fab.setVisibility(View.GONE);
            progressBarFragment.show();
            loadState();
        }
    }


    void deactivateNeed() {
        try {
            IO.r.update(coll, __.jo().put(MongoColl._idKey, _id)
                    , __.jo().put("$set", __.jo().put("active", false))
                    , __.jo(), __.jo()
                    , new UIAck(self) {
                        @Override
                        protected void onRes(Object res, JSONObject ctx) {
                            __.chill("saveStatus");
                        }
                    });
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }


    private void loadState() {
        try {
            IO.r.find(coll, __.jo().put(MongoColl._idKey, _id), __.jo(), __.jo(), new UIAck(self) {
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
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }


    private void saveState() {
        try {
            JSONObject need = __.jo().put("active", needSwitch.isChecked()).put("ownerID", "joan"); //// TODO: 24/09/2017

            for (String key : formFields.keySet())
                if (key.equals("search"))
                    need.put(key, formFields.get(key).getTvContent().getText());
                else
                    need.put(key, formFields.get(key).getEtContent().getText());

            if (_id == null)
                IO.r.insert(coll, need.put("deleted", false), __.jo(), __.jo(), ackIn());
            else
                IO.r.update(coll, __.jo().put(MongoColl._idKey, _id), __.jo().put("$set", need), __.jo(), __.jo(), ackIn());

        } catch (JSONException | Regina.NullRequiredParameterException e) {
            __.fatal(e);
        }
    }


    private UIAck ackIn() {
        return new UIAck(self) {
            @Override
            protected void onRes(Object res, JSONObject ctx) {
                close();
                __.showShortToast(self, "Recherche enregistrée");
            }
        };
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
            if (!key.equals("search"))
                formFields.get(key).open();
        isFormOpen = true;
    }

    private void close() {
        for (String key : formFields.keySet())
            formFields.get(key).close();
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
