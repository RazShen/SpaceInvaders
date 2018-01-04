package task;
import biuoop.GUI;
import highscores.HighScoresTable;
import interfaces.Task;
import levels.SingleLevel;
import rungame.AnimationRunner;
import rungame.GameFlow;
import java.io.File;
import java.io.IOException;

/**
 * This class features a run game task, which creates game flow and uses it to run the level information list it gets.
 *
 * @author Raz Shenkman
 * @version 1.0
 * @since 2017-06-12.
 */
public class RunGameTask implements Task<Void> {
    private GUI gui;
    private AnimationRunner animationRunner;
    private SingleLevel singleLevel;
    private File highScoresFile;
    private HighScoresTable highScoresTable;

    /**
     * Constructor for run game task from gui, animation runner, list of level information and a high score table.
     * @param gui inputted gui.
     * @param animationRunner inputted animation runner.
     * @param level inputted level.
     * @param highScoresTable high score table.
     */
    public RunGameTask(GUI gui, AnimationRunner animationRunner, SingleLevel level,
                       HighScoresTable highScoresTable) {
        this.gui = gui;
        this.animationRunner = animationRunner;
        this.singleLevel = level;
        this.highScoresFile = new File("highscores");
        this.highScoresTable = highScoresTable;
    }

    /**
     * Run a task and get a returned value.
     * @return Void.
     */
    public Void run() {
        GameFlow game = new GameFlow(this.animationRunner, this.gui.getKeyboardSensor(), this.highScoresTable);
        game.runLevels(this.singleLevel);
        this.singleLevel.reset();
        try {
            highScoresTable.save(this.highScoresFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can't save high score table");
        }
        return null;
    }
}
