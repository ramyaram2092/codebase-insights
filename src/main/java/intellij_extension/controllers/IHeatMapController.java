package intellij_extension.controllers;

import javafx.scene.Node;

/**
 * An interface intended to abstract controllers for the standard (grid)
 * heat map and the extended (char) heat map.
 *
 * NOTE: Many of these methods are now irrelevant as of moving the HeatMapPane view out of HeatMapController
 */
public interface IHeatMapController
{
    /**
     * Removes all view components from this controller's view.
     */
    //void clearHeatContainer();

    /**
     * Orders the file metrics calculators to analyze the current CodeBase.
     * After this is called, populateHeatMap() can be used to display the processed data.
     */
    void extractData();

    /**
     * Assuming this.recalculateHeat() has already been
     * called, updates the view with the heat data.
     * Does not clear existing data.
     */
    //void populateHeatMap(); //Moved to HeatMapPane

    /**
     * @return the outermost container for the controller's view
     */
    //Node getView();
}
