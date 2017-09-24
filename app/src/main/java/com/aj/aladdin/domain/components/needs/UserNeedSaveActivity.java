package com.aj.aladdin.domain.components.needs;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Switch;

import com.aj.aladdin.R;
import com.aj.aladdin.tools.components.fragments.ItemDividerFragment;
import com.aj.aladdin.tools.components.fragments.simple.FormField;
import com.aj.aladdin.tools.components.services.FormFieldKindTranslator;
import com.aj.aladdin.tools.components.services.IO;
import com.aj.aladdin.tools.oths.db.Colls;
import com.aj.aladdin.tools.oths.utils.JSONServices;
import com.aj.aladdin.tools.oths.utils.__;
import com.aj.aladdin.tools.regina.Regina;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.socket.client.Ack;

public class UserNeedSaveActivity extends AppCompatActivity {

    public final static String _ID = "_ID";

    private String _id = "59c704cb2d1ceb027bfbd817";

    private UserNeedSaveActivity self = this;

    private boolean isFormOpen = false;

    private Map<String, FormField> formFields = new HashMap<>();

    private JSONObject formParams;

    private Switch needSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_need_save);

        needSwitch = (Switch) findViewById(R.id.need_switch);

        formParams = JSONServices.loadJsonFromAsset("form_params_user_need.json", this);
        JSONArray orderedFieldsKeys;
        try {
            orderedFieldsKeys = formParams.getJSONArray("ordered_fields_names");

            for (int i = 0; i < orderedFieldsKeys.length(); i++) {
                String key = orderedFieldsKeys.getString(i);

                JSONObject fieldParam = formParams.getJSONObject(key);

                FormField formField = FormField.newInstance(
                        fieldParam.getString("label"), FormFieldKindTranslator.tr(fieldParam.getInt("kind")));

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.form_layout, formField, key)
                        .commit();

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.form_layout, ItemDividerFragment.newInstance(false), "item_divider" + i)
                        .commit();

                formFields.put(key, formField);
            }

        } catch (JSONException e) {
            __.fatalError(e);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_save_need);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFormOpen) open();
                else if (validState(view)) saveState();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (_id != null)
            loadState();
    }


    private void loadState() {
        try {
            IO.r.find(Colls.USER_NEEDS, __.jo().put("_id", _id), __.jo(), __.jo(), new Ack() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() { //mandatory to modify an activity's ui view
                        @Override
                        public void run() {
                            if (args[0] != null)
                                __.showShortToast(self, "Une erreur s'est produite");
                            else {
                                JSONArray jar = (JSONArray) args[1];
                                try {
                                    JSONObject need = jar.getJSONObject(0);
                                    for (String key : formFields.keySet())
                                        formFields.get(key).getTvContent().setText(need.getString(key));
                                    needSwitch.setChecked(need.getBoolean("active"));
                                } catch (JSONException e) {
                                    __.fatalError(e);
                                }
                            }
                        }
                    });
                }
            });
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatalError(e);
        }
    }


    private void saveState() {
        try {
            JSONObject need = __.jo().put("active", needSwitch.isChecked()).put("deleted", false);

            for (String key : formFields.keySet())
                need.put(key, formFields.get(key).getTvContent().getText());

            if (_id == null)
                IO.r.insert(Colls.USER_NEEDS, need, __.jo(), __.jo(), ackIn());
            else
                IO.r.update(Colls.USER_NEEDS, __.jo().put("_id", _id), need, __.jo(), __.jo(), ackIn());

        } catch (JSONException | Regina.NullRequiredParameterException e) {
            __.fatalError(e);
        }
    }


    private Ack ackIn() {
        return new Ack() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() { //mandatory to modify an activity's ui view
                    @Override
                    public void run() {
                        close();
                        __.showShortToast
                                (self, args[0] != null ? "Une erreur s'est produite" : "Recherche enregistrée");
                    }
                });
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

}
