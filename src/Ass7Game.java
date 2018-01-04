import highscores.HighScoresTable;
import interfaces.Menu;
import interfaces.Task;
import levels.SingleLevel;
import rungame.AnimationRunner;
import rungame.MenuAnimation;
import task.ExitGameTask;
import task.RunGameTask;
import task.ShowHiScoresTask;
import java.io.File;

/**
 * This class operates a menu, and getting the status of the menu and running it's tasks.
 *
 * @author Raz Shenkman
 * @version 1.0
 * @since 2017-04-24
 */

public class Ass7Game {
    /**
     * This method loads the high score table and create a new level, and a menu, which it operates with the
     * animation runner.
     * @param args inputted argument (must be valid level set).
     */
    public static void main(String[] args) {
        AnimationRunner animationRunner = new AnimationRunner(60);
        File scoretable = new File("highscores");
        HighScoresTable highScoresTable = HighScoresTable.loadFromFile(scoretable);
        SingleLevel level = new SingleLevel(1);
        Menu<Task<Void>> menu = new MenuAnimation<Task<Void>>("Space Invaders",
                animationRunner.getGui().getKeyboardSensor(), animationRunner);
        menu.addSelection("s", "Start Game", new RunGameTask(animationRunner.getGui(), animationRunner,
                level, highScoresTable));
        menu.addSelection("h", "High scores", new ShowHiScoresTask(animationRunner, highScoresTable));
        menu.addSelection("q", "Quit game", new ExitGameTask(animationRunner.getGui()));
        while (true) {
            animationRunner.run(menu);
            // wait for user selection
            Task<Void> task = menu.getStatus();
            task.run();
            menu.reset();
        }
    }

}
