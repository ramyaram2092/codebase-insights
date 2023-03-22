package intellij_extension.controllers;

import com.intellij.openapi.application.PreloadingActivity;
import com.intellij.openapi.progress.ProgressIndicator;
import intellij_extension.Constants;
import intellij_extension.Constants.GroupingMode;
import intellij_extension.Constants.HeatMetricOptions;
import intellij_extension.models.redesign.Codebase;
import intellij_extension.models.redesign.DashboardModel;
import intellij_extension.utility.DashboardCalculationUtility;
import intellij_extension.utility.HeatCalculationUtility;
import intellij_extension.utility.RepositoryAnalyzer;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import static intellij_extension.Constants.HEAT_METRIC_OPTIONS;

public class HeatMapController extends PreloadingActivity implements IHeatMapController {

    //region Vars
    private static HeatMapController instance;
    private Codebase codeBase;
    //endregion

    //region Constructor
    private HeatMapController() {
        this.codeBase = Codebase.getInstance();
    }

    public static HeatMapController getInstance() {
        if (instance == null) {
            instance = new HeatMapController();
        }

        return instance;
    }
    //endregion

    //region Start up
    @Override
    public void preload(@NotNull ProgressIndicator indicator) {
        System.out.println("Preloading HeatMapController..."); //logger fails here
        //Analyze Codebase
        extractData();
        DashboardCalculationUtility.assignDashboardData();
        //Trigger view update
        codeBase.heatMapGroupingChanged(GroupingMode.COMMITS); //ensures that our dashboard can render the top most-associated commit groups
        System.out.println("Finished preloading HeatMapController.");
    }

    public void extractData() {
        //Obtain file metrics by analyzing the code base
        try {
            //Calculate file sizes for every commit
            // TODO, is this a static class or not?
            RepositoryAnalyzer repositoryAnalyzer = new RepositoryAnalyzer();
            RepositoryAnalyzer.attachBranchNameList(codeBase);
            codeBase.selectDefaultBranch();
            RepositoryAnalyzer.attachCodebaseData(codeBase);

            //Now the Codebase contains all the data it needs
        } catch (IOException | GitAPIException e) {
            System.out.println("There was an error: ");
            e.printStackTrace();
            System.out.println(e); //logger fails here
            System.out.println(e.getMessage());
        }
        System.out.println("Heat calculations complete. Number of files: " + codeBase.getActiveFileObjects().size());
    }
    //endregion

    //region View-to-Model communication bridge
    public void heatMapComponentSelected(String id) {
        codeBase.heatMapComponentSelected(id);
    }

    // A way for FileHistoryDetails to get the branch list.
    public void branchListRequested() {
        codeBase.branchListRequested();
    }

    public void newBranchSelected(String branchName) {
        codeBase.newBranchSelected(branchName);
    }

    public void newHeatMetricSelected(String heatMetricOption) {
        HeatMetricOptions newOption;
        if (heatMetricOption.equals(HEAT_METRIC_OPTIONS.get(0))) {
            newOption = HeatMetricOptions.OVERALL;
        } else if (heatMetricOption.equals(HEAT_METRIC_OPTIONS.get(1))) {
            newOption = HeatMetricOptions.FILE_SIZE;
        } else if (heatMetricOption.equals(HEAT_METRIC_OPTIONS.get(2))) {
            newOption = HeatMetricOptions.NUM_OF_COMMITS;
        } else {
            newOption = HeatMetricOptions.NUM_OF_AUTHORS;
        }

        codeBase.newHeatMetricSelected(newOption);
    }

    public void commitSelected(String commitHash) {
        codeBase.commitSelected(commitHash);
    }

    public void changeHeatMapToCommit(String commitHash) {
        codeBase.changeHeatMapToCommit(commitHash);
    }

    public void heatMapGroupingChanged(String newTab) {
        GroupingMode newGroupingMode;
        if (newTab.equals(Constants.COMMIT_GROUPING_TEXT)) {
            newGroupingMode = GroupingMode.COMMITS;
        } else {
            newGroupingMode = GroupingMode.PACKAGES;
        }

        codeBase.heatMapGroupingChanged(newGroupingMode);
    }
    //endregion
}
