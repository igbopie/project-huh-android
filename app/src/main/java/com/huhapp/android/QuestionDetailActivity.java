package com.huhapp.android;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.huhapp.android.api.Api;
import com.huhapp.android.api.model.Comment;
import com.huhapp.android.api.model.Question;
import com.huhapp.android.common.logger.Log;
import com.huhapp.android.util.CommentViewUtil;
import com.huhapp.android.util.PropertyAccessor;
import com.huhapp.android.util.QuestionViewUtil;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class QuestionDetailActivity extends ListActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public static String EXTRA_QUESTION_ID = "__question_id__";

    public static final String REASON_INAPPROPRIATE = "Inappropriate";
    public static final String REASON_HATEFUL = "Hateful";
    public static final String REASON_CANCEL = "Cancel";

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
        Log.i("QuestionDetailActivity", "QuestionId "+questionId);
        adapterArrayList = new ArrayList();

        // initiate the listadapter
        adapter = new QuestionAndCommentsAdapter(this, adapterArrayList);

        // assign the list adapter
        setListAdapter(adapter);
        setContentView(R.layout.activity_question_detail);

        if (getActionBar()!= null) {
            getActionBar().setTitle("Loading...");
        }

        textBoxButton = (TextView) findViewById(R.id.textBoxButton);
        textBoxInput = (EditText) findViewById(R.id.textBoxInput);

        textBoxButton.setTextColor(Color.parseColor("#BBBBBB"));
        textBoxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textBoxInput.getText().toString().trim().length() > 0) {
                    new PostComment(textBoxInput.getText().toString()).execute();
                }
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

        PtrFrameLayout swipeToRefresh = (PtrFrameLayout) findViewById(R.id.swipeLayout);
        CustomPullAnimation headerView = new CustomPullAnimation(this);
        swipeToRefresh.setHeaderView(headerView);
        swipeToRefresh.addPtrUIHandler(headerView);
        swipeToRefresh.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                new GetQuestionAndComments().execute();
            }
        });

        // here as it uses view components
        new GetQuestionAndComments(true).execute();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_question_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("CLICK", ""+item.getItemId());
        if (item.getItemId() == R.id.questionDetailMoreOptions) {
            ///
            final PopupMenu popup = new PopupMenu(this, findViewById(R.id.questionDetailMoreOptions));
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_question_detail_popup, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.questionDetailShareOptions) {
                        startActivity(Intent.createChooser(getShareIntent(), "Share"));
                    } else if (item.getItemId() == R.id.questionDetailFlagOptions) {
                        CharSequence colors[] = new CharSequence[]{
                                REASON_INAPPROPRIATE,
                                REASON_HATEFUL,
                                REASON_CANCEL};

                        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionDetailActivity.this);
                        builder.setTitle("Select a Reason");
                        builder.setItems(colors, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String reason = null;
                                if (which == 0) {
                                    reason = REASON_INAPPROPRIATE;
                                } else if (which == 1) {
                                    reason = REASON_HATEFUL;
                                }

                                if (reason != null) {
                                    new Flag(reason).execute();
                                }
                            }
                        });
                        builder.show();

                        Log.i("FLAG", "FLAG");
                    }
                    return false;
                }
            });
            popup.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent getShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, question.getUrl());
        return shareIntent;
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
                return LayoutInflater.from(getContext()).inflate(R.layout.question_list_item_layout, parent, false);
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
        private boolean showLoader = false;

        private GetQuestionAndComments() {
            this.showLoader = false;
        }

        private GetQuestionAndComments(boolean showLoader) {
            this.showLoader = showLoader;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (showLoader){
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            question = Api.getQuestion(questionId);
            comments = Api.getComments(questionId);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            ((PtrFrameLayout) findViewById(R.id.swipeLayout)).refreshComplete();

            adapterArrayList.clear();
            if (question != null) {
                adapterArrayList.add(question);
            }
            if (comments != null) {
                adapterArrayList.addAll(comments);
            }
            adapter.notifyDataSetChanged();

            ActionBar mActionBar = getActionBar();
            //mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(question.getType().getColor())));
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(true);
            mActionBar.setTitle(question.getType().getWord());
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
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Api.createComment(text, questionId);
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

            new GetQuestionAndComments(true).execute();
        }
    }

    private class Flag extends AsyncTask<Void,Void,Void> {
        String text;
        private Flag(String text) {
            this.text = text;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Api.questionFlag(questionId, text);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            Toast.makeText(QuestionDetailActivity.this, "Question Flagged", Toast.LENGTH_SHORT).show();
        }
    }
}