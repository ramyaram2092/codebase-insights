package intellij_extension.utility;

import intellij_extension.Constants;
import intellij_extension.models.redesign.Codebase;
import intellij_extension.models.redesign.Commit;
import intellij_extension.models.redesign.FileObject;
import intellij_extension.models.redesign.HeatObject;
import intellij_extension.utility.commithistory.JGitHelper;
import intellij_extension.utility.filesize.FileSizeCalculator;
import org.apache.commons.io.output.NullOutputStream;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;


/**
 * This class gathers all data from JGit and stores it in a Codebase model.
 * The attachCodebaseData(CodebaseV2 codebase) method is meant to do everything at once, including
 * file size computation and the number of commits per file for all commits in a Git repository.
 */
public class RepositoryAnalyzer {

    private static final boolean DEBUG_BRANCH = false;
    private static final boolean DEBUG_COMMIT = false;
    private static final boolean DEBUG_FILE = false;
    private static final boolean DEBUG_DIFF_ENTRY = false;
    private static final String DEBUG_COMMIT_HASH = "commit 0cdfe6bf92eddb57763f491b6db6edc6f56324f5 1636656382 ------p";
    private static final String DEBUG_FILENAME = "HeatCalculationUtility.java";
    private static Git git;

    // Init git variable based on local repo
    public RepositoryAnalyzer() throws IOException {
        Repository repo = JGitHelper.openLocalRepository();
        git = new Git(repo);
    }

    // Init git variable based on supplied path
    public RepositoryAnalyzer(File projectPath) throws IOException {
        git = new Git(JGitHelper.openLocalRepository(projectPath));
    }

    // This method assumes "x/y/v/target";
    public static @NotNull String getFilename(@NotNull String path) {
        // If path uses \ replace with /
        path = path.replace("\\", "/");
        return path.substring(path.lastIndexOf("/") + 1);
    }

    // Only for testing =/
    public static Git getGit() {
        return git;
    }

    // Builds list of strings based on local checked out branches
    public static void attachBranchNameList(Codebase codebase) throws GitAPIException {
        // Get the list of all LOCAL branches
        List<Ref> call = git.branchList().call();
        // Alternatively: Get the list of all branches, both local and REMOTE --> call = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();

        // Add all branch names to the Codebase
        for (Ref ref : call) {
            String branchName = getFilename(ref.getName());
            if (DEBUG_BRANCH) {
                System.out.printf("Branch name: %s, \n\tRef: %s, \n\tRef.getName(): %s, \n\tRef.getObjectId.getName(): %s%n",
                        branchName, ref, ref.getName(), ref.getObjectId().getName());
            }
            codebase.getBranchNameList().add(branchName.toLowerCase());
        }
    }

