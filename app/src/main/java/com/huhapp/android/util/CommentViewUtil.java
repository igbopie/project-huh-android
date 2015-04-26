package com.huhapp.android.util;

import android.content.Context;
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

    public static void fillView(View convertView, final Comment comment, final Context context) {
// Lookup view for data population
        TextView qText = (TextView) convertView.findViewById(R.id.qText);
        final VoteDownView voteDownView = (VoteDownView) convertView.findViewById(R.id.voteDown);
        final VoteUpView voteUpView = (VoteUpView) convertView.findViewById(R.id.voteUp);
        final TextView voteMeter = (TextView) convertView.findViewById(R.id.voteMeter);
        TextView createdText = (TextView) convertView.findViewById(R.id.createdText);
        TextView repliesText = (TextView) convertView.findViewById(R.id.repliesText);

        qText.setText(comment.getText() + "?");
        voteMeter.setText(comment.getVoteScore() + "");
        createdText.setText(DateUtil.getDateInMillis(comment.getCreated()));
        repliesText.setText(comment.getUsername());

        if (comment.getMyVote() > 0){
            voteDownView.setActive(false);
            voteUpView.setActive(true);
        } else if (comment.getMyVote() < 0) {
            voteDownView.setActive(true);
            voteUpView.setActive(false);
        } else {
            voteDownView.setActive(false);
            voteUpView.setActive(false);
        }

        voteDownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoteDown questionVoteUp = new VoteDown(comment.getId(), context, new OnTaskCompleted<Comment>() {
                    @Override
                    public void onTaskCompleted(Comment result) {
                        if (result != null) {
                            comment.setMyVote(result.getMyVote());
                            comment.setVoteScore(result.getVoteScore());
                            if (result.getMyVote() > 0){
                                voteDownView.setActive(false);
                                voteUpView.setActive(true);
                            } else if (result.getMyVote() < 0) {
                                voteDownView.setActive(true);
                                voteUpView.setActive(false);
                            } else {
                                voteDownView.setActive(false);
                                voteUpView.setActive(false);
                            }
                            voteMeter.setText(result.getVoteScore() + "");
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
                            if (result.getMyVote() > 0){
                                voteDownView.setActive(false);
                                voteUpView.setActive(true);
                            } else if (result.getMyVote() < 0) {
                                voteDownView.setActive(true);
                                voteUpView.setActive(false);
                            } else {
                                voteDownView.setActive(false);
                                voteUpView.setActive(false);
                            }
                            voteMeter.setText(result.getVoteScore() + "");
                        }
                    }
                });
                questionVoteUp.execute();
            }
        });
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
            return Api.commentVoteUp(commentId, PrefUtils.getFromPrefs(this.context, PrefUtils.PREFS_USER_ID, ""));
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
            return Api.commentVoteDown(questionId, PrefUtils.getFromPrefs(this.context, PrefUtils.PREFS_USER_ID, ""));
        }

        @Override
        protected void onPostExecute(Comment result) {
            super.onPostExecute(result);
            listener.onTaskCompleted(result);
        }

    }
}
