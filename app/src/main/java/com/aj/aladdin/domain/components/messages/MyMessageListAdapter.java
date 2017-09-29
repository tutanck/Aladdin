package com.aj.aladdin.domain.components.messages;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aj.aladdin.R;

import java.util.List;

/**
 * Created by joan on 21/09/17.
 */
public class MyMessageListAdapter extends RecyclerView.Adapter<MyMessageListAdapter.ViewHolder> {

    private List<Message> mUserNeeds;

    public MyMessageListAdapter(
            Context context,
            List<Message> userNeeds
    ) {
        mUserNeeds = userNeeds;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_user_need, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(mUserNeeds.get(position));
    }

    @Override
    public int getItemCount() {
        return mUserNeeds.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mSearchTextView;
        private TextView mApplicantTextView;
        private FloatingActionButton fabNeedStatus;

        private Message message;


        public ViewHolder(View v) {
            super(v);

            mTitleTextView = (TextView) v.findViewById(R.id.need_title_textview);
            mSearchTextView = (TextView) v.findViewById(R.id.need_search_textview);
            mApplicantTextView = (TextView) v.findViewById(R.id.need_nb_pokes_textview);
            v.setOnClickListener(this);
        }

        public void bindItem(Message message) {
            this.message = message;
            mTitleTextView.setText(this.message.getMessage());
            mSearchTextView.setText(this.message.getSenderID());
         }


        @Override
        public void onClick(View view) {

        }

        void deleteNeed() {

        }
    }
}
