How to add a new file metric:
    1. Add a new variable to HeatObject to represent your metric.
    2. Add the heat metric in HEAT_METRIC_OPTIONS and HeatMetricOptions inside Constants.java
    3. Add a new switch case inside inside HeatCalculationUtility.assignHeatLevels(...)