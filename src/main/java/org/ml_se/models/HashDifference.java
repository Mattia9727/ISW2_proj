package org.ml_se.models;

public class HashDifference {

    private final String oldClassName;
    private final String newClassName;
    private final String actualHash;
    private int lines;
    private int addedLines;
    private int removedLines;
    private int numberOfRevisions;
    private int churn = 0;
    private int locTouched = 0;
    private int nAuthors = 0;
    private int maxLocAdded = 0;
    private int avgLocAdded = 0;
    private int maxChurn = 0;
    private int avgChurn = 0;
    private boolean isBuggy;
    private int ov;
    private int fv;



    public HashDifference(String oldClassName, String newClassName, String actualHash) {
        this.oldClassName = oldClassName;
        this.newClassName = newClassName;
        this.actualHash = actualHash;
    }


    public String getOldClassName() {
        return oldClassName;
    }

    public String getNewClassName() {
        return newClassName;
    }

    public String getActualHash() {
        return actualHash;
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

    public int getChurn() {
        return churn;
    }

    public void setChurn(int churn) {
        this.churn = churn;
    }

    public int getLocTouched() {
        return locTouched;
    }

    public void setLocTouched(int locTouched) {
        this.locTouched = locTouched;
    }

    public int getnAuthors() {
        return nAuthors;
    }

    public void setnAuthors(int nAuthors) {
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

    public int getMaxChurn() {
        return maxChurn;
    }

    public void setMaxChurn(int maxChurn) {
        this.maxChurn = maxChurn;
    }

    public int getAvgChurn() {
        return avgChurn;
    }

    public void setAvgChurn(int avgChurn) {
        this.avgChurn = avgChurn;
    }

    public boolean isBuggy() {
        return isBuggy;
    }

    public void setBuggy(boolean buggy) {
        isBuggy = buggy;
    }

    public int getOv() {
        return ov;
    }

    public void setOv(int ov) {
        this.ov = ov;
    }

    public int getFv() {
        return fv;
    }

    public void setFv(int fv) {
        this.fv = fv;
    }
}
