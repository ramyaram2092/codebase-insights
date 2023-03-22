package intellij_extension.utility;

import intellij_extension.Constants;
import intellij_extension.models.redesign.Codebase;
import intellij_extension.models.redesign.FileObject;
import javafx.scene.paint.Color;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static intellij_extension.utility.RepositoryAnalyzerTest.PROJECT_ROOT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HeatCalculationUtilityTest
{
    @Test
    void calculateColorOfHeat()
    {
        final int TEST_HEAT_LEVEL = 1;
        Color colorString = HeatCalculationUtility.colorOfHeat(TEST_HEAT_LEVEL); //method under test
        assertEquals("0xff0000ff", colorString.toString());
    }


    //TODO
    /*@Test
    void assignHeatLevelsFileSize_HeatMapPane() throws GitAPIException, IOException
    {
        //Create test objects
        Codebase codebase = Codebase.getInstance();
        RepositoryAnalyzer repositoryAnalyzer = new RepositoryAnalyzer(PROJECT_ROOT);
        repositoryAnalyzer.attachBranchNameList(codebase);
        codebase.newBranchSelected("development");
        repositoryAnalyzer.attachCodebaseData(codebase);

        HeatCalculationUtility.assignHeatLevels(codebase, Constants.HeatMetricOptions.FILE_SIZE); //method being tested

        //Verify heat at certain commits
        FileObject fileUnderTest = codebase.createOrGetFileObjectFromPath("HeatMapPane.java");
        assertEquals(1, fileUnderTest.getHeatObjectAtCommit("e00ab236f6648e2ff5065cddea7299e1690d30ca").getHeatLevel()); //805 chars
        assertEquals(5, fileUnderTest.getHeatObjectAtCommit("53f50f2d788b039575ad9fd22c5a983daf8b633a").getHeatLevel()); //1283 chars but the file went unchanged for many commits
        assertEquals(7, fileUnderTest.getHeatObjectAtCommit("53cc875e0fbccf8bb5470b978e39064b0c236339").getHeatLevel()); //863 chars
        assertEquals(8, fileUnderTest.getHeatObjectAtCommit("1c03e6821c31743997976a3cb1b53105ea46770e").getHeatLevel()); //1561 chars
        assertEquals(7, fileUnderTest.getHeatObjectAtCommit("1e589e61ef75003b1df88bdb738f9d9f4a4f5f8a").getHeatLevel()); //749 chars
        assertEquals(9, fileUnderTest.getHeatObjectAtCommit("723a3eae7a8524b06733e9568f1b2240a0537b0b").getHeatLevel()); //3459 chars
        assertEquals(8, fileUnderTest.getHeatObjectAtCommit("fdad0bc82ad51ecb67371e0dfaf4d6de987df691").getHeatLevel()); //749 chars (seems to have been a different branch, maybe we want to exclude this? FIXME)
        assertEquals(10, fileUnderTest.getHeatObjectAtCommit("ea38dc864361ce5772c5eff60064d5c01dcfe554").getHeatLevel()); //3491 chars
    }*/
}
