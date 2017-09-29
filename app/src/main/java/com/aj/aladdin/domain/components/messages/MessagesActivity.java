package com.aj.aladdin.domain.components.messages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aj.aladdin.R;
import com.aj.aladdin.tools.oths.db.DB;
import com.aj.aladdin.tools.oths.db.IO;
import com.aj.aladdin.tools.oths.utils.MongoUtils;
import com.aj.aladdin.tools.oths.utils.__;
import com.aj.aladdin.tools.regina.Regina;
import com.aj.aladdin.tools.utils.BAck;
import com.aj.aladdin.tools.utils.UIAck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    public final static String coll = DB.MESSAGES;

    private RecyclerView mRecyclerView;
    private MessageListAdapter mAdapter;

    private List<Message> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        mRecyclerView = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MessageListAdapter(this, messageList);
        mRecyclerView.setAdapter(mAdapter);

        EditText chatboxET = (EditText) findViewById(R.id.chatbox_et);
        Button chatboxSendBtn = (Button) findViewById(R.id.chatbox_send_btn);

        chatboxSendBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String msg = chatboxET.getText().toString();
                        if (TextUtils.isEmpty(msg)) return;
                        sendMessage(msg);
                        chatboxET.setText("");
                    }
                }
        );

    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, MessagesActivity.class));
    }


    @Override
    public void onStart() {
        super.onStart();
        loadMessages();
    }


    private void sendMessage(String text) {
        try {
            IO.r.insert(
                    coll
                    , __.jo().put("senderID", "joan").put("toID", "joan").put("message", text).put("date", new Date())//// TODO: 29/09/2017 joan + date
                    , __.jo()
                    , __.jo()
                    , new UIAck(this) {
                        @Override
                        protected void onRes(Object res, JSONObject ctx) {
                            loadMessages();
                        }
                    }
            );
        } catch (JSONException | Regina.NullRequiredParameterException e) {
            __.fatalError(e);
        }
    }


    private void loadMessages() {
        try {
            IO.r.find(
                    coll//// TODO: 29/09/2017 joan
                    , __.jo().put(
                            "$or"
                            , __.jar()
                                    .put(__.jo().put("senderID", "joan").put("toID", "joan"))
                                    .put(__.jo().put("senderID", "joan").put("toID", "joan"))
                    )
                    , __.jo().put("sort", __.jo().put("date", -1))
                    , __.jo()
                    , new UIAck(this) {
                        @Override
                        protected void onRes(Object res, JSONObject ctx) {
                            try {
                                JSONArray jar = (JSONArray) res;
                                messageList.clear();
                                for (int i = 0; i < jar.length(); i++) {
                                    JSONObject jo = jar.getJSONObject(i);
                                    messageList.add(new Message(jo.getString("message"), jo.getString("senderID"), jo.getString("date")));
                                }
                                mAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                __.fatalError(e); //SNO : if a doc exist the Need field should exist too
                            }
                        }
                    }
            );
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatalError(e);
        }
    }

}
