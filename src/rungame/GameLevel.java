package rungame;
import biuoop.DrawSurface;
import biuoop.KeyboardSensor;
import collisiondata.BallRemover;
import collisiondata.BlockRemover;
import collisiondata.Counter;
import collisiondata.GameEnvironment;
import collisiondata.ScoreTrackingListener;
import collisiondata.Velocity;
import drawable.Ball;
import drawable.LevelNameDrawer;
import drawable.LivesIndicator;
import drawable.MultAlienColumn;
import drawable.Paddle;
import drawable.ScoreIndicator;
import drawable.Shield;
import drawable.SpriteCollection;
import interfaces.Animation;
import interfaces.Collidable;
import interfaces.LevelInformation;
import interfaces.Sprite;
import levels.SingleLevel;
import java.util.ArrayList;
import java.awt.Color;
import java.util.Random;


/**
 * This class controls the game level, the sprite, the collidables and the playing algorithm.
 *
 * @author Raz Shenkman
 * @version 1.0
 * @since 2017-05-24
 */
public class GameLevel implements Animation {
    private SpriteCollection spriteCollection;
    private GameEnvironment environment;
    private Counter decreasingBlockCounter;
    private Counter decreasingBallCounter;
    private Counter score;
    private Counter livesCounter;
    private ScoreIndicator scoreIndicator;
    private Paddle gamePaddle;
    private AnimationRunner runner;
    private boolean running;
    private KeyboardSensor keyboard;
    private LevelInformation levelInformation;
    private BallRemover ballRemover;
    private LivesIndicator livesIndicator;
    private MultAlienColumn multAlienColumn;
    private Long paddleShotTime;
    private Long randomShootTime;
    private ArrayList<Shield> shieldArrayList;
    private BlockRemover blockRemover;
    private ScoreTrackingListener scoreTrackingListener;
    /**
     * This constructor creates a game from a single level, keyboard, animation runner, and live and score counters.
     * @param singleLevel single level
     * @param animationRunner animation runner.
     * @param keyboard keyboard.
     * @param livesCounter counter for lives.
     * @param scoreCounter counter for score.
     */
    public GameLevel(SingleLevel singleLevel, KeyboardSensor keyboard, AnimationRunner animationRunner,
                     Counter livesCounter, Counter scoreCounter) {
        this.spriteCollection = new SpriteCollection();
        this.environment = new GameEnvironment();
        this.decreasingBlockCounter = new Counter();
        this.decreasingBallCounter = new Counter();
        this.score = scoreCounter;
        this.livesCounter = livesCounter;
        this.scoreIndicator = new ScoreIndicator(score);
        this.levelInformation = singleLevel;
        this.keyboard = keyboard;
        this.runner = animationRunner;
        this.livesIndicator = new LivesIndicator(this.livesCounter);
        this.multAlienColumn = singleLevel.getMultAlienColumn();
        this.paddleShotTime = (long) 1;
        this.randomShootTime = (long) 1;
        this.shieldArrayList = singleLevel.getShieldBlocks();
        this.blockRemover = new BlockRemover(this, this.decreasingBlockCounter);
        this.scoreTrackingListener = new ScoreTrackingListener(this.score);
    }

    /**
     * This method adds a collidable object to the game environment.
     * @param c a collidable object.
     */
    public void addCollidable(Collidable c) {
        this.environment.addCollidable(c);
    }

    /**
     * This method adds a sprites to the list of sprites (sprite collection).
     * @param s a sprite to add.
     */
    public void addSprite(Sprite s) {
        this.spriteCollection.addSprite(s);
    }

    /**
     * This method initializes the game.
     */
    public void initialize() {
        this.addSprite(this.levelInformation.getBackground());
        this.decreasingBlockCounter.increase(this.levelInformation.numberOfBlocksToRemove());
        this.gamePaddle = createPaddle();
        gamePaddle.addToGame(this);
        this.addSprite(this.scoreIndicator);
        this.addSprite(new LevelNameDrawer(this.levelInformation.levelName()));
        this.addSprite(this.livesIndicator);
        this.multAlienColumn.addToGame(this, this.blockRemover, this.scoreTrackingListener);
        this.addSprite(this.multAlienColumn);
        this.addShields();
    }

    /**
     * This method clears the sprites and collidables (used after the player loses life to re initialize the game) and
     * adds new sprites and collidables based on the previous ones.
     */
    public void deInitialize() {
        this.spriteCollection.clear();
        this.environment.clear();
        this.addSprite(this.levelInformation.getBackground());
        gamePaddle.addToGame(this);
        this.addSprite(this.scoreIndicator);
        this.addSprite(new LevelNameDrawer(this.levelInformation.levelName()));
        this.addSprite(this.livesIndicator);
        this.multAlienColumn.returnToPlace();
        this.multAlienColumn.initializeSpeed();
        this.multAlienColumn.removeListenersFromBlocks(this.blockRemover, this.scoreTrackingListener);
        this.multAlienColumn.addAgainToGame(this, this.blockRemover, this.scoreTrackingListener);
        this.addSprite(multAlienColumn);
        this.addShields();
        this.gamePaddle.setHittedPaddle();
        this.multAlienColumn.returnNotTouchDown();
    }

