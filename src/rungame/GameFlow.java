package rungame;
import biuoop.DialogManager;
import biuoop.GUI;
import biuoop.KeyboardSensor;
import collisiondata.Counter;
import drawable.EndScreen;
import drawable.HighScoresAnimation;
import highscores.HighScoresTable;
import highscores.ScoreInfo;
import levels.SingleLevel;
import java.io.File;
import java.io.IOException;

/**
 * This class features a game flow object. used for running the game.
 *
 * @author Raz Shenkman
 * @version 1.0
 * @since 2017-06-09
 */

public class GameFlow {
    private KeyboardSensor keyboardSensor;
    private AnimationRunner animationRunner;
    private Counter scoreCounter;
    private Counter livesCounter;
    private HighScoresTable highScoreTable;
    private File f;
    private GUI gui;
    private int numOfLevel;

    /**
     * Constructor for the game flow, getting a keyboard and an animation runner.
     * @param ar animation runner.
     * @param ks keyboard sensor.
     * @param highScoresTable high score table.
     */
    public GameFlow(AnimationRunner ar, KeyboardSensor ks, HighScoresTable highScoresTable) {
        this.keyboardSensor = ks;
        this.animationRunner = ar;
        this.scoreCounter = new Counter();
        this.livesCounter = new Counter();
        this.livesCounter.increase(3);
        this.highScoreTable = highScoresTable;
        this.f = new File("highscores");
        this.gui = ar.getGui();
        this.numOfLevel = 1;
    }

    /**
     * Method for running a level, stops only when user dies.
     * @param singleLevel single level.
     */
    public void runLevels(SingleLevel singleLevel) {
        while (true) {
            singleLevel.reset();
            this.setLevel(singleLevel);
            GameLevel level = new GameLevel(singleLevel, this.keyboardSensor, this.animationRunner,
                    livesCounter, scoreCounter);
            level.initialize();
            while (this.livesCounter.getValue() > 0 && level.areThereBlocksLeft()) {
                level.playOneTurn();
            }
            // Check if the user's lives are 0.
            if (this.livesCounter.getValue() == 0) {
                this.animationRunner.run(new KeyPressStoppableAnimation(this.animationRunner.getGui()
                        .getKeyboardSensor(), "space",
                        new EndScreen(this.scoreCounter.getValue(), false)));
                this.addHighScore();
                this.animationRunner.run(new KeyPressStoppableAnimation(this.animationRunner.getGui()
                        .getKeyboardSensor(), "space", new HighScoresAnimation(this.highScoreTable)));
                break;
            }
        }
    }

    /**
     * This method adds a high score to the table if it has a relevant value.
     */
    public void addHighScore() {
        int rank = this.highScoreTable.getRank(scoreCounter.getValue());
        // If the table's size is less than 5.
        if (highScoreTable.size() < 5) {
            DialogManager dialog = this.gui.getDialogManager();
            String name = dialog.showQuestionDialog("Enter Name", "What is your name?", "");
            this.highScoreTable.add(new ScoreInfo(name, scoreCounter.getValue()));
            try {
                highScoreTable.save(this.f);
            } catch (IOException e) {
                int x = 5;
            }
        // If the rank is relevant- less than the size of the table.
        } else if (rank <= highScoreTable.size()) {
            DialogManager dialog = this.gui.getDialogManager();
            String name = dialog.showQuestionDialog("Enter Name", "What is your name?", "");
            this.highScoreTable.add(new ScoreInfo(name, scoreCounter.getValue()));
            try {
                highScoreTable.save(this.f);
            } catch (IOException e) {
                int x = 5;
            }
        }
    }

    /**
     * This method sets the level values.
     * @param singleLevel single level.
     */
    public void setLevel(SingleLevel singleLevel) {
        int num = this.numOfLevel;
        singleLevel.getMultAlienColumn().initializeAlienColumn();
        singleLevel.setName("Battle no. " + num);
        singleLevel.setNumberOfBlocksToRemove(50);
        singleLevel.setPaddleWidth(75);
        singleLevel.setPaddleSpeed(700);
        singleLevel.setShieldBlocks();
        singleLevel.getMultAlienColumn().setSpeed(this.numOfLevel);
        ++this.numOfLevel;
    }
}
