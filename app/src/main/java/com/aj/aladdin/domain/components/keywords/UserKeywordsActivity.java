package com.aj.aladdin.domain.components.keywords;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aj.aladdin.R;
import com.aj.aladdin.tools.oths.db.IO;
import com.aj.aladdin.tools.oths.db.DB;
import com.aj.aladdin.tools.oths.utils.__;
import com.aj.aladdin.tools.regina.Regina;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;

public class UserKeywordsActivity extends AppCompatActivity {

    public UserKeywordsActivity self = UserKeywordsActivity.this;


    public final static String coll = DB.USER_KEYWORDS;

    public final static String USERID = "userID";

    private ArrayList<UserKeyword> mUserKeywords = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private UserKeywordsRecyclerAdapter mAdapter;

    private EditText etKeyword;
    private Button btnAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_keywords);

        btnAdd = (Button) findViewById(R.id.add_keyword_button);
        btnAdd.setEnabled(false);

        etKeyword = (EditText) findViewById(R.id.add_keyword_input);


        mRecyclerView = (RecyclerView) findViewById(R.id.keywords_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mAdapter = new UserKeywordsRecyclerAdapter(mUserKeywords);
        mRecyclerView.setAdapter(mAdapter);

        functionalizeETKeyword();
        functionalizeBtnAdd();
        setRecyclerViewItemTouchListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadKeywords();
    }

    private void loadKeywords() {
        try {
            IO.r.find(
                    coll
                    , __.jo().put(USERID, getIntent().getStringExtra(USERID)).put("deleted",false)
                    , __.jo().put("sort", __.jo().put("keyword", 1))
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
                                        mUserKeywords.clear();
                                        for (int i = 0; i < jar.length(); i++) {
                                            JSONObject jo = jar.getJSONObject(i);
                                            mUserKeywords.add(new UserKeyword(jo.getString("keyword"), jo.getBoolean("active"),self));
                                        }
                                        mAdapter.notifyDataSetChanged();
                                    } catch (JSONException e) {
                                        __.fatalError(e); //SNO : if a doc exist the keyword field should exist too
                                    }
                                }
                            });
                        }
                    }
            );
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatalError(e);
        }
    }


    private void functionalizeBtnAdd() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeyword(etKeyword.getText().toString(),true,false);
            }
        });
    }


    private boolean isKeyword(String input) {
        return !TextUtils.isEmpty(input) && !__.found("[^a-zA-Z0-9]", input);
    }

    void setKeyword(String input, boolean active, boolean deleted) {
        String keyword = input.trim();
        if (!isKeyword(keyword)) {
            __.showLongSnack(btnAdd, "Un mot-clé est composé d'un seul mot (caractères alphanumériques sans accents).");
        } else try {
            String userID = getIntent().getStringExtra(USERID);
            IO.r.update(coll
                    , __.jo().put(USERID, userID).put("keyword", keyword)
                    , __.jo().put(USERID, userID).put("keyword", keyword).put("active", active).put("deleted",deleted)
                    , __.jo().put("upsert", true)
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
                                    } else {
                                        etKeyword.setText("");
                                        loadKeywords();
                                    }
                                }
                            });
                        }
                    }
            );
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatalError(e);
        }
    }


    private void functionalizeETKeyword() {
        etKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnAdd.setEnabled(etKeyword.getText().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }



    private void setRecyclerViewItemTouchListener() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                ((UserKeywordsRecyclerAdapter.ViewHolder)viewHolder).deleteKeyword();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

}
