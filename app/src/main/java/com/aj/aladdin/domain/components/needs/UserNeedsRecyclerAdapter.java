package com.aj.aladdin.domain.components.needs;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aj.aladdin.R;
import com.aj.aladdin.db.IO;
import com.aj.aladdin.db.colls.NEEDS;
import com.aj.aladdin.db.colls.itf.Coll;
import com.aj.aladdin.main.A;
import com.aj.aladdin.utils.__;
import com.aj.aladdin.tools.regina.Regina;
import com.aj.aladdin.tools.regina.ack.UIAck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by joan on 21/09/17.
 */
public class UserNeedsRecyclerAdapter extends RecyclerView.Adapter<UserNeedsRecyclerAdapter.ViewHolder> {

    private List<UserNeed> mUserNeeds;

    private UserNeedsRecyclerAdapter self = this;

    public UserNeedsRecyclerAdapter(
            List<UserNeed> userNeeds
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
        private TextView mNbPokesTextView;
        private FloatingActionButton fabNeedStatus;

        private UserNeed mUserNeed;


        public ViewHolder(View v) {
            super(v);
            mTitleTextView = (TextView) v.findViewById(R.id.need_title_textview);
            mSearchTextView = (TextView) v.findViewById(R.id.need_search_textview);
            mNbPokesTextView = (TextView) v.findViewById(R.id.need_nb_pokes_textview);
            fabNeedStatus = (FloatingActionButton) v.findViewById(R.id.fab_need_status);
            v.setOnClickListener(this);
        }

        public void bindItem(UserNeed userNeed) {
            this.mUserNeed = userNeed;
            mTitleTextView.setText(mUserNeed.getTitle());
            mSearchTextView.setText(mUserNeed.getSearch());
            int color = mUserNeed.isActive() ? R.color.Lime : R.color.Red;
            fabNeedStatus.setBackgroundTintList(
                    ColorStateList.valueOf
                            (ContextCompat.getColor(mUserNeed.getContext(), color))
            );
            //// TODO: 24/09/2017 nb pokes
        }


        @Override
        public void onClick(View view) {
            UserNeedActivity.start(mUserNeed.getContext(), mUserNeed.get_id());
        }


        void deleteNeed(String userID, List<UserNeed> userNeeds, UserNeedsRecyclerAdapter adapter) {
            Activity contextActivity = (Activity) mUserNeed.getContext();
            NEEDS.deleteNeed(mUserNeed.get_id(),
                    new UIAck(contextActivity) {
                        @Override
                        protected void onRes(Object res, JSONObject ctx) {
                            NEEDS.loadNeeds(userID, new UIAck(contextActivity) {
                                        @Override
                                        protected void onRes(Object res, JSONObject ctx) {
                                            UserNeedsFragment.reloadList(res,userNeeds,adapter,contextActivity);
                                        }
                                    }
                            );
                        }
                    });
        }
    }
}
