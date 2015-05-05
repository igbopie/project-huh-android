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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.huhapp.android.api.Api;
import com.huhapp.android.api.model.Notification;
import com.huhapp.android.api.model.Question;
import com.huhapp.android.huhapp.R;
import com.huhapp.android.util.DateUtil;
import com.huhapp.android.util.PrefUtils;
import com.huhapp.android.util.QuestionTabsFragment;
import com.huhapp.android.util.QuestionViewUtil;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment  extends ListFragment
{
    NotificationAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public static NotificationsFragment newInstance()
    {
        NotificationsFragment myFragment = new NotificationsFragment();

        Bundle args = new Bundle();
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //new CustomToast(getActivity(), numbers_digits[(int) id]);
        Intent intent = new Intent(this.getActivity(), QuestionDetailActivity.class);
        Notification notification = adapter.getItem(position);
        intent.putExtra(QuestionDetailActivity.EXTRA_QUESTION_ID, notification.getQuestionId());
        startActivity(intent);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        adapter = new NotificationAdapter(
                inflater.getContext(),
                new ArrayList<Notification>());

        setListAdapter(adapter);

        //super.onCreateView(inflater, container, savedInstanceState);
        View view =  inflater.inflate(R.layout.fragment_notification_list, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // http://stackoverflow.com/questions/26858692/swiperefreshlayout-setrefreshing-not-showing-indicator-initially
        mSwipeRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetNotificationTask().execute();
            }
        });

        new GetNotificationTask().execute();
    }

    public class NotificationAdapter extends ArrayAdapter<Notification> {
        public NotificationAdapter(Context context, List<Notification> notifications) {
            super(context, 0, notifications);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Notification notification = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_list_item_layout, parent, false);
            }

            TextView messageTextView = (TextView) convertView.findViewById(R.id.message);
            messageTextView.setText(notification.getMessage());

            TextView createdTextView = (TextView) convertView.findViewById(R.id.created);
            createdTextView.setText(DateUtil.getDateInMillis(notification.getCreated()));

            // Return the completed view to render on screen
            return convertView;
        }
    }

    private class GetNotificationTask extends AsyncTask<Void,Void,List<Notification>> {
        private GetNotificationTask() {
        }

        @Override
        protected void onPreExecute() {
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
            super.onPreExecute();
        }

        @Override
        protected List<Notification> doInBackground(Void... voids) {
            return Api.notificationList(PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_USER_ID, ""));
        }

        @Override
        protected void onPostExecute(List<Notification> result) {
            super.onPostExecute(result);

            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }

            if (result != null) {
                adapter.clear();
                adapter.addAll(result);
                adapter.notifyDataSetChanged();
            }
            //swipeLayout.setRefreshing(false);*/
        }
    }

}
