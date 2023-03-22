package intellij_extension.views;

import intellij_extension.Constants;
import intellij_extension.Constants.GroupingMode;
import intellij_extension.Constants.HeatMetricOptions;
import intellij_extension.controllers.HeatMapController;
import intellij_extension.models.redesign.*;
import intellij_extension.observer.CodeBaseObserver;
import intellij_extension.utility.HeatCalculationUtility;
import intellij_extension.views.interfaces.IContainerView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.*;


public class DashboardPane implements IContainerView, CodeBaseObserver {

    //region Vars
    // Basically this class' main nodes
    // ScrollPane -> AnchorPane -> VBox
    private final ScrollPane scrollPane;
    private final AnchorPane anchorPane;
    private final VBox vbox; //holds all the content for the dashboard
    //endregion

    //region Constructors
    public DashboardPane() {
        // Create ScrollPane
        scrollPane = new ScrollPane();
        // Set Properties
        scrollPane.setMinWidth(Constants.ZERO_WIDTH);

        // Create the AnchorPane inside the ScrollPane
        anchorPane = new AnchorPane();
        scrollPane.setContent(anchorPane);
        // Set Properties
        anchorPane.setMinWidth(Constants.ZERO_WIDTH);
        anchorPane.prefWidthProperty().bind(scrollPane.widthProperty());
        anchorPane.prefHeightProperty().bind(scrollPane.heightProperty());

        //Create VBox to hold everything else
        vbox = new VBox();
        vbox.setPadding(Constants.HEATMAP_PADDING);
        anchorPane.getChildren().add(vbox);

        // Register self as an observer of the model
        Codebase model = Codebase.getInstance();
        model.registerObserver(this);
    }
    //endregion

    //region IContainerView methods
    @Override
    public Node getNode() {
        return scrollPane;
    }

    @Override
    public void clearPane() {
        vbox.getChildren().clear();
    }
    //endregion

    //region CodeBaseObserver methods

    private void setupDashboard(Map<String, TreeSet<FileObject>> setOfFiles, String targetCommit)
    {
        Platform.runLater(() -> {
            System.out.println("Creating the dashboard...");
            clearPane();

            //Set a heading to explain the average scores below it
            final int HEADER_FONT_SIZE = 18;
            Label scoreTitleLabel = createLabel("Average Heat Scores Out of 10 From Each Metric", HEADER_FONT_SIZE);
            vbox.getChildren().add(scoreTitleLabel);

            //Add the average heat scores to the dashboard
            FlowPane scoreFlowPane = setupScoreFlowPane();
            vbox.getChildren().add(scoreFlowPane);

            //Set a heading to explain the hottest files below it
            Label hottestFilesTitleLabel = createLabel("The #1 Hottest Files From Each Metric", HEADER_FONT_SIZE);
            hottestFilesTitleLabel.setPadding(new Insets(36, 0, 0, 0));
            vbox.getChildren().add(hottestFilesTitleLabel);

            //Add hottest files
            addHottestFileHyperlinks();

            //Set a heading to explain the hottest files below it
            Label mostCommittedGroupsLabel = createLabel("Top 3 Most Strongly-Associated Groups of Files", HEADER_FONT_SIZE);
            mostCommittedGroupsLabel.setPadding(new Insets(36, 0, 0, 0));
            vbox.getChildren().add(mostCommittedGroupsLabel);

            //Add most-committed groups
            FlowPane mostCommittedGroupsFlowPane = setupMostCommittedGroupsFlowPane(setOfFiles, targetCommit);
            vbox.getChildren().add(mostCommittedGroupsFlowPane);

            System.out.println("Finished creating the dashboard.");
        });
    }

    /**
     * Creates a FlowPane that holds ScoreContainers for every heat metric.
     * Inside each ScoreContainer is the average score for that metric across all files
     * present at the latest commit and a caption to indicate the metric name.
     */
    private FlowPane setupScoreFlowPane()
    {
        FlowPane scoreFlowPane = createFlowPane();
        //anchorPane.getChildren().add(scoreFlowPane);

        //Get the list of all average scores
        DashboardModel dashboardModel = DashboardModel.getInstance();
        ArrayList<Double> averageScoreList = dashboardModel.getAverageHeatScoreList();
        assert averageScoreList.size() == HeatMetricOptions.values().length;

        //For every metric, add a ScoreContainer to represent the average of that metric
        int i = 0;
        for (String heatMetricText : Constants.HEAT_METRIC_OPTIONS)
        {
            //Get the score and heat metric name (caption)
            double averageScore = averageScoreList.get(i);
            String captionText = String.format("%s Score", heatMetricText); //displays text such as "Overall Score" in each label

            //Place above data into a ScoreContainer view
            ScoreContainer scoreContainerNumberOfAuthors = new ScoreContainer(averageScore, captionText);
            scoreFlowPane.getChildren().add(scoreContainerNumberOfAuthors.getNode());

            i++;
        }

        return scoreFlowPane;
    }



