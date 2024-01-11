package org.create_dataset.models;

import java.time.LocalDate;
import java.util.List;

public class Bug {
    String issueName;
    List<String> versions;
    LocalDate iv = null;
    LocalDate ov;
    LocalDate fv;
    int ivVersion;
    int ovVersion;
    int fvVersion;


    public Bug(String issueName, List<String> versions, LocalDate ov, LocalDate fv) {
        this.issueName = issueName;
        this.versions = versions;
        this.ov = ov;
        this.fv = fv;
    }

    public String getIssueName() {
        return issueName;
    }

    public List<String> getVersions() {
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    public LocalDate getIv() {
        return iv;
    }

    public void setIv(LocalDate iv) {
        this.iv = iv;
    }

    public LocalDate getOv() {
        return ov;
    }

    public LocalDate getFv() {
        return fv;
    }

    public int getIvVersion() {
        return ivVersion;
    }

    public void setIvVersion(int ivVersion) {
        this.ivVersion = ivVersion;
    }

    public int getOvVersion() {
        return ovVersion;
    }

    public void setOvVersion(int ovVersion) {
        this.ovVersion = ovVersion;
    }

    public int getFvVersion() {
        return fvVersion;
    }

    public void setFvVersion(int fvVersion) {
        this.fvVersion = fvVersion;
    }

    private void retrieveOvVersion(List<Version> versionList){
        for (int i=0; i<versionList.size(); i++){
            if (this.getOv().isBefore(versionList.get(i).getDate())){
                this.setOvVersion(i);
                break;
            }
        }
    }

    private void retrieveFvVersion(List<Version> versionList){
        for (int i=0; i<versionList.size(); i++){
            if (this.getFv().isBefore(versionList.get(i).getDate())){
                this.setFvVersion(i);
                break;
            }
        }
    }

    public void retrieveIvByVersions(List<Version> versionList){
        for (int i=1; i<versionList.size()-1; i++){
            if (this.getVersions().contains(versionList.get(i).getName())){
                this.setIvVersion(i);
                break;
            }
        }
    }

    public void retrieveOvFvVersions(List<Version> versionList){
        retrieveFvVersion(versionList);
        retrieveOvVersion(versionList);
        retrieveIvByVersions(versionList);
    }

    public void retrieveVersionsByIvFv(List<Version> versionList){
        for(int i=ivVersion; i<fvVersion; i++){
            versions.add(versionList.get(i).getName());
        }
    }


}
