package com.aj.aladdin.domain.components.needs;

import android.content.Context;
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
import com.aj.aladdin.tools.components.fragments.ItemDividerFragment;
import com.aj.aladdin.tools.components.fragments.simple.FormField;
import com.aj.aladdin.tools.components.services.FormFieldKindTranslator;
import com.aj.aladdin.tools.oths.PageFragment;
import com.aj.aladdin.tools.oths.utils.JSONServices;
import com.aj.aladdin.tools.oths.utils.__;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserNeedActivity extends AppCompatActivity {

    public final static String _ID = "_ID";

    ArrayList<FormField> formFields = new ArrayList<>();

    JSONObject formParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_need);

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


        formParams = JSONServices.loadJsonFromAsset("form_params_user_need.json", this);
        JSONArray orderedFieldsNames;
        try {
            orderedFieldsNames = formParams.getJSONArray("ordered_fields_names");

            for (int i = 0; i < orderedFieldsNames.length(); i++) {
                String fieldName = orderedFieldsNames.getString(i);

                JSONObject fieldParam = formParams.getJSONObject(fieldName);

                FormField formField = FormField.newInstance(
                        fieldParam.getString("label"), FormFieldKindTranslator.tr(fieldParam.getInt("kind")));

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.form_layout, formField, fieldName)
                        .commit();

                formFields.add(formField);

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.form_layout, ItemDividerFragment.newInstance(false), "item_divider" + i)
                        .commit();
            }

        } catch (JSONException e) {
            __.fatalError(e);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_save_need);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (FormField formField : formFields)
                    formField.toggle();
            }
        });
    }


    private static class PagerAdapter extends FragmentPagerAdapter {

        private Context context;

        private String TAB_TITLES[] = new String[]{"PROFILS TROUVES", "PROPOSITIONS RECUES"};

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
