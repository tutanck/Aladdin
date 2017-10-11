package com.aj.aladdin.main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.aj.aladdin.R;
import com.aj.aladdin.db.IO;
import com.aj.aladdin.db.colls.PROFILES;
import com.aj.aladdin.db.colls.itf.Coll;
import com.aj.aladdin.domain.components.ads.AdsFragment;
import com.aj.aladdin.domain.components.messages.ConversationsFragment;
import com.aj.aladdin.domain.components.needs.main.UserNeedsFragment;
import com.aj.aladdin.domain.components.profile.ProfileFragment;
import com.aj.aladdin.tools.components.fragments.ProgressBarFragment;

import com.aj.aladdin.tools.regina.Regina;
import com.aj.aladdin.tools.regina.ack.UIAck;
import com.aj.aladdin.tools.regina.ack.VoidBAck;
import com.aj.aladdin.tools.utils.Avail;
import com.aj.aladdin.tools.utils.__;
import com.aj.aladdin.welcome.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity {

    private static final String AVAILABILITY = "AVAILABILITY";

    private int availability = Avail.OFFLINE;

    private FirebaseAuth.AuthStateListener authListener;

    private FirebaseAuth mAuth;

    private static String user_id;


    public static void start(Activity context, String username) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null)
            __.fatal("Attempt to start MainActivity with currentUser == null");

        String authID = currentUser.getUid();

        if (username != null) try {
            IO.r.insert(PROFILES.coll
                    , __.jo().put(PROFILES.authIDKey, authID)
                            .put(PROFILES.usernameKey, username)
                            .put(PROFILES.availabilityKey, Avail.AVAILABLE)
                    , __.jo(), __.jo(), new UIAck(context) {
                        @Override
                        protected void onRes(Object res, JSONObject ctx) {
                            start(context, null);
                        }
                    });
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }

        else try {
            IO.r.find(PROFILES.coll
                    , __.jo().put(PROFILES.authIDKey, authID)
                    , __.jo().put(PROFILES.authIDKey, 1).put(PROFILES.availabilityKey, 1)
                    , __.jo(), new UIAck(context) {
                        @Override
                        protected void onRes(Object res, JSONObject ctx) {
                            JSONArray userArray = ((JSONArray) res);

                            if (userArray.length() > 1)
                                __.fatal("MainActivity::onStart : multiple users with the same authID : " + authID);

                            if (userArray.length() == 0)
                                __.fatal("MainActivity::onStart : no user with the authID : " + authID);

                            if (userArray.length() == 1) try {
                                JSONObject userJSON = userArray.getJSONObject(0);
                                A.resetUser_id(context, userJSON.getString(Coll._idKey));
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra(AVAILABILITY, userJSON.getInt(PROFILES.availabilityKey));
                                context.startActivity(intent);
                                context.finish();
                            } catch (JSONException e) {
                                __.fatal(e);
                            }
                        }
                    });
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (A.user_id(this) == null)
            __.fatal("MainActivity -> A::user_id is null"); //SNO

        user_id = A.user_id(this);

        PROFILES.setAvailability(user_id, Avail.AVAILABLE, new VoidBAck(MainActivity.this));

        mAuth.addAuthStateListener(authListener);

        IO.socket.on(PROFILES.collTag + user_id, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    if (((JSONObject) args[1]).getInt("op") == 2) // update
                        PROFILES.getAvailability(user_id, new UIAck(MainActivity.this) {
                            @Override
                            protected void onRes(Object res, JSONObject ctx) {
                                JSONArray jar = ((JSONArray) res);
                                if (jar.length() != 1)
                                    __.fatal("MainActivity::onStart : multiple users with the same authID");
                                else try {
                                    availability = jar.getJSONObject(0).getInt(PROFILES.availabilityKey);
                                    refreshAvailabilityColor();
                                } catch (JSONException e) {
                                    __.fatal(e);
                                }
                            }
                        });
                } catch (JSONException e) {
                    __.fatal(e);
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null)
            mAuth.removeAuthStateListener(authListener);
        IO.socket.off(PROFILES.collTag + user_id);
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

        availability = getIntent().getIntExtra(AVAILABILITY, Avail.AVAILABLE);
        refreshAvailabilityColor();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setContentView(R.layout.activity_main);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(
                new PagerAdapter(
                        getSupportFragmentManager()
                        , MainActivity.this
                )
        );

        viewPager.setOffscreenPageLimit(3);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Changement de disponibilité");

                if (availability == Avail.AVAILABLE)
                    builder.setMessage(getString(R.string.avail_to_busy_warning));
                else
                    builder.setMessage(getString(R.string.avail_to_available_warning));

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PROFILES.setAvailability(user_id, Avail.nextStatus(availability), new VoidBAck(MainActivity.this));
                    }
                });

                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
                break;

            case R.id.action_settings:
                PROFILES.setAvailability(user_id, Avail.OFFLINE, new UIAck(this) {
                    @Override
                    protected void onRes(Object res, JSONObject ctx) {
                        mAuth.signOut();
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }


    private static class PagerAdapter extends FragmentPagerAdapter {

        private Context context;

        private String TAB_TITLES[] = new String[]{"ANNONCES", "PROFIL", "BESOINS", "MESSAGES"};

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return AdsFragment.newInstance();
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

    private void refreshAvailabilityColor() {
        getSupportActionBar().setHomeAsUpIndicator(Avail.getDrawable(availability));
    }

}
