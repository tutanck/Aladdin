package com.aj.aladdin.domain.components.keywords;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aj.aladdin.R;
import com.aj.aladdin.db.IO;
import com.aj.aladdin.utils.__;
import com.aj.aladdin.tools.regina.Regina;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;

public class UtherKeywordsActivity extends AppCompatActivity {

    public UtherKeywordsActivity self = UtherKeywordsActivity.this;

    public final static String USERID = "userID";

    private ListView mListView;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component_list_view);

        mListView = (ListView) findViewById(R.id.listview);

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        mListView.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();

        try {
            IO.r.find(
                    "users"
                    , new JSONObject().put("username", "luffy")
                    , new JSONObject()
                    , new JSONObject()
                    , new Ack() {
                        @Override
                        public void call(Object... args) {
                            if (args[0] != null)
                                System.out.println("result: " + args[1]);
                            else
                                System.out.println("error : " + args[0] + " context :" + args[2]);
                        }
                    }
            );
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            e.printStackTrace();
        }



        try {
            IO.r.find(
                    "USER_KEYWORDS"
                    , __.jo().put(USERID, getIntent().getStringExtra(USERID)).put("active",true).put("deleted",false)
                    , __.jo()
                    , __.jo()
                    , new Ack() {
                        @Override
                        public void call(Object... args) {
                            runOnUiThread(new Runnable() { //mandatory to modify an activity's ui view
                                @Override
                                public void run() {
                                    if (args[0] != null) {
                                        __.showLongToast(self, "Une erreur s'est produite");
                                        finish();
                                    } else try {
                                        JSONArray jar = (JSONArray) args[1];
                                        adapter.clear();
                                        for (int i = 0; i < jar.length(); i++)
                                            adapter.add(jar.getJSONObject(i).getString("keyword"));
                                    } catch (JSONException e) {
                                        __.fatal(e); //SNO : if a doc exist the keyword field should exist too
                                    }
                                }
                            });
                        }
                    }
            );
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }

}
