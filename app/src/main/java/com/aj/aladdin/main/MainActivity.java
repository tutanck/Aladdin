package com.aj.aladdin.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aj.aladdin.R;
import com.aj.aladdin.domain.components.messages.ConverstationsFragment;
import com.aj.aladdin.domain.components.needs.UserNeedsFragment;
import com.aj.aladdin.domain.components.profile.ProfileFragment;
import com.aj.aladdin.tools.components.fragments.ProgressBarFragment;
import com.aj.aladdin.tools.oths.db.DB;
import com.aj.aladdin.tools.oths.db.IO;
import com.aj.aladdin.tools.oths.PageFragment;
import com.aj.aladdin.tools.oths.utils.__;
import com.aj.aladdin.tools.regina.Regina;
import com.aj.aladdin.tools.utils.UIAck;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private static String user_id = null;

    public static String user_id() {
        return user_id;
    }

    public static ProgressBarFragment progressBarFragment;

    public static void start(Activity caller, String authID) {
        try {
            IO.r.find(DB.USER_PROFILE
                    , __.jo().put("authID", authID)
                    , __.jo().put("authID", 1), __.jo()
                    , new UIAck(caller) {
                        @Override
                        protected void onRes(Object res, JSONObject ctx) {
                            JSONArray userArray = ((JSONArray) res);

                            if (userArray.length() > 1)
                                __.fatal("MainActivity::onStart : multiple users with the same authID");

                            if (userArray.length() == 1) try {
                                user_id = userArray.getJSONObject(0).getString("_id");
                                caller.startActivity(new Intent(caller, MainActivity.class));
                                caller.finish();
                            } catch (JSONException e) {
                                __.fatal(e);
                            }
                            else try {
                                IO.r.insert(DB.USER_PROFILE
                                        , __.jo().put("authID", authID)
                                        , __.jo(), __.jo()
                                        , new UIAck(caller) {
                                            @Override
                                            protected void onRes(Object res, JSONObject ctx) {
                                                start(caller, authID);
                                            }
                                        });
                            } catch (Regina.NullRequiredParameterException | JSONException e) {
                                __.fatal(e);
                            }
                        }
                    });
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(
                new PagerAdapter(
                        getSupportFragmentManager()
                        , MainActivity.this
                )
        );

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        progressBarFragment = (ProgressBarFragment) getSupportFragmentManager().findFragmentById(R.id.waiter_modal_fragment);
    }


    private static class PagerAdapter extends FragmentPagerAdapter {

        private Context context;

        private String TAB_TITLES[] = new String[]{"OFFRES", "PROFIL", "BESOINS", "MESSAGES"};

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PageFragment.newInstance(position + 1);
                case 1:
                    return ProfileFragment.newInstance(true);
                case 2:
                    return UserNeedsFragment.newInstance();
                case 3:
                    return ConverstationsFragment.newInstance();
                default:
                    throw new RuntimeException("Unknown top level tab menu");
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TAB_TITLES[position];
        }

        @Override
        public int getCount() {
            return TAB_TITLES.length;
        }
    }

}
