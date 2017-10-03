package com.aj.aladdin.domain.components.keywords;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aj.aladdin.R;
import com.aj.aladdin.db.colls.USER_KEYWORDS;
import com.aj.aladdin.tools.regina.ack.UIAck;
import com.aj.aladdin.utils.__;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UtherKeywordsActivity extends AppCompatActivity {

    public UtherKeywordsActivity self = UtherKeywordsActivity.this;

    public final static String USER_ID = "USER_ID";

    private ListView mListView;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component_list_view);

        mListView = (ListView) findViewById(R.id.list_view);

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        mListView.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();

        USER_KEYWORDS.loadUtherKeywords(getIntent().getStringExtra(USER_ID)
                , new UIAck(this) {
                    @Override
                    protected void onRes(Object res, JSONObject ctx) {
                        try {
                            JSONArray jar = (JSONArray) res;
                            adapter.clear();
                            for (int i = 0; i < jar.length(); i++)
                                adapter.add(jar.getJSONObject(i).getString(USER_KEYWORDS.keywordKey));
                        } catch (JSONException e) {
                            __.fatal(e); //SNO : if a doc exist the keyword field should exist too
                        }

                    }
                });
    }

}
