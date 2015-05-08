package com.huhapp.android;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class QuestionDetailActivity extends ListActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public static String EXTRA_QUESTION_ID = "__question_id__";

    private String questionId;
    private Question question;
    private List<Comment> comments;
    private ArrayList adapterArrayList;
    private QuestionAndCommentsAdapter adapter;
    private EditText textBoxInput;
    private TextView textBoxButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        questionId = getIntent().getStringExtra(EXTRA_QUESTION_ID);
        adapterArrayList = new ArrayList();

        // initiate the listadapter
        adapter = new QuestionAndCommentsAdapter(this, adapterArrayList);

        // assign the list adapter
        setListAdapter(adapter);



        setContentView(R.layout.activity_question_detail);

        textBoxButton = (TextView) findViewById(R.id.textBoxButton);
        textBoxInput = (EditText) findViewById(R.id.textBoxInput);

        textBoxButton.setTextColor(Color.parseColor("#BBBBBB"));
        textBoxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostComment(textBoxInput.getText().toString()).execute();
            }
        });

        textBoxInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    textBoxButton.setTextColor(Color.parseColor("#52ACBE"));
                } else {
                    textBoxButton.setTextColor(Color.parseColor("#BBBBBB"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //nothint
            }
        });

        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetQuestionAndComments().execute();
            }
        });

        // http://stackoverflow.com/questions/26858692/swiperefreshlayout-setrefreshing-not-showing-indicator-initially
        mSwipeRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        // here as it uses view components
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
                QuestionViewUtil.fillView(convertView, question, QuestionDetailActivity.this, true);
            } else {
                Comment comment = (Comment) item;
                CommentViewUtil.fillView(convertView, comment, question, QuestionDetailActivity.this);
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

            SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
            mSwipeRefreshLayout.setRefreshing(true);

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
            SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
            mSwipeRefreshLayout.setRefreshing(false);
            adapterArrayList.clear();
            adapterArrayList.add(question);
            adapterArrayList.addAll(comments);
            adapter.notifyDataSetChanged();
        }
    }

    private class PostComment extends AsyncTask<Void,Void,Void> {
        String text;
        private PostComment(String text) {
            this.text = text;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
            mSwipeRefreshLayout.setRefreshing(true);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            String userId = PrefUtils.getFromPrefs(QuestionDetailActivity.this, PrefUtils.PREFS_USER_ID, "");
            Api.createComment(text, userId, questionId);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Refresh and close keyboard
            textBoxInput.setText("");
            textBoxInput.clearFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(textBoxInput.getWindowToken(), 0);

            new GetQuestionAndComments().execute();
        }
    }
}