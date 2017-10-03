package com.aj.aladdin.domain.components.messages;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aj.aladdin.R;
import com.aj.aladdin.main.A;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Activity mContext;
    private List<Message> mMessageList;

    public MessageListAdapter(Context context, List<Message> messageList) {
        mContext = (Activity) context;
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        if (mMessageList.get(position).getSenderID().equals(((A)mContext.getApplication()).getUser_id()))
            return VIEW_TYPE_MESSAGE_SENT;
        else
            return VIEW_TYPE_MESSAGE_RECEIVED;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_SENT)
            return new SentMessageHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false));
        else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED)
            return new ReceivedMessageHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false));

        throw new RuntimeException("MessageListAdapter : Unknown Message Type Exception");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(mMessageList.get(position));
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(mMessageList.get(position));
        }
    }


    public class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        public SentMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            timeText.setText(message.getCreatedAt());
        }
    }


    public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        public ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            timeText.setText(message.getCreatedAt());
            nameText.setText(message.getSenderID());

            // Insert the profile image from the URL into the ImageView.
            //_DateUtils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage); // TODO: 29/09/2017
        }
    }
}