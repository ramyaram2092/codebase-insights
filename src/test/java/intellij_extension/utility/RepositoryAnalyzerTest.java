package intellij_extension.utility;

import intellij_extension.models.redesign.Codebase;
import intellij_extension.models.redesign.FileObject;
import intellij_extension.models.redesign.HeatObject;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * If you fail any of these tests locally it might be because you don't have the branches checked out.
 */
public class RepositoryAnalyzerTest {
    public static final File PROJECT_ROOT = new File(".");
    public static final File BOOGUS_PROJECT_ROOT_1 = new File("BOOGUS PROJECT ROOT $#^&(#$*)!(_@");

    // This test just infinitely loops, guessing automatically finding the repo doesn't work...
//    @Test
//    void constructor_Default_Success()
//    {
//        assertDoesNotThrow(() -> {
//            RepositoryAnalyzer repo = new RepositoryAnalyzer();
//        });
//    }

    @Test
    void constructor_FilePathParameter_Success() {
        assertDoesNotThrow(() -> {
            new RepositoryAnalyzer(PROJECT_ROOT);
        });
    }

    @Test
    void constructor_FilePathParameter_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RepositoryAnalyzer(BOOGUS_PROJECT_ROOT_1);
        });
    }

    @Test
    void attachBranchNameList_BranchesMasterDevelopment_Success() throws IOException, GitAPIException {
        // Set up test data...
        Codebase codebase = Codebase.getInstance();
        RepositoryAnalyzer repositoryAnalyzer = new RepositoryAnalyzer(PROJECT_ROOT);
        repositoryAnalyzer.attachBranchNameList(codebase); //method being tested

        //Our branch list is always changing, so I just check if there are at least 3 branches.
        assertTrue(codebase.getBranchNameList().size() >= 3);
        // Ensure certain branches are present
        assertTrue(codebase.getBranchNameList().contains("master"));
        assertTrue(codebase.getBranchNameList().contains("development"));
        // We could check more but everyone might not have the same branches checked out
        // Master/development are reasonable to have and should be sufficient for the test.
    }

    // Only test on dead branches.
    // Active branches will have a changing branch size.
    @Test
    void attachCodebaseData_BranchSize_ViewModelControllerBranch() throws IOException, GitAPIException {
        int EXPECTED_BRANCH_SIZE = 101;

        // Create test objects
        Codebase codebase = Codebase.getInstance();
        RepositoryAnalyzer repositoryAnalyzer = new RepositoryAnalyzer(PROJECT_ROOT);
        repositoryAnalyzer.attachBranchNameList(codebase);
        codebase.newBranchSelected("view-model-communication");
        repositoryAnalyzer.attachCodebaseData(codebase); // method being tested

        assertEquals(EXPECTED_BRANCH_SIZE, codebase.getActiveCommits().size());
    }

    // Only test on dead branches.
    // Active branches will have a changing branch size.
    @Test
    void attachCodebaseData_BranchSize_UIDevelopmentCommitHistory() throws IOException, GitAPIException {
        int EXPECTED_BRANCH_SIZE = 59;

        // Create test objects
        Codebase codebase = Codebase.getInstance();
        RepositoryAnalyzer repositoryAnalyzer = new RepositoryAnalyzer(PROJECT_ROOT);
        repositoryAnalyzer.attachBranchNameList(codebase);
        codebase.newBranchSelected("ui-development-commit-history");
        repositoryAnalyzer.attachCodebaseData(codebase); // method being tested

        assertEquals(EXPECTED_BRANCH_SIZE, codebase.getActiveCommits().size());
    }

    // File's History https://github.iu.edu/P532-Fall2021/team3-project/commits/9db51280e8bffb279acb8b1f36abaa209bc6e9a2/src/main/java/intellij_extension/CodebaseInsightsToolWindowFactory.java
    @Test
    void attachCodebaseData_FileHeatMetrics_CodebaseInsightsToolWindowFactory() throws IOException, GitAPIException {
        final String TEST_FILE_NAME = "CodebaseInsightsToolWindowFactory.java";

        final String TEST_HASH_1 = "fa50f9cf9edd5c46cd8980a01bd2c9c847b8a0b6"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/fa50f9cf9edd5c46cd8980a01bd2c9c847b8a0b6
        // Hidden merge commit (as in not present in GitHub history for the file but is caught by our code)
        final String TEST_HASH_2 = "1c03e6821c31743997976a3cb1b53105ea46770e"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/1c03e6821c31743997976a3cb1b53105ea46770e
        final String TEST_HASH_3 = "1e589e61ef75003b1df88bdb738f9d9f4a4f5f8a"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/1e589e61ef75003b1df88bdb738f9d9f4a4f5f8a
        final String TEST_HASH_4 = "2432beabe943ec84ce1accd9dc751d4e02205905"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/2432beabe943ec84ce1accd9dc751d4e02205905
        final String TEST_HASH_5 = "723a3eae7a8524b06733e9568f1b2240a0537b0b"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/723a3eae7a8524b06733e9568f1b2240a0537b0b
        // Hidden merge commit (as in not present in GitHub history for the file but is caught by our code)
        final String TEST_HASH_6 = "748e142e937b064f7df97cd6e22869cd20707d29"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/748e142e937b064f7df97cd6e22869cd20707d29
        // Hidden merge commit (as in not present in GitHub history for the file but is caught by our code)
        final String TEST_HASH_7 = "0cdfe6bf92eddb57763f491b6db6edc6f56324f5"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/0cdfe6bf92eddb57763f491b6db6edc6f56324f5
        final String TEST_HASH_8 = "223e2ee0638ca432f965886a8e4d6179d1639f99"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/223e2ee0638ca432f965886a8e4d6179d1639f99
        final String TEST_HASH_9 = "9db51280e8bffb279acb8b1f36abaa209bc6e9a2"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/9db51280e8bffb279acb8b1f36abaa209bc6e9a2
        // Hidden merge commit (as in not present in GitHub history for the file but is caught by our code)
        final String TEST_HASH_10 = "419c658dadf645d647ea9bf3068bac588cdff740"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/419c658dadf645d647ea9bf3068bac588cdff740

        // For fa50f
        final long EXPECTED_LINE_COUNT_1 = 55;
        final long EXPECTED_FILE_SIZE_1 = 2123; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_1 = 1;
        final int EXPECTED_NUMBER_OF_AUTHORS_1 = 1;
        final String EXPECTED_EMAIL_OF_AUTHOR_1_1 = "ebehar@iu.edu";

        // For 1c03e
        final long EXPECTED_LINE_COUNT_2 = 55;
        final long EXPECTED_FILE_SIZE_2 = 2123; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_2 = 2;
        final int EXPECTED_NUMBER_OF_AUTHORS_2 = 1;
        final String EXPECTED_EMAIL_OF_AUTHOR_2_1 = "ebehar@iu.edu";

        // For 1e589
        final long EXPECTED_LINE_COUNT_3 = 55;
        final long EXPECTED_FILE_SIZE_3 = 2135; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_3 = 3;
        final int EXPECTED_NUMBER_OF_AUTHORS_3 = 1;
        final String EXPECTED_EMAIL_OF_AUTHOR_3_1 = "ebehar@iu.edu";

        // For 2432be
        final long EXPECTED_LINE_COUNT_4 = 53;
        final long EXPECTED_FILE_SIZE_4 = 2065; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_4 = 4;
        final int EXPECTED_NUMBER_OF_AUTHORS_4 = 1;
        final String EXPECTED_EMAIL_OF_AUTHOR_4_1 = "ebehar@iu.edu";

        // For 723a3
        final long EXPECTED_LINE_COUNT_5 = 44;
        final long EXPECTED_FILE_SIZE_5 = 1675; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_5 = 5;
        final int EXPECTED_NUMBER_OF_AUTHORS_5 = 2;
        final String EXPECTED_EMAIL_OF_AUTHOR_5_1 = "ebehar@iu.edu";
        final String EXPECTED_EMAIL_OF_AUTHOR_5_2 = "fyffep";

        // For 748e1
        final long EXPECTED_LINE_COUNT_6 = 52;
        final long EXPECTED_FILE_SIZE_6 = 1996; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_6 = 6;
        final int EXPECTED_NUMBER_OF_AUTHORS_6 = 2;
        final String EXPECTED_EMAIL_OF_AUTHOR_6_1 = "ebehar@iu.edu";
        final String EXPECTED_EMAIL_OF_AUTHOR_6_2 = "fyffep";

        // For 0cdfe
        final long EXPECTED_LINE_COUNT_7 = 44;
        final long EXPECTED_FILE_SIZE_7 = 1675; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_7 = 7;
        final int EXPECTED_NUMBER_OF_AUTHORS_7 = 2;
        final String EXPECTED_EMAIL_OF_AUTHOR_7_1 = "ebehar@iu.edu";
        final String EXPECTED_EMAIL_OF_AUTHOR_7_2 = "fyffep";

        // For 223e2
        final long EXPECTED_LINE_COUNT_8 = 54;
        final long EXPECTED_FILE_SIZE_8 = 2093; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_8 = 8;
        final int EXPECTED_NUMBER_OF_AUTHORS_8 = 2;
        final String EXPECTED_EMAIL_OF_AUTHOR_8_1 = "ebehar@iu.edu";
        final String EXPECTED_EMAIL_OF_AUTHOR_8_2 = "fyffep";

        // For 9db51
        final long EXPECTED_LINE_COUNT_9 = 56;
        final long EXPECTED_FILE_SIZE_9 = 2204; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_9 = 9;
        final int EXPECTED_NUMBER_OF_AUTHORS_9 = 2;
        final String EXPECTED_EMAIL_OF_AUTHOR_9_1 = "ebehar@iu.edu";
        final String EXPECTED_EMAIL_OF_AUTHOR_9_2 = "fyffep";

        // For 419c6
        final long EXPECTED_LINE_COUNT_10 = 56;
        final long EXPECTED_FILE_SIZE_10 = 2204; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_10 = 10;
        final int EXPECTED_NUMBER_OF_AUTHORS_10 = 2;
        final String EXPECTED_EMAIL_OF_AUTHOR_10_1 = "ebehar@iu.edu";
        final String EXPECTED_EMAIL_OF_AUTHOR_10_2 = "fyffep";

        //Create test objects
        Codebase codebase = Codebase.getInstance();
        RepositoryAnalyzer repositoryAnalyzer = new RepositoryAnalyzer(PROJECT_ROOT);
        repositoryAnalyzer.attachBranchNameList(codebase);
        codebase.newBranchSelected("development");
        repositoryAnalyzer.attachCodebaseData(codebase); //method being tested

        // Verify the results
        FileObject fileObject = codebase.getFileObjectFromFilename(TEST_FILE_NAME);
        HeatObject heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_1);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_1, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_1, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_1, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_1, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_1_1));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_2);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_2, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_2, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_2, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_2, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_2_1));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_3);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_3, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_3, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_3, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_3, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_3_1));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_4);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_4, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_4, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_4, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_4, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_4_1));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_5);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_5, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_5, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_5, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_5, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_5_1));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_5_2));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_6);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_6, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_6, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_6, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_6, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_6_1));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_6_2));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_7);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_7, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_7, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_7, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_7, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_7_1));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_7_2));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_8);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_8, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_8, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_8, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_8, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_8_1));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_8_2));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_9);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_9, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_9, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_9, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_9, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_9_1));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_9_2));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_10);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_10, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_10, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_10, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_10, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_10_1));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_10_2));
    }

    // File's History: https://github.iu.edu/P532-Fall2021/team3-project/commits/d8dabff8ad133f719daeceaa863d9b5802c2b919/src/test/java/testdata/TestData.java
    @Test
    void attachCodebaseData_FileHeatMetrics_TestData() throws IOException, GitAPIException {
        final String TEST_FILE_NAME = "TestData.java";

        final String TEST_HASH_1 = "3d47a7d9f4a2dd4c4aa63d794616708a0d50d8ff"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/3d47a7d9f4a2dd4c4aa63d794616708a0d50d8ff
        // Hidden merge commit (as in not present in GitHub history for the file but is caught by our code)
        final String TEST_HASH_2 = "3834daaff7f057d90e095d9f4ce63be19825539c"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/3834daaff7f057d90e095d9f4ce63be19825539c
        final String TEST_HASH_3 = "fb5b5d27631eee85b16b63ae84873975706d4f4a"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/fb5b5d27631eee85b16b63ae84873975706d4f4a
        final String TEST_HASH_4 = "f6e1406f5a67c61648ce70aa929c02d097b3b6ad"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/f6e1406f5a67c61648ce70aa929c02d097b3b6ad
        final String TEST_HASH_5 = "6383f661187987fe28130ec70e3810054956188c"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/6383f661187987fe28130ec70e3810054956188c
        final String TEST_HASH_6 = "2432beabe943ec84ce1accd9dc751d4e02205905"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/2432beabe943ec84ce1accd9dc751d4e02205905
        final String TEST_HASH_7 = "8920bac47d7b6fb59fef4d8c2cce3875545edf96"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/8920bac47d7b6fb59fef4d8c2cce3875545edf96
        final String TEST_HASH_8 = "d8dabff8ad133f719daeceaa863d9b5802c2b919"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/d8dabff8ad133f719daeceaa863d9b5802c2b919

        // For 3d47a
        final long EXPECTED_LINE_COUNT_1 = 37;
        final long EXPECTED_FILE_SIZE_1 = 1634; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_1 = 1;
        final int EXPECTED_NUMBER_OF_AUTHORS_1 = 1;
        final String EXPECTED_EMAIL_OF_AUTHOR_1_1 = "rramsam@iu.edu";

        // For 3834d
        final long EXPECTED_LINE_COUNT_2 = 37;
        final long EXPECTED_FILE_SIZE_2 = 1634; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_2 = 2;
        final int EXPECTED_NUMBER_OF_AUTHORS_2 = 2;
        final String EXPECTED_EMAIL_OF_AUTHOR_2_1 = "rramsam@iu.edu";
        final String EXPECTED_EMAIL_OF_AUTHOR_2_2 = "fyffep";

        // For fb5b5
        final long EXPECTED_LINE_COUNT_3 = 37;
        final long EXPECTED_FILE_SIZE_3 = 1670; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_3 = 3;
        final int EXPECTED_NUMBER_OF_AUTHORS_3 = 2;
        final String EXPECTED_EMAIL_OF_AUTHOR_3_1 = "rramsam@iu.edu";
        final String EXPECTED_EMAIL_OF_AUTHOR_3_2 = "fyffep";

        // For f6e14
        final long EXPECTED_LINE_COUNT_4 = 41;
        final long EXPECTED_FILE_SIZE_4 = 1883; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_4 = 4;
        final int EXPECTED_NUMBER_OF_AUTHORS_4 = 2;
        final String EXPECTED_EMAIL_OF_AUTHOR_4_1 = "rramsam@iu.edu";
        final String EXPECTED_EMAIL_OF_AUTHOR_4_2 = "fyffep";

        // For 6383f
        final long EXPECTED_LINE_COUNT_5 = 41;
        final long EXPECTED_FILE_SIZE_5 = 2018; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_5 = 5;
        final int EXPECTED_NUMBER_OF_AUTHORS_5 = 3;
        final String EXPECTED_EMAIL_OF_AUTHOR_5_1 = "rramsam@iu.edu";
        final String EXPECTED_EMAIL_OF_AUTHOR_5_2 = "fyffep";
        final String EXPECTED_EMAIL_OF_AUTHOR_5_3 = "ebehar@iu.edu";

        // For 2432b
        final long EXPECTED_LINE_COUNT_6 = 44;
        final long EXPECTED_FILE_SIZE_6 = 2208; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_6 = 6;
        final int EXPECTED_NUMBER_OF_AUTHORS_6 = 3;
        final String EXPECTED_EMAIL_OF_AUTHOR_6_1 = "rramsam@iu.edu";
        final String EXPECTED_EMAIL_OF_AUTHOR_6_2 = "fyffep";
        final String EXPECTED_EMAIL_OF_AUTHOR_6_3 = "ebehar@iu.edu";

        // For 8920b
        final long EXPECTED_LINE_COUNT_7 = 44;
        final long EXPECTED_FILE_SIZE_7 = 2423; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_7 = 7;
        final int EXPECTED_NUMBER_OF_AUTHORS_7 = 3;
        final String EXPECTED_EMAIL_OF_AUTHOR_7_1 = "rramsam@iu.edu";
        final String EXPECTED_EMAIL_OF_AUTHOR_7_2 = "fyffep";
        final String EXPECTED_EMAIL_OF_AUTHOR_7_3 = "ebehar@iu.edu";

        // For d8dab
        final long EXPECTED_LINE_COUNT_8 = 44;
        final long EXPECTED_FILE_SIZE_8 = 2462; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_8 = 8;
        final int EXPECTED_NUMBER_OF_AUTHORS_8 = 3;
        final String EXPECTED_EMAIL_OF_AUTHOR_8_1 = "rramsam@iu.edu";
        final String EXPECTED_EMAIL_OF_AUTHOR_8_2 = "fyffep";
        final String EXPECTED_EMAIL_OF_AUTHOR_8_3 = "ebehar@iu.edu";

        //Create test objects
        Codebase codebase = Codebase.getInstance();
        RepositoryAnalyzer repositoryAnalyzer = new RepositoryAnalyzer(PROJECT_ROOT);
        repositoryAnalyzer.attachBranchNameList(codebase);
        codebase.newBranchSelected("development");
        repositoryAnalyzer.attachCodebaseData(codebase); //method being tested

        // Verify the results
        FileObject fileObject = codebase.getFileObjectFromFilename(TEST_FILE_NAME);
        HeatObject heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_1);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_1, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_1, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_1, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_1, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_1_1));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_2);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_2, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_2, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_2, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_2, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_2_1));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_2_2));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_3);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_3, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_3, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_3, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_3, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_3_1));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_3_2));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_4);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_4, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_4, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_4, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_4, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_4_1));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_4_2));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_5);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_5, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_5, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_5, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_5, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_5_1));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_5_2));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_5_3));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_6);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_6, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_6, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_6, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_6, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_6_1));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_6_2));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_6_3));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_7);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_7, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_7, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_7, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_7, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_7_1));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_7_2));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_7_3));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_8);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_8, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_8, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_8, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_8, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_8_1));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_8_2));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_8_3));
    }


    // File History: https://github.iu.edu/P532-Fall2021/team3-project/commits/view-model-communication/src/main/java/intellij_extension/observer/CodeBaseObservable.java
    @Test
    void attachCodebaseData_FileHeatMetrics_CodeBaseObservable() throws IOException, GitAPIException {
        final String TEST_FILE_NAME = "CodeBaseObservable.java";

        final String TEST_HASH_1 = "723a3eae7a8524b06733e9568f1b2240a0537b0b"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/723a3eae7a8524b06733e9568f1b2240a0537b0b
        // Hidden merge commit (as in not present in GitHub history for the file but is caught by our code)
        final String TEST_HASH_2 = "0cdfe6bf92eddb57763f491b6db6edc6f56324f5"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/0cdfe6bf92eddb57763f491b6db6edc6f56324f5
        final String TEST_HASH_3 = "9db51280e8bffb279acb8b1f36abaa209bc6e9a2"; // https://github.iu.edu/P532-Fall2021/team3-project/commit/9db51280e8bffb279acb8b1f36abaa209bc6e9a2

        // For 723a3
        final long EXPECTED_LINE_COUNT_1 = 10;
        final long EXPECTED_FILE_SIZE_1 = 218; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_1 = 1;
        final int EXPECTED_NUMBER_OF_AUTHORS_1 = 1;
        final String EXPECTED_EMAIL_OF_AUTHOR_1_1 = "fyffep";

        // For 0cdfe
        final long EXPECTED_LINE_COUNT_2 = 10;
        final long EXPECTED_FILE_SIZE_2 = 218; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_2 = 2;
        final int EXPECTED_NUMBER_OF_AUTHORS_2 = 1;
        final String EXPECTED_EMAIL_OF_AUTHOR_2_1 = "fyffep";

        // For 9db51
        final long EXPECTED_LINE_COUNT_3 = 21;
        final long EXPECTED_FILE_SIZE_3 = 675; // TODO Bugged? If you DL file from GitHub it has a different size.
        final int EXPECTED_NUMBER_OF_COMMITS_3 = 3;
        final int EXPECTED_NUMBER_OF_AUTHORS_3 = 2;
        final String EXPECTED_EMAIL_OF_AUTHOR_3_1 = "fyffep";
        final String EXPECTED_EMAIL_OF_AUTHOR_3_2 = "ebehar@iu.edu";

        //Create test objects
        Codebase codebase = Codebase.getInstance();
        RepositoryAnalyzer repositoryAnalyzer = new RepositoryAnalyzer(PROJECT_ROOT);
        repositoryAnalyzer.attachBranchNameList(codebase);
        codebase.newBranchSelected("view-model-communication");
        repositoryAnalyzer.attachCodebaseData(codebase); //method being tested

        // Verify the results
        FileObject fileObject = codebase.getFileObjectFromFilename(TEST_FILE_NAME);
        HeatObject heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_1);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_1, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_1, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_1, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_1, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_1_1));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_2);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_2, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_2, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_2, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_2, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_2_1));

        // Verify the results
        heatObject = fileObject.getHeatObjectAtCommit(TEST_HASH_3);
        // Ensures we have the right object
        assertEquals(TEST_FILE_NAME, fileObject.getFilename());
        assertEquals(TEST_FILE_NAME, heatObject.getFilename());
        // Check data
        assertEquals(EXPECTED_LINE_COUNT_3, heatObject.getLineCount());
        assertEquals(EXPECTED_FILE_SIZE_3, heatObject.getFileSize());
        assertEquals(EXPECTED_NUMBER_OF_COMMITS_3, heatObject.getNumberOfCommits());
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_3, heatObject.getNumberOfAuthors());
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_3_1));
        assertTrue(fileObject.getUniqueAuthorEmails().contains(EXPECTED_EMAIL_OF_AUTHOR_3_2));
    }
}