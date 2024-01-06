package org.create_dataset.models;

public class HashDifference {

    private String oldClassName;
    private String newClassName;
    private String prevHash;
    private String actualHash;
    private int lines;
    private int addedLines;
    private int removedLines;
    private int numberOfRevisions;
    private int locTouched = 0;
    private int nAuthors = 0;
    private int maxLocAdded = 0;
    private int avgLocAdded = 0;

    public HashDifference(String oldClassName, String newClassName, String prevHash, String actualHash, int lines, int addedLines, int removedLines, int numberOfRevisions) {
        this.oldClassName = oldClassName;
        this.newClassName = newClassName;
        this.prevHash = prevHash;
        this.actualHash = actualHash;
        this.lines = lines;
        this.addedLines = addedLines;
        this.removedLines = removedLines;
        this.numberOfRevisions = numberOfRevisions;
    }

    public String getOldClassName() {
        return oldClassName;
    }

    public void setOldClassName(String oldClassName) {
        this.oldClassName = oldClassName;
    }

    public String getNewClassName() {
        return newClassName;
    }

    public void setNewClassName(String newClassName) {
        this.newClassName = newClassName;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }

    public String getActualHash() {
        return actualHash;
    }

    public void setActualHash(String actualHash) {
        this.actualHash = actualHash;
    }

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    public int getAddedLines() {
        return addedLines;
    }

    public void setAddedLines(int addedLines) {
        this.addedLines = addedLines;
    }

    public int getRemovedLines() {
        return removedLines;
    }

    public void setRemovedLines(int removedLines) {
        this.removedLines = removedLines;
    }

    public int getNumberOfRevisions() {
        return numberOfRevisions;
    }

    public void setNumberOfRevisions(int numberOfRevisions) {
        this.numberOfRevisions = numberOfRevisions;
    }

    public int getLocTouched() {
        return locTouched;
    }

    public void setLocTouched(int locTouched) {
        this.locTouched = locTouched;
    }

    public int getNAuthors() {
        return nAuthors;
    }

    public void setNAuthors(int nAuthors) {
        this.nAuthors = nAuthors;
    }

    public int getMaxLocAdded() {
        return maxLocAdded;
    }

    public void setMaxLocAdded(int maxLocAdded) {
        this.maxLocAdded = maxLocAdded;
    }

    public int getAvgLocAdded() {
        return avgLocAdded;
    }

    public void setAvgLocAdded(int avgLocAdded) {
        this.avgLocAdded = avgLocAdded;
    }
}
