package com.huhapp.android.util;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.huhapp.android.api.Api;
import com.huhapp.android.api.model.Comment;
import com.huhapp.android.api.model.Question;
import com.huhapp.android.customview.VoteDownView;
import com.huhapp.android.customview.VoteUpView;
import com.huhapp.android.huhapp.R;

/**
 * Created by igbopie on 4/26/15.
 */
public class CommentViewUtil {


    public static Integer VOTER_WIDTH = null;

    public static void fillView(View convertView, final Comment comment, final Question question, final Context context) {
// Lookup view for data population
        TextView qText = (TextView) convertView.findViewById(R.id.qText);
        final TextView voteDownView = (TextView) convertView.findViewById(R.id.voteDown);
        final TextView voteUpView = (TextView) convertView.findViewById(R.id.voteUp);
        final TextView voteMeter = (TextView) convertView.findViewById(R.id.voteMeter);
        final View voter =  convertView.findViewById(R.id.voter);
        TextView createdText = (TextView) convertView.findViewById(R.id.createdText);
        TextView repliesText = (TextView) convertView.findViewById(R.id.repliesText);

        qText.setText(comment.getText());
        createdText.setText(DateUtil.getDateInMillis(comment.getCreated()));
        repliesText.setText(comment.getUsername());


        setVoter(comment, question, voteMeter, voteUpView, voteDownView, voter);

        voteDownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoteDown questionVoteUp = new VoteDown(comment.getId(), context, new OnTaskCompleted<Comment>() {
                    @Override
                    public void onTaskCompleted(Comment result) {
                        if (result != null) {
                            comment.setMyVote(result.getMyVote());
                            comment.setVoteScore(result.getVoteScore());
                            setVoter(comment, question, voteMeter, voteUpView, voteDownView, voter);
                        }
                    }
                });
                questionVoteUp.execute();
            }
        });

        voteUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoteUp questionVoteUp = new VoteUp(comment.getId(), context, new OnTaskCompleted<Comment>() {
                    @Override
                    public void onTaskCompleted(Comment result) {
                        if (result != null) {
                            comment.setMyVote(result.getMyVote());
                            comment.setVoteScore(result.getVoteScore());
                            setVoter(comment, question, voteMeter, voteUpView, voteDownView, voter);
                        }
                    }
                });
                questionVoteUp.execute();
            }
        });
    }

    private static void setVoter(Comment comment, Question question, TextView voteMeter,TextView voteUpView,TextView voteDownView, View voter) {

        if (VOTER_WIDTH == null) {
            voteMeter.setText("9999");
            voteMeter.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            VOTER_WIDTH = voteMeter.getMeasuredWidth();
        }
        voter.getLayoutParams().width = VOTER_WIDTH;
        voteMeter.setText(comment.getVoteScore() + "");


        if (comment.getMyVote() > 0){
            voteMeter.setTextColor(Color.parseColor(question.getType().getColor()));
            voteDownView.setTextColor(Color.parseColor("#BBBBBB"));
            voteUpView.setTextColor(Color.parseColor(question.getType().getColor()));
        } else if (comment.getMyVote() < 0) {
            voteMeter.setTextColor(Color.parseColor(question.getType().getColor()));
            voteDownView.setTextColor(Color.parseColor(question.getType().getColor()));
            voteUpView.setTextColor(Color.parseColor("#BBBBBB"));
        } else {
            voteMeter.setTextColor(Color.parseColor("#BBBBBB"));
            voteDownView.setTextColor(Color.parseColor("#BBBBBB"));
            voteUpView.setTextColor(Color.parseColor("#BBBBBB"));
        }
    }


    private static class VoteUp extends AsyncTask<Void,Void,Comment> {
        private String commentId;
        private OnTaskCompleted<Comment> listener;
        private Context context;
        private VoteUp(String commentId, Context context, OnTaskCompleted listener) {
            this.commentId = commentId;
            this.listener = listener;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Comment doInBackground(Void... voids) {
            return Api.commentVoteUp(commentId, PropertyAccessor.getUserId());
        }

        @Override
        protected void onPostExecute(Comment result) {
            super.onPostExecute(result);
            listener.onTaskCompleted(result);
        }
    }

    private static class VoteDown extends AsyncTask<Void,Void,Comment> {
        private String questionId;
        private OnTaskCompleted<Comment> listener;
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
        protected Comment doInBackground(Void... voids) {
            return Api.commentVoteDown(questionId, PropertyAccessor.getUserId());
        }

        @Override
        protected void onPostExecute(Comment result) {
            super.onPostExecute(result);
            listener.onTaskCompleted(result);
        }

    }
}
