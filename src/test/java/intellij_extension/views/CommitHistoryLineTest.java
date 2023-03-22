package intellij_extension.views;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import testdata.TestData;

public class CommitHistoryLineTest {

    private static CommitInfoRow line;

    @BeforeAll
    public static void setUpBeforeClass() {
        line = new CommitInfoRow(TestData.CHTD_ROW_NUMBER,
                TestData.CHTD_DESCRIPTION, TestData.CHTD_AUTHOR,
                TestData.CHTD_DATE, TestData.CHTD_HASH);
    }

    @AfterAll
    public static void setUpAfterClass() {
    }

    @Test
    public void Getter_RowNumberSetCorrectly() {
        Assert.assertEquals(line.getRowNumber().getValue(), TestData.CHTD_ROW_NUMBER);
    }

    @Test
    public void Getter_DescriptionSetCorrectly() {
        Assert.assertEquals(line.getCommitDescription().getValue(), TestData.CHTD_DESCRIPTION);
    }

    @Test
    public void Getter_AuthorSetCorrectly() {
        Assert.assertEquals(line.getCommitAuthor().getValue(), TestData.CHTD_AUTHOR);
    }

    @Test
    public void Getter_DateSetCorrectly() {
        Assert.assertEquals(line.getCommitDate().getValue(), TestData.CHTD_DATE);
    }

    @Test
    public void Getter_HashSetCorrectly() {
        Assert.assertEquals(line.getCommitHash().getValue(), TestData.CHTD_HASH);
    }
}