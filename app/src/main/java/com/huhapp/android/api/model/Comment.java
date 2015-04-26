package com.huhapp.android.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by igbopie on 4/11/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment implements Serializable {
    @JsonProperty("_id")
    private String id;
    private String text;
    private Date created;
    private Date updated;
    private int voteScore;
    private int nVotes;
    private int nDownVotes;
    private int nUpVotes;
    private String username;
    private int myVote;

    public int getMyVote() {
        return myVote;
    }

    public void setMyVote(int myVote) {
        this.myVote = myVote;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public int getVoteScore() {
        return voteScore;
    }

    public void setVoteScore(int voteScore) {
        this.voteScore = voteScore;
    }

    public int getnVotes() {
        return nVotes;
    }

    public void setnVotes(int nVotes) {
        this.nVotes = nVotes;
    }

    public int getnDownVotes() {
        return nDownVotes;
    }

    public void setnDownVotes(int nDownVotes) {
        this.nDownVotes = nDownVotes;
    }

    public int getnUpVotes() {
        return nUpVotes;
    }

    public void setnUpVotes(int nUpVotes) {
        this.nUpVotes = nUpVotes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", voteScore=" + voteScore +
                ", nVotes=" + nVotes +
                ", nDownVotes=" + nDownVotes +
                ", nUpVotes=" + nUpVotes +
                ", username='" + username + '\'' +
                ", myVote=" + myVote +
                '}';
    }
}
