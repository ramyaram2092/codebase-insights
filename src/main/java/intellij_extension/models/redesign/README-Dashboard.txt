The dashboard classes are designed so that they do not have to change when a new heat metric is added.
They instead iterate over HEAT_METRIC_OPTIONS and/or HeatMetricOptions inside Constants.java.

Structure of Data:
    All Dashboard data is contained in the DashboardModel, which has two lists:
        - averageHeatScoreList -- contains the average score across all files present at the latest commit for each metric
        - namesOfHottestFileList -- contains the name of the #1 hottest file from each metric
    The indices in these lists correspond to the heat metrics in HEAT_METRIC_OPTIONS and HeatMetricOptions inside Constants.java

When this data is populated:
    1. HeatMapController starts up through the preloading phase, then collects Codebase data
    2. Then, it calls DashboardCalculationUtility.assignDashboardData(). This populates the aforementioned DashboardModel.
    3. Finally, when preloading is complete, the DashboardPane displays all data from the DashboardModel because HeatMapController triggers the Observer Pattern.