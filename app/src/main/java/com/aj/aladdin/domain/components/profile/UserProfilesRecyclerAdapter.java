package com.aj.aladdin.domain.components.profile;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aj.aladdin.R;
import com.aj.aladdin.domain.components.messages.MessagesActivity;

import java.util.ArrayList;

/**
 * Created by joan on 21/09/17.
 */
public class UserProfilesRecyclerAdapter extends RecyclerView.Adapter<UserProfilesRecyclerAdapter.ViewHolder> {

    private ArrayList<UserProfile> mProfiles;
    private Context mContext;

    public UserProfilesRecyclerAdapter(
            Context context,
            ArrayList<UserProfile> profiles
    ) {
        mContext = context;
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
        holder.bindItem(mProfiles.get(position), mContext);
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

        private UserProfile mProfile;

        private Context mContext;


        public ViewHolder(View v) {
            super(v);

            userProfileIV = v.findViewById(R.id.userProfileIV);
            usernameTV = v.findViewById(R.id.usernameTV);
            userStatusFAB = v.findViewById(R.id.userStatusFAB);
            userReputationRBar = v.findViewById(R.id.userReputationRBar);
            userDistanceTV = v.findViewById(R.id.userDistanceTV);
            messageTV = v.findViewById(R.id.messageTV);
            messageDateTV = v.findViewById(R.id.messageDateTV);
            v.setOnClickListener(this);
        }

        public void bindItem(UserProfile userProfile, Context context) {
            this.mProfile = userProfile;
            this.mContext = context;

            usernameTV.setText(userProfile.getUsername());
            userStatusFAB.setBackgroundTintList(
                    ColorStateList.valueOf
                            (ContextCompat.getColor
                                    (context, userProfile.isOnline() ? R.color.Lime : R.color.Red)));
        }


        @Override
        public void onClick(View view) {
            MessagesActivity.start(mContext,mProfile.get_id(),mProfile.getUsername());
        }
    }
}
