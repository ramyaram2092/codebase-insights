package intellij_extension.views;

import intellij_extension.Constants;
import intellij_extension.views.interfaces.IContainerView;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

public class InfoSplitPane implements IContainerView {

    private SplitPane parent;

    // Top half is Commit H istory for a single branch
    private FileHistoryPane fileHistoryPane;

    // Bottom half is Commit Details for a single commit in the selected branch
    private CommitDetailsPane commitDetailsPane;

    public InfoSplitPane() {
        parent = new SplitPane();
        parent.setOrientation(Orientation.VERTICAL);
        // Ensure heat map can take over full tool window
        parent.setMinWidth(Constants.ZERO_WIDTH);

        // Top half
        fileHistoryPane = new FileHistoryPane();
        parent.getItems().add(fileHistoryPane.getNode());

        //Bottom half
        commitDetailsPane = new CommitDetailsPane();
        parent.getItems().add(commitDetailsPane.getNode());
    }

    @Override
    public Node getNode() {
        return parent;
    }

    @Override
    public void clearPane() {

    }
}