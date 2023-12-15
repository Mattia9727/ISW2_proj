package org.create_dataset;

import org.create_dataset.models.Commit;
import org.create_dataset.models.Version;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Locale.ITALIAN;
import static org.create_dataset.Utils.readJsonArrayFromUrl;
import static org.create_dataset.Utils.readJsonFromUrl;

public class DatasetRetriever {

    // Init stringhe usate
    String projName;
    String projID;
    String pathname;
    List<RevCommit> commitList = new ArrayList<>();

    public DatasetRetriever(String projName, String projID, String pathname) {
        this.projName = projName;
        this.projID = projID;
        this.pathname = pathname;
    }

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public String getProjID() {
        return projID;
    }

    public void setProjID(String projID) {
        this.projID = projID;
    }

    public String getPathname() {
        return pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    public List<Version> getReleasesFromJira() throws IOException, JSONException {
        List<Version> versions = new ArrayList<>();
        String versionsUrl = "https://issues.apache.org/jira/rest/api/2/project/"+this.projID+"/version";
        JSONObject jsons = readJsonFromUrl(versionsUrl);
        JSONArray values = jsons.getJSONArray("values");

        for (int i=0; i< values.length(); i++) {
            if(!values.getJSONObject(i).has("releaseDate")){
                continue;
            }
            String name = values.getJSONObject(i).getString("name");
            String releaseDate = values.getJSONObject(i).getString("releaseDate");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            formatter = formatter.withLocale(ITALIAN);  // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
            LocalDate date = LocalDate.parse(releaseDate, formatter);

            Version v = new Version(date,name);
            versions.add(v);
        }

        versions.sort(Comparator.comparing(Version::getDate));

        return versions;
    }

    public static List<Version> getReleasesFromGit(String projName) throws IOException, JSONException {
        List<Version> versions = new ArrayList<>();
        String versionsUrlGit = "https://api.github.com/repos/apache/"+projName+"/tags";
        JSONArray versionArray = readJsonArrayFromUrl(versionsUrlGit);

//        for (i=0; i< versionArray.length(); i++) {
//            Map<String, String> map = new HashMap<String, String>();
//            map.put("name", versionArray.getJSONObject(i).getString("name"));
//            map.put("hash", versionArray.getJSONObject(i).getJSONObject("commit").getString("sha"));
//            versions.add(map);
//            //FileUtils.writeStringToFile(file, jsons.getJSONObject(i).getString("name") + ";\n", true);
//            //System.out.println(i);
//            System.out.println("name: "+ map.get("name")+", sha: "+map.get("hash"));
//            Version v = new Version(sha,name);
//
//        }
        return versions;
    }


    // Prendo tutti i commit in Git e metto hash e date nella lista gitCommits
    public List<Commit> getCommits() throws IOException, GitAPIException {

        OutputStream outS = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(outS);
        File path = new File(this.pathname + "\\.git");
        try(Git git = Git.open(path)) {
            Repository repository = git.getRepository();
            ObjectId head = repository.resolve("HEAD");

            df.setRepository(repository);
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);

            List<Commit> gitCommits = new ArrayList<>();
            Iterable<RevCommit> commits = git.log().add(head).setMaxCount(10000).call();

            int i = 1;

            for (RevCommit c : commits) {

                PersonIdent authorIdent = c.getAuthorIdent();
                Date commitDate = authorIdent.getWhen();
                LocalDate cDate = commitDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                Commit commit = new Commit(String.valueOf(i), c.getName(), cDate);

                gitCommits.add(commit);
                i += 1;
                this.commitList.add(c);
            }
            return gitCommits;
        }
    }


    public void getEveryReleaseFromGit() throws GitAPIException, IOException{
        File path = new File(this.pathname);
        Git git = Git.init().setDirectory(path).call();
        List<Ref> tags = git.tagList().call();

        PersonIdent author;
        Date cmDate;
        ZoneId zi = ZoneId.systemDefault();
        LogCommand log;

        HashMap<String,LocalDate> releases = new HashMap<>();
        for (Ref ref: tags) {

            if(ref.getName().contains("docker")) {
                continue;
            }

            log = git.log();

            Ref peeledRef = git.getRepository().getRefDatabase().peel(ref);
            if(peeledRef.getPeeledObjectId() != null) {
                log.add(peeledRef.getPeeledObjectId());
            } else {
                log.add(ref.getObjectId());
            }

            Iterable<RevCommit> logs = log.call();

            RevCommit cm = logs.iterator().next();
            author = cm.getAuthorIdent();
            cmDate = author.getWhen();

            releases.put(ref.getName(), cmDate.toInstant().atZone(zi).toLocalDate());
        }

    }

}