    // Massive data gathering method
    // For every file gets line count, file size, # of authors/commits at every commit.
    public static void attachCodebaseData(@NotNull Codebase codebase) {
        try {

            // Get all commits in the repos for active branch
            List<RevCommit> commitList = getCommitsByBranch(codebase.getActiveBranch());
            Iterator<RevCommit> commitIterator = commitList.iterator();

            // Iterate through the commits and gather data per file
            RevCommit previousCommit = null;
            RevCommit processCommit;
            while (commitIterator.hasNext()) {
                // Get commit to process
                processCommit = commitIterator.next();
                if (DEBUG_COMMIT) {
                    PersonIdent authorInfo = processCommit.getAuthorIdent();
                    System.out.printf("\nProcessing commit: %s%n Commit Author: %s%n Commit Email: %s%n Commit Time: %s%n", processCommit.getName(), authorInfo.getName(), authorInfo.getEmailAddress(), authorInfo.getWhen());
                    System.out.printf("LogMessage: %s%n", processCommit.getShortMessage());
                    System.out.printf("Parent Count: %s%n", processCommit.getParentCount());
                }


                // Build CodebaseInsight's data object
                // Extracts: author, email, full/short message, date, hash
                Commit commitExtract = new Commit(processCommit);
                // Add to active commit list
                codebase.getActiveCommits().add(commitExtract);
                // Process heat metrics for every file
                processHeatMetrics(codebase, processCommit, previousCommit);

                // Find the difference between the processCommit and the previous commit
                final List<DiffEntry> diffs = diffCommit(processCommit.getName());
                // Save the DiffEntry list
                commitExtract.addDiffEntriesToDiffList(diffs);

                if (DEBUG_DIFF_ENTRY) {
                    System.out.println("DiffEntry size: " + diffs.size());
                }

                // Process heat metrics for DiffEntry files (# of commits/authors)
                for (DiffEntry diffEntry : diffs) {
                    // Get file id (aka path)
                    String newFilePath = diffEntry.getNewPath();
                    // TODO proper file filtering
                    if (newFilePath.endsWith(".java")) {
                        // Get filename from diffEntry's path
                        String fileName = getFilename(newFilePath);

                        if (DEBUG_FILE) {
                            if (fileName.equals(DEBUG_FILENAME)) {
                                System.out.printf("%s  found in commit %s%n", fileName, processCommit.getName());
                                System.out.printf("%s  old Path: %s%n", fileName, diffEntry.getOldPath());
                                System.out.printf("%s  new Path: %s%n", fileName, diffEntry.getNewPath());
                            }
                        }

                        // Get FileObject based on filename
                        FileObject fileObject = codebase.getFileObjectFromFilename(fileName);
                        // Count the number of times the file was changed
                        incrementNumberOfTimesChanged(fileObject, processCommit.getName());
                        // Count the number of authors the file has
                        incrementNumberOfAuthors(fileObject, processCommit);
                        commitExtract.getFileSet().add(fileName);
                        // Update latest commit for fileObject
                        fileObject.setLatestCommitInDiffEntryList(processCommit.getName());
                        // TODO consider tracking paths per change/commit
                        //  it's not always a rename event that changes path. HeatCalculationUtility is an example of this.
                        // Blindly take new path
                        fileObject.setPath(diffEntry.getNewPath());

                        if (DEBUG_FILE) {
                            if (fileObject.getFilename().equals(DEBUG_FILENAME)) {
                                HeatObject processHeatObject = fileObject.getHeatObjectAtCommit(processCommit.getName());
                                System.out.printf("%s's HeatMetric lineCount %s.%n", DEBUG_FILENAME, processHeatObject.getLineCount());
                                System.out.printf("%s's HeatMetric fileSize %s.%n", DEBUG_FILENAME, processHeatObject.getFileSize());
                                System.out.printf("%s's HeatMetric commits %s.%n", DEBUG_FILENAME, processHeatObject.getNumberOfCommits());
                                System.out.printf("%s's HeatMetric authors %s.%n", DEBUG_FILENAME, processHeatObject.getNumberOfAuthors());
                            }
                        }
                    }
                }

                // Update previous Commit
                previousCommit = processCommit;

                // Set project root
                codebase.setProjectRootPath(git.getRepository().getDirectory().getAbsoluteFile().getParentFile().getParent());
            }

            // Record latest commit hash
            codebase.setLatestCommitHash(latestCommitHash(commitList));

        } catch (IOException | GitAPIException e) {
            Constants.LOG.error(e);
            Constants.LOG.error(e.getMessage());
        }
    }

    private static List<RevCommit> getCommitsByBranch(String branchName) throws IOException, GitAPIException {
        // Get branch id
        ObjectId branchId = git.getRepository().resolve(branchName);
        // Get commits based on branch id
        Iterator<RevCommit> commitIterator = git.log().add(branchId).call().iterator();

        // Convert the commitIterable to a list for ease of use
        List<RevCommit> commitList = new LinkedList<>();
        while (commitIterator.hasNext()) {
            commitList.add(0, commitIterator.next());
        }

        // Sort the commits by date with the oldest commits first
        Comparator<RevCommit> TIME = Comparator.comparingInt(RevCommit::getCommitTime);
        commitList.sort(TIME);

        if (DEBUG_BRANCH) {
            System.out.printf("Getting commits for branch %s,%n number of commits %s%n", branchName, commitList.size());
        }

        return commitList;
    }

