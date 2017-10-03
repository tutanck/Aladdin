package com.aj.aladdin.domain.components.needs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.aj.aladdin.R;
import com.aj.aladdin.tools.utils.__;


public class UserNeedActivity extends AppCompatActivity {

    private final static String USER_NEED = "USER_NEED";

    private UserNeed mUserNeed = null;

    private EditText searchET;
    private ImageButton searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_need);

        mUserNeed = (UserNeed) getIntent().getSerializableExtra(USER_NEED);

        searchET = (EditText) findViewById(R.id.need_search_bar_et);
        searchET.setText(mUserNeed.getSearchText());

        searchBtn = (ImageButton) findViewById(R.id.need_search_bar_btn);
        searchBtn.setEnabled(false);


        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.user_need_results_viewpager);
        viewPager.setAdapter(
                new PagerAdapter(
                        getSupportFragmentManager()
                        , UserNeedActivity.this
                )
        );

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.user_need_results_sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_open_need_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = searchET.getText().toString().trim();

                if (TextUtils.isEmpty(searchText))
                    __.showShortSnack(view, "Impossible de sauvegarder une recherche vide!");
                else //send old need's _id but new searchText
                    UserNeedSaveActivity.start(UserNeedActivity.this, mUserNeed.get_id(), searchText);
            }
        });

        functionalizeSearchBtn();
        functionalizeSearchET();
    }


    private void functionalizeSearchBtn() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                __.showShortToast(UserNeedActivity.this, "searching...");
            }
        });
    }


    private void functionalizeSearchET() {
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchBtn.setEnabled(searchET.getText().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    public static void start(Context context, UserNeed userNeed) {
        Intent intent = new Intent(context, UserNeedActivity.class);
        intent.putExtra(USER_NEED, userNeed);
        context.startActivity(intent);
    }


    public static void start(Context context) {
        context.startActivity(new Intent(context, UserNeedActivity.class));
    }

    private static class PagerAdapter extends FragmentPagerAdapter {

        private Context mContext;

        private String TAB_TITLES[] = new String[]{"PROFILS TROUVES", "POKES RECUES"};

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return NeedProfilesFragment.newInstance();
                case 1:
                    return NeedProfilesFragment.newInstance();
                default:
                    throw new RuntimeException("PagerAdapter's top level tab menu");
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
