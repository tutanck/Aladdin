package com.aj.aladdin.domain.components.profile;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aj.aladdin.R;
import com.aj.aladdin.domain.components.needs.UserNeedActivity;

import java.util.ArrayList;

/**
 * Created by joan on 21/09/17.
 */
public class UserProfilesRecyclerAdapter extends RecyclerView.Adapter<UserProfilesRecyclerAdapter.ViewHolder> {

    private ArrayList<UserProfile> mProfiles;

    public UserProfilesRecyclerAdapter(
            ArrayList<UserProfile> profiles
    ) {
        mProfiles = profiles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_user_profile, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(mProfiles.get(position));
    }

    @Override
    public int getItemCount() {
        return mProfiles.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mSearchTextView;
        private TextView mApplicantTextView;
        private FloatingActionButton fabNeedStatus;

        private UserProfile mNeedProfile;


        public ViewHolder(View v) {
            super(v);

            mTitleTextView = (TextView) v.findViewById(R.id.need_title_textview);
            mSearchTextView = (TextView) v.findViewById(R.id.need_search_textview);
            mApplicantTextView = (TextView) v.findViewById(R.id.need_nb_pokes_textview);
            fabNeedStatus = (FloatingActionButton) v.findViewById(R.id.fab_need_status);
            v.setOnClickListener(this);
        }

        public void bindItem(UserProfile needProfile) {
            this.mNeedProfile = needProfile;
           /* mTitleTextView.setText(mNeedProfile.getTitle());
            mSearchTextView.setText(mNeedProfile.getSearch());
            int color = mNeedProfile.isActive() ? R.color.Lime : R.color.Red;
            fabNeedStatus.setBackgroundTintList(
                    ColorStateList.valueOf
                            (ContextCompat.getColor(mNeedProfile.getContext(), color))
            );*/
         }


        @Override
        public void onClick(View view) {
            UserNeedActivity.start(mNeedProfile.getContext(), mNeedProfile.get_id());
        }
    }
}
