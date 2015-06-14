package com.huhapp.android.util;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huhapp.android.R;
import com.huhapp.android.api.Api;
import com.huhapp.android.api.model.Question;
import com.huhapp.android.common.logger.Log;
import com.huhapp.android.customview.VoteDownView;
import com.huhapp.android.customview.VoteUpView;
import com.squareup.picasso.Picasso;

/**
 * Created by igbopie on 4/26/15.
 */
public class QuestionViewUtil {
    public static Integer VOTER_WIDTH = null;

    public static void fillView(View convertView, final Question question, final Context context, boolean showUser) {
// Lookup view for data population
        TextView qText = (TextView) convertView.findViewById(R.id.qText);
        View qType = convertView.findViewById(R.id.qType);
        final TextView voteDownView = (TextView) convertView.findViewById(R.id.voteDown);
        final TextView voteUpView = (TextView) convertView.findViewById(R.id.voteUp);
        final TextView voteMeter = (TextView) convertView.findViewById(R.id.voteMeter);
        final View voter = convertView.findViewById(R.id.voter);
        TextView createdText = (TextView) convertView.findViewById(R.id.createdText);
        TextView repliesText = (TextView) convertView.findViewById(R.id.repliesText);
        TextView username = (TextView) convertView.findViewById(R.id.username);
        View middlePartContainer = convertView.findViewById(R.id.middlePartContainer);
        View questionContainer = convertView.findViewById(R.id.questionContainer);
        ImageView image = (ImageView) convertView.findViewById(R.id.userImage);

        username.setText(question.getUsername()+" asks...");
        Picasso.with(context)
                .load(Api.getImageUrl(question))
                //.resize(50, 50)
                //.centerCrop()
                .into(image);

        qText.setAutoLinkMask(Linkify.WEB_URLS);
        qText.setLinksClickable(showUser);


        Spanned text = Html.fromHtml(
                String.format("<font color=\"%s\">%s</font> %s?",
                        question.getType().getColor(),
                        question.getType().getWord(),
                        question.getText()
                )
        );
        qText.setText(text);
        qType.setBackgroundColor(Color.parseColor(question.getType().getColor()));
        createdText.setText(DateUtil.getDateInMillis(question.getCreated()) + "");
        repliesText.setText(question.getnComments() + " replies");

        setVoter(question, voteMeter, voteUpView, voteDownView, voter);

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
            return Api.questionVoteUp(questionId, PropertyAccessor.getUserId());
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
            return Api.questionVoteDown(questionId, PropertyAccessor.getUserId());
        }

        @Override
        protected void onPostExecute(Question result) {
            super.onPostExecute(result);
            listener.onTaskCompleted(result);
        }

    }
}
