package com.aj.aladdin.domain.components.keywords;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.aj.aladdin.R;

import java.util.ArrayList;

/**
 * Created by joan on 21/09/17.
 */
public class UserKeywordsRecyclerAdapter extends RecyclerView.Adapter<UserKeywordsRecyclerAdapter.ViewHolder> {

    private ArrayList<UserKeyword> mUserKeywords;

    public UserKeywordsRecyclerAdapter(
            ArrayList<UserKeyword> userKeywords
    ) {
        mUserKeywords = userKeywords;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_user_keyword, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(mUserKeywords.get(position));
    }

    @Override
    public int getItemCount() {
        return mUserKeywords.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;
        private Switch mSwitch;

        private boolean haveSwitchListener = false;

        private UserKeyword mUserKeyword;


        public ViewHolder(View v) {
            super(v);

            mTextView = (TextView) v.findViewById(R.id.keyword_textview);
            mSwitch = (Switch) v.findViewById(R.id.keyword_switch);
        }

        public void bindItem(UserKeyword userKeyword) {
            this.mUserKeyword = userKeyword;
            mTextView.setText(mUserKeyword.getKeyword());
            mSwitch.setChecked(mUserKeyword.isActive());

            if (!haveSwitchListener) {
                mSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userKeyword.getActivity()
                                .saveKeyword(mUserKeyword.getKeyword(), !mUserKeyword.isActive(), false);
                    }
                });

                haveSwitchListener = true;
            }
        }

        void deleteKeyword() {
            mUserKeyword.getActivity().saveKeyword(mUserKeyword.getKeyword(), mUserKeyword.isActive(), true);
        }

    }
}
