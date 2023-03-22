package intellij_extension;

import com.intellij.openapi.diagnostic.Logger;
import intellij_extension.models.redesign.Codebase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Constants {

    public static final Logger LOG = Logger.getInstance(Codebase.class); // TODO This should probably change to a more reasonable class?

    // Default Branches
    public static final String[] DEFAULT_BRANCHES = {"development", "master", "main"};

    public static final GroupingMode DEFAULT_GROUPING = GroupingMode.PACKAGES;
    public enum GroupingMode {
        COMMITS,
        PACKAGES
    }

    public static final FilterMode DEFAULT_FILTERING = FilterMode.X_FILES;
    public enum FilterMode {
        ALL_FILES,
        X_FILES
    }
    
    // Heat Metric List
    // Note that this affects HeatMapController::newHeatMetricSelected()
    public static final String OVERALL_TEXT = "Overall Heat";
    public static final String FILE_SIZE_TEXT = "File Size";
    public static final String NUMBER_OF_COMMITS_TEXT = "Number of Commits";
    public static final String NUMBER_OF_AUTHORS_TEXT = "Number of Authors";
    public static final ObservableList<String> HEAT_METRIC_OPTIONS = FXCollections.observableArrayList(
            OVERALL_TEXT,
            FILE_SIZE_TEXT,
            NUMBER_OF_COMMITS_TEXT,
            NUMBER_OF_AUTHORS_TEXT
    );
    // !!!
    //IMPORTANT: Make sure the HEAT_METRIC_OPTIONS and HeatMetricOptions correspond
    //because other code iterates through them both with this assumption.
    // !!!
    public enum HeatMetricOptions {
        OVERALL,
        FILE_SIZE,
        NUM_OF_COMMITS,
        NUM_OF_AUTHORS
    }

    // Heat
    public static final int HEAT_MIN = 1;
    public static final int HEAT_MAX = 10;

    //region UI Properties
    // Banners
    public static final int BANNER_MIN_HEIGHT = 40;
    // HeatMap FlowPane
    public static final int HEATMAP_VERTICAL_SPACING = 10;
    public static final int HEATMAP_HORIZONTAL_SPACING = 10;
    public static final Insets HEATMAP_PADDING = new Insets(10, 10, 10, 10);
    // Heat Colors
    public static final Color HEAT_MIN_COLOR = Color.BLUE;
    public static final Color HEAT_MAX_COLOR = Color.RED;
    // General Banner Constants
    public static final int ZERO_WIDTH = 0;
    public static final Pos BANNER_ALIGNMENT = Pos.CENTER_LEFT;
    public static final int BANNER_SPACING = 15;
    public static final Insets BANNER_INSETS = new Insets(0, 0, 0, 10);
    // Headers (in Banners)
    public static final String HEADER_FONT = "Veranda";
    public static final int HEADER_TEXT_SIZE = 24;
    public static final FontWeight HEADER_TEXT_FONT_WEIGHT = FontWeight.BOLD;
    // Text Font properties in Selected File Pane
    public static final String SF_TEXT_FONT = "Veranda";
    public static final int SF_TEXT_SIZE = 14;
    public static final FontWeight SF_TEXT_FONT_WEIGHT = FontWeight.BOLD;
    // File Commit History Commit List Columns
    public static final int FCH_DESCRIPTION_COLUMN_MAX_WIDTH = 300;
    // Commit Details Banner
    public static final Font TOOLTIP_FONT = new Font(16);
    //Commit Details Banner
    public static final Pos CD_BANNER_ALIGNMENT = Pos.TOP_LEFT;
    public static final int CD_BANNER_SPACING = 5;
    public static final double FILE_LIST_SIZE_MULTIPLIER = 0.70;
    // Commit Details File List
    public static final double CD_DETAILS_WRAPPING_PERCENTAGE = 0.9f;
    // Heat Map Flow Pane
    public static final int TOOLTIP_DURATION = 10;
    public static final int MAX_NUMBER_OF_FILTERING_FILES = 20;
    public static final double X_FILES_MAJOR_TICK = 5;
    public static final int X_FILES_MINOR_TICK = 5;
    public static final int DROP_SHADOW_DEPTH = 20;
    //endregion

    //region UI Strings
    public static final String BLANK = "";
    public static final String FCH_DEFAULT_HEADER_TEXT = "File's Commit History:";
    public static final String FCH_HEADER_SUFFIX_TEXT = "'s Commit History:";
    public static final String CD_HEADER_TEXT = "Commit Details:";
    public static final String CD_DESCRIPTION = "Description: ";
    public static final String CD_AUTHOR = "Author: ";
    public static final String CD_DATE = "Date: ";
    public static final String CD_HASH = "Hash: ";
    public static final String CD_ADDED_FILES = "Added Files:";
    public static final String CD_COPIED_FILES = "Copied Files:";
    public static final String CD_MODIFIED_FILES = "Modified Files:";
    public static final String CD_RENAMED_FILES = "Renamed Files:";
    public static final String CD_DELETED_FILES = "Deleted Files:";
    public static final String SF_TITLE_TEXT = "Selected File Details";
    public static final String SF_TEXT_FILENAME = "Filename: ";
    public static final String SF_TEXT_PACKAGE_NAME = "Package Name: ";
    public static final String SF_TEXT_AUTHORS = "Authors: ";
    public static final String SF_TEXT_FILE_SIZE = "File Size: ";
    public static final String SF_TEXT_NO_OF_COMMITS = "No of Commits: ";
    public static final String SF_TEXT_LINE_COUNT = "Line Count: ";
    public static final String BRANCH_COMBOBOX_TITLE = "Evaluating Branch:";
    public static final String HEAT_METRIC_COMBOBOX_TITLE = "Heat Data:";
    public static final String DASHBOARD_TEXT = "Dashboard";
    public static final String HEAT_GROUPING_TEXT = "Group by Package";
    public static final String COMMIT_GROUPING_TEXT = "Group by Commit Contiguity";
    public static final String SEPARATOR = "~";
    public static final String NO_FILES_EXIST = "No files exist";
    public static final String TOOLTIP_FORMAT = "%s%s\nHeat Level = %d\n%s\n\nGroup: %s";
    public static final String ALL_FILES_RADIO_BUTTON_DISPLAY_TEXT = "All Files";
    public static final String TOP_FILES_RADIO_BUTTON_DISPLAY_TEXT = "Top %s Hottest Files";
    public static final String TOP_FILE_WARNING = ""; //""THIS IS ONE OF THE TOP 20 HOTTEST FILES!!!\n\n";
    public static final Border BORDER = new Border(new BorderStroke(Color.BLUE,
            BorderStrokeStyle.SOLID, new CornerRadii(1), new BorderWidths(4),
            new Insets(-7, -7, -7, -7)));
    //endregion

    //Prevent instantiation
    private Constants() {
    }
}
