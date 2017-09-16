package com.aj.aladdin.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aj.aladdin.domain.components.userprofile.ProfileFragment;
import com.aj.aladdin.rnd.PageFragment;

public class MainActivityPagerAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 4;
    private String TAB_TITLES[] = new String[]{"OFFRES", "PROFIL", "BESOINS", "MESSAGES"};
    private Context context;

    public MainActivityPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
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