    /**
     * Creates a FlowPane that holds the top 3 most strongly associated commit contiguity groups.
     * Every file within each group is assigned a tooltip and color.
     * Does not add the FlowPane to the dashboard.
     * @param setOfFiles a TreeMap that is expected to be sorted in descending order by level of contiguity/association
     * @param targetCommit the commit hash to retrieve HeatObjects at since each file will be displayed with overall heat.
     */
    private FlowPane setupMostCommittedGroupsFlowPane(Map<String, TreeSet<FileObject>> setOfFiles, String targetCommit)
    {
        FlowPane flowPane = createFlowPane();

        //This method should only render the top N most-associated groups.
        //The loop below stops when i = MAX_GROUPS.
        final int MAX_GROUPS = 3;
        int i = 0;

        //For each group...
        for (Map.Entry<String, TreeSet<FileObject>> entry : setOfFiles.entrySet())
        {
            // Get package name
            String groupingKey = entry.getKey();
            // Create a container for it
            HeatFileContainer heatFileContainer = new HeatFileContainer(groupingKey);
            heatFileContainer.setStyle("-fx-background-color: #BBBBBB");

            // Add files to the package container
            for (FileObject fileObject : entry.getValue()) {
                // Get HeatObject/Level for targetCommit
                HeatObject heatObject = fileObject.getHeatObjectAtCommit(targetCommit);
                if (heatObject == null) continue;
                int heatLevel = heatObject.getHeatLevel();

                // Generate color
                Color fileHeatColor = HeatCalculationUtility.colorOfHeat(heatLevel);

                // Convert color to hex
                String colorString = fileHeatColor.toString();
                String colorFormat = String.format("-fx-background-color: #%s", colorString.substring(colorString.indexOf("x") + 1));

                // Add a pane (rectangle) package container
                HeatFileComponent heatFileComponent = new HeatFileComponent(fileObject, heatLevel, heatFileContainer);
                heatFileComponent.setStyle(colorFormat);

                // Set the tool tip for the component (has to happen after glow is added, so we know what a top 20 file is)
                String heatMetric = fileObject.getHeatMetricString(heatObject, HeatMetricOptions.OVERALL); //default to overall since a dashboard is an overview
                heatFileComponent.setHeatMetric(heatMetric);
                heatFileComponent.setFileToolTip(groupingKey);

                heatFileContainer.addNode(heatFileComponent);
            }

            // Only add if we actually made children for it.
            if (heatFileContainer.getChildren().size() > 0)
            {
                flowPane.getChildren().add(heatFileContainer);

                //Stop when there are enough groups
                i++;
                if (i >= MAX_GROUPS)
                    break;
            }
        }


        return flowPane;
    }


    /**
     * Creates a basic FlowPane and assigns it properties
     * that will allow it to reside on the dashboard.
     * The view must be added to another node (e.g. anchorPane)
     * later.
     */
    private FlowPane createFlowPane()
    {
        FlowPane flowPane = new FlowPane();
        // Set Properties
        flowPane.setMinWidth(Constants.ZERO_WIDTH);
        flowPane.prefWidthProperty().bind(scrollPane.widthProperty());
        flowPane.setVgap(Constants.HEATMAP_VERTICAL_SPACING);
        flowPane.setHgap(Constants.HEATMAP_HORIZONTAL_SPACING);
        flowPane.setPadding(Constants.HEATMAP_PADDING);

        return flowPane;
    }

    private Label createLabel(String text, int fontSize)
    {
        Label label = new Label(text);
        label.setFont(Font.font(Constants.HEADER_FONT, FontWeight.BOLD, fontSize));
        label.wrapTextProperty().set(true);

        return label;
    }



    /**
     * Places a Hyperlink for the #1 hottest file from each metric onto the dashboard
     */
    private void addHottestFileHyperlinks()
    {
        DashboardModel dashboardModel = DashboardModel.getInstance();
        ArrayList<String> namesOfHottestFilesList = dashboardModel.getNamesOfHottestFileList();
        assert namesOfHottestFilesList.size() == HeatMetricOptions.values().length;

        //For every metric, add a Hyperlink containing the heat metric name and the file name
        int i = 0;
        for (String heatMetricText : Constants.HEAT_METRIC_OPTIONS)
        {
            String fileName = namesOfHottestFilesList.get(i);

            //Add the name of the file as a Hyperlink to the Dashboard
            Hyperlink hyperlink = createFileHyperlink(heatMetricText, fileName);
            vbox.getChildren().add(hyperlink);

            i++;
        }
    }

    /**
     * Creates a Hyperlink so that, when clicking on a file name, it is as if that file
     * was selected in the heat map. Other views are populated with that file's data accordingly.
     */
    private Hyperlink createFileHyperlink(String heatMetricName, String fileName)
    {
        Hyperlink hyperlink = new Hyperlink(String.format("Hottest file from %s: %s", heatMetricName, fileName));
        hyperlink.setFont(Font.font(Constants.SF_TEXT_FONT, Constants.SF_TEXT_FONT_WEIGHT, Constants.SF_TEXT_SIZE));
        hyperlink.wrapTextProperty().set(true);
        //On clicking the hyperlink, populate the plugin panes with the file data
        if (!fileName.equals(Constants.NO_FILES_EXIST))
        {
            hyperlink.setOnAction(event -> HeatMapController.getInstance().heatMapComponentSelected(fileName));
        }

        return hyperlink;
    }


    @Override
    public void refreshHeatMap(TreeMap<String, TreeSet<FileObject>> setOfFiles, String targetCommit, GroupingMode currentGroupingMode, HeatMetricOptions currentHeatMetricOption) {
        setupDashboard(setOfFiles, targetCommit);
    }

    @Override
    public void branchListRequested(String activeBranch, Iterator<String> branchList) {
        // Nothing to do for this action
    }

    @Override
    public void newBranchSelected(TreeMap<String, TreeSet<FileObject>> setOfFiles, String targetCommit, GroupingMode groupingMode, HeatMetricOptions heatMetricOption) {
        setupDashboard(setOfFiles, targetCommit);
    }

    @Override
    public void fileSelected(FileObject selectedFile, Iterator<Commit> filesCommits) {
        // Nothing to do for this action
    }

    public void commitSelected(Commit commit) {
        // Nothing to do for this action
    }
    //endregion
}