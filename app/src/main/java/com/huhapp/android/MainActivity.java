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

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.huhapp.android.api.Api;
import com.huhapp.android.api.model.Question;
import com.huhapp.android.common.activities.SampleActivityBase;
import com.huhapp.android.common.logger.Log;
import com.huhapp.android.huhapp.R;
import com.huhapp.android.util.PrefUtils;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.List;

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
    private Drawable faAddDeactive;
    private Drawable faAddActive;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initRes();

        new SignUp().execute();

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
        faAddDeactive = new IconDrawable(this, Iconify.IconValue.fa_plus_circle).colorRes(R.color.active).actionBarSize();
        faAddActive = new IconDrawable(this, Iconify.IconValue.fa_plus_circle).colorRes(R.color.active).actionBarSize();
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
        getActionBar().setIcon(android.R.color.transparent);
        this.menu.findItem(R.id.search).setIcon(faSearchDeactive);
        this.menu.findItem(R.id.me).setIcon(faUserDeactive);
        this.menu.findItem(R.id.notifications).setIcon(faBellODeactive);
        this.menu.findItem(R.id.more).setIcon(faEllipsisHDeactive);
        this.menu.findItem(R.id.add).setIcon(faAddDeactive);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //I don't want to change the others icon
        if (item.getItemId() == R.id.add) {
            this.createQuestion();
            return true;
        }

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

    private void createQuestion() {
        Intent intent = new Intent(this, CreateQuestionActivity.class);
        startActivity(intent);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.sample_content_fragment, fragment);
        transaction.commit();
    }

    private class SignUp extends AsyncTask<Void,Void,String> {

        private SignUp() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String userId = PrefUtils.getFromPrefs(MainActivity.this, PrefUtils.PREFS_USER_ID, "");
            if (userId == null || userId.length() == 0) {
                return Api.createUser();
            } else {
                return userId;
            }
        }

        @Override
        protected void onPostExecute(String userId) {
            super.onPostExecute(userId);
            if (userId != null && userId.length() > 0) {
                PrefUtils.saveToPrefs(MainActivity.this, PrefUtils.PREFS_USER_ID, userId);
                MainActivity.this.setSearchTabActive();
                //Toast toast = Toast.makeText(MainActivity.this, "Signed up", Toast.LENGTH_SHORT);
                //toast.show();
            } else {
                //
                Toast toast = Toast.makeText(MainActivity.this, "Error login in", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
