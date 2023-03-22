package intellij_extension.views;

import intellij_extension.models.redesign.Commit;
import javafx.beans.property.SimpleStringProperty;

public class CommitInfoRow {
    private final SimpleStringProperty rowNumber = new SimpleStringProperty();
    private final SimpleStringProperty commitDescription = new SimpleStringProperty();
    private final SimpleStringProperty commitAuthor = new SimpleStringProperty();
    private final SimpleStringProperty commitDate = new SimpleStringProperty();
    private final SimpleStringProperty commitHash = new SimpleStringProperty();

    public CommitInfoRow(String rowNumber, String commitDescription, String commitAuthor, String commitDate, String commitHash) {
        this.rowNumber.set(rowNumber);
        this.commitDescription.set(commitDescription);
        this.commitAuthor.set(commitAuthor);
        this.commitDate.set(commitDate);
        this.commitHash.set(commitHash);
    }

    public SimpleStringProperty getRowNumber() {
        return rowNumber;
    }

    public SimpleStringProperty getCommitDescription() {
        return commitDescription;
    }

    public SimpleStringProperty getCommitAuthor() {
        return commitAuthor;
    }

    public SimpleStringProperty getCommitDate() {
        return commitDate;
    }

    public SimpleStringProperty getCommitHash() {
        return commitHash;
    }

    public void update(Commit commit) {
        commitDescription.setValue(commit.getShortMessage());
        commitAuthor.setValue(commit.getAuthor());
        commitDate.setValue(commit.getDate());
        commitHash.setValue(commit.getHash());
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %s", getRowNumber().getValue(), getCommitDescription().getValue(), getCommitAuthor().getValue(), getCommitDate().getValue(), getCommitHash().getValue());
    }
}