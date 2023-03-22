package intellij_extension.models.redesign;

import java.util.ArrayList;

public class DashboardModel
{
    private static DashboardModel instance; // Singleton

    //These lists correspond to Constants.HEAT_METRIC_OPTIONS and Constants.HeatMetricOptions to provide corresponding
    // averages or file names according to each heat metric.
    private ArrayList<Double> averageHeatScoreList;
    private ArrayList<String> namesOfHottestFileList;

    private DashboardModel() {
        //Empty constructor
    }

    public static synchronized DashboardModel getInstance() {
        if (instance == null) {
            instance = new DashboardModel();
            System.out.println("DashboardModel has been created"); //logger doesn't work here
        }
        return instance;
    }

    public static void setInstance(DashboardModel instance) {
        DashboardModel.instance = instance;
    }

    public ArrayList<Double> getAverageHeatScoreList() {
        return averageHeatScoreList;
    }

    public void setAverageHeatScoreList(ArrayList<Double> averageHeatScoreList) {
        this.averageHeatScoreList = averageHeatScoreList;
    }

    public ArrayList<String> getNamesOfHottestFileList() {
        return namesOfHottestFileList;
    }

    public void setNamesOfHottestFileList(ArrayList<String> namesOfHottestFileList) {
        this.namesOfHottestFileList = namesOfHottestFileList;
    }
}
