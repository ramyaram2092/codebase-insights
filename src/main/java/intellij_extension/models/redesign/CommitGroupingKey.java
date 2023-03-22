package intellij_extension.models.redesign;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.TreeSet;

import static intellij_extension.Constants.SEPARATOR;

public class CommitGroupingKey implements Comparable<CommitGroupingKey>
{
    private final int maxCommonCommits;
    private final TreeSet<FileObject> fileObjects;

    public CommitGroupingKey(TreeSet<FileObject> fileObjects, int maxCommonCommits)
    {
        this.maxCommonCommits = maxCommonCommits;
        this.fileObjects = fileObjects;
    }

    @Override
    public String toString() {
        return maxCommonCommits + SEPARATOR + fileObjects.hashCode();
    }

    @Override
    public int compareTo(@NotNull CommitGroupingKey other) {
        return Integer.compare(this.maxCommonCommits, other.maxCommonCommits);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommitGroupingKey that = (CommitGroupingKey) o;
        return maxCommonCommits == that.maxCommonCommits && Objects.equals(fileObjects, that.fileObjects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxCommonCommits, fileObjects);
    }
}
