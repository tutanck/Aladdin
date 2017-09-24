package com.aj.aladdin.main;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aj.aladdin.R;
import com.aj.aladdin.domain.components.needs.UserNeedsFragment;
import com.aj.aladdin.domain.components.profile.ProfileFragment;
import com.aj.aladdin.tools.oths.db.IO;
import com.aj.aladdin.tools.oths.PageFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        IO.r.connect();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        IO.r.disconnect();
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
                    return PageFragment.newInstance(position + 1);
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
