package intellij_extension.utility.commithistory;

import intellij_extension.Constants;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static testdata.TestData.NUMBER_OF_COMMITS_IN_GITIGNORE;

/**
 * Checks that the .gitignore file from this plugin is being measured with
 * the correct number of commits.
 */
class CommitCountCalculatorTest
{
    /**
     * Since it's a little tricky to test whether or not JGit is working,
     * this test just checks if the CommitCountCalculator threw an IOException
     * (which is caused by the repository not being opened correctly).
     */
    @Test
    void defaultConstructorTest()
    {
        assertDoesNotThrow(() -> {
            CommitCountCalculator commitCountCalculator = new CommitCountCalculator(); //method under test
            Constants.LOG.info("Created a " + commitCountCalculator + " with a default constructor. " +
                    "This print message prevents an unused variable code smell.");
        });
    }

    //Ensure that the .gitignore has the expected number of commits
    @Test
    void calculateNumberOfCommitsPerFileTest() throws IOException, GitAPIException
    {
        CommitCountCalculator commitCountCalculator = new CommitCountCalculator();
        //Method under test
        HashMap<String, Integer> filePathToCommitCountMap =
                commitCountCalculator.calculateNumberOfCommitsPerFile(commitCountCalculator.getAllCommits());

        assertEquals(NUMBER_OF_COMMITS_IN_GITIGNORE, (int) filePathToCommitCountMap.get(".gitignore"));
    }
}
