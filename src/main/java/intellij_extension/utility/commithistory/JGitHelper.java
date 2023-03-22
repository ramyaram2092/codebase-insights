package intellij_extension.utility.commithistory;

import intellij_extension.CodebaseInsightsToolWindowFactory;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

/**
 * Contains utility methods for opening Git repositories.
 * Credit to the JGit Cookbook for creating this class https://github.com/centic9/jgit-cookbook/tree/master/src/main/java/org/dstadler/jgit/helper
 */
public class JGitHelper
{
    private JGitHelper() {
        //This is a utility class
    }

    public static Repository openLocalRepository() throws IOException
    {
        final String projectRootPath = locateProjectRoot();
        assert projectRootPath != null;

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder
                .readEnvironment() // scan environment GIT_* variables
                //.findGitDir() // scan up the file system tree
                .findGitDir(new File(projectRootPath))
                .build();
    }

    //Same as above method, but requires a path parameter
    public static Repository openLocalRepository(File projectPath) throws IOException
    {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir(projectPath)
                .build();
    }

    /**
     * @return the path of the project that the user has open in IntelliJ or null
     * as a default.
     */
    public static String locateProjectRoot()
    {
        //Pull the 'project' from CodebaseInsightsToolWindowFactory, and wait until it exists if necessary
        synchronized (CodebaseInsightsToolWindowFactory.projectSynchronizer) {
            if (CodebaseInsightsToolWindowFactory.getProject() == null) {
                try {
                    //Wait until the 'project' is not-null
                    CodebaseInsightsToolWindowFactory.projectSynchronizer.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                    //Restore interrupted state... (recommended by SonarQube)
                    Thread.currentThread().interrupt();
                }
            }
        }
        return CodebaseInsightsToolWindowFactory.getProject().getBasePath();
    }
}
