package caldis.spero.toh;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

public class MainController {

    @FXML
    private TextField diskInput;
    @FXML
    private Pane gamePane;
    @FXML
    private Label moveLabel;
    @FXML
    private Label timerLabel; // Label for the timer
    @FXML
    private Label winMessageLabel; // Label for the win message
    @FXML
    private Pane confettiPane; // Pane for confetti animation

    private GameLogic gameLogic;
    private TimerManager timerManager;
    private ConfettiAnimator confettiAnimator;

    private int moveCount = 0;
    private Timer timer;
    private int countdown = 10;
    private int numDisks = 3; // Store the number of disks
    private boolean timerStarted = false; // Flag to check if the timer has started

    @FXML
    private void initialize() {
        gameLogic = new GameLogic(this);
        timerManager = new TimerManager();
        confettiAnimator = new ConfettiAnimator();
        diskInput.setOnAction(event -> startGame());
        moveLabel.setText("Moves: 0");
        timerLabel.setText("Time: 0.00 seconds"); // Initialize the timer label
        winMessageLabel.setText(""); // Initialize the win message label

        // Ensure the application closes properly
        Platform.runLater(() -> {
            Stage stage = (Stage) gamePane.getScene().getWindow();
            stage.setOnCloseRequest(event -> handleCloseRequest());
        });
    }

    @FXML
    private void startGame() {
        try {
            numDisks = Integer.parseInt(diskInput.getText());
        } catch (NumberFormatException e) {
            numDisks = 3;
        }

        if (numDisks < 3) {
            numDisks = 3;
        } else if (numDisks > 12) {
            numDisks = 12;
        }

        gameLogic.startGame(numDisks, gamePane);
        moveCount = 0;
        updateMoveLabel();
        resetTimerLabel();
        winMessageLabel.setText("");
    }

    public void incrementMoveCount() {
        moveCount++;
        updateMoveLabel();
    }

    public void startTimer() {
        timerStarted = true;
        timerManager.startTimer(timerLabel);
    }

    public void stopTimer() {
        timerManager.stopTimer();
        timerStarted = false;
    }

    public boolean isTimerStarted() {
        return timerStarted;
    }

    public int getNumDisks() {
        return numDisks;
    }

    public Pane getGamePane() {
        return gamePane;
    }

    private void updateMoveLabel() {
        moveLabel.setText("Moves: " + moveCount);
    }

    private void resetTimerLabel() {
        timerLabel.setText("Time: 0.00 seconds");
    }

    public void displayWinMessage() {
        double elapsedSeconds = timerManager.getElapsed() / 1000.0;
        winMessageLabel.setText("You won in " + moveCount + " moves! Time: " + String.format("%.2f", elapsedSeconds) + " seconds.");
    }

    public void startCountdown() {
        countdown = 10;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    countdown--;
                    if (countdown > 0) {
                        timerLabel.setText(String.format("Resetting in %d seconds...", countdown));
                    } else {
                        timer.cancel();
                        resetGame();
                    }
                });
            }
        }, 1000, 1000);
    }

    public void animateConfetti() {
        confettiAnimator.animateConfetti(confettiPane, gamePane);
    }

    private void handleCloseRequest() {
        timerManager.stopTimer();
        Platform.exit();
    }

    private void resetGame() {
        Platform.runLater(() -> {
            moveLabel.setText("Moves: 0");
            resetTimerLabel();
            winMessageLabel.setText("");
            confettiPane.getChildren().clear();
            confettiPane.setVisible(false);
            startGame(); // Start the game with the same number of disks
        });
    }
}