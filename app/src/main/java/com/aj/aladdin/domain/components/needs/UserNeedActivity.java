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
import android.view.View;

import com.aj.aladdin.R;
import com.aj.aladdin.tools.oths.PageFragment;


public class UserNeedActivity extends AppCompatActivity {

    public final static String _ID = "_ID";

    private String _id = null;

    UserNeedActivity self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_need);

        _id = getIntent().getStringExtra(_ID);

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
                UserNeedSaveActivity.start(self,_id);
            }
        });
    }


    public static void start(Context context) {
        context.startActivity(new Intent(context, UserNeedActivity.class));
    }

    public static void start(Context context, String _id) {
        Intent intent = new Intent(context, UserNeedActivity.class);
        intent.putExtra(_ID,_id);
        context.startActivity(intent);
    }


    private static class PagerAdapter extends FragmentPagerAdapter {

        private Context context;

        private String TAB_TITLES[] = new String[]{"PROFILS TROUVES", "POKES RECUES"};

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
                    return PageFragment.newInstance(position + 1);
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