    private static String latestCommitHash(List<RevCommit> commitList) throws IOException, GitAPIException
    {
        //Choose the commit with the latest time value
        Comparator<RevCommit> TIME = Comparator.comparingInt(RevCommit::getCommitTime);
        Optional<RevCommit> optional = commitList.stream().max(TIME);
        if (optional.isPresent())
            return optional.get().getName();
        else
            throw new NoSuchElementException("No commits were made to the repository, so the latest could not be found");
    }

    // For every file create HeatObject for processCommit
    // And copy HeatObject from previousCommit into new HeatObject
    // So we don't walk the tree again, after above is done, calculate fileSize and line count for new HeatObjects
    // TODO can we do this in the DiffEntry b/c we'll know the file's fileSize / LineCount changed if they appear in the DiffEntry list
    private static void processHeatMetrics(Codebase codeBase, @NotNull RevCommit processCommit, RevCommit previousCommit) throws IOException {
        // Prepare a TreeWalk that can walk through the version of the repo at revCommit
        RevTree tree = processCommit.getTree();
        TreeWalk treeWalk = new TreeWalk(git.getRepository());
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);

        // Traverse through the old version of the project until the target file is found.
        while (treeWalk.next()) {
            // TODO proper file filtering
            String path = treeWalk.getPathString();
            if (path.endsWith(".java")) {

                // Get FileObject based on path
                FileObject fileObject = codeBase.createOrGetFileObjectFromPath(path);

                // Transfer old metrics to new heat object for this new commit
                // processCommit == current, latest is previous commit
                transferHeatMetricsFromLatestToCurrent(fileObject, processCommit);

                // Update line count and file size
                updateLineCountAndFileSizeMetrics(treeWalk, fileObject, processCommit.getName());

                // Update last time we saw this file in a TreeWalk
                fileObject.setLatestCommitInTreeWalk(processCommit.getName());

                if (DEBUG_FILE) {
                    if (fileObject.getFilename().equals(DEBUG_FILENAME)) {
                        System.out.println("~~~~~FILE FOUND IN TREEWALK~~~~~");
                        HeatObject processHeatObject = fileObject.getHeatObjectAtCommit(processCommit.getName());
                        System.out.printf("%s's HeatMetric lineCount %s.%n", DEBUG_FILENAME, processHeatObject.getLineCount());
                        System.out.printf("%s's HeatMetric fileSize %s.%n", DEBUG_FILENAME, processHeatObject.getFileSize());
                        System.out.printf("%s's HeatMetric commits %s.%n", DEBUG_FILENAME, processHeatObject.getNumberOfCommits());
                        System.out.printf("%s's HeatMetric authors %s.%n", DEBUG_FILENAME, processHeatObject.getNumberOfAuthors());
                    }
                }
            }
        }
    }

    private static void transferHeatMetricsFromLatestToCurrent(FileObject fileObject, RevCommit processCommit) {
        String previousCommitHashId = fileObject.getLatestCommitInTreeWalk();
        // Probably first commit..
        if (previousCommitHashId.isEmpty()) {
            // Get the FileObject's HeatObject for this commit
            HeatObject heatObject = fileObject.createOrGetHeatObjectAtCommit(processCommit.getName());
            heatObject.setFilename(fileObject.getFilename());
            // That's all we can do if no previous commit found.
            return;
        }

        // Start off a new HeatObject with the values of the previous heat object
        // The steps after this one will overwrite any that have updated.
        HeatObject previousHeatObject = fileObject.getHeatObjectAtCommit(previousCommitHashId); // if this is null this is a problem or something needs update
        HeatObject processHeatObject = fileObject.createOrGetHeatObjectAtCommit(processCommit.getName()); // this should always be a creation

        // TODO in the future we can add the some sort of drop off here so as they don't change over commits they become less hot.
        processHeatObject.setFilename(fileObject.getFilename()); // I think this is necessary
        processHeatObject.setLineCount(previousHeatObject.getLineCount());
        processHeatObject.setFileSize(previousHeatObject.getFileSize());
        processHeatObject.setNumberOfCommits(previousHeatObject.getNumberOfCommits());
        processHeatObject.setNumberOfAuthors(previousHeatObject.getNumberOfAuthors());
    }

    private static void updateLineCountAndFileSizeMetrics(@NotNull TreeWalk treeWalk, @NotNull FileObject fileObject, String commitHash) throws IOException {
        // Create an input stream that has the old version of the file open
        ObjectId objectId = treeWalk.getObjectId(0);
        ObjectLoader loader = git.getRepository().open(objectId);
        InputStream inputStream = loader.openStream();

        //Get number of lines and file size from inputStream
        long lineCount = FileSizeCalculator.computeLineCount(inputStream);
        long fileSize = loader.getSize();

        // Get the FileObject's HeatObject for this commit
        HeatObject heatObject = fileObject.getHeatObjectAtCommit(commitHash);
        // Update vars with new info.
        heatObject.setLineCount(lineCount);
        heatObject.setFileSize(fileSize);
    }

    private static void incrementNumberOfTimesChanged(@NotNull FileObject fileObject, String commitHash) {
        // Get info about the previous commit
        int oldNumberOfCommits = 0;
        String prevCommit = fileObject.getLatestCommitInTreeWalk();
        if (!prevCommit.isEmpty()) {
            if (DEBUG_FILE) {
                if (fileObject.getFilename().equals(DEBUG_FILENAME)) {
                    System.out.printf("%s's latest commit is %s.%n", DEBUG_FILENAME, prevCommit);
                }
            }
            // Default is -1 so we ignore that. Just start at 0 instead of taking old value.
            if (fileObject.getHeatObjectAtCommit(prevCommit).getNumberOfCommits() > 0) {
                oldNumberOfCommits = fileObject.getHeatObjectAtCommit(prevCommit).getNumberOfCommits();
            }
        }

        // Retrieve the HeatObject that holds the number of commits for the target file
        HeatObject heatObject = fileObject.getHeatObjectAtCommit(commitHash);
        // Increment the HeatObject's number of commits
        heatObject.setNumberOfCommits(oldNumberOfCommits + 1);

        if (DEBUG_FILE) {
            if (fileObject.getFilename().equals(DEBUG_FILENAME)) {
                System.out.printf("File %s has %s commits as of %s%n", DEBUG_FILENAME, heatObject.getNumberOfCommits(), commitHash);
            }
        }
    }

    private static void incrementNumberOfAuthors(@NotNull FileObject fileObject, @NotNull RevCommit commit) {
        // Extract and add author info to FileObject
        PersonIdent authorInfo = commit.getAuthorIdent();
        // These are sets so if the string already exists nothing will happen
        fileObject.getUniqueAuthors().add(authorInfo.getName());
        fileObject.getUniqueAuthorEmails().add(authorInfo.getEmailAddress());

        // Retrieve the HeatObject associated with this commit
        HeatObject heatObject = fileObject.getHeatObjectAtCommit(commit.getName());
        // Attach author count
        // Using email b/c that's possible more unique than a name
        heatObject.setNumberOfAuthors(fileObject.getUniqueAuthorEmails().size());

        if (DEBUG_FILE) {
            if (fileObject.getFilename().equals(DEBUG_FILENAME)) {
                System.out.printf("File %s has %s authors as of %s%n", DEBUG_FILENAME, heatObject.getNumberOfAuthors(), commit.getName());
            }
        }
    }

    private static List<DiffEntry> diffCommit(String commitHash) throws IOException {
        // Get the commit you are looking for.
        RevCommit newCommit;
        try (RevWalk walk = new RevWalk(git.getRepository())) {
            newCommit = walk.parseCommit(git.getRepository().resolve(commitHash));
        }

        if (DEBUG_DIFF_ENTRY) {
            System.out.printf("\nLogCommit: %s%n", newCommit);
            System.out.printf("LogMessage: %s%n", newCommit.getFullMessage());
            System.out.printf("Parent Count: %s%n", newCommit.getParentCount());
        }

        // Compute diff and return
        try {
            return getDiffOfCommit(newCommit);
        } catch (GitAPIException e) {
            Constants.LOG.error(e.getMessage());
            // TODO Proper exception handling in this case
            //  Not sure this is best option (returning empty list)
            return new ArrayList<>();
        }
    }

    // Helper gets the DiffEntry list
    private static List<DiffEntry> getDiffOfCommit(RevCommit newCommit) throws IOException, GitAPIException {
        // Get commit that is previous to the current one.
        RevCommit oldCommit = getPrevHash(newCommit);
        if (oldCommit == null) {
            // If no old commit then this is the first commit and there is no DiffEntries
            // TODO - DECISION - should we make fake DiffEntry list will all files plus Addition category?
            return new ArrayList<>();
        }

        // Use treeIterator to create the diffs.
        AbstractTreeIterator oldTreeIterator = getCanonicalTreeParser(oldCommit);
        AbstractTreeIterator newTreeIterator = getCanonicalTreeParser(newCommit);

        // Find the difference between the oldTree and newTree
        List<DiffEntry> diffs = git.diff()
                .setOldTree(oldTreeIterator)
                .setNewTree(newTreeIterator)
                .call();

        if (DEBUG_DIFF_ENTRY) {
            System.out.println("\n~~~~~!!!PRE-FORMAT!!!~~~~~");
            for (DiffEntry diffEntry : diffs) {
                System.out.println("~~~~~FILE FOUND IN DIFF TREE~~~~~");
                System.out.printf("DiffEntry ChangeType: %s%n", diffEntry.getChangeType());
                System.out.printf("DiffEntry OldPath: %s%n", diffEntry.getOldPath());
                System.out.printf("DiffEntry NewPath: %s%n", diffEntry.getNewPath());
                System.out.printf("DiffEntry OldId: %s%n", diffEntry.getOldId().toObjectId().getName());
                System.out.printf("DiffEntry NewId: %s%n", diffEntry.getNewId().toObjectId().getName());
            }
        }

        // Format the diffs, used for finding renames - NOT PERFECT!
        OutputStream outputStream = NullOutputStream.NULL_OUTPUT_STREAM;
        try (DiffFormatter formatter = new DiffFormatter(outputStream)) {
            formatter.setRepository(git.getRepository());
            formatter.setDetectRenames(true);
            formatter.getRenameDetector().addAll(diffs);
            diffs = formatter.getRenameDetector().compute();
        }

        if (DEBUG_DIFF_ENTRY) {
            System.out.println("\n~~~~~!!!POST FORMAT!!!~~~~~");
            for (DiffEntry diffEntry : diffs) {
                if (getFilename(diffEntry.getNewPath()).equals(DEBUG_FILENAME)) {
                    System.out.println("~~~~~FILE FOUND IN DIFF TREE~~~~~");
                    System.out.printf("DiffEntry ChangeType: %s%n", diffEntry.getChangeType());
                    System.out.printf("DiffEntry OldPath: %s%n", diffEntry.getOldPath());
                    System.out.printf("DiffEntry NewPath: %s%n", diffEntry.getNewPath());
                    System.out.printf("DiffEntry OldId: %s%n", diffEntry.getOldId().toObjectId().getName());
                    System.out.printf("DiffEntry NewId: %s%n", diffEntry.getNewId().toObjectId().getName());
                }
            }
        }

        return diffs;
    }

    // Helper function to get the previous commit. Written by Whitecat from https://stackoverflow.com/questions/39935160/how-to-use-jgit-to-get-list-of-changes-in-files
    private static RevCommit getPrevHash(RevCommit commit) {
        // Make a rev walk
        try (RevWalk walk = new RevWalk(git.getRepository())) {
            // Set starting point of RevWalk based on RevCommit
            walk.markStart(commit);
            // One loop means previous commit
            int count = 0;
            for (RevCommit rev : walk) {
                // Got the previous commit.
                if (count == 1) {
                    return rev;
                }
                count++;
            }
            // Proper destruction if nothing is found
            walk.dispose();
        } catch (IOException e) {
            Constants.LOG.error(e.getMessage());
            return null;
        }
        //Reached end and no previous commits.
        return null;
    }

    // Helper function to get the tree of the changes in a commit. Written by Rüdiger Herrmann from https://www.codeaffine.com/2016/06/16/jgit-diff/
    @Contract("_ -> new")
    private static @NotNull AbstractTreeIterator getCanonicalTreeParser(ObjectId commitId) throws IOException {
        try (RevWalk walk = new RevWalk(git.getRepository())) {
            RevCommit commit = walk.parseCommit(commitId);
            ObjectId treeId = commit.getTree().getId();
            try (ObjectReader reader = git.getRepository().newObjectReader()) {
                return new CanonicalTreeParser(null, reader, treeId);
            }
        }
    }
}

