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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huhapp.android.MainActivity;
import com.huhapp.android.QuestionDetailActivity;
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
    SwipeRefreshLayout mSwipeRefreshLayout;

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
        Intent intent = new Intent(this.getActivity(), QuestionDetailActivity.class);
        Question question = adapter.getItem(position);
        intent.putExtra(QuestionDetailActivity.EXTRA_QUESTION_ID, question.getId());
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        apiEndpoint = getArguments().getString("apiEndpoint", Api.ENDPOINT_QUESTIONS_LATEST);

        adapter = new QuestionAdapter(
                inflater.getContext(),
                new ArrayList<Question>());

        setListAdapter(adapter);



        //super.onCreateView(inflater, container, savedInstanceState);
        View view =  inflater.inflate(R.layout.fragment_questions_list, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.CREATE_QUESTION_CODE && resultCode == Activity.RESULT_OK) {
            new GetQuestionsTask().execute();
        }
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
                new GetQuestionsTask().execute();
            }
        });

        GetQuestionsTask gQT = new GetQuestionsTask();
        gQT.execute();
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

            QuestionViewUtil.fillView(convertView, question, getActivity(), false);

            // Return the completed view to render on screen
            return convertView;
        }
    }

    private class GetQuestionsTask extends AsyncTask<Void,Void,List<Question>> {
        private GetQuestionsTask() {
        }

        @Override
        protected void onPreExecute() {
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
            super.onPreExecute();
        }

        @Override
        protected List<Question> doInBackground(Void... voids) {
            return Api.getQuestionBySomething(apiEndpoint,
                    PropertyAccessor.getUserId(),
                    PropertyAccessor.getUserLongitude(),
                    PropertyAccessor.getUserLatitude());
        }

        @Override
        protected void onPostExecute(List<Question> result) {
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
