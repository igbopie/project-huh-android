package com.huhapp.android;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huhapp.android.api.Api;
import com.huhapp.android.api.model.Comment;
import com.huhapp.android.api.model.Question;
import com.huhapp.android.common.logger.Log;
import com.huhapp.android.huhapp.R;
import com.huhapp.android.util.CommentViewUtil;
import com.huhapp.android.util.PrefUtils;
import com.huhapp.android.util.QuestionViewUtil;

import java.util.ArrayList;
import java.util.List;


public class QuestionDetailActivity extends ListActivity {

    public static String EXTRA_QUESTION_ID = "__question_id__";

    private String questionId;
    private Question question;
    private List<Comment> comments;
    private ArrayList adapterArrayList;
    private QuestionAndCommentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionId = getIntent().getStringExtra(EXTRA_QUESTION_ID);
        adapterArrayList = new ArrayList();

        // initiate the listadapter
        adapter = new QuestionAndCommentsAdapter(this, adapterArrayList);

        // assign the list adapter
        setListAdapter(adapter);

        new GetQuestionAndComments().execute();

    }

    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
    }

    public class QuestionAndCommentsAdapter extends ArrayAdapter {
        public QuestionAndCommentsAdapter(Context context, ArrayList questionAndComments) {
            super(context, R.layout.question_list_item_layout, questionAndComments);
        }

        @Override
        public int getViewTypeCount() {
            return 2; //question and comments
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position) instanceof Question? 0: 1;
        }
        // Given the item type, responsible for returning the correct inflated XML layout file
        private View getInflatedLayoutForType(int type, ViewGroup parent) {
            if (type == 0) {
                return LayoutInflater.from(getContext()).inflate(R.layout.question_list_item_layout, parent, false);
            } else {
                return LayoutInflater.from(getContext()).inflate(R.layout.comment_list_item_layout, parent, false);
            }
        }

        // Get a View that displays the data at the specified position in the data set.
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                // Get the data item type for this position
                int type = getItemViewType(position);
                // Inflate XML layout based on the type
                convertView = getInflatedLayoutForType(type, parent);
            }

            // Get the data item for this position
            Object item = getItem(position);
            if (item instanceof Question) {
                Question question = (Question) item;
                QuestionViewUtil.fillView(convertView, question, QuestionDetailActivity.this);
            } else {
                Comment comment = (Comment) item;
                CommentViewUtil.fillView(convertView, comment, QuestionDetailActivity.this);
            }

            return convertView;
        }
    }

    private class GetQuestionAndComments extends AsyncTask<Void,Void,Void> {

        private GetQuestionAndComments() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String userId = PrefUtils.getFromPrefs(QuestionDetailActivity.this, PrefUtils.PREFS_USER_ID, "");

            question = Api.getQuestion(questionId, userId);
            comments = Api.getComments(questionId, userId);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            adapterArrayList.clear();
            adapterArrayList.add(question);
            adapterArrayList.addAll(comments);
            adapter.notifyDataSetChanged();
        }
    }
}