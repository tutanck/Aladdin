package com.aj.aladdin.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aj.aladdin.domain.components.userprofile.ProfileFragment;
import com.aj.aladdin.tools.oths.PageFragment;

public class MainActivityPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    private String TAB_TITLES[] = new String[]{"OFFRES", "PROFIL", "BESOINS", "MESSAGES"};


    public MainActivityPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 1:
                return PageFragment.newInstance(position + 1);
            case 2:
                return ProfileFragment.newInstance(position + 1);
            case 3:
                return PageFragment.newInstance(position + 1);
            case 4:
                return PageFragment.newInstance(position + 1);
            default:
                return PageFragment.newInstance(position + 1);
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }
}