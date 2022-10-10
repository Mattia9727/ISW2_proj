package create_dataset;

public class HashDifference {

    private String className;
    private String prevHash;
    private String actualHash;
    private int lines;
    private int addedLines;
    private int removedLines;

    private int numberOfRevisions;


    public HashDifference(String className, String prevHash, String actualHash, int lines, int addedLines, int removedLines, int numberOfRevisions) {
        this.className = className;
        this.prevHash = prevHash;
        this.actualHash = actualHash;
        this.lines = lines;
        this.addedLines = addedLines;
        this.removedLines = removedLines;
        this.numberOfRevisions = numberOfRevisions;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public String getActualHash() {
        return actualHash;
    }

    public int getLines() {
        return lines;
    }

    public int getAddedLines() {
        return addedLines;
    }

    public int getRemovedLines() {
        return removedLines;
    }

    public String getClassName() {
        return className;
    }

    public void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }

    public void setActualHash(String actualHash) {
        this.actualHash = actualHash;
    }

    public int getNumberOfRevisions() {
        return numberOfRevisions;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    public void setAddedLines(int addedLines) {
        this.addedLines = addedLines;
    }

    public void setRemovedLines(int removedLines) {
        this.removedLines = removedLines;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setNumberOfRevisions(int numberOfRevisions) {
        this.numberOfRevisions = numberOfRevisions;
    }
}
