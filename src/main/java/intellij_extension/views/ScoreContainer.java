package intellij_extension.views;

import intellij_extension.Constants;
import intellij_extension.utility.HeatCalculationUtility;
import intellij_extension.views.interfaces.IContainerView;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Displays a single number in large font with a caption below it.
 * This is intended to be used to display the average score for each heat metric on the dashboard.
 */
public class ScoreContainer implements IContainerView
{
    private VBox containerVBox;
    private static final Font jumboNumberFont = Font.font(Constants.HEADER_FONT, Constants.SF_TEXT_FONT_WEIGHT, 48);
    private static final Font captionFont = Font.font(Constants.HEADER_FONT, Constants.SF_TEXT_FONT_WEIGHT, 18);

    public ScoreContainer(double numericPart, String caption) {
        String numberAsText = Double.toString(numericPart);
        Color numberTextColor = HeatCalculationUtility.colorOfHeat((int) Math.round(numericPart)); //Color numberAsText font according to the heat score

        setup(numberAsText, numberTextColor, caption);
    }

    //Create the view elements
    private void setup(String numericPart, Color numberTextColor, String caption)
    {
        Label jumboNumberLabel = new Label(numericPart);
        jumboNumberLabel.setFont(jumboNumberFont);
        jumboNumberLabel.wrapTextProperty().set(true);
        jumboNumberLabel.setAlignment(Pos.CENTER);
        jumboNumberLabel.setTextFill(numberTextColor);

        Label captionLabel = new Label(caption);
        captionLabel.setFont(captionFont);
        captionLabel.wrapTextProperty().set(true);
        captionLabel.setAlignment(Pos.CENTER);

        containerVBox = new VBox();
        containerVBox.setMaxWidth(100); //100 is an arbitrary max width to keep the caption from stretching out too long
        containerVBox.getChildren().addAll(jumboNumberLabel, captionLabel);
    }

    @Override
    public Node getNode() {
        return containerVBox;
    }

    @Override
    public void clearPane() {
        containerVBox.getChildren().clear();
    }
}