/**
 * UNUSED METHODS!!
 * UNUSED METHODS!!
 * UNUSED METHODS!!
 * UNUSED METHODS!!
 * UNUSED METHODS!!
 * UNUSED METHODS!!
 * UNUSED METHODS!!
 * UNUSED METHODS!!
 * UNUSED METHODS!!
 * UNUSED METHODS!!
 */


//    /**
//     * Returns an InputStream with the old version of the file open.
//     * This is method is expensive because it has to search through the entire
//     * old version of the repository to find the file.
//     *
//     * @param filePath   the path of the file to open relative to the project root dir (ex: "my-project/my-package/my-file.java")
//     * @param commitHash a commit hash, such as "1e589e61ef75003b1df88bdb738f9d9f4a4f5f8a" that the file is present on.
//     * @throws IOException           when there are problems opening the commit
//     * @throws IllegalStateException if the file could not be found at that commit
//     */
//    public InputStream obtainFileContents(String filePath, String commitHash) throws IOException {
//        //Create a commit object from the commit hash
//        ObjectId commitId = git.getRepository().resolve(commitHash);
//        assert commitId != null;
//        RevWalk revWalk = new RevWalk(git.getRepository());
//        RevCommit commit = revWalk.parseCommit(commitId);
//
//        //Prepare a TreeWalk that can walk through the version of the repos at that commit
//        RevTree tree = commit.getTree();
//        TreeWalk treeWalk = new TreeWalk(git.getRepository());
//        treeWalk.addTree(tree);
//        treeWalk.setRecursive(true);
//
//        //Traverse through the old version of the project until the target file is found.
//        //I couldn't get `treeWalk.setFilter(PathFilter.create(filePath));` to work, so this is an alternative approach.
//        while (treeWalk.next()) {
//            String path = treeWalk.getPathString();
//            if (path.endsWith(filePath)) {
//                //Return an input stream that has the old version of the file open
//                ObjectId objectId = treeWalk.getObjectId(0);
//                ObjectLoader loader = git.getRepository().open(objectId);
//                return loader.openStream();
//            }
//        }
//
//        //Could not find the file on that commit
//        throw new IllegalStateException(String.format("The file `%s` could not be found in the commit `%s`.", filePath, commitHash));
//    }


