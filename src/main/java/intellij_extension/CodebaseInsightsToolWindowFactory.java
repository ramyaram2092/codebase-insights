package intellij_extension;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import intellij_extension.views.HeatMapSplitPane;
import intellij_extension.views.InfoSplitPane;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class CodebaseInsightsToolWindowFactory implements ToolWindowFactory {
    public static final Object projectSynchronizer = new Object(); //used for accessing `project` on other threads
    private static Project project;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        //Store the project parameter so that the calculation thread can access it
        synchronized (CodebaseInsightsToolWindowFactory.projectSynchronizer) {
            CodebaseInsightsToolWindowFactory.project = project;
            projectSynchronizer.notifyAll();
        }

        final JFXPanel fxPanel = new JFXPanel();
        fxPanel.setBackground(Color.BLACK);
        JComponent component = toolWindow.getComponent();
        int componentWidth = component.getWidth();
        int componentHeight = component.getHeight();

        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            SplitPane root = new SplitPane();
            Scene mainScene = new Scene(root, componentWidth, componentHeight);
            root.setMinWidth(Constants.ZERO_WIDTH);
            root.prefWidthProperty().bind(mainScene.widthProperty());
            root.prefHeightProperty().bind(mainScene.heightProperty());

            // Left Half of Tools Windows
            HeatMapSplitPane heatMapSplit = new HeatMapSplitPane(mainScene);
            root.getItems().add(heatMapSplit.getNode());

            // Right Half of Tools Window
            // Will split into two sections
            InfoSplitPane commitInfoSplitPane = new InfoSplitPane();
            root.getItems().add(commitInfoSplitPane.getNode());

            fxPanel.setScene(mainScene);
        });

        component.getParent().add(fxPanel);
    }

    public static Project getProject() {
        return project;
    }
}