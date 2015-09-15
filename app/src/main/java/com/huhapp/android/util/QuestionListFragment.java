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
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.huhapp.android.CustomPullAnimation;
import com.huhapp.android.MainActivity;
import com.huhapp.android.QuestionDetailActivity;
import com.huhapp.android.R;
import com.huhapp.android.api.Api;
import com.huhapp.android.api.model.Question;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class QuestionListFragment extends ListFragment implements PtrHandler
{
    QuestionAdapter adapter;
    String apiEndpoint;
    PtrFrameLayout swipeToRefresh;
    ProgressBar progressBar;
    View noQuestionsPlaceholder;

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

        swipeToRefresh = (PtrFrameLayout) view.findViewById(R.id.swipeLayout);
        swipeToRefresh.setPtrHandler(this);
        CustomPullAnimation headerView = new CustomPullAnimation(getActivity());
        swipeToRefresh.setHeaderView(headerView);
        swipeToRefresh.addPtrUIHandler(headerView);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        noQuestionsPlaceholder = view.findViewById(R.id.noQuestionsPlaceholder);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.CREATE_QUESTION_CODE && resultCode == Activity.RESULT_OK) {
            new GetQuestionsTask(true).execute();
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GetQuestionsTask gQT = new GetQuestionsTask(true);
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
        private boolean showLoader;

        public GetQuestionsTask(boolean showLoader) {
            this.showLoader = showLoader;
        }

        public GetQuestionsTask() {
            this.showLoader = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (showLoader) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<Question> doInBackground(Void... voids) {
            return Api.getQuestionBySomething(apiEndpoint,
                    PropertyAccessor.getUserLongitude(),
                    PropertyAccessor.getUserLatitude());
        }

        @Override
        protected void onPostExecute(List<Question> result) {
            super.onPostExecute(result);


            progressBar.setVisibility(View.INVISIBLE);

            if (result != null) {
                adapter.clear();
                adapter.addAll(result);
                adapter.notifyDataSetChanged();
            }

            if (adapter.getCount() == 0) {
                noQuestionsPlaceholder.setVisibility(View.VISIBLE);
            } else {
                noQuestionsPlaceholder.setVisibility(View.GONE);
            }

            if (swipeToRefresh != null) {
                swipeToRefresh.refreshComplete();
            }
        }
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        new GetQuestionsTask().execute();
    }

}
