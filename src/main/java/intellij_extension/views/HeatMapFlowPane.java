package intellij_extension.views;

import intellij_extension.Constants;
import intellij_extension.controllers.HeatMapController;
import intellij_extension.models.redesign.Codebase;
import intellij_extension.models.redesign.Commit;
import intellij_extension.models.redesign.FileObject;
import intellij_extension.models.redesign.HeatObject;
import intellij_extension.observer.CodeBaseObserver;
import intellij_extension.utility.HeatCalculationUtility;
import intellij_extension.views.interfaces.IContainerView;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.*;

import static intellij_extension.Constants.*;

/**
 * A view that holds rectangles to represent files for a particular commit.
 * Each rectangle is colored based on the heat values assigned to its corresponding file.
 */
public class HeatMapFlowPane implements IContainerView, CodeBaseObserver {

    //region Vars
    // Basically this class' main nodes
    // ScrollPane -> AnchorPane -> FlowPane
    private final VBox parent;
    private final ScrollPane scrollPane;
    private final AnchorPane anchorPane;
    private final FlowPane flowPane;
    // Banner that holds heat metric, branch comboBoxes, and filtering controls
    private final VBox topHorizontalBanner;
    private final HBox controlsContainer;
    private FilterMode filteringMode;
    private int filterMax = Constants.MAX_NUMBER_OF_FILTERING_FILES;
    private GroupingMode groupingMode;
    private ComboBox<String> heatMetricComboBox;
    private RadioButton allFilesButton;
    private RadioButton topFilesButton;
    private Slider topFilesSlider;
    private HeatFileComponent currentSelectedFile;

    // For filtering
    private PriorityQueue<HeatFileComponent> topHeatFileComponents;
    //endregion

    //region Constructors
    public HeatMapFlowPane() {
        parent = new VBox();
        parent.setMinWidth(Constants.ZERO_WIDTH);
        parent.setMinHeight(Constants.ZERO_WIDTH);

        // Create the top horizontal banner
        topHorizontalBanner = new VBox();
        topHorizontalBanner.setMinWidth(Constants.ZERO_WIDTH);
        topHorizontalBanner.prefWidthProperty().bind(parent.widthProperty());
        // Child layout properties
        topHorizontalBanner.setAlignment(Constants.BANNER_ALIGNMENT);
        parent.getChildren().add(topHorizontalBanner);

        // Create ScrollPane
        scrollPane = new ScrollPane();
        // Set Properties
        scrollPane.setMinWidth(Constants.ZERO_WIDTH);
        scrollPane.prefWidthProperty().bind(parent.widthProperty());
        scrollPane.prefHeightProperty().bind(parent.heightProperty());
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        parent.getChildren().add(scrollPane);

        // Create the AnchorPane inside the ScrollPane
        anchorPane = new AnchorPane();
        scrollPane.setContent(anchorPane);
        // Set Properties
        anchorPane.setMinWidth(Constants.ZERO_WIDTH);
        anchorPane.prefWidthProperty().bind(scrollPane.widthProperty());
        anchorPane.prefHeightProperty().bind(scrollPane.heightProperty());

        // Create HeatMapFlowPane inside the AnchorPane
        flowPane = new FlowPane();
        anchorPane.getChildren().add(flowPane);
        // Set Properties
        flowPane.setMinWidth(Constants.ZERO_WIDTH);
        flowPane.prefWidthProperty().bind(scrollPane.widthProperty());
        flowPane.setVgap(Constants.HEATMAP_VERTICAL_SPACING);
        flowPane.setHgap(Constants.HEATMAP_HORIZONTAL_SPACING);
        flowPane.setPadding(Constants.HEATMAP_PADDING);

        // Create the controls!
        controlsContainer = new HBox();
        createHeatMetricComboBox();
        createTopAllControls();

        // Register self as an observer of the model
        Codebase model = Codebase.getInstance();
        model.registerObserver(this);
    }
    //endregion

