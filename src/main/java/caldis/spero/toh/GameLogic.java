package caldis.spero.toh;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    private final double poleBaseX[] = {200, 400, 600};
    private final double DISK_HEIGHT = 20;
    private final double OFFSET_Y = 5 * DISK_HEIGHT;
    private int selectedTower = -1;
    private MainController mainController;
    private TowerOfHanoi towerOfHanoi;
    private List<Rectangle>[] towers = new ArrayList[3];

    public GameLogic(MainController mainController) {
        this.mainController = mainController;
        for (int i = 0; i < 3; i++) {
            towers[i] = new ArrayList<>();
        }
    }

    public void startGame(int numDisks, Pane gamePane) {
        gamePane.getChildren().clear();
        towerOfHanoi = new TowerOfHanoi(numDisks);

        double width = 20;
        addPolesAndAreas(numDisks, DISK_HEIGHT, gamePane);

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

    private void addPolesAndAreas(int numDisks, double diskHeight, Pane gamePane) {
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
                mainController.incrementMoveCount();

                if (!mainController.isTimerStarted()) {
                    mainController.startTimer();
                }

                if (checkWinCondition()) {
                    mainController.stopTimer();
                    mainController.displayWinMessage();
                    mainController.startCountdown();
                    mainController.animateConfetti();
                }
            }
            clearHighlights();
            selectedTower = -1;
        }
    }

    private void highlightPossibleMoves(int from) {
        for (int i = 0; i < 3; i++) {
            Rectangle pole = (Rectangle) mainController.getGamePane().lookup("#pole-" + i);
            if (i == from) {
                pole.setFill(Color.BLUE);
            } else if (canMoveDisk(from, i)) {
                pole.setFill(Color.GREEN);
            }
        }
    }

    private void clearHighlights() {
        for (int i = 0; i < 3; i++) {
            Rectangle pole = (Rectangle) mainController.getGamePane().lookup("#pole-" + i);
            pole.setFill(Color.BLACK);
        }
    }

    public boolean canMoveDisk(int from, int to) {
        return !towerOfHanoi.getTowers()[from].isEmpty() &&
                (towerOfHanoi.getTowers()[to].isEmpty() ||
                        towerOfHanoi.getTowers()[from].peek() < towerOfHanoi.getTowers()[to].peek());
    }

    public void moveDisk(int from, int to) {
        towerOfHanoi.moveDisk(from, to);
        updateDiskPositions();
    }

    private void updateDiskPositions() {
        for (int i = 0; i < 3; i++) {
            List<Rectangle> tower = towers[i];
            for (int j = 0; j < tower.size(); j++) {
                Rectangle disk = tower.get(j);
                disk.setX(poleBaseX[i] - disk.getWidth() / 2 + 5); // Adjust X-coordinate to center
                disk.setY(250 - (j + 1) * DISK_HEIGHT + OFFSET_Y); // Adjust Y-coordinate with offset
            }
        }
    }

    public boolean checkWinCondition() {
        return towerOfHanoi.getTowers()[2].size() == towerOfHanoi.getNumDisks();
    }
}