package intellij_extension.views;

import intellij_extension.Constants;
import intellij_extension.views.interfaces.IContainerView;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;

public class HeatMapSplitPane implements IContainerView {

    //region Vars
    // Basically this class' main node
    private SplitPane parent;

    // Top is banner plus tab view.
    private HeatMapPane heatMapPane;

    // Bottom is for a selected file
    private SelectedFileTitledPane selectedFileView;
    //endregion

    //region Constructors
    private HeatMapSplitPane() {
    }

    public HeatMapSplitPane(Scene scene) {
        parent = new SplitPane();
        parent.setOrientation(Orientation.VERTICAL);
        parent.setMinWidth(Constants.ZERO_WIDTH);
        parent.prefHeightProperty().bind(scene.heightProperty());

        // Top Half: heatMapPane (holds banner and tabbed view)
        heatMapPane = new HeatMapPane();
        parent.getItems().add(heatMapPane.getNode());

        // Bottom half: SelectedFileTitledPane
        selectedFileView = new SelectedFileTitledPane();
        parent.getItems().add(selectedFileView.getNode());
    }
    //endregion

    //region IContainerView methods
    @Override
    public Node getNode() {
        return parent;
    }

    @Override
    public void clearPane() {
        throw new UnsupportedOperationException("A " + this.getClass() + " is not intended to be cleared.");
    }
    //endregion
}