package org.create_dataset.models;

import java.time.LocalDate;

public class Version {

    private String hash;
    private LocalDate date;
    private String name;

    public Version(String hash, LocalDate date, String name) {
        this.hash = hash;
        this.date = date;
        this.name = name;
    }

    public Version(LocalDate date, String name) {
        this.hash = "";
        this.date = date;
        this.name = name;
    }

    public String getHash() {
        return hash;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getVersion() {
        return name;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setVersion(String date) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Version{" +
                "hash='" + hash + '\'' +
                ", date='" + date + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
