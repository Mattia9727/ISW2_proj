package org.create_dataset.models;

import org.eclipse.jgit.diff.DiffEntry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Version {

    private LocalDate date;
    private String name;
    private List<Commit> commitList;
    private List<DiffEntry> diffList;
    private List<HashDifference> hashDiffs;

    public Version(LocalDate date, String name) {
        this.date = date;
        this.name = name;
        this.commitList = new ArrayList<>();
    }

    public Version(String name) {
        this.name = name;
        this.commitList = new ArrayList<>();
    }


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Commit> getCommitList() {
        return commitList;
    }

    public void setCommitList(List<Commit> commitList) {
        this.commitList = commitList;
    }

    public List<DiffEntry> getDiffList() {
        return diffList;
    }

    public void setDiffList(List<DiffEntry> diffList) {
        this.diffList = diffList;
    }

    public List<HashDifference> getHashDiffs() {
        return hashDiffs;
    }

    public void setHashDiffs(List<HashDifference> hashDiff) {
        this.hashDiffs = hashDiff;
    }

    public String getHash() { return commitList.get(0).getHash();}

    public HashDifference getHashDiffByName(String name) {
        int i;
        for (i=0; i<hashDiffs.size(); i++){
            if (hashDiffs.get(i).getNewClassName().equals(name)){
                break;
            }
        }
        return hashDiffs.get(i);
    }

    @Override
    public String toString() {
        return "Version{" +
                "date=" + date +
                ", name='" + name + '\'' +
                ", commitList=" + commitList +
                '}';
    }
}
