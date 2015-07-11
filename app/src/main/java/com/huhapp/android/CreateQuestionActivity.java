package com.huhapp.android;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.huhapp.android.api.model.Question;
import com.huhapp.android.api.model.QuestionType;
import com.huhapp.android.util.PropertyAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class CreateQuestionActivity extends ListActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private ProgressDialog progressDialog;
    private QuestionTypeAdapter adapter;
    private ArrayList<QuestionType> questionTypes;
    private ArrayList<QuestionType> displayedQuestionTypes;
    private boolean choosingQuestionTypeMode = false;
    private QuestionType selectedQuestionType;
    private final int MAX_CHARS = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        displayedQuestionTypes = new ArrayList<QuestionType>();
        questionTypes = new ArrayList<QuestionType>();
        // initiate the listadapter
        adapter = new QuestionTypeAdapter(this, displayedQuestionTypes);
        // assign the list adapter
        setListAdapter(adapter);

        new FetchQuestionTypes().execute();


        final TextView questionTextCounter = (TextView) findViewById(R.id.questionTextCounter);
        final EditText questionText = (EditText) findViewById(R.id.questionText);


        questionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int charsLeft = MAX_CHARS - s.length();
                questionTextCounter.setText(charsLeft+"");

                if (charsLeft < 0){
                    questionTextCounter.setTextColor(Color.RED);
                } else {
                    questionTextCounter.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //nothint
            }
        });



    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("CREATEQUESTIONACTIVITY", "CLICK");

        View createQuestionContainer = findViewById(R.id.createQuestionContainer);
        EditText questionText = (EditText) findViewById(R.id.questionText);
        if (!choosingQuestionTypeMode) {
            displayedQuestionTypes.clear();
            displayedQuestionTypes.addAll(questionTypes);
            adapter.notifyDataSetChanged();
            createQuestionContainer.setVisibility(View.INVISIBLE);

            questionText.clearFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(questionText.getWindowToken(), 0);

            choosingQuestionTypeMode = true;

        } else {
            selectedQuestionType = adapter.getItem(position);
            displayedQuestionTypes.clear();
            displayedQuestionTypes.add(selectedQuestionType);
            adapter.notifyDataSetChanged();
            createQuestionContainer.setVisibility(View.VISIBLE);

            questionText.requestFocus();

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInputFromWindow(questionText.getWindowToken(), 0, 0);

            choosingQuestionTypeMode = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //I don't want to change the others icon
        if (item.getItemId() == R.id.createQuestion) {

            EditText questionText = (EditText) findViewById(R.id.questionText);
            String text = questionText.getText().toString();
            if (text.length() > MAX_CHARS) {
                Toast.makeText(CreateQuestionActivity.this, "Text too long", Toast.LENGTH_SHORT).show();
            } else {
                new CreateQuestion(selectedQuestionType, text).execute();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class QuestionTypeAdapter extends ArrayAdapter<QuestionType> {
        public QuestionTypeAdapter(Context context, ArrayList<QuestionType> questionTypes) {
            super(context, R.layout.question_type_list_item_layout, questionTypes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                // Inflate XML layout based on the type
                convertView =  LayoutInflater.from(getContext()).inflate(R.layout.question_type_list_item_layout, parent, false);
            }

            QuestionType qType = getItem(position);
            TextView qTypeText = (TextView) convertView.findViewById(R.id.qType);

            qTypeText.setText(qType.getWord());
            qTypeText.setBackgroundColor(Color.parseColor(qType.getColor()));

            //Fill
            return convertView;
        }
    }


    private class FetchQuestionTypes extends AsyncTask<Void,Void,List<QuestionType>> {

        private FetchQuestionTypes() {}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(CreateQuestionActivity.this, null, "Loading...");
        }

        @Override
        protected List<QuestionType> doInBackground(Void... voids) {
            return Api.questionTypeList();
        }

        @Override
        protected void onPostExecute(List<QuestionType> result) {
            super.onPostExecute(result);

            questionTypes.clear();
            questionTypes.addAll(result);
            displayedQuestionTypes.clear();

            //Random
            int randomIndex = new Random().nextInt(questionTypes.size());
            selectedQuestionType = questionTypes.get(randomIndex);
            displayedQuestionTypes.add(selectedQuestionType);

            adapter.notifyDataSetChanged();

            if (progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    private class CreateQuestion extends AsyncTask<Void,Void,Question> {
        private QuestionType questionType;
        private String text;

        private CreateQuestion(QuestionType questionType, String text) {
            this.questionType = questionType;
            this.text = text;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(CreateQuestionActivity.this, null, "Loading...");
        }

        @Override
        protected Question doInBackground(Void... voids) {
            return Api.questionCreate(this.questionType.getWord(),
                    this.text,
                    PropertyAccessor.getUserId(),
                    PropertyAccessor.getUserLongitude(),
                    PropertyAccessor.getUserLatitude());
        }

        @Override
        protected void onPostExecute(Question result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            progressDialog = null;

            if (result != null) {

                //CLOSE ACTIVITY
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(CreateQuestionActivity.this, "Opps! something went wrong. Try again.", Toast.LENGTH_LONG).show();
            }
        }
    }



}
