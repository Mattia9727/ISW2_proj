package org.create_dataset;

import org.apache.commons.io.FileUtils;
import org.create_dataset.models.Version;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class Utils {

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JSONArray json = new JSONArray(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
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

    public static void createCSV(JSONArray jsonarray) throws JSONException {
        try {
            File file = new File("output.csv");
            String csv = CDL.toString(jsonarray);
            FileUtils.writeStringToFile(file, csv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<Map<String, String>> getNFix(String projID) throws IOException, JSONException {
        List<Map<String, String>> versions = new ArrayList<>();
        String versionsUrl = "https://issues.apache.org/jira/rest/api/2/project/" + projID + "/version";
        int i;

        JSONObject jsons = readJsonFromUrl(versionsUrl);

        for (i = 0; i < jsons.getJSONArray("values").length(); i++) {
            if (!jsons.getJSONArray("values").getJSONObject(i).has("releaseDate")) {
                continue;
            }
            System.out.println(jsons.getJSONArray("values").getJSONObject(i).getString("name"));
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", jsons.getJSONArray("values").getJSONObject(i).getString("name"));
            map.put("releaseDate", jsons.getJSONArray("values").getJSONObject(i).getString("releaseDate"));
            versions.add(map);
            //FileUtils.writeStringToFile(file, jsons.getJSONObject(i).getString("name") + ";\n", true);
            //System.out.println(i);
        }
        return versions;
    }


    public static void getIssues(String projName) throws IOException, JSONException {
        Logger logger = Logger.getLogger(Utils.class.getName());
        int j = 0, i = 0, total = 1;
        do {
            //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
            j = i + 1000;
            String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22" + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR" + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt=" + i + "&maxResults=" + j;
            JSONObject json = readJsonFromUrl(url);
            JSONArray issues = json.getJSONArray("issues");
            total = json.getInt("total");
            for (; i < total && i < j; i++) {
                //Iterate through each bug
                String key = issues.getJSONObject(i % 1000).get("key").toString();
                System.out.println(key);
                //FileUtils.writeStringToFile(file, key+";", "US-ASCII",true);
            }
        } while (i < total);
    }
}
