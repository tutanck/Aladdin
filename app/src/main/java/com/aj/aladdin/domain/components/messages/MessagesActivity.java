package com.aj.aladdin.domain.components.messages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aj.aladdin.R;
import com.aj.aladdin.db.colls.MESSAGES;
import com.aj.aladdin.db.colls.itf.Coll;
import com.aj.aladdin.main.A;
import com.aj.aladdin.tools.utils.__;
import com.aj.aladdin.tools.regina.ack.UIAck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    private final static String CONTACT_ID = "CONTACT_ID";

    private List<Message> messageList = new ArrayList<>();

    private MessageListAdapter mAdapter;

    private String contactID = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new MessageListAdapter(this, messageList);
        mRecyclerView.setAdapter(mAdapter);

        contactID = getIntent().getStringExtra(CONTACT_ID);

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

    public static void start(Context context, String contactID) {
        Intent intent = new Intent(context, MessagesActivity.class);
        intent.putExtra(CONTACT_ID, contactID);
        context.startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
        loadMessages();
    }


    private void sendMessage(String text) {
        MESSAGES.sendMessage(((A) getApplication()).getUser_id(), contactID, text, new UIAck(this) {
            @Override
            protected void onRes(Object res, JSONObject ctx) {
                loadMessages();
            }
        });
    }


    private void loadMessages() {
        MESSAGES.loadMessages(((A) getApplication()).getUser_id()
                , contactID
                , new UIAck(this) {
                    @Override
                    protected void onRes(Object res, JSONObject ctx) {
                        try {
                            JSONArray jar = (JSONArray) res;
                            messageList.clear();
                            for (int i = 0; i < jar.length(); i++) {
                                JSONObject jo = jar.getJSONObject(i);
                                messageList.add(new Message(jo.getString(MESSAGES.messageKey), jo.getString(MESSAGES.senderIDKey), jo.getString(Coll.dateKey)));
                            }
                            Log.i("messageList", messageList.toString());
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            __.fatal(e); //SNO : if a doc exist the Message field should exist too
                        }
                    }
                });
    }

}
