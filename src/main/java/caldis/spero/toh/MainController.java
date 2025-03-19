package caldis.spero.toh;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    private List<Rectangle>[] towers = new ArrayList[3];
    private int selectedTower = -1;
    private final double poleBaseX[] = {200, 400, 600};
    private final int MIN_DISKS = 3;
    private final int MAX_DISKS = 12;
    private final double DISK_HEIGHT = 20;
    private final double OFFSET_Y = 5 * DISK_HEIGHT;

    private int moveCount = 0;
    private Timer timer;
    private TimerTask timerTask;
    private int countdown = 10;
    private int numDisks = MIN_DISKS; // Store the number of disks
    private boolean timerStarted = false; // Flag to check if the timer has started
    private long startTime; // Variable to store the start time
    private long elapsed; // Variable to store the elapsed time

    @FXML
    private void initialize() {
        for (int i = 0; i < towers.length; i++) {
            towers[i] = new ArrayList<>();
        }
        diskInput.setOnAction(event -> startGame());
        moveLabel.setText("Moves: 0");
        timerLabel.setText("Time: 0.00 seconds"); // Initialize the timer label
        winMessageLabel.setText(""); // Initialize the win message label
        confettiPane.setVisible(false); // Initialize confetti pane visibility

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
            numDisks = MIN_DISKS;
        }

        if (numDisks < MIN_DISKS) {
            numDisks = MIN_DISKS;
        } else if (numDisks > MAX_DISKS) {
            numDisks = MAX_DISKS;
        }

        gamePane.getChildren().clear();
        for (int i = 0; i < towers.length; i++) {
            towers[i].clear();
        }
        moveCount = 0;
        timerStarted = false;
        updateMoveLabel();
        resetTimerLabel();
        winMessageLabel.setText("");
        double width = 20;

        addPolesAndAreas(numDisks, DISK_HEIGHT);

        for (int i = numDisks; i > 0; i--) {
            Rectangle disk = new Rectangle(width * i, DISK_HEIGHT);
            disk.setFill(Color.LIGHTBLUE);
            disk.setStroke(Color.DARKBLUE);
            disk.setStrokeWidth(2);
            disk.setStrokeType(StrokeType.OUTSIDE);
            disk.setX(poleBaseX[0] - (width * i) / 2 + 5); // Adjust X-coordinate to center
            disk.setY(250 - (numDisks - i + 1) * DISK_HEIGHT + OFFSET_Y); // Adjust Y-coordinate with offset
            towers[0].add(disk);
            gamePane.getChildren().add(disk);
        }
    }

    private void addPolesAndAreas(int numDisks, double diskHeight) {
        double poleHeight = (numDisks + 1) * diskHeight;
        gamePane.getChildren().addAll(
                createClickableArea(100, 0),
                createClickableArea(300, 1),
                createClickableArea(500, 2)
        );
        gamePane.getChildren().addAll(
                createPole(200, 0, poleHeight),
                createPole(400, 1, poleHeight),
                createPole(600, 2, poleHeight)
        );
    }

    private Rectangle createClickableArea(double x, int index) {
        Rectangle area = new Rectangle(x, 50 + OFFSET_Y, 200, 200);
        area.setFill(Color.TRANSPARENT);
        area.setOnMouseClicked(event -> handlePoleClick(index));
        return area;
    }

    private Rectangle createPole(double x, int index, double height) {
        Rectangle pole = new Rectangle(x, 250 - height + OFFSET_Y, 10, height);
        pole.setFill(Color.BLACK);
        pole.setId("pole-" + index);
        pole.setOnMouseClicked(event -> handlePoleClick(index));
        return pole;
    }

    private void handlePoleClick(int index) {
        if (selectedTower == -1) {
            // Select the tower
            selectedTower = index;
            highlightPossibleMoves(index);
        } else {
            // Try to move the disk
            if (index != selectedTower && canMoveDisk(selectedTower, index)) {
                moveDisk(selectedTower, index);
                moveCount++;
                updateMoveLabel();

                if (!timerStarted) {
                    startTimer();
                }

                if (checkWinCondition()) {
                    stopTimer();
                    displayWinMessage();
                    startCountdown();
                    animateConfetti();
                }
            }
            clearHighlights();
            selectedTower = -1;
        }
    }

    private void highlightPossibleMoves(int from) {
        for (int i = 0; i < towers.length; i++) {
            Rectangle pole = (Rectangle) gamePane.lookup("#pole-" + i);
            if (i == from) {
                pole.setFill(Color.BLUE);
            } else if (canMoveDisk(from, i)) {
                pole.setFill(Color.GREEN);
            }
        }
    }

    private void clearHighlights() {
        for (int i = 0; i < towers.length; i++) {
            Rectangle pole = (Rectangle) gamePane.lookup("#pole-" + i);
            pole.setFill(Color.BLACK);
        }
    }

    private boolean canMoveDisk(int from, int to) {
        if (towers[from].isEmpty()) return false;
        if (towers[to].isEmpty()) return true;
        return towers[to].get(towers[to].size() - 1).getWidth() > towers[from].get(towers[from].size() - 1).getWidth();
    }

    private void moveDisk(int from, int to) {
        if (!towers[from].isEmpty()) {
            Rectangle disk = towers[from].remove(towers[from].size() - 1);
            towers[to].add(disk);

            double x = poleBaseX[to] - disk.getWidth() / 2 + 5; // Adjust X-coordinate to center
            double y = 250 - towers[to].size() * disk.getHeight() + OFFSET_Y;
            disk.setX(x);
            disk.setY(y);
        }
    }

    private void updateMoveLabel() {
        moveLabel.setText("Moves: " + moveCount);
    }

    private void resetTimerLabel(){
        timerLabel.setText("Time: 0.00 seconds");
    }

    private boolean checkWinCondition() {
        // Check if all disks are moved to the last tower
        return towers[2].size() == numDisks;
    }

    private void displayWinMessage() {
        double elapsedSeconds = elapsed / 1000.0;
        winMessageLabel.setText("You won in " + moveCount + " moves! Time: " + String.format("%.2f", elapsedSeconds) + " seconds.");
    }

    private void startCountdown() {
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

    private void startTimer() {
        timerStarted = true;
        startTime = System.currentTimeMillis();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    elapsed = System.currentTimeMillis() - startTime;
                    double elapsedSeconds = elapsed / 1000.0;
                    timerLabel.setText(String.format("Time: %.2f seconds", elapsedSeconds));
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 50);
    }

    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerStarted = false;
        }
        if (timer != null) {
            timer.cancel();
        }
    }

    private void resetGame() {
        Platform.runLater(() -> {
            moveLabel.setText("Moves: 0");
            resetTimerLabel();
            winMessageLabel.setText("");
            confettiPane.getChildren().clear();
            confettiPane.setVisible(false);
            for (int i = 0; i < towers.length; i++) {
                towers[i].clear();
            }
            gamePane.getChildren().clear();
            startGame(); // Start the game with the same number of disks
        });
    }

    private void handleCloseRequest() {
        stopTimer();
        Platform.exit();
    }

    private void animateConfetti() {
        confettiPane.setVisible(true);
        Random random = new Random();
        Timeline timeline = new Timeline();

        for (int i = 0; i < 200; i++) { // Double the amount of confetti
            Rectangle confetti = new Rectangle(5, 10);
            confetti.setFill(Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
            confetti.setX(random.nextInt((int) gamePane.getWidth()));
            confetti.setY(0);

            TranslateTransition transition = new TranslateTransition(Duration.seconds(2 + random.nextDouble()), confetti);
            transition.setByY(gamePane.getHeight());
            transition.setCycleCount(1);
            transition.setOnFinished(event -> confettiPane.getChildren().remove(confetti)); // Remove confetti after animation

            // Use a linear interval for confetti generation
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i * 0.05), e -> {
                confettiPane.getChildren().add(confetti);
                transition.play();
            }));
        }

        timeline.play();
    }
}