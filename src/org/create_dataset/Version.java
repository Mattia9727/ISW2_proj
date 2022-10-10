package create_dataset;

public class Version {

    private String hash;
    private String date;
    private String version;

    public Version(String hash, String date) {
        this.hash = hash;
        this.date = date;
        this.version = version;
    }

    public String getHash() {
        return hash;
    }

    public String getDate() {
        return date;
    }

    public String getVersion() {
        return version;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setVersion(String date) {
        this.version = version;
    }

}
