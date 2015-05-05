package com.huhapp.android.util;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.huhapp.android.api.Api;
import com.huhapp.android.api.model.Question;
import com.huhapp.android.customview.VoteDownView;
import com.huhapp.android.customview.VoteUpView;
import com.huhapp.android.huhapp.R;

/**
 * Created by igbopie on 4/26/15.
 */
public class QuestionViewUtil {
    public static void fillView(View convertView, final Question question, final Context context) {
// Lookup view for data population
        TextView qText = (TextView) convertView.findViewById(R.id.qText);
        TextView qType = (TextView) convertView.findViewById(R.id.qType);
        final TextView voteDownView = (TextView) convertView.findViewById(R.id.voteDown);
        final TextView voteUpView = (TextView) convertView.findViewById(R.id.voteUp);
        final TextView voteMeter = (TextView) convertView.findViewById(R.id.voteMeter);
        TextView createdText = (TextView) convertView.findViewById(R.id.createdText);
        TextView repliesText = (TextView) convertView.findViewById(R.id.repliesText);

        qText.setText(question.getText()+"?");
        qType.setText(question.getType().getWord());
        qType.setBackgroundColor(Color.parseColor(question.getType().getColor()));
        createdText.setText(DateUtil.getDateInMillis(question.getCreated()));
        repliesText.setText(question.getnComments() + " replies");

        setVoter(question, voteMeter, voteUpView, voteDownView);

        voteDownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoteDown voteUp = new VoteDown(question.getId(), context, new OnTaskCompleted<Question>() {
                    @Override
                    public void onTaskCompleted(Question result) {
                        if (result != null) {
                            question.setMyVote(result.getMyVote());
                            question.setVoteScore(result.getVoteScore());
                            setVoter(question, voteMeter, voteUpView, voteDownView);
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
                            setVoter(question, voteMeter, voteUpView, voteDownView);
                        }
                    }
                });
                voteUp.execute();
            }
        });
    }

    private static void setVoter(Question question,TextView voteMeter,TextView voteUpView,TextView voteDownView) {
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
