/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huhapp.android.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huhapp.android.R;
import com.huhapp.android.common.logger.Log;
import com.huhapp.android.common.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

public abstract class QuestionTabsFragment extends Fragment {

    static final String LOG_TAG = "SlidingTabsBasicFragment";

    /**
     * A custom {@link android.support.v4.view.ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link android.support.v4.view.ViewPager} which will be used in conjunction with the {@link com.huhapp.android.common.view.SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    private SamplePagerAdapter fragmentAdapter;

    /**
     * Inflates the {@link android.view.View} which will be displayed by this {@link android.support.v4.app.Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    // BEGIN_INCLUDE (fragment_onviewcreated)
    /**
     * This is called after the {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)} has finished.
     * Here we can pick out the {@link android.view.View}s we need to configure from the content view.
     *
     * We set the {@link android.support.v4.view.ViewPager}'s adapter to be an instance of {@link QuestionTabsFragment.SamplePagerAdapter}. The
     * {@link com.huhapp.android.common.view.SlidingTabLayout} is then given the {@link android.support.v4.view.ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fragmentAdapter = new SamplePagerAdapter();
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(fragmentAdapter);
        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_view_container, R.id.text);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.buttonTabMainColor));
        mSlidingTabLayout.setViewPager(mViewPager);
        // END_INCLUDE (setup_slidingtablayout)
    }
    // END_INCLUDE (fragment_onviewcreated)

    /**
     * The {@link android.support.v4.view.PagerAdapter} used to display pages in this sample.
     * The individual pages are simple and just display two lines of text. The important section of
     * this class is the {@link #getPageTitle(int)} method which controls what is displayed in the
     * {@link com.huhapp.android.common.view.SlidingTabLayout}.
     */
    class SamplePagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<Fragment>();

        public SamplePagerAdapter() {
            super(getFragmentManager());
        }

        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return getPagerItems().length;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return getPagerItems()[position].title;
        }

        @Override
        public Fragment getItem(int position) {
            try {
                return fragmentList.get(position);
            } catch(IndexOutOfBoundsException ex) {
                Fragment fragment = QuestionListFragment.newInstance(getPagerItems()[position].apiEndpoint);
                fragmentList.add(position, fragment);
                return fragment;
            }
        }

    }

    protected class QuestionListPagerItem {
        public String title;
        public String apiEndpoint;

        public QuestionListPagerItem(String title, String apiEndpoint) {
            this.title = title;
            this.apiEndpoint = apiEndpoint;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment currentFragment = fragmentAdapter.getItem(mViewPager.getCurrentItem());
        if (currentFragment != null) {
            currentFragment.onActivityResult(requestCode, resultCode, data);
        }

        Log.i("FRAGMENTPARENT", mViewPager.getCurrentItem() + " "+requestCode + " " + resultCode);
    }

    //
    protected abstract QuestionListPagerItem[] getPagerItems();
}
