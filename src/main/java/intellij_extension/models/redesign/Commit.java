package intellij_extension.models.redesign;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Commit
 * - We will extract information from RevCommits (from JGit)
 * - List<DiffEntry>
 * - Author, message, date, and hash
 * - whatever else we need
 * <p>
 * <p>
 * No setters b/c once the commit is built... that's it. Nothing should change after that.
 * TODO - Where do DiffEntries come from?
 **/
public class Commit {

    private ArrayList<DiffEntry> commitDiffs;
    private String author;
    private String authorEmail;
    private String fullMessage;
    private String shortMessage;
    private String date;
    private String hash;

    private Set<String> fileSet;

    public Commit(RevCommit revCommit) {
        PersonIdent authorIdent = revCommit.getAuthorIdent();

        this.author = revCommit.getAuthorIdent().getName();
        this.authorEmail = revCommit.getAuthorIdent().getEmailAddress();
        this.fullMessage = revCommit.getFullMessage();
        this.shortMessage = revCommit.getShortMessage();
        this.date = authorIdent.getWhen().toString();
        this.hash = revCommit.getName();

        this.fileSet = new HashSet<>();
        this.commitDiffs = new ArrayList<>();
    }

    public ArrayList<DiffEntry> getCommitDiffs() {
        return commitDiffs;
    }

    public void addDiffEntriesToDiffList(List<DiffEntry> diffs) {
        commitDiffs.addAll(diffs);
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getFullMessage() {
        return fullMessage;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public String getDate() {
        return date;
    }

    public String getHash() {
        return hash;
    }

    public Set<String> getFileSet() {
        return fileSet;
    }

    public void addFileToSet(String file) {
        fileSet.add(file);
    }
}
