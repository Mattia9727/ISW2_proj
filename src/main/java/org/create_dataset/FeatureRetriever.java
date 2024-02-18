package org.create_dataset;

import org.create_dataset.models.HashDifference;
import org.create_dataset.models.VersionRelease;
import org.eclipse.jgit.diff.*;

import java.io.*;
import java.util.*;

public class FeatureRetriever {

    String pathname;

    public FeatureRetriever(String pathname) {
        this.pathname = pathname;
    }

    private Map<Integer, Integer> calculateLines(Edit edit){
        Map<Integer, Integer> currLineMap = new HashMap<>();

        int locVariation;
        int locVariationAdd;
        int locVariationRemove;
        int addLines=0;
        int removedLines=0;
        int churn;

        locVariationAdd = edit.getEndB() - edit.getBeginB();
        addLines += locVariationAdd;

        locVariationRemove = edit.getEndA() - edit.getBeginA();
        removedLines += locVariationRemove;

        locVariation = locVariationAdd + locVariationRemove;
        churn = locVariationAdd - locVariationRemove;


        currLineMap.put(1,locVariation);
        currLineMap.put(2,addLines);
        currLineMap.put(3,removedLines);
        currLineMap.put(4,churn);
        return currLineMap;
    }

    private void retrieveLines(HashDifference hd, EditList edList){
        int maxLocAdd=0;
        int avgLocAdd=0;
        int maxChurn=0;
        int avgChurn=0;
        List<Integer>locAddList = new ArrayList<>();
        List<Integer>churnList = new ArrayList<>();
        for (Edit edit : edList) {
            Map<Integer, Integer> currLineMap = calculateLines(edit);
            hd.setLocTouched(hd.getLocTouched() + currLineMap.get(1));
            hd.setAddedLines(hd.getAddedLines() + currLineMap.get(2));
            hd.setRemovedLines(hd.getRemovedLines() + currLineMap.get(3));
            hd.setChurn(hd.getChurn() + currLineMap.get(4));
            hd.setLines(hd.getLines() + currLineMap.get(2) - currLineMap.get(3));

            if (currLineMap.get(2)>maxLocAdd) maxLocAdd=currLineMap.get(2);
            if (currLineMap.get(4)>maxChurn) maxChurn=currLineMap.get(4);

            locAddList.add(currLineMap.get(2));
            churnList.add(currLineMap.get(4));
        }
        for (int la : locAddList) {
            avgLocAdd += la;
        }
        for (int c : churnList) {
            avgChurn += c;
        }
        hd.setAvgLocAdded(avgLocAdd/locAddList.size());
        hd.setAvgChurn(avgChurn/churnList.size());
        hd.setMaxLocAdded(maxLocAdd);
        hd.setMaxChurn(maxChurn);
        hd.setNumberOfRevisions(edList.size());
    }


    private int retrieveNAuthors(String afterHash, String filename, String pathname) throws IOException {
        Runtime rt = Runtime.getRuntime();
        String command = "git shortlog -sn 4b825dc642cb6eb9a060e54bf8d69288fbee49 " + afterHash + " -- " + filename;
        File dir = new File(pathname);
        Process pr = rt.exec(command, null, dir);
        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line = input.readLine();
        int nAuthors = 0;
        while (line != null) {
            line = input.readLine();
            nAuthors += 1;
        }

        return nAuthors;
    }

    public void retrieveAllFeatures(List<VersionRelease> versionReleases, String pathname) throws IOException {
        List<VersionRelease> myVersionReleases = new ArrayList<>(versionReleases.subList(1, versionReleases.size() - 1));
        int nV=1;
        HashDifference foundHD;
        for (VersionRelease v : myVersionReleases){
            for (int i=0; i<v.getHashDiffs().size(); i++){
                HashDifference hd = v.getHashDiffs().get(i);
                DiffEntry de = v.getDiffList().get(i);
                if (de.getChangeType()== DiffEntry.ChangeType.MODIFY){
                    foundHD = DatasetRetriever.findOldVersionHashDiff(versionReleases, de.getOldPath(), nV);
                    if (foundHD!=null) hd.setLines(foundHD.getLines());
                    else System.out.println("ERRORE");
                }
                retrieveLines(hd, v.getEditsList().get(i));
                hd.setnAuthors(retrieveNAuthors(hd.getActualHash(),hd.getNewClassName(), pathname));
            }
            nV++;
        }
    }

}
