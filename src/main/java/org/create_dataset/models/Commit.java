package org.create_dataset.models;

import java.time.LocalDate;

public class Commit {
    private String index;
    private String hash;
    private LocalDate date;
    private String comment;
    private Bug bug;


    public Commit(String index, String hash, LocalDate date, String comment) {
        this.index = index;
        this.hash = hash;
        this.date = date;
        this.comment = comment;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Bug getBug() {
        return bug;
    }

    public void setBug(Bug bug) {
        this.bug = bug;
    }
}
