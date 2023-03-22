package intellij_extension.views.extras;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
    Thank you kerner1000
    https://stackoverflow.com/questions/41462622/keep-tooltip-open-as-long-as-mouse-is-over-it
 **/
public class HoveringTooltip extends Tooltip {

    private final int duration;
    private final Map<Object, Boolean> mapHoveringTarget2Hovering = new ConcurrentHashMap<>();
    private Timer timer = new Timer();

    public HoveringTooltip(int duration, String message) {
        super(message);
        super.setAutoHide(false);
        this.duration = duration;
    }

    public void addHoveringTarget(Node object) {
        mapHoveringTarget2Hovering.put(object, false);
        object.setOnMouseEntered(e -> {
            onMouseEntered(object);
        });
        object.setOnMouseExited(e -> {
            onMouseExited(object);
        });
    }

    public void addHoveringTarget(Scene object) {
        mapHoveringTarget2Hovering.put(object, false);
        object.setOnMouseEntered(e -> {
            onMouseEntered(object);
        });
        object.setOnMouseExited(e -> {
            onMouseExited(object);
        });
    }

    @Override
    public void hide() {
        // super.hide();
    }

    public boolean isHovering() {
        return isHoveringProperty().get();
    }

    public BooleanProperty isHoveringProperty() {
        synchronized (mapHoveringTarget2Hovering) {
            for (Map.Entry<Object, Boolean> e : mapHoveringTarget2Hovering.entrySet()) {
                if (e.getValue()) {
                    // if one hovering target is hovering, return true
                    return new ReadOnlyBooleanWrapper(true);
                }
            }
            // no hovering on any target, return false
            return new ReadOnlyBooleanWrapper(false);
        }
    }

    private synchronized void onMouseEntered(Object object) {
        // stop a potentially running hide timer
        timer.cancel();
        mapHoveringTarget2Hovering.put(object, true);
    }

    private synchronized void onMouseExited(Object object) {
        mapHoveringTarget2Hovering.put(object, false);
        startTimer();
    }

    private void startTimer() {
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (!isHovering())
                            HoveringTooltip.super.hide();
                    }
                });
            }
        }, duration);
    }
}