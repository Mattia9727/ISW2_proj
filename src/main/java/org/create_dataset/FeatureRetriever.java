package org.create_dataset;

import org.create_dataset.models.HashDifference;
import org.create_dataset.models.Version;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.Repository;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FeatureRetriever {

    String pathname;

    public FeatureRetriever(String pathname) {
        this.pathname = pathname;
    }

    private Map<Integer, Integer> calculateLines(Edit edit){
        Map<Integer, Integer> currLineMap = new HashMap<>();

        int locVariation = 0;
        int locVariationAdd = 0;
        int locVariationRemove = 0;
        int addLines=0;
        int removedLines=0;

        locVariationAdd = edit.getEndB() - edit.getBeginB();
        addLines += locVariationAdd;

        locVariationRemove = edit.getEndA() - edit.getBeginA();
        removedLines += locVariationRemove;

        locVariation = locVariationAdd + locVariationRemove;

        currLineMap.put(1,locVariation);
        currLineMap.put(2,addLines);
        currLineMap.put(3,removedLines);
        return currLineMap;
    }

    public void retrieveLines(HashDifference hd, EditList edList) throws IOException {

        for (Edit edit : edList) {  // TODO: Da rivedere se necessario
            Map<Integer, Integer> currLineMap = calculateLines(edit);
            hd.setLocTouched(currLineMap.get(1));
            hd.setAddedLines(currLineMap.get(2));
            hd.setRemovedLines(currLineMap.get(3));
            hd.setLines(hd.getLines() + hd.getAddedLines() - hd.getRemovedLines());
        }
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

}
