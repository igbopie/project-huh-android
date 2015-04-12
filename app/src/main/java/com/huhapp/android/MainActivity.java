/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.huhapp.android;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.huhapp.android.common.activities.SampleActivityBase;
import com.huhapp.android.huhapp.R;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends SampleActivityBase {

    public static final String TAG = "MainActivity";

    private Drawable faSearchDeactive;
    private Drawable faSearchActive;
    private Drawable faUserDeactive;
    private Drawable faUserActive;
    private Drawable faBellODeactive;
    private Drawable faBellOActive;
    private Drawable faEllipsisHDeactive;
    private Drawable faEllipsisHActive;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initRes();
        if (savedInstanceState == null) {
            this.setSearchTabActive();
        }
    }

    private void initRes(){
        faSearchDeactive = new IconDrawable(this, Iconify.IconValue.fa_search).colorRes(R.color.deactive).actionBarSize();
        faSearchActive = new IconDrawable(this, Iconify.IconValue.fa_search).colorRes(R.color.active).actionBarSize();
        faUserDeactive = new IconDrawable(this, Iconify.IconValue.fa_user).colorRes(R.color.deactive).actionBarSize();
        faUserActive = new IconDrawable(this, Iconify.IconValue.fa_user).colorRes(R.color.active).actionBarSize();
        faBellODeactive = new IconDrawable(this, Iconify.IconValue.fa_bell_o).colorRes(R.color.deactive).actionBarSize();
        faBellOActive = new IconDrawable(this, Iconify.IconValue.fa_bell_o).colorRes(R.color.active).actionBarSize();
        faEllipsisHDeactive = new IconDrawable(this, Iconify.IconValue.fa_ellipsis_h).colorRes(R.color.deactive).actionBarSize();
        faEllipsisHActive = new IconDrawable(this, Iconify.IconValue.fa_ellipsis_h).colorRes(R.color.active).actionBarSize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        this.setDeactiveIcons();
        this.menu.findItem(R.id.search).setIcon(faSearchActive);
        return super.onPrepareOptionsMenu(menu);
    }

    private void setDeactiveIcons() {
        this.menu.findItem(R.id.search).setIcon(faSearchDeactive);
        this.menu.findItem(R.id.me).setIcon(faUserDeactive);
        this.menu.findItem(R.id.notifications).setIcon(faBellODeactive);
        this.menu.findItem(R.id.more).setIcon(faEllipsisHDeactive);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.setDeactiveIcons();
        //item.getIcon().setC
        switch(item.getItemId()) {
            case R.id.search:
                item.setIcon(faSearchActive);
                this.setSearchTabActive();
                return true;
            case R.id.me:
                item.setIcon(faUserActive);
                this.setMeTabActive();
                return true;
            case R.id.notifications:
                item.setIcon(faBellOActive);
                return true;
            case R.id.more:
                item.setIcon(faEllipsisHActive);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSearchTabActive() {
        this.setFragment(new SearchFragment());
    }

    private void setMeTabActive() {
        this.setFragment(new MeFragment());
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.sample_content_fragment, fragment);
        transaction.commit();
    }
}
