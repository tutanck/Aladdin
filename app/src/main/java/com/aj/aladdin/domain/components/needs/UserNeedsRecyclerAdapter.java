package com.aj.aladdin.domain.components.needs;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTitleTextView;
        private TextView mSearchTextView;
        private TextView mApplicantTextView;
        private FloatingActionButton fabNeedStatus;

        private UserNeed mUserNeed;


        public ViewHolder(View v) {
            super(v);

            mTitleTextView = (TextView) v.findViewById(R.id.need_title_textview);
            mSearchTextView = (TextView) v.findViewById(R.id.need_search_textview);
            mApplicantTextView = (TextView) v.findViewById(R.id.need_nb_pokes_textview);
            fabNeedStatus = (FloatingActionButton) v.findViewById(R.id.fab_need_status);
            v.setOnClickListener(this);
        }

        public void bindItem(UserNeed userNeed) {
            this.mUserNeed = userNeed;
            mTitleTextView.setText(mUserNeed.getTitle());
            mSearchTextView.setText(mUserNeed.getSearch());
            //todo color
            //// TODO: 24/09/2017 nb pokes
        }

        @Override
        public void onClick(View view) {
            UserNeedActivity.start(mUserNeed.getContext(), mUserNeed.get_id());
        }
    }
}