    //region UI Creation
    private void createHeatMetricComboBox() {
        // Create the HBox for the combo boxes
        topHorizontalBanner.getChildren().add(controlsContainer);
        // Add constraints to width/height
        controlsContainer.setMinHeight(Constants.BANNER_MIN_HEIGHT);
        controlsContainer.setMinWidth(Constants.ZERO_WIDTH);
        controlsContainer.prefWidthProperty().bind(topHorizontalBanner.widthProperty());
        // Child layout properties
        controlsContainer.setAlignment(Constants.BANNER_ALIGNMENT);
        controlsContainer.setSpacing(Constants.BANNER_SPACING);
        controlsContainer.setPadding(Constants.BANNER_INSETS);

        // Label for heatMetric ComboBox
        Text heatMetricTitle = new Text();
        controlsContainer.getChildren().add(heatMetricTitle);
        heatMetricTitle.setText(Constants.HEAT_METRIC_COMBOBOX_TITLE);

        // Create heatMetric comboBox
        heatMetricComboBox = new ComboBox<>();
        controlsContainer.getChildren().add(heatMetricComboBox);
        // Set up observable list
        heatMetricComboBox.setItems(Constants.HEAT_METRIC_OPTIONS);
        heatMetricComboBox.getSelectionModel().select(0);
        // Set up the select action
        heatMetricComboBox.setOnAction(this::heatMetricOptionSelectedAction);
    }

    private void createTopAllControls() {
        // Top-Vs-All Files controls
        // Radio button group
        ToggleGroup fileFilteringGroup = new ToggleGroup();

        // Radio buttons
        allFilesButton = new RadioButton(Constants.ALL_FILES_RADIO_BUTTON_DISPLAY_TEXT);
        allFilesButton.setToggleGroup(fileFilteringGroup);
        allFilesButton.setSelected(false);
        allFilesButton.selectedProperty().addListener(this::allRadioButtonClicked);
        controlsContainer.getChildren().add(allFilesButton);

        topFilesButton = new RadioButton(String.format(Constants.TOP_FILES_RADIO_BUTTON_DISPLAY_TEXT, Constants.MAX_NUMBER_OF_FILTERING_FILES));
        topFilesButton.setToggleGroup(fileFilteringGroup);
        topFilesButton.setSelected(true);
        topFilesButton.selectedProperty().addListener(this::topRadioButtonClicked);
        controlsContainer.getChildren().add(topFilesButton);

        // Slider to control # of top commits
        topFilesSlider = new Slider();
        topFilesSlider.setMin(0);
        topFilesSlider.setMax(Constants.MAX_NUMBER_OF_FILTERING_FILES);
        topFilesSlider.setValue(Constants.MAX_NUMBER_OF_FILTERING_FILES);
        topFilesSlider.setShowTickLabels(true);
        topFilesSlider.setSnapToTicks(true);
        topFilesSlider.setSnapToPixel(true);
        topFilesSlider.setMajorTickUnit(Constants.X_FILES_MAJOR_TICK);
        topFilesSlider.setMinorTickCount(Constants.X_FILES_MINOR_TICK);
        topFilesSlider.setOnMouseReleased(this::sliderValueChangeConfirmed);
        topFilesSlider.valueProperty().addListener(this::sliderValueChanging);
        controlsContainer.getChildren().add(topFilesSlider);
    }
    //endregion

    //region UI Actions
    private void heatMetricOptionSelectedAction(ActionEvent event) {
        String selectedValue = heatMetricComboBox.getValue();

        HeatMapController.getInstance().newHeatMetricSelected(selectedValue);
    }

    private void allRadioButtonClicked(javafx.beans.Observable observable, boolean wasPreviouslySelected, boolean isNowSelected) {
        System.out.printf("ARB isNowSelected %s%n", isNowSelected);
        if (isNowSelected) {
            setFilteringMode(Constants.FilterMode.ALL_FILES, -1);
        }
    }

    private void topRadioButtonClicked(Observable observable, boolean wasPreviouslySelected, boolean isNowSelected) {
        System.out.printf("TRD isNowSelected %s%n", isNowSelected);
        topFilesSlider.setVisible(isNowSelected);
        if (isNowSelected) {
            setFilteringMode(Constants.FilterMode.X_FILES, (int) topFilesSlider.getValue());
        }
    }

    private void sliderValueChangeConfirmed(MouseEvent mouseEvent) {
        // We can only use this slider when top radio is selected
        // So we know we are in X_FILES mode
        if (topFilesSlider.getValue() == 0) {
            topFilesSlider.setValue(1);
        }
        topFilesButton.setText(String.format("Top %s Hottest Files", (int) topFilesSlider.getValue()));
        setFilteringMode(Constants.FilterMode.X_FILES, (int) topFilesSlider.getValue());
    }

    private void sliderValueChanging(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
        topFilesButton.setText(String.format("Top %s Hottest Files", (int) topFilesSlider.getValue()));
    }
    //endregion

