package intellij_extension.utility;

import intellij_extension.Constants;
import intellij_extension.models.redesign.Codebase;
import intellij_extension.models.redesign.DashboardModel;
import intellij_extension.models.redesign.FileObject;
import intellij_extension.models.redesign.HeatObject;

import java.util.ArrayList;

public class DashboardCalculationUtility
{
    private DashboardCalculationUtility() {
        //This is a utility class
    }

    /**
     * Returns the average heat level for latest commit in the Codebase.
     * Rounds the output value to the nearest 10th place.
     */
    public static double averageHeatLevel(Codebase codebase, Constants.HeatMetricOptions heatMetricOption)
    {
        //Compute total amount of heat across all files at the latest commit
        long heatSum = 0;
        int numberOfFiles = 0;
        String latestCommitHash = codebase.getLatestCommitHash();
        for (FileObject fileObject : codebase.getActiveFileObjects())
        {
            HeatObject heatObject = fileObject.getHeatObjectAtCommit(latestCommitHash);

            if (heatObject == null) continue; //file was not a part of the commit

            heatSum += heatObject.getHeatLevel();
            numberOfFiles++;
        }

        //Compute average heat
        double heatAverage = (double)(heatSum) / numberOfFiles;
        heatAverage = Math.round(heatAverage * 10) / 10.0; //round to nearest 10th decimal place
        return heatAverage;
    }

    public static void assignDashboardData()
    {
        //Compute average heat scores and the hottest files for each metric
        Codebase codebase = Codebase.getInstance();
        ArrayList<Double> averageHeatScoreList = new ArrayList<>();
        ArrayList<String> namesOfHottestFileList = new ArrayList<>();
        for (Constants.HeatMetricOptions heatMetricOption : Constants.HeatMetricOptions.values())
        {
            //Compute every file's heat
            HeatCalculationUtility.assignHeatLevels(codebase, heatMetricOption);

            //Determine average heat for the metric
            double average = averageHeatLevel(codebase, heatMetricOption);
            averageHeatScoreList.add(average);

            //Determine hottest file for the metric
            namesOfHottestFileList.add(findHottestFile(codebase));
        }

        //Place the data into the DashboardModel
        DashboardModel dashboardModel = DashboardModel.getInstance();
        dashboardModel.setAverageHeatScoreList(averageHeatScoreList);
        dashboardModel.setNamesOfHottestFileList(namesOfHottestFileList);
    }

    /**
     * Assuming assignHeatLevels() has already been called, returns
     * the name of the file with the most heat. Many files can have the highest heat, however.
     * If there are no files in the Codebase, returns "No files exist".
     */
    private static String findHottestFile(Codebase codebase)
    {
        String latestCommitHash = codebase.getLatestCommitHash();

        //Determine max heat
        int highestHeat = Integer.MIN_VALUE;
        String nameOfHottestFile = Constants.NO_FILES_EXIST;
        for (FileObject fileObject : codebase.getActiveFileObjects())
        {
            HeatObject heatObject = fileObject.getHeatObjectAtCommit(latestCommitHash);

            if (heatObject == null) continue; //file was not a part of the commit

            if (heatObject.getHeatLevel() > highestHeat) {
                highestHeat = heatObject.getHeatLevel();
                nameOfHottestFile = fileObject.getFilename();
            }
        }

        return nameOfHottestFile;
    }
}
