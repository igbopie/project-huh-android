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

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.huhapp.android.api.Api;
import com.huhapp.android.api.model.Question;
import com.huhapp.android.common.logger.Log;
import com.huhapp.android.common.view.SlidingTabLayout;
import com.huhapp.android.customview.VoteDownView;
import com.huhapp.android.customview.VoteUpView;
import com.huhapp.android.huhapp.R;

import java.util.ArrayList;
import java.util.List;

public class QuestionListFragment extends ListFragment
{
    QuestionAdapter adapter;
    String apiEndpoint;

    public static QuestionListFragment newInstance(String apiEndpoint)
    {
        QuestionListFragment myFragment = new QuestionListFragment();

        Bundle args = new Bundle();
        args.putString("apiEndpoint", apiEndpoint);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //new CustomToast(getActivity(), numbers_digits[(int) id]);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        apiEndpoint = getArguments().getString("apiEndpoint", Api.ENDPOINT_QUESTIONS_LATEST);

        adapter = new QuestionAdapter(
                inflater.getContext(),
                new ArrayList<Question>());

        setListAdapter(adapter);

        GetQuestionsTask gQT = new GetQuestionsTask();
        gQT.execute();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public class QuestionAdapter extends ArrayAdapter<Question> {
        public QuestionAdapter(Context context, List<Question> questions) {
            super(context, 0, questions);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Question question = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.question_list_item_layout, parent, false);
            }
            // Lookup view for data population
            TextView qText = (TextView) convertView.findViewById(R.id.qText);
            TextView qType = (TextView) convertView.findViewById(R.id.qType);
            VoteDownView voteDownView = (VoteDownView) convertView.findViewById(R.id.voteDown);
            VoteUpView voteUpView = (VoteUpView) convertView.findViewById(R.id.voteUp);
            TextView voteMeter = (TextView) convertView.findViewById(R.id.voteMeter);
            TextView createdText = (TextView) convertView.findViewById(R.id.createdText);
            TextView repliesText = (TextView) convertView.findViewById(R.id.repliesText);

            qText.setText(question.getText()+"?");
            qType.setText(question.getType().getWord());
            qType.setBackgroundColor(Color.parseColor(question.getType().getColor()));
            voteMeter.setText(question.getVoteScore() + "");
            createdText.setText(DateUtil.getDateInMillis(question.getCreated()));
            repliesText.setText(question.getnComments() + " replies");

            voteDownView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("VIEW", "Down VOTE!");
                }
            });

            voteUpView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("VIEW", "Up VOTE!");
                }
            });
            // Return the completed view to render on screen
            return convertView;
        }
    }

    private class GetQuestionsTask extends AsyncTask<Void,Void,List<Question>> {

        private GetQuestionsTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Question> doInBackground(Void... voids) {
            return Api.getQuestionBySomething(apiEndpoint);
        }

        @Override
        protected void onPostExecute(List<Question> result) {
            super.onPostExecute(result);
            if (result != null) {
                adapter.clear();
                adapter.addAll(result);
                adapter.notifyDataSetChanged();
            }
            //swipeLayout.setRefreshing(false);*/
        }
    }

}