    /**
     * This method runs the game by creating a drawing surface and using the sprite method for drawing and notifying
     * the sprite objects (ball, paddle, blocks) the paddleShotTime has passed.
     */
    public void playOneTurn() {
        if (this.livesCounter.getValue() > 0) {
            this.runner.run(new CountdownAnimation(2, 3, this.spriteCollection));
        }
        this.running = true;
        this.runner.run(this);
        this.deInitialize();
    }


    /**
     * This method remove a collidable object from the game environment of this game.
     * @param c collidable object.
     */
    public void removeCollidable(Collidable c) {
        this.environment.removeColilidable(c);
    }

    /**
     * This method removes sprite object from the sprite collection of this game.
     * @param s sprite object.
     */
    public void removeSprite(Sprite s) {
        this.spriteCollection.removeSpirte(s);
    }

    /**
     * Boolean if the game should stop.
     * @return true/false.
     */
    public boolean shouldStop() {
        return !this.running;
    }

    /**
     * This method prints all the sprites and the game and decide if the game should stop.
     * @param d draw surface.
     * @param dt is the amount of seconds passed since the last call.
     */
    public void doOneFrame(DrawSurface d, double dt) {
        this.ballRemover = new BallRemover(this, this.decreasingBallCounter);
        this.spriteCollection.drawAllOn(d);
        this.spriteCollection.notifyAllTimePassed(dt);
        if (this.keyboard.isPressed("p")) {
            this.runner.run(new KeyPressStoppableAnimation(this.runner.getGui().getKeyboardSensor(),
                    "space", new PauseScreen()));
        } else if (this.keyboard.isPressed("space")) {
            addBall(System.currentTimeMillis());
        }
        this.addRandomBall(System.currentTimeMillis());
        if (decreasingBlockCounter.getValue() == 0) {
            this.running = false;
        } else if (this.livesCounter.getValue() == 0) {
            this.running = false;
        } else if (this.multAlienColumn.ifHitBottom()) {
            this.livesCounter.decrease(1);
            this.running = false;
        } else if (this.gamePaddle.hittedPaddle()) {
            this.livesCounter.decrease(1);
            this.running = false;
        }
    }

    /**
     * This method creates a paddle.
     * @return new paddle.
     */
    public Paddle createPaddle() {
        return new Paddle(this.levelInformation, this.runner.getGui());
    }

    /**
     * This method checks if there are any blocks left.
     * @return true/false.
     */
    public boolean areThereBlocksLeft() {
        if (this.decreasingBlockCounter.getValue() > 0) {
            return true;
        }
        return false;
    }

    /**
     * This method shoots a random ball from the position of the paddle.
     * @param shootTime user press "space" time.
     */
    private void addBall(Long shootTime) {
        if ((shootTime - this.paddleShotTime) <= 350) {
            return;
        }
        double x = this.gamePaddle.getCollisionRectangle().getUpperLeft().getX();
        double y = this.gamePaddle.getCollisionRectangle().getUpperLeft().getY();
        Ball ball = new Ball((int) (x + (gamePaddle.getCollisionRectangle().getWidth() / 2)), (int) (y - 15), 3,
                Color.WHITE, this.ballRemover);
        ball.setGameEnvironment(this.environment);
        ball.setVelocity(new Velocity(0, -500));
        ball.addToGame(this);
        this.paddleShotTime = shootTime;
    }

    /**
     * This method adds a random ball (ball that shot by an alien).
     * @param shootTime of a ball by alien.
     */
    private void addRandomBall(Long shootTime) {
        if ((shootTime - this.randomShootTime) <= 500) {
            return;
        }
        int r = 0;
        do {
            Random random = new Random();
            r = random.nextInt(10);
        } while (this.multAlienColumn.getAlienColumnsList().get(r).getNumOfBlockInGame() <= 0);
        double x = (this.multAlienColumn.getAlienColumnsList().get(r).findDist() + 20);
        double y = (this.multAlienColumn.getAlienColumnsList().get(r).findDepth() + 5);
        Ball ball = new Ball((int) x, (int) y, 6, Color.RED, this.ballRemover);
        ball.setGameEnvironment(this.environment);
        ball.setVelocity(new Velocity(0, 300));
        ball.addToGame(this);
        this.randomShootTime = shootTime;
    }

    /**
     * This method adds the not hit shields to the game.
     */
    public void addShields() {
        for (Shield shield: this.shieldArrayList) {
            if (shield.getInGame()) {
                shield.setHits(-1);
                shield.setWidth(5);
                shield.setHeight(5);
                shield.addToGame(this);
                shield.addHitListener(this.blockRemover);
                shield.addHitListener(this.scoreTrackingListener);
            }
        }
    }
}
