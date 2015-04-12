package com.huhapp.android.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by igbopie on 4/11/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Question implements Serializable {
    private QuestionType type;
    private String text;
    private Date created;
    private int voteScore;
    private int nComments;
    private int nVotes;
    private int nUpVotes;
    private int nDownVotes;
    private String username;
    private String url;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Question{" +
                "type=" + type +
                ", text='" + text + '\'' +
                ", created=" + created +
                ", voteScore=" + voteScore +
                ", nComments=" + nComments +
                ", nVotes=" + nVotes +
                ", nUpVotes=" + nUpVotes +
                ", nDownVotes=" + nDownVotes +
                ", username='" + username + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getVoteScore() {
        return voteScore;
    }

    public void setVoteScore(int voteScore) {
        this.voteScore = voteScore;
    }

    public int getnComments() {
        return nComments;
    }

    public void setnComments(int nComments) {
        this.nComments = nComments;
    }

    public int getnVotes() {
        return nVotes;
    }

    public void setnVotes(int nVotes) {
        this.nVotes = nVotes;
    }

    public int getnUpVotes() {
        return nUpVotes;
    }

    public void setnUpVotes(int nUpVotes) {
        this.nUpVotes = nUpVotes;
    }

    public int getnDownVotes() {
        return nDownVotes;
    }

    public void setnDownVotes(int nDownVotes) {
        this.nDownVotes = nDownVotes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

}
