package caldis.spero.toh;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML
    private TextField diskInput;
    @FXML
    private Pane gamePane;

    private List<Rectangle>[] towers = new ArrayList[3];
    private int selectedTower = -1;
    private final double poleBaseX[] = {200, 400, 600};

    @FXML
    private void initialize() {
        for (int i = 0; i < towers.length; i++) {
            towers[i] = new ArrayList<>();
        }
    }

    @FXML
    private void startGame() {
        gamePane.getChildren().clear();
        int numDisks = Integer.parseInt(diskInput.getText());
        double width = 20;
        double height = 20;

        addPolesAndAreas(numDisks, height);

        for (int i = numDisks; i > 0; i--) {
            Rectangle disk = new Rectangle(width * i, height);
            disk.setFill(Color.LIGHTBLUE);
            disk.setStroke(Color.DARKBLUE);
            disk.setStrokeWidth(2);
            disk.setStrokeType(StrokeType.OUTSIDE);
            disk.setX(poleBaseX[0] - (width * i) / 2);
            disk.setY(250 - (numDisks - i + 1) * height); // Adjust Y-coordinate
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
        Rectangle area = new Rectangle(x, 50, 200, 200);
        area.setFill(Color.TRANSPARENT);
        area.setOnMouseClicked(event -> handlePoleClick(index));
        return area;
    }

    private Rectangle createPole(double x, int index, double height) {
        Rectangle pole = new Rectangle(x, 250 - height, 10, height);
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

            double x = poleBaseX[to] - disk.getWidth() / 2;
            double y = 250 - towers[to].size() * disk.getHeight();
            disk.setX(x);
            disk.setY(y);
        }
    }
}