    public void setGroupingMode(GroupingMode groupingMode) {
        this.groupingMode = groupingMode;
    }

    public void setFilteringModeDefaults() {
        this.filteringMode = Constants.DEFAULT_FILTERING;
        this.filterMax = Constants.MAX_NUMBER_OF_FILTERING_FILES;
    }

    public void setFilteringMode(FilterMode filterMode, int numberOfFiles) {
        this.filteringMode = filterMode;
        this.filterMax = numberOfFiles;
        // Radio buttons are weird and this is needed to guard against them
        if (topHeatFileComponents != null) {
            filterHeatMap();
        }
    }

    private void filterHeatMap() {
        // Clear the flowPane because we only want the top X files in it, but currently it has all files
        flowPane.getChildren().clear();

        // Iterator over topHeatFiles collected from refreshHeatMap(...)
        // (not sorted/organized by package anymore, we lost that)
        int index = 0;
        for (HeatFileComponent heatFile : topHeatFileComponents) {
            // Get the heatFile's container
            HeatFileContainer currentContainer = heatFile.getContainer();

            // Check if already added
            if (!flowPane.getChildren().contains(currentContainer)) {
                // Clear container
                currentContainer.getChildren().clear();
                // Add container to flowPane
                flowPane.getChildren().add(heatFile.getContainer());
            }

            // Re-add the heatFile back to the parent
            currentContainer.getChildren().add(heatFile);

            // Adds the glowing behaviour to the file component iff the file is in the top 20
            if (index < Constants.MAX_NUMBER_OF_FILTERING_FILES) {
                heatFile.setHottestFileEffect();
            }

            // Set the tool tip for the component (has to happen after glow is added, so we know what a top 20 file is)
            heatFile.setFileToolTip(currentContainer.getTitle());

            // Break if we reached the max
            if (index + 1 == filterMax)
                break;

            // Continue otherwise
            index++;
        }
//        System.out.printf("Index: %s%n", index);
        flowPane.layout();
    }

    //region IContainerView methods
    @Override
    public Node getNode() {
        return parent;
    }

    @Override
    public void clearPane() {
        flowPane.getChildren().clear();
    }
    //endregion

    //region CodeBaseObserver methods

    /**
     * Clears the pane, then displays all files present in the target commit.
     * Each file is represented by a rectangular pane.
     *
     * @param setOfFiles a sorted grouping of files in a Codebase. Each HeatObject
     *                   inside the FileObject must already have its heatLevel according to the current
     *                   heat metric(s).
     */
    @Override
    public void refreshHeatMap(TreeMap<String, TreeSet<FileObject>> setOfFiles, String targetCommit, GroupingMode groupingMode, HeatMetricOptions heatMetricOption) {
        // If not our grouping mode, then don't do anything
        if (!this.groupingMode.equals(groupingMode)) return;

        // Set up for X top files
        topHeatFileComponents = new PriorityQueue<>(Comparator.comparing(HeatFileComponent::getFileHeatLevel).reversed());

        Platform.runLater(() -> {
            flowPane.getChildren().clear();

            for (Map.Entry<String, TreeSet<FileObject>> entry : setOfFiles.entrySet()) {
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
                    String heatMetric = fileObject.getHeatMetricString(heatObject, heatMetricOption);
                    heatFileComponent.setHeatMetric(heatMetric);

                    heatFileContainer.addNode(heatFileComponent);
                    topHeatFileComponents.add(heatFileComponent);
                    heatFileComponent.setOnMouseClicked(event -> {
                        if (currentSelectedFile != null) currentSelectedFile.setBorder(Border.EMPTY);
                        currentSelectedFile = heatFileComponent;
                        heatFileComponent.setBorder(BORDER);
                        HeatMapController.getInstance().heatMapComponentSelected(fileObject.getPath().toString());
                    });
                }

                // Only add if we actually made children for it.
                if (heatFileContainer.getChildren().size() > 0) {
                    flowPane.getChildren().add(heatFileContainer);
                }
            }

            filterHeatMap();

            System.out.println("Finished adding panes to the heat map.");
        });
    }

    @Override
    public void branchListRequested(String activeBranch, Iterator<String> branchList) {
        // Nothing to do for this action
    }

    @Override
    public void newBranchSelected(TreeMap<String, TreeSet<FileObject>> setOfFiles, String targetCommit, GroupingMode groupingMode, HeatMetricOptions heatMetricOption) {
        refreshHeatMap(setOfFiles, targetCommit, groupingMode, heatMetricOption);
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