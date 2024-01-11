package org.create_dataset;

import org.create_dataset.models.Bug;
import org.create_dataset.models.Version;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Locale.ITALIAN;
import static org.create_dataset.Utils.readJsonFromUrl;

public class JiraManager {

    private JiraManager() {
    }

    public static List<Version> getReleasesFromJira(String projName) throws IOException, JSONException {
        List<Version> versions = new ArrayList<>();
        Version v = new Version(LocalDate.ofEpochDay(0),"init");
        versions.add(v);

        String versionsUrl = "https://issues.apache.org/jira/rest/api/2/project/" + projName;
        JSONObject jsons = readJsonFromUrl(versionsUrl);
        JSONArray values = jsons.getJSONArray("versions");

        for (int i = 0; i < values.length(); i++) {
            if (!values.getJSONObject(i).has("releaseDate")) {
                continue;
            }
            String name = values.getJSONObject(i).getString("name");
            String releaseDate = values.getJSONObject(i).getString("releaseDate");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            formatter = formatter.withLocale(ITALIAN);  // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
            LocalDate date = LocalDate.parse(releaseDate, formatter);

            v = new Version(date, name);
            versions.add(v);
        }

        versions.sort(Comparator.comparing(Version::getDate));

        return versions;
    }


    public static List<Bug> getIssuesFromJira(String projName) throws IOException, JSONException {
        List<Bug> bugs = new ArrayList<>();
        int j;
        int i = 0;
        int total;
        do {
            //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
            j = i + 1000;
            String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22" + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR" + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt=" + i + "&maxResults=" + j;
            JSONObject json = readJsonFromUrl(url);
            JSONArray issues = json.getJSONArray("issues");
            total = json.getInt("total");
            for (; i < total && i < j; i++) {

                JSONObject bugInfo = issues.getJSONObject(i % 1000).getJSONObject("fields");
                String issueName = issues.getJSONObject(i % 1000).getString("key");
                List<String> versions = new ArrayList<>();
                JSONArray versionsArray = bugInfo.getJSONArray("versions");
                for (int k = 0; k < versionsArray.length(); k++) {
                    // Ottenere l'oggetto JSONObject corrente
                    JSONObject v = versionsArray.getJSONObject(k);
                    versions.add(v.getString("name"));
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                // Conversione della stringa in LocalDate
                LocalDate created = LocalDate.parse(bugInfo.getString("created").substring(0, 10), formatter);
                LocalDate resolutiondate = LocalDate.parse(bugInfo.getString("resolutiondate").substring(0, 10), formatter);


                Bug bug = new Bug(issueName,versions,created,resolutiondate);
                bugs.add(bug);
            }
        } while (i < total);

        bugs.sort(Comparator.comparing(Bug::getFv));

        return bugs;
    }
}
