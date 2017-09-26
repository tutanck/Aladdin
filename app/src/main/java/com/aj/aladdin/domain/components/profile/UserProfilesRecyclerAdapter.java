package com.aj.aladdin.domain.components.profile;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
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

        private ImageView userProfileIV;
        private TextView usernameTV;
        private FloatingActionButton userStatusFAB;
        private RatingBar userReputationRBar;
        private TextView userDistanceTV;
        private TextView messageTV;
        private TextView messageDateTV;

        private UserProfile mNeedProfile;


        public ViewHolder(View v) {
            super(v);

            userProfileIV = (ImageView) v.findViewById(R.id.userProfileIV);
            usernameTV = (TextView) v.findViewById(R.id.usernameTV);
            userStatusFAB = (FloatingActionButton) v.findViewById(R.id.userStatusFAB);
            userReputationRBar = (RatingBar) v.findViewById(R.id.userReputationRBar);
            userDistanceTV = (TextView) v.findViewById(R.id.userDistanceTV);
            messageTV = (TextView) v.findViewById(R.id.messageTV);
            messageDateTV = (TextView) v.findViewById(R.id.messageDateTV);
            v.setOnClickListener(this);
        }

        public void bindItem(UserProfile userProfile) {
            this.mNeedProfile = userProfile;
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
