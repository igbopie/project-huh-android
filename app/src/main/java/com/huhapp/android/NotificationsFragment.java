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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huhapp.android.api.Api;
import com.huhapp.android.api.model.Notification;
import com.huhapp.android.util.DateUtil;
import com.huhapp.android.util.NotificationUpdateListener;
import com.huhapp.android.util.PropertyAccessor;
import com.huhapp.android.util.QuestionViewUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationsFragment  extends ListFragment
{
    NotificationAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Timer readTimer;
    boolean detached = false;
    NotificationUpdateListener notificationListener;

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

            TextView qText = (TextView) convertView.findViewById(R.id.qText);
            TextView createdTextView = (TextView) convertView.findViewById(R.id.createdText);
            TextView messageTextView = (TextView) convertView.findViewById(R.id.notificationText);
            TextView comment1View = (TextView) convertView.findViewById(R.id.comment1Text);
            ImageView image = (ImageView) convertView.findViewById(R.id.notificationImage);

            comment1View.setVisibility(View.GONE);

            qText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.questionFontSize));
            qText.setTextColor(getResources().getColor(R.color.questionFontColor));
            comment1View.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.questionFontSize));
            comment1View.setTextColor(getResources().getColor(R.color.questionFontColor));

            if (notification.isRead()){
                messageTextView.setTextColor(getResources().getColor(R.color.questionSmallFontColor));
                convertView.setBackgroundColor(Color.WHITE);
            } else {
                messageTextView.setTextColor(getResources().getColor(R.color.buttonTabMainColor));
                convertView.setBackgroundColor(getResources().getColor(R.color.notificationUnreadBackground));
            }

            String message = "";
            if (notification.getType().equals("OnQuestionPosted")){
                message = "A new question was posted";
                image.setImageResource(R.drawable.notification_question);

            } else if (notification.getType().equals("OnCommentOnMyQuestion")){
                message = "Your question was commented";
                image.setImageResource(R.drawable.notification_comment);

                qText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.questionSmallFontSize));
                qText.setTextColor(getResources().getColor(R.color.notMainNotification));

                if (notification.getComment() != null) {
                    comment1View.setText(notification.getComment().getText());
                    comment1View.setVisibility(View.VISIBLE);
                }

            } else if (notification.getType().equals("OnCommentOnMyComment")){
                message = "Your comment was commented";
                image.setImageResource(R.drawable.notification_comment);

                qText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.questionSmallFontSize));
                qText.setTextColor(getResources().getColor(R.color.notMainNotification));

                if (notification.getComment() != null) {
                    comment1View.setText(notification.getComment().getText());
                    comment1View.setVisibility(View.VISIBLE);
                }

            } else if (notification.getType().equals("OnUpVoteOnMyQuestion")){
                message = "Your question was up voted";
                image.setImageResource(R.drawable.notification_up_vote);

            } else if (notification.getType().equals("OnDownVoteOnMyQuestion")){
                message = "Your question was down voted";
                image.setImageResource(R.drawable.notification_down_vote);

            } else if (notification.getType().equals("OnUpVoteOnMyComment")){
                message = "Your comment was up voted";
                image.setImageResource(R.drawable.notification_up_vote);

                qText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.questionSmallFontSize));
                qText.setTextColor(getResources().getColor(R.color.notMainNotification));

                if (notification.getComment() != null) {
                    comment1View.setText(notification.getComment().getText());
                    comment1View.setVisibility(View.VISIBLE);
                }

            } else if (notification.getType().equals("OnDownVoteOnMyComment")){
                message = "Your comment was down voted";
                image.setImageResource(R.drawable.notification_down_vote);

                qText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.questionSmallFontSize));
                qText.setTextColor(getResources().getColor(R.color.notMainNotification));

                if (notification.getComment() != null) {
                    comment1View.setText("- " + notification.getComment().getText());
                    comment1View.setVisibility(View.VISIBLE);
                }

            } else {
                message = "Unknown message";
            }

            messageTextView.setText(message);
            createdTextView.setText(DateUtil.getDateInMillis(notification.getCreated()));
            qText.setText(QuestionViewUtil.getSpannedFromQuestion(notification.getQuestion()));

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
            return Api.notificationList(PropertyAccessor.getUserId());
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

                if (readTimer == null && !detached) {
                    readTimer = new Timer();
                    readTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (!detached) {
                                readTimer = null;
                                new MarkAllAsRead().execute();
                            }
                        }
                    }, 3000);
                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            notificationListener = (NotificationUpdateListener) activity;
        } catch (ClassCastException e) {
            // no problemo
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        notificationListener = null;
        detached = true;
        if (readTimer != null) {
            readTimer.cancel();
            readTimer = null;
        }
    }

    private class MarkAllAsRead extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            return Api.notificationMarkAllAsRead(PropertyAccessor.getUserId());
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (notificationListener != null) {
                notificationListener.notificationUpdated();
            }

        }
    }
}
