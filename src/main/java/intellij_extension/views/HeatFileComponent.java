package intellij_extension.views;

import intellij_extension.Constants;
import intellij_extension.controllers.HeatMapController;
import intellij_extension.models.redesign.FileObject;
import intellij_extension.views.extras.HoveringTooltip;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import static intellij_extension.Constants.*;
import static intellij_extension.Constants.BLANK;

public class HeatFileComponent extends Pane {

    private final String fileObjectPath;
    private final String fileName;
    private final Integer fileHeatLevel;
    private HeatFileContainer parent;
    private boolean hasHottestFileEffect;
    private boolean toolTipAdded;
    private String heatMetric;

    public HeatFileComponent(FileObject fileObject, int fileHeatLevel, HeatFileContainer parent) {
        super();
        setMinWidth(Constants.ZERO_WIDTH);
        this.setPrefWidth(20);
        this.setPrefHeight(30);
        this.parent = parent;

        this.fileName = fileObject.getFilename();
        this.fileObjectPath = fileObject.getPath().toString();
        this.fileHeatLevel = fileHeatLevel;

        this.setOnMouseClicked(event -> {
            HeatMapController.getInstance().heatMapComponentSelected(fileObjectPath);
        });
    }

    public void setFileToolTip(String groupName) {
        // No need to add it twice
        if (toolTipAdded) return;

        // Add a tooltip to the file pane
        HoveringTooltip tooltip = new HoveringTooltip(Constants.TOOLTIP_DURATION, getToolTipMessage(groupName));
        tooltip.addHoveringTarget(this);
        tooltip.setFont(Constants.TOOLTIP_FONT);
        tooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(this, tooltip);
        toolTipAdded = true;
    }

    private String getToolTipMessage(String groupName) {
        return String.format(TOOLTIP_FORMAT, getWarningMessage(), fileName, fileHeatLevel, heatMetric, groupName);
    }

    // Sets a top 20 warning message in the tool tip if file is one of the top 20 files
    private String getWarningMessage() {
        if (hasHottestFileEffect) return TOP_FILE_WARNING;
        else return BLANK;
    }

    // Adds a fade transition to the file when called
    public void setHottestFileEffect() {
        // No need to add it twice
        if (hasHottestFileEffect) return;

        hasHottestFileEffect = true;
        // Is overwritten by dropshadow unless blend is used.
//        Glow glow = new Glow();
//        glow.setLevel(1.5);
//        this.setEffect(glow);
        Platform.runLater(() -> {
            DropShadow borderGlow = new DropShadow();
            borderGlow.setOffsetY(0f);
            borderGlow.setOffsetX(0f);
            borderGlow.setColor(Color.RED);
            borderGlow.setWidth(DROP_SHADOW_DEPTH);
            borderGlow.setHeight(DROP_SHADOW_DEPTH);
            this.setEffect(borderGlow);  //Apply the borderGlow effect to the JavaFX node

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(borderGlow.widthProperty(), DROP_SHADOW_DEPTH),
                            new KeyValue(borderGlow.heightProperty(), DROP_SHADOW_DEPTH)),
                    new KeyFrame(Duration.seconds(1), new KeyValue(borderGlow.widthProperty(), 0),
                            new KeyValue(borderGlow.heightProperty(), 0)));

            timeline.setAutoReverse(true);
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        });
    }

    public Integer getFileHeatLevel() {
        return fileHeatLevel;
    }

    public HeatFileContainer getContainer() {
        return parent;
    }

    public String getFileName() {
        return fileName;
    }

    public String getHeatMetric() {
        return heatMetric;
    }

    public void setHeatMetric(String heatMetric) {
        this.heatMetric = heatMetric;
    }
}