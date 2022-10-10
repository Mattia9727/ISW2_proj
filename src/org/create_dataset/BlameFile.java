package create_dataset;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

// Simple example that shows how to get the Blame-information for a file
public class BlameFile {
    public static void main(String[] args) throws IOException, GitAPIException {
            Git git = null;
            Repository repo = null;
            File path = new File("C:\\Users\\matti\\IdeaProjects\\openjpa\\.git");
            git = Git.open(path);
            repo = git.getRepository();
            String[] list = new File("C:\\Users\\matti\\IdeaProjects\\openjpa").list();
            if (list == null) {
                throw new IllegalStateException("Did not find any files at " + new File("C:\\Users\\matti\\IdeaProjects\\openjpa\\").getAbsolutePath());
            }

            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd HH:mm");
            for (String file : list) {
                System.out.println(new File("C:\\Users\\matti\\IdeaProjects\\openjpa\\"+file));
                if (new File("C:\\Users\\matti\\IdeaProjects\\openjpa\\"+file).isDirectory()) {
                    continue;
                }

                System.out.println("Blaming " + file);
                BlameResult result = new Git(repo).blame().setFilePath(file)
                        .setTextComparator(RawTextComparator.WS_IGNORE_ALL).call();
                RawText rawText = result.getResultContents();
                for (int i = 0; i < rawText.size(); i++) {
                    PersonIdent sourceAuthor = result.getSourceAuthor(i);
                    RevCommit sourceCommit = result.getSourceCommit(i);
                    System.out.println(sourceAuthor.getName() +
                            (sourceCommit != null ? " - " + DATE_FORMAT.format(((long)sourceCommit.getCommitTime())*1000) +
                                    " - " + sourceCommit.getName() : "") +
                            ": " + rawText.getString(i));
                }
            }
    }
}