//    public void attachLineCountToCodebase(Codebase codeBase, String commitHash) throws IOException {
//        //Create a commit object from the commit hash
//        ObjectId commitId = git.getRepository().resolve(commitHash);
//        assert commitId != null;
//        RevWalk revWalk = new RevWalk(git.getRepository());
//        RevCommit revCommit = revWalk.parseCommit(commitId);
//
//        attachLineCountToCodebase(codeBase, revCommit);
//    }


//    private AbstractTreeIterator prepareTreeParser(String commitHash) throws IOException {
//        // from the commit we can build the tree which allows us to construct the TreeParser
//        // noinspection Duplicates
//        try (RevWalk walk = new RevWalk(git.getRepository())) {
//            RevCommit commit = walk.parseCommit(git.getRepository().resolve(commitHash));
//            RevTree tree = walk.parseTree(commit.getTree().getId());
//
//            CanonicalTreeParser treeParser = new CanonicalTreeParser();
//            try (ObjectReader reader = git.getRepository().newObjectReader()) {
//                // Maybe this reset was doing us wrong??
//                // Not exactly sure what it does nor am I looking into it
//                // - Ethan
//                treeParser.reset(reader, tree.getId());
//            }
//            walk.dispose();
//            return treeParser;
//        }
//    }


//    public Iterable<RevCommit> getAllCommits() throws IOException, GitAPIException {
//        return git.log().all().call();
//    }

