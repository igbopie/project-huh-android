package com.huhapp.android.util;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huhapp.android.api.Api;
import com.huhapp.android.api.model.Question;
import com.huhapp.android.common.logger.Log;
import com.huhapp.android.customview.VoteDownView;
import com.huhapp.android.customview.VoteUpView;
import com.huhapp.android.huhapp.R;

/**
 * Created by igbopie on 4/26/15.
 */
public class QuestionViewUtil {
    public static Integer QUESTION_TYPE_WIDTH = null;
    public static Integer VOTER_WIDTH = null;

    public static void fillView(View convertView, final Question question, final Context context, boolean showUser) {
// Lookup view for data population
        TextView qText = (TextView) convertView.findViewById(R.id.qText);
        TextView qType = (TextView) convertView.findViewById(R.id.qType);
        final TextView voteDownView = (TextView) convertView.findViewById(R.id.voteDown);
        final TextView voteUpView = (TextView) convertView.findViewById(R.id.voteUp);
        final TextView voteMeter = (TextView) convertView.findViewById(R.id.voteMeter);
        final View voter = convertView.findViewById(R.id.voter);
        TextView createdText = (TextView) convertView.findViewById(R.id.createdText);
        TextView repliesText = (TextView) convertView.findViewById(R.id.repliesText);
        View middlePartContainer = convertView.findViewById(R.id.middlePartContainer);
        View questionContainer = convertView.findViewById(R.id.questionContainer);

        qText.setAutoLinkMask(Linkify.WEB_URLS);
        qText.setLinksClickable(showUser);
        qText.setText(question.getText() + "?");

        if (QUESTION_TYPE_WIDTH == null) {
            qType.setText("WHERE");
            qType.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            QUESTION_TYPE_WIDTH = qType.getMeasuredWidth();
        }
        qType.getLayoutParams().width = QUESTION_TYPE_WIDTH;
        qType.setText(question.getType().getWord());

        qType.setBackgroundColor(Color.parseColor(question.getType().getColor()));
        String timeText = DateUtil.getDateInMillis(question.getCreated()) + "";
        if (showUser) {
            timeText += " by "+question.getUsername();
        }
        createdText.setText(timeText);
        repliesText.setText(question.getnComments() + " replies");

        setVoter(question, voteMeter, voteUpView, voteDownView, voter);


        /*convertView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(convertView.getMeasuredWidth(), View.MeasureSpec.AT_MOST);
        qText.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
        questionContainer.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
        middlePartContainer.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
        convertView.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
        //middlePartContainer.
        /*if (middlePartContainer.getMeasuredHeight() < qText.getMeasuredHeight()) {
            middlePartContainer.getLayoutParams().height = qText.getMeasuredHeight();
        } else {
            qText.getLayoutParams().height = middlePartContainer.getMeasuredHeight();
        }
        if (qText.getMeasuredHeight() < convertView.getMeasuredHeight()) {
            qText.getLayoutParams().height = convertView.getMeasuredHeight();
        } else {
            qText.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        Log.i("QUESTIONVIEWUTIL",  qText.getText()+ "");
        Log.i("QUESTIONVIEWUTIL",  qText.getMeasuredHeight()+ "-" + middlePartContainer.getMeasuredHeight() + "-" + qText.getLineCount() +"-"+qText.getLineHeight() + "-"+ questionContainer.getMeasuredHeight()+ "-" + convertView.getMeasuredHeight());
        */

        voteDownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoteDown voteUp = new VoteDown(question.getId(), context, new OnTaskCompleted<Question>() {
                    @Override
                    public void onTaskCompleted(Question result) {
                        if (result != null) {
                            question.setMyVote(result.getMyVote());
                            question.setVoteScore(result.getVoteScore());
                            setVoter(question, voteMeter, voteUpView, voteDownView, voter);
                        }
                    }
                });
                voteUp.execute();
            }
        });

        voteUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoteUp voteUp = new VoteUp(question.getId(), context, new OnTaskCompleted<Question>() {
                    @Override
                    public void onTaskCompleted(Question result) {
                        if (result != null) {
                            question.setMyVote(result.getMyVote());
                            question.setVoteScore(result.getVoteScore());
                            setVoter(question, voteMeter, voteUpView, voteDownView, voter);
                        }
                    }
                });
                voteUp.execute();
            }
        });


    }

    private static void setVoter(Question question,TextView voteMeter,TextView voteUpView,TextView voteDownView, View voter) {

        if (VOTER_WIDTH == null) {
            voteMeter.setText("9999");
            voteMeter.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            VOTER_WIDTH = voteMeter.getMeasuredWidth();
        }
        voter.getLayoutParams().width = VOTER_WIDTH;
        voteMeter.setText(question.getVoteScore() + "");


        if (question.getMyVote() > 0){
            voteMeter.setTextColor(Color.parseColor(question.getType().getColor()));
            voteDownView.setTextColor(Color.parseColor("#BBBBBB"));
            voteUpView.setTextColor(Color.parseColor(question.getType().getColor()));
        } else if (question.getMyVote() < 0) {
            voteMeter.setTextColor(Color.parseColor(question.getType().getColor()));
            voteDownView.setTextColor(Color.parseColor(question.getType().getColor()));
            voteUpView.setTextColor(Color.parseColor("#BBBBBB"));
        } else {
            voteMeter.setTextColor(Color.parseColor("#BBBBBB"));
            voteDownView.setTextColor(Color.parseColor("#BBBBBB"));
            voteUpView.setTextColor(Color.parseColor("#BBBBBB"));
        }
    }

    private static class VoteUp extends AsyncTask<Void,Void,Question> {
        private String questionId;
        private OnTaskCompleted<Question> listener;
        private Context context;
        private VoteUp(String questionId, Context context, OnTaskCompleted listener) {
            this.questionId = questionId;
            this.listener = listener;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Question doInBackground(Void... voids) {
            return Api.questionVoteUp(questionId, PrefUtils.getFromPrefs(this.context, PrefUtils.PREFS_USER_ID, ""));
        }

        @Override
        protected void onPostExecute(Question result) {
            super.onPostExecute(result);
            listener.onTaskCompleted(result);
        }
    }

    private static class VoteDown extends AsyncTask<Void,Void,Question> {
        private String questionId;
        private OnTaskCompleted<Question> listener;
        private Context context;
        private VoteDown(String questionId, Context context, OnTaskCompleted listener) {
            this.questionId = questionId;
            this.listener = listener;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Question doInBackground(Void... voids) {
            return Api.questionVoteDown(questionId, PrefUtils.getFromPrefs(this.context, PrefUtils.PREFS_USER_ID, ""));
        }

        @Override
        protected void onPostExecute(Question result) {
            super.onPostExecute(result);
            listener.onTaskCompleted(result);
        }

    }
}
