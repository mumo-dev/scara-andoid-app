package com.example.mumo.scara.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Question {
    private String questionId;
    private String text;
    private String username;
    private String category;
    private int votes;
    private int answers;
    private String imageReference;
    private long createdAt;

    public Question() {}

    public Question(String text, String username, String category, int votes, String url, int answers, long createdAt) {
        this.text = text;
        this.username = username;
        this.category = category;
        this.votes = votes;
        this.imageReference = url;
        this.createdAt = createdAt;
        this.answers =answers;
    }

    @Exclude
    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getImageReference() {
        return imageReference;
    }

    public void setImageReference(String imageReference) {
        this.imageReference = imageReference;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getAnswers() {
        return answers;
    }

    public void setAnswers(int answers) {
        this.answers = answers;
    }
}
