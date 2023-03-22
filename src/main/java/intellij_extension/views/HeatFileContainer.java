package intellij_extension.views;

import intellij_extension.Constants;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class HeatFileContainer extends GridPane {
    private final ArrayList<Pane> children = new ArrayList<>();
    private String title;

    /**
     * Creates a FlowPane that holds HeatFileComponents or other elements.
     *
     * @param title a label to help identify this container
     */
    public HeatFileContainer(String title) {
        super();
        setMinWidth(Constants.ZERO_WIDTH);

        //Set margins
        this.setVgap(Constants.HEATMAP_VERTICAL_SPACING);
        this.setHgap(Constants.HEATMAP_HORIZONTAL_SPACING);
        this.setPadding(Constants.HEATMAP_PADDING);
        this.title = title;

        //Every time the container resizes, re-arrange the grid
        this.maxWidthProperty().addListener((observable, oldValue, newValue) -> reorganize());
    }

    public void addNode(Pane node) {
        children.add(node);
        reorganize();
    }

    private void reorganize() {
        //Determine size of grid
        int numColumns = (children.size() / 2) + 1;
        int numRows = (children.size() / 2) - 1;

        //Validate the row & column counts
        final int MAX_COLUMNS = 6;
        if (numColumns < 1)
            numColumns = 1;
        else if (numColumns > MAX_COLUMNS)
            numColumns = MAX_COLUMNS;
        if (numRows < 1)
            numRows = 1;
        //Add more rows until there are enough to fit all children
        while (numColumns * numRows < children.size())
            numRows++;

        //Add all children to the grid
        int i = 0;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numColumns && i < children.size(); c++) {
                Pane element = children.get(i++);
                //Remove the child if it's already in the grid
                if (this.getChildren().contains(element))
                    this.getChildren().remove(element);
                //Place node in the grid
                this.add(element, c, r);
            }
        }
    }

    public String getTitle() {
        return title;
    }
}
