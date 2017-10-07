package com.aj.aladdin.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.aj.aladdin.R;
import com.aj.aladdin.db.IO;
import com.aj.aladdin.db.colls.itf.Coll;
import com.aj.aladdin.domain.components.messages.ConversationsFragment;
import com.aj.aladdin.domain.components.needs.main.UserNeedsFragment;
import com.aj.aladdin.domain.components.profile.ProfileFragment;
import com.aj.aladdin.tools.components.fragments.ProgressBarFragment;


import com.aj.aladdin.tools.oths.PageFragment;

import com.aj.aladdin.tools.regina.Regina;
import com.aj.aladdin.tools.regina.ack.UIAck;
import com.aj.aladdin.tools.utils.__;
import com.aj.aladdin.welcome.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authListener;

    private FirebaseAuth mAuth;

    private static String user_id;


    public static ProgressBarFragment progressBarFragment; //// TODO: 02/10/2017  memory leaks

    public static void start(Activity context) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null)
            __.fatal("Attempt to start MainActivity with currentUser == null");

        String authID = currentUser.getUid();

        try {
            IO.r.find("PROFILES", __.jo().put("authID", authID)
                    , __.jo().put("authID", 1), __.jo(), new UIAck(context) {
                        @Override
                        protected void onRes(Object res, JSONObject ctx) {
                            JSONArray userArray = ((JSONArray) res);

                            if (userArray.length() > 1)
                                __.fatal("MainActivity::onStart : multiple users with the same authID");

                            if (userArray.length() == 1) try {
                                A.resetUser_id(context, userArray.getJSONObject(0).getString(Coll._idKey));
                                startActivity(context);
                            } catch (JSONException e) {
                                __.fatal(e);
                            }
                            else try {
                                IO.r.insert("PROFILES", __.jo().put("authID", authID)
                                        , __.jo(), __.jo(), new UIAck(context) {
                                            @Override
                                            protected void onRes(Object res, JSONObject ctx) {
                                                start(context);
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


    private static void startActivity(Activity context) {
        context.startActivity(new Intent(context, MainActivity.class));
        context.finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (A.user_id(this) == null)
            __.fatal("MainActivity -> A::user_id is null"); //SNO
        else
            user_id = A.user_id(this);
        mAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null)
            mAuth.removeAuthStateListener(authListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null)
                    LoginActivity.start(MainActivity.this);
            }
        };

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
                    return ProfileFragment.newInstance(user_id, true);
                case 2:
                    return UserNeedsFragment.newInstance();
                case 3:
                    return ConversationsFragment.newInstance();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mAuth.signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
