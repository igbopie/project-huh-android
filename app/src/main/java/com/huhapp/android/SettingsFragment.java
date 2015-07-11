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

package com.huhapp.android;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.huhapp.android.common.logger.Log;
import com.huhapp.android.menu.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends ListFragment {
    SettingsAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private static final int NOTIFICATIONS = 1;
    private static final int TERMS_OF_USE = 2;
    private static final int PRIVACY_POLICY = 3;
    private static final int RULES = 4;
    private static final int SEND_FEEDBACK = 5;

    public static List<MenuItem> getSettingsMenu() {

        List<MenuItem> settingsMenu = new ArrayList<MenuItem>();

        settingsMenu.add(new MenuItem("Settings", true));
        settingsMenu.add(new MenuItem("Notifications", NOTIFICATIONS, false));

        settingsMenu.add(new MenuItem("About", true));
        settingsMenu.add(new MenuItem("Terms of use", TERMS_OF_USE, false));
        settingsMenu.add(new MenuItem("Privacy policy", PRIVACY_POLICY, false));
        settingsMenu.add(new MenuItem("Rules", RULES, false));
        settingsMenu.add(new MenuItem("Send feedback", SEND_FEEDBACK, false));

        return settingsMenu;
    }

    public static SettingsFragment newInstance() {
        SettingsFragment myFragment = new SettingsFragment();

        Bundle args = new Bundle();
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        MenuItem item = adapter.getItem(position);
        Integer tag = item.getTag();
        if (tag == null) {
            return;
        }
        switch (tag.intValue()) {
            case NOTIFICATIONS:
                Log.i("MORE", "NOTIFICATIONS");
                break;
            case TERMS_OF_USE:
                Log.i("MORE", "TERMS_OF_USE");
                break;
            case PRIVACY_POLICY:
                Log.i("MORE", "PRIVACY_POLICY");
                break;
            case RULES:
                Log.i("MORE", "RULES");
                break;
            case SEND_FEEDBACK:
                Log.i("MORE", "SEND_FEEDBACK");
                break;
        }
        //new CustomToast(getActivity(), numbers_digits[(int) id]);
        //Intent intent = new Intent(this.getActivity(), QuestionDetailActivity.class);
        //intent.putExtra(QuestionDetailActivity.EXTRA_QUESTION_ID, notification.getQuestionId());
        //startActivity(intent);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        adapter = new SettingsAdapter(
                inflater.getContext(),
                getSettingsMenu());

        setListAdapter(adapter);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        ListView lv = (ListView) view.findViewById(android.R.id.list);
        lv.setDivider(null);

        view.setBackgroundColor(Color.WHITE);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public class SettingsAdapter extends ArrayAdapter<MenuItem> {
        public SettingsAdapter(Context context, List<MenuItem> menu) {
            super(context, 0, menu);
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).isTitle() ? 0 : 1;
        }

        // Given the item type, responsible for returning the correct inflated XML layout file
        private View getInflatedLayoutForType(int type, ViewGroup parent) {
            if (type == 0) {
                return LayoutInflater.from(getContext()).inflate(R.layout.menu_item_title_layout, parent, false);
            } else {
                return LayoutInflater.from(getContext()).inflate(R.layout.menu_item_layout, parent, false);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            MenuItem menuItem = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                // Get the data item type for this position
                int type = getItemViewType(position);
                // Inflate XML layout based on the type
                convertView = getInflatedLayoutForType(type, parent);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.text);
            textView.setText(menuItem.getText());

            // Return the completed view to render on screen
            return convertView;
        }
    }


}
