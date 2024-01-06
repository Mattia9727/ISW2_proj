package org.create_dataset.models;

import java.time.LocalDate;

public class Commit {
    private String index;
    private String hash;
    private LocalDate date;
    private boolean isBug;


    public Commit(String index, String hash, LocalDate date) {
        this.index = index;
        this.hash = hash;
        this.date = date;
        this.isBug = false;
    }

    public Commit(String index, String hash, LocalDate date, boolean isBug) {
        this.index = index;
        this.hash = hash;
        this.date = date;
        this.isBug = isBug;
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

    public boolean isBug() {
        return isBug;
    }

    public void setBug(boolean bug) {
        isBug = bug;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "index='" + index + '\'' +
                ", hash='" + hash + '\'' +
                ", date=" + date +
                ", isBug=" + isBug +
                '}';
    }
}
