package caldis.spero.toh;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.Timer;
import java.util.TimerTask;

public class TimerManager {
    private Timer timer;
    private TimerTask timerTask;
    private long startTime;
    private long elapsed;
    private boolean timerStarted;

    public void startTimer(Label timerLabel) {
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

    public void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerStarted = false;
        }
        if (timer != null) {
            timer.cancel();
        }
    }

    public long getElapsed() {
        return elapsed;
    }
}