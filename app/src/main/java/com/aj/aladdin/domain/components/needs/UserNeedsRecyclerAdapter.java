package com.aj.aladdin.domain.components.needs;

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
public class UserNeedsRecyclerAdapter extends RecyclerView.Adapter<UserNeedsRecyclerAdapter.ViewHolder> {

    private ArrayList<UserNeed> mUserNeeds;

    public UserNeedsRecyclerAdapter(
            ArrayList<UserNeed> userNeeds
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


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTextView;
        private TextView mSearchTextView;
        private Switch mSwitch;

        private boolean haveSwitchListener = false;

        private UserNeed mUserNeed;


        public ViewHolder(View v) {
            super(v);

            mTitleTextView = (TextView) v.findViewById(R.id.need_title_textview);
            mSearchTextView = (TextView) v.findViewById(R.id.need_search_textview);
            mSwitch = (Switch) v.findViewById(R.id.need_switch);
        }

        public void bindItem(UserNeed userNeed) {
            this.mUserNeed = userNeed;
            mTitleTextView.setText(mUserNeed.getTitle());
            mSearchTextView.setText(mUserNeed.getSearch());
           // mSwitch.setChecked(mUserNeed.isActive());

            /*if (!haveSwitchListener) {
                mSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mUserNeed.getActivity().setNeedStatus(mUserNeed.get_id(), !mUserNeed.isActive(), false);
                    }
                });

                haveSwitchListener = true;
            }*/
        }

        void deleteNeed() {
            //mUserNeed.getActivity().setNeedStatus(mUserNeed.get_id(), mUserNeed.isActive(), true);
        }

    }
}
