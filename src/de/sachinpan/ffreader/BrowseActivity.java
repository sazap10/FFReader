package de.sachinpan.ffreader;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.viewpagerindicator.TabPageIndicator;

import de.sachinpan.ffreader.utils.Utils;

public class BrowseActivity extends FragmentActivity {
SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private static String[] items,itemLinks;
    private static Context activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        activity = this;
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());
        items = getResources().getStringArray(R.array.categories);
        itemLinks = getResources().getStringArray(R.array.categories_links);

        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment = new BrowseSectionFragment();
            Bundle args = new Bundle();
            args.putInt(BrowseSectionFragment.ARG_SECTION_NUMBER, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
            case 0:
                return getString(R.string.title_fanfic).toUpperCase(l);
            case 1:
                return getString(R.string.title_crossovers).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A section fragment representing a section of the browse activity
     */
    public static class BrowseSectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public BrowseSectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_browse,
                    container, false);
            ListView lv = (ListView) rootView.findViewById(R.id.listView1);
            lv.setAdapter(new ArrayAdapter<String>(activity,
                    android.R.layout.simple_list_item_1, items));
			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					final String item = (String) parent
							.getItemAtPosition(position);
					boolean isCrossover = getArguments().getInt(
							ARG_SECTION_NUMBER) == 1;
					String link = itemLinks[position];
					Intent intent = new Intent(activity, ItemsActivity.class);
					intent.putExtra(Utils.CROSSOVER_FLAG, isCrossover);
					intent.putExtra(Utils.LINK, link);
					intent.putExtra(Utils.TITLE, item);
					startActivity(intent);
				}
			});
            return rootView;
        }
        
        
    }
}