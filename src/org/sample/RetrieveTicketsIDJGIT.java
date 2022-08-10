package org.sample;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RetrieveTicketsIDJGIT {

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

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
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
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


//    public static void getFilenamesFromCommitDate(List<Map<String, String>> versions, List<Map<String, String>> gitVersions, File file) {
//        try {
//            List<HashDifference> diffList = new ArrayList<>();
//            File dir = new File("C:\\Users\\matti\\IdeaProjects\\openjpa");
//            Runtime rt = Runtime.getRuntime();
//            Process pr = null;
//            pr = rt.exec("git log --date=format:\"%Y-%m-%d\" --pretty=format:\"%h - %ad\"", null, dir);
//            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
//            //Process pr = rt.exec("cmd /c dir");
//
//            //Process pr = rt.exec("git ls-tree -r --name-only "+version.get("hash"), null, dir);
//            //Process pr = rt.exec("git log --date=iso-local --pretty=format:\"%h - %ad\"");
//            String line = null;
//            String lastData = "";
//            String hash = "";
//            List<Version> hashDates = new ArrayList<>();
//            while ((line = input.readLine()) != null) {
//                String[] data = line.split(" - ");
//                Version hd = new Version(data[0],data[1]);
//                hashDates.add(hd);
//                hash = data[0];
//                String releaseDate = data[1];
//                if (releaseDate.equals(lastData)) {
//                    continue;
//                }
//
//                for (Map<String, String> version : versions) {
//                    String x = version.get("releaseDate");
//                    if (x.equals(releaseDate)){
//                        version.put("hash", hash);
//                        lastData = releaseDate;
//                        break;
//                    }
//                }
//            }
//            for (Map<String, String> version : versions){
//                String appDate = version.get("releaseDate");
//                for (Map<String, String> gitV : gitVersions){
//                    if (gitV.get("name").equals(version.get("name"))){
//                        version.put("hash",gitV.get("hash"));
//                    }
//                }
//                while (version.get("hash") == null){
//                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(appDate);
//                    Calendar cal = Calendar.getInstance();
//                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//                    cal.setTime(date);
//                    cal.add(Calendar.DAY_OF_MONTH, -1);
//                    appDate = df.format(cal.getTime());
//                    for (Version hd:hashDates){
//                        if (hd.getDate().equals(appDate)){
//                            version.put("hash",hd.getHash());
//                        }
//                    }
//
//                }
//            }
//            String prevHash = "4b825dc642cb6eb9a060e54bf8d69288fbee49";
//            for (Map<String, String> version : versions) {
//                System.out.println("Version "+version.get("name")+" - Date: "+version.get("releaseDate")+" -> "+prevHash + " - " + version.get("hash"));
//                Process pr2 = rt.exec("git ls-tree -r --name-only " + version.get("hash"), null, dir);
//                Process pr3 = rt.exec("git diff --numstat 4b825dc642cb6eb9a060e54bf8d69288fbee49 " + version.get("hash"), null, dir);  //QUI CHIAMATA LOC
//                BufferedReader input2 = new BufferedReader(new InputStreamReader(pr3.getInputStream()));
//                String line2 = null;
//                while ((line2 = input2.readLine()) != null) {
//                    String[] app = line2.split("/");
//                    if (!app[app.length - 1].endsWith(".java") ||
//                            app[app.length - 1].endsWith("Test.java") ||
//                            app[app.length - 1].endsWith("Tests.java") ||
//                            app[app.length - 1].endsWith("TestCase.java") ||
//                            app[app.length - 1].startsWith("Test")) {
//                        continue;
//                    }
//                    String[] a = line2.split("\t");
//                    diffList.add(new HashDifference(a[2], prevHash, version.get("hash"), Integer.parseInt(a[0]), Integer.parseInt(a[1])));
//                    FileUtils.writeStringToFile(file, version.get("name") + ";" + version.get("releaseDate") + ";" + a[2] + ";" + a[0] + ";" + a[1] + ";" + hash + "\n", true);
//                }
//                prevHash = version.get("hash");
//            }
//        }catch (IOException e){
//            throw new RuntimeException(e);
//        //} catch (ParseException e) {
//        //    throw new RuntimeException(e);
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//    }



//    public static void getVersionsFromGitTags(String projName){
//        Runtime rt = Runtime.getRuntime();
//        Process pr = null;
//        try {
//            //pr = rt.exec("cd C:\\Users\\matti\\IdeaProjects\\openjpa");
//            pr = rt.exec("git tag -l --sort=-version:refname 1* 2*0 2*1 2*2 2*3 2*4 3* 0*");
//            System.out.println(pr.getInputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public static List<Map<String, String>> getVersions(String projID) throws IOException, JSONException {
        List<Map<String, String>> versions = new ArrayList<>();
        String versionsUrl = "https://issues.apache.org/jira/rest/api/2/project/"+projID+"/version";
        int i;

        JSONObject jsons = readJsonFromUrl(versionsUrl);

        for (i=0; i< jsons.getJSONArray("values").length(); i++) {
            if(!jsons.getJSONArray("values").getJSONObject(i).has("releaseDate")){
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

    public static List<Map<String, String>> getVersionsFromGit(String projName) throws IOException, JSONException {
        List<Map<String, String>> versions = new ArrayList<>();
        String versionsUrlGit = "https://api.github.com/repos/apache/"+projName+"/tags";
        int i;

        JSONArray versionArray = readJsonArrayFromUrl(versionsUrlGit);

        for (i=0; i< versionArray.length(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", versionArray.getJSONObject(i).getString("name"));
            map.put("hash", versionArray.getJSONObject(i).getJSONObject("commit").getString("sha"));
            versions.add(map);
            //FileUtils.writeStringToFile(file, jsons.getJSONObject(i).getString("name") + ";\n", true);
            //System.out.println(i);
            System.out.println("name: "+ map.get("name")+", sha: "+map.get("hash"));
        }
        return versions;
    }

    public static void getIssues(String projName) throws IOException, JSONException {
        int j = 0, i = 0, total = 1;
        do {
            //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
            j = i + 1000;
            String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=" + projName;
                    //+ "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                    //+ "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
                    //+ i + "&maxResults=" + j;
            JSONObject json = readJsonFromUrl(url);
            String string = json.toString(2);
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

    public static void main(String[] args) throws IOException, JSONException {

        String projName = "openjpa";
        String projID = "12310351";
        File path = new File("C:\\Users\\matti\\IdeaProjects\\openjpa\\.git");
        File file = new File("output4.csv");
        FileUtils.writeStringToFile(file, "version;filename;loc;loc_added;loc_deleted;type\n", false);
        List<Map<String, String>> versions = getVersions(projID);
        //List<Map<String, String>> gitVersions = getVersionsFromGit(projName);

        Git git = null;
        Repository repository = null;
        List<Map<String, String>> gitCommits;
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Iterable<RevCommit> commits = null;
        Iterable<RevCommit> log = null;
        OutputStream outS = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(outS);

        List<RevCommit> commitList = new ArrayList<>();

        // Prendo tutti i commit in Git e metto hash e date nella lista gitCommits
        try {
            git = Git.open(path);
            repository = git.getRepository();
            ObjectId head = repository.resolve("HEAD");


            df.setRepository(repository);
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);
            List<Version> hashDates = new ArrayList<>();
            //List<String> hashes = new ArrayList<>();
            gitCommits = new ArrayList<>();
            log = git.log().call();
            commits = git.log().add(head).setMaxCount(10000).call();

            int i = 1;


            for (RevCommit c : commits) {
                Map<String, String> map = new HashMap<>();
                PersonIdent authorIdent = c.getAuthorIdent();
                Date commitDate = authorIdent.getWhen();
                Calendar cal = Calendar.getInstance();
                cal.setTime(commitDate);
                String appDate = sdf.format(cal.getTime());
                map.put("hash", c.getName());
                map.put("releaseDate", appDate);
                map.put("index", String.valueOf(i));
                gitCommits.add(map);
                i+=1;
                commitList.add(c);
            }

        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }

        int found = 0;
        Comparator<Map<String, String>> sortByDate = Comparator.comparing(x -> x.get("releaseDate"));
        versions.sort(sortByDate);
        gitCommits.sort(sortByDate);
        List<Map<String,String>> finalMap = new ArrayList<>();
        Map<String, String> initmap = new HashMap<>();
        initmap.put("hash", gitCommits.get(0).get("hash"));
        initmap.put("version", "0");
        initmap.put("releaseDate", "0");
        initmap.put("index", "0");
        finalMap.add(initmap);

        for (Map<String, String> jiraMap : versions) {
            while (found != 1) {
                if (jiraMap.get("releaseDate").compareTo(gitCommits.get(0).get("releaseDate"))<=0){
                    found = 1;
                    Map<String, String> newmap = new HashMap<>();
                    newmap.put("hash", gitCommits.get(0).get("hash"));
                    newmap.put("version", jiraMap.get("name"));
                    newmap.put("releaseDate", gitCommits.get(0).get("releaseDate"));
                    newmap.put("index", gitCommits.get(0).get("index"));
                    finalMap.add(newmap);
                }
                gitCommits.remove(0);
            }
            found = 0;
        }
        try {

            List<DiffEntry> diff = null;
            List<HashDifference> hashDiffs = new ArrayList<>();


            for (int i=1; i<finalMap.size(); i++){

                diff = git.diff().setShowNameAndStatusOnly(true).setOldTree(prepareTreeParser(repository, finalMap.get(i-1).get("hash"))).setNewTree(prepareTreeParser(repository, finalMap.get(i).get("hash"))).call();
                for (DiffEntry d : diff) {

                    String filename = d.getNewPath();
                    String[] app = filename.split("/");
                    if (!app[app.length - 1].endsWith(".java") ||
                            app[app.length - 1].endsWith("Test.java") ||
                            app[app.length - 1].endsWith("Tests.java") ||
                            app[app.length - 1].endsWith("TestCase.java") ||
                            app[app.length - 1].startsWith("Test")) {
                        continue;
                    }
                    FileHeader fh = df.toFileHeader(d);
                    int j=0;
                    int hFound=0;

                    for (HashDifference h : hashDiffs){
                        if (h.getClassName().compareTo(filename) == 0 && h.getActualHash().compareTo(finalMap.get(i).get("hash")) == 0){
                            hFound = 1;
                            break;
                        }
                        j+=1;
                    }
                    if (hFound==0){
                        HashDifference newH = new HashDifference(filename,finalMap.get(i-1).get("hash"),finalMap.get(i).get("hash"),0,0,0);
                        hashDiffs.add(newH);
                    }
                    int loc_variation = 0;
                    for (Edit edit: fh.toEditList()){
                        if (edit.getType() == Edit.Type.INSERT) {
                            loc_variation = edit.getEndB()- edit.getBeginB();
                            hashDiffs.get(j).setAddedLines(loc_variation);
                            hashDiffs.get(j).setLines(hashDiffs.get(j).getLines()+loc_variation);
                        }
                        else if (edit.getType() == Edit.Type.DELETE) {
                            loc_variation = edit.getEndA() - edit.getBeginA();
                            hashDiffs.get(j).setRemovedLines(loc_variation);
                            hashDiffs.get(j).setLines(hashDiffs.get(j).getLines()-loc_variation);
                        }
                        else if (edit.getType() == Edit.Type.REPLACE) {
                            loc_variation = edit.getEndB()- edit.getBeginB();
                            hashDiffs.get(j).setAddedLines(loc_variation);
                            hashDiffs.get(j).setLines(hashDiffs.get(j).getLines()+loc_variation);
                            loc_variation = edit.getEndA() - edit.getBeginA();
                            hashDiffs.get(j).setRemovedLines(loc_variation);
                            hashDiffs.get(j).setLines(hashDiffs.get(j).getLines()-loc_variation);
                        }
//                        if (edit.getType() == Edit.Type.EMPTY) {
//                            hashDiffs.get(j).setAddedLines(0);
//                            hashDiffs.get(j).setRemovedLines(0);
//                        }
                    }
                    for (HashDifference pHash : hashDiffs){
                        FileUtils.writeStringToFile(file, finalMap.get(i).get("version") + ";" + pHash.getClassName() + ";" + pHash.getLines() + ";" + pHash.getAddedLines() + ";" + pHash.getRemovedLines() + "\n", true);

                    }

//                    FileUtils.writeStringToFile(file, "version;date;filename;loc;loc_touched\n", false);
                }
            }
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
        }


        return;
    }


}
