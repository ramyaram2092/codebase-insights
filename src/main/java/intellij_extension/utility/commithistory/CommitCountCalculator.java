package intellij_extension.utility.commithistory;

import com.google.common.collect.FluentIterable;
import intellij_extension.Constants;
import javafx.util.Pair;
import org.apache.commons.collections.IteratorUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CommitCountCalculator {
    private static Repository repository;
    private static Git git;
    private static TreeWalk treeWalk;

    private static final FluentIterable<RevCommit> EMPTY_REV_COMMITS = new FluentIterable<>() {
        @Override
        public Iterator<RevCommit> iterator() {
            return IteratorUtils.emptyIterator();
        }
    };

    public CommitCountCalculator() throws IOException {
        //Open the repos
        this.repository = JGitHelper.openLocalRepository();
        this.git = new Git(repository);
        this.treeWalk = getTreeWalkFromRepository();
    }

    public static void main(String[] args) throws GitAPIException, IOException {
        //Print the number of times each file was changed in the entire commit history
        CommitCountCalculator commitCountCalculator = new CommitCountCalculator();
        HashMap<String, Integer> numberOfCommitsPerFile = commitCountCalculator.calculateNumberOfCommitsPerFile(
                commitCountCalculator.getAllCommits());
        Iterator<String> timesUpdatedIterator = numberOfCommitsPerFile.keySet().iterator();
        while (timesUpdatedIterator.hasNext()) {
            String filePath = timesUpdatedIterator.next();
            int timesUpdated = numberOfCommitsPerFile.get(filePath);
            Constants.LOG.info("File `" + filePath + "` was updated " + timesUpdated + " times.");
        }



        /*
        List of all commits on this repos. Useful for testing purposes.
LogCommit: commit e00ab236f6648e2ff5065cddea7299e1690d30ca 1635618767 -----sp
LogCommit: commit 42e94d1665aea4a478fba044efc2ae669922b85d 1635558668 -----sp
LogCommit: commit a19b9b01f987a352760b130900d5e272bdb0f65c 1635557829 -----sp
LogCommit: commit e65331ab7d7c2e85dc382d04be821a82ddddb3e7 1635557782 -----sp
LogCommit: commit d45e60226c9fd913cdb83a0e56a9faf2a8f0eebd 1635520493 -----sp
LogCommit: commit 721806b2eea2327dfeb8c7be6ccf9adba9136df1 1635520134 -----sp
LogCommit: commit 325c458f88fc9ac1fdfccc8e24d8da4cc2187e5d 1635519404 -----sp
LogCommit: commit 63ace9eb9200b9f2fe2f860dce83f5d0c31e4c07 1635519195 -----sp
LogCommit: commit c93d6b4f9ea6a79f8be29cefe763b849cf5bb53f 1635519107 -----sp
LogCommit: commit bc8ae3c28fd60e39eadf4dc58c8ab671d4fa04f2 1635518891 -----sp
LogCommit: commit 6328ef93acaf16a9fd2cfec2f5c84c0801ce927d 1635518677 -----sp
LogCommit: commit 7552cf05247b54eb69511af4a3ce2e39340e98b8 1635518375 -----sp
LogCommit: commit 0b68f5637eb1edeb18adbe5d275f3d26ff380bad 1635513647 -----sp
LogCommit: commit 5ac2dbf61b8aa9b1b28038b507df68e3943d8f41 1635448335 -----sp
LogCommit: commit c13924b5f5019c246519ab786d7c0b8e40f92c5a 1635431101 -----sp
LogCommit: commit f59976cf6c1973042e42a206c9403a2f4e773263 1635401972 -----sp
LogCommit: commit ee7b3e3b708bef4bdacc77428733aebe310c3aee 1635356662 -----sp
LogCommit: commit 201408af066ef1f6b2515c473ddfa9aae822698c 1635351170 -----sp
LogCommit: commit 27082ba11d8d812b31c82458a72ad9c48920fb21 1635351154 -----sp
LogCommit: commit a49266a94e1f0960f7bb01a2aefc7d3ad37a076d 1635350953 -----sp
LogCommit: commit 0d124558bb1000395288d12299d7d290aec61521 1635171571 -----sp
         */
    }

    public static ObjectLoader getObjectLoader(ObjectId objectId) {
        try {
            return repository.open(treeWalk.getObjectId(0));
        } catch (IOException e) {
            // TODO: What if the loader is not present?
            return null;
        }
    }

    public HashMap<String, Integer> calculateNumberOfCommitsPerFile(Iterable<RevCommit> commitList) {
        HashMap<String, Integer> filePathToChangeCountMap = new HashMap<>();
        try {
            //Iterate through the commits two-at-a-time
            Iterator<RevCommit> commitIterator = commitList.iterator();
            RevCommit newerCommit;
            if (commitIterator.hasNext())
                newerCommit = commitIterator.next();
            else {
                Constants.LOG.error("There were not enough commits to compute the number of times each file was changed.");
                return new HashMap<>();
            }
            while (commitIterator.hasNext()) {
                RevCommit olderCommit = commitIterator.next();

                final List<DiffEntry> diffs = git.diff()
                        .setOldTree(prepareTreeParser(olderCommit.getName()))
                        .setNewTree(prepareTreeParser(newerCommit.getName()))
                        .call();


                //Count the number of times each file was changed
                for (DiffEntry diffEntry : diffs) {
                    String filePath = diffEntry.getOldPath(); //arbitrarily choose the older name of the file even if its name changed in the commit
                    if (filePathToChangeCountMap.containsKey(filePath)) {
                        //Increment hash map value since it exists
                        int timesChanged = filePathToChangeCountMap.get(filePath);
                        filePathToChangeCountMap.put(filePath, timesChanged + 1);
                    } else {
                        filePathToChangeCountMap.put(filePath, 1);
                    }
                }

                newerCommit = olderCommit;
            }
        } catch (IOException | GitAPIException e) {
            Constants.LOG.error(e);
            Constants.LOG.error(e.getMessage());
        }
        return filePathToChangeCountMap;
    }

    private AbstractTreeIterator prepareTreeParser(String objectId) throws IOException {
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

    public Iterable<RevCommit> getAllCommits() throws IOException, GitAPIException {
        return git.log().all().call();
    }

    private static Iterable<RevCommit> getCommitsByBranch(String branchName) {
        //Choose the branch
        ObjectId branchId;
        try {
            branchId = repository.resolve(branchName);
            return git.log().add(branchId).call();
        } catch (IOException | GitAPIException e) {
            Constants.LOG.error(e);
            Constants.LOG.error(e.getMessage());
            return EMPTY_REV_COMMITS;
        }
    }

    public static Pair<Iterable<RevCommit>, TreeWalk> getCommitsAndTreeByBranch(String branchName) {
        return new Pair<>(getCommitsByBranch(branchName), treeWalk);
    }

    private static TreeWalk getTreeWalkFromRepository() {
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.setRecursive(true);
        treeWalk.setPostOrderTraversal(false);
        return treeWalk;
    }
}
