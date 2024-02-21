package org.ml_se;

import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


public class Utils {



    private Utils() {
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream(); BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        //noinspection Duplicates
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }

    public static float calculateMedian(List<Float> floatList) {
        // Ensure the list is not empty
        if (floatList.isEmpty()) {
            throw new IllegalArgumentException("The list is empty. Unable to calculate the median.");
        }

        // Convert the List<Float> to an array of floats
        float[] floatArray = new float[floatList.size()];
        for (int i = 0; i < floatList.size(); i++) {
            floatArray[i] = floatList.get(i);
        }

        // Sort the array
        Arrays.sort(floatArray);

        // Get the length of the array
        int size = floatArray.length;

        // Calculate the median
        if (size % 2 == 1) {
            // Odd length, return the middle element
            return floatArray[size / 2];
        } else {
            // Even length, calculate the average of the middle elements
            float middle1 = floatArray[(size - 1) / 2];
            float middle2 = floatArray[size / 2];
            return (float) ((middle1 + middle2) / 2.0);
        }
    }

    public static float calculateAvg(List<Float> floatList) {
        float avg = 0;
        for (float f : floatList) {
            avg += f;
        }
        return avg/floatList.size();
    }

}
