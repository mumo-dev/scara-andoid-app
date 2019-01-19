package com.example.mumo.scara.model;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class Question {
    private String questionId;
    private String text;
    private String username;
    private String category;
    private int votes;
    private String imageReference;
    private Date createdAt;

    public Question() {}

    public Question(String text, String username, String category, int votes, String url, Date createdAt) {
        this.text = text;
        this.username = username;
        this.category = category;
        this.votes = votes;
        this.imageReference = url;
        this.createdAt = createdAt;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
