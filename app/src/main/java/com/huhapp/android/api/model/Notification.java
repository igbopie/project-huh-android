package com.huhapp.android.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by igbopie on 4/11/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification implements Serializable {


    private String type;
    private String message;
    private String questionId;
    private String commentId;
    private String yourCommentId;
    private boolean read;
    private Date created;
    private Comment comment;
    private Comment yourComment;
    private Question question;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getYourCommentId() {
        return yourCommentId;
    }

    public void setYourCommentId(String yourCommentId) {
        this.yourCommentId = yourCommentId;
    }

    public Comment getYourComment() {
        return yourComment;
    }

    public void setYourComment(Comment yourComment) {
        this.yourComment = yourComment;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", questionId='" + questionId + '\'' +
                ", commentId='" + commentId + '\'' +
                ", yourCommentId='" + yourCommentId + '\'' +
                ", read=" + read +
                ", created=" + created +
                ", comment=" + comment +
                ", yourComment=" + yourComment +
                ", question=" + question +
                '}';
    }
}
