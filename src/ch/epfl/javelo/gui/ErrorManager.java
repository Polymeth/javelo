package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * The system to display an error box
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class ErrorManager {
    private final VBox grid;
    private final SequentialTransition seqT;
    private final Text text;

    /**
     * Initialize the error animation
     */
    public ErrorManager() {
        grid = new VBox();
        text = new Text();
        grid.getStylesheets().add("error.css");
        grid.setMouseTransparent(true);
        grid.getChildren().add(text);

        FadeTransition transition = new FadeTransition(Duration.millis(200), grid);
        transition.setFromValue(0);
        transition.setToValue(0.8);
        PauseTransition pause = new PauseTransition(Duration.millis(2000));
        FadeTransition transition2 = new FadeTransition(Duration.millis(500), grid);
        transition2.setFromValue(0.8);
        transition2.setToValue(0);

        seqT = new SequentialTransition(transition, pause, transition2);
    }

    /**
     * @return a pane with an error message
     */
    public Pane pane() {
        return grid;
    }

    /**
     * Display the error animation
     * @param error the error text to display
     */
    public void displayError(String error) {
        seqT.stop();
        java.awt.Toolkit.getDefaultToolkit().beep();
        text.setText(error);
        seqT.play();
    }

}
