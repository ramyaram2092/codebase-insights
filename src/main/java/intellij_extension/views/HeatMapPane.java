package intellij_extension.views;

import intellij_extension.Constants;
import intellij_extension.Constants.GroupingMode;
import intellij_extension.Constants.HeatMetricOptions;
import intellij_extension.controllers.HeatMapController;
import intellij_extension.models.redesign.Codebase;
import intellij_extension.models.redesign.Commit;
import intellij_extension.models.redesign.FileObject;
import intellij_extension.observer.CodeBaseObserver;
import intellij_extension.views.interfaces.IContainerView;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

public class HeatMapPane implements IContainerView, CodeBaseObserver {

    //region Vars
    private final ObservableList<String> activeBranchList = FXCollections.observableArrayList();
    // Basically this class' main node
    private final VBox parent;
    // Banner that holds heat metric, branch comboBoxes, and filtering controls
    private final VBox topHorizontalBanner;
    private ComboBox<String> branchComboBox;
    // Heat flow panes
    private HeatMapFlowPane heatMapTabContent;
    private HeatMapFlowPane commitTabContent;
    //endregion

    //region Constructors
    public HeatMapPane() {
        parent = new VBox();
        parent.setMinWidth(Constants.ZERO_WIDTH);

        // Create the top horizontal banner
        topHorizontalBanner = new VBox();
        topHorizontalBanner.setMinWidth(Constants.ZERO_WIDTH);
        topHorizontalBanner.prefWidthProperty().bind(parent.widthProperty());
        // Child layout properties
        topHorizontalBanner.setAlignment(Constants.BANNER_ALIGNMENT);
        parent.getChildren().add(topHorizontalBanner);

        createBranchComboBox();

        createTabs();

        Codebase model = Codebase.getInstance();
        model.registerObserver(this);
        HeatMapController.getInstance().branchListRequested();
    }

    private void createBranchComboBox() {
        // Create the HBox for the combo boxes
        HBox comboBoxContainer = new HBox();
        topHorizontalBanner.getChildren().add(comboBoxContainer);
        // Add constraints to width/height
        comboBoxContainer.setMinHeight(Constants.BANNER_MIN_HEIGHT);
        comboBoxContainer.setMinWidth(Constants.ZERO_WIDTH);
        comboBoxContainer.prefWidthProperty().bind(parent.widthProperty());
        // Child layout properties
        comboBoxContainer.setAlignment(Constants.BANNER_ALIGNMENT);
        comboBoxContainer.setSpacing(Constants.BANNER_SPACING);
        comboBoxContainer.setPadding(Constants.BANNER_INSETS);

        // Label for branch ComboBox
        Text branchTitle = new Text();
        comboBoxContainer.getChildren().add(branchTitle);
        branchTitle.setText(Constants.BRANCH_COMBOBOX_TITLE);

        // Create branch comboBox
        branchComboBox = new ComboBox<>();
        comboBoxContainer.getChildren().add(branchComboBox);
        // Set up observable list
        branchComboBox.setItems(activeBranchList);
        // Set up the select action
        branchComboBox.setOnAction(this::branchSelectedAction);
    }

    private void createTabs() {
        // Tabbed view
        TabPane tabPane = new TabPane();
        parent.getChildren().add(tabPane);
        tabPane.setMinHeight(Constants.ZERO_WIDTH);
        tabPane.prefHeightProperty().bind(parent.heightProperty());
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Set  up tabs
        // Dashboard tab
        Tab tab = new Tab();
        tab.setText(Constants.DASHBOARD_TEXT);
        DashboardPane dashboardContent = new DashboardPane();
        tab.setContent(dashboardContent.getNode());
        tabPane.getTabs().add(tab);

        // Package tab
        tab = new Tab();
        tab.setText(Constants.HEAT_GROUPING_TEXT);
        heatMapTabContent = new HeatMapFlowPane();
        heatMapTabContent.setGroupingMode(GroupingMode.PACKAGES);
        heatMapTabContent.setFilteringModeDefaults();
        tab.setContent(heatMapTabContent.getNode());
        tabPane.getTabs().add(tab);

        // Commit tab
        tab = new Tab();
        tab.setText(Constants.COMMIT_GROUPING_TEXT);
        commitTabContent = new HeatMapFlowPane();
        commitTabContent.setGroupingMode(GroupingMode.COMMITS);
        commitTabContent.setFilteringModeDefaults();
        tab.setContent(commitTabContent.getNode());
        tabPane.getTabs().add(tab);

        tabPane.getSelectionModel().selectedItemProperty().addListener(this::tabSelectedAction);
        updateUIControlsBasedOnTab(Constants.DASHBOARD_TEXT);
    }
    //endregion

    //region UI actions
    private void branchSelectedAction(ActionEvent event) {
        String selectedValue = branchComboBox.getValue();

        HeatMapController.getInstance().newBranchSelected(selectedValue);
    }

    private void tabSelectedAction(Observable observable, Tab oldTab, Tab newTab) {
        updateUIControlsBasedOnTab(newTab.getText());

        HeatMapController.getInstance().heatMapGroupingChanged(newTab.getText());
    }

    private void updateUIControlsBasedOnTab(String tab) {
        switch (tab) {
            case Constants.COMMIT_GROUPING_TEXT:
            case Constants.HEAT_GROUPING_TEXT:
                if (!topHorizontalBanner.isVisible()) {
                    parent.getChildren().add(0, topHorizontalBanner); // Make first child so its at the top
                }
                topHorizontalBanner.setVisible(true);
                break;
            case Constants.DASHBOARD_TEXT:
                parent.getChildren().remove(topHorizontalBanner);
                topHorizontalBanner.setVisible(false);
                break;
            default:
                // Unknown state/tab just leaving it as-is...
                break;
        }
        parent.layout();
    }
    //endregion

    //region IContainerView methods
    @Override
    public Node getNode() {
        return parent;
    }

    @Override
    public void clearPane() {

    }
    //endregion

    //region CodeBaseObserver methods
    @Override
    public void refreshHeatMap(TreeMap<String, TreeSet<FileObject>> setOfFiles, String targetCommit, GroupingMode groupingMode, HeatMetricOptions heatMetricOption) {
        // Nothing to do for this action
    }

    @Override
    public void branchListRequested(String activeBranch, @NotNull Iterator<String> branchList) {
        activeBranchList.clear();

        while (branchList.hasNext()) {
            String branchName = branchList.next();
            activeBranchList.add(branchName);
        }

        branchComboBox.getSelectionModel().select(activeBranch);
    }

    @Override
    public void newBranchSelected(TreeMap<String, TreeSet<FileObject>> setOfFiles, String targetCommit, GroupingMode groupingMode, HeatMetricOptions heatMetricOption) {
        // Nothing to do for this action
    }

    @Override
    public void fileSelected(FileObject selectedFile, Iterator<Commit> filesCommits) {
        // Nothing to do for this action

    }

    @Override
    public void commitSelected(Commit commit) {
        // Nothing to do for this action
    }
    //endregion
}
