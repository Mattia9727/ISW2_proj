package org.create_dataset.models;

import java.time.LocalDate;

public class Commit {
    private String index;

    private String hash;
    private LocalDate date;


    public Commit(String index, String hash, LocalDate date) {
        this.index = index;
        this.hash = hash;
        this.date = date;

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

    @Override
    public String toString() {
        return "Commit{" +
                "index='" + index + '\'' +
                ", hash='" + hash + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
