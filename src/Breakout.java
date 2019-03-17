import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Maksym Sabadyshyn 
 * @author Oleg Boychuk
 */
public class Breakout extends GraphicsProgram {

	/*
	 * File: Breakout.java ------------------- Name: Section Leader:
	 * 
	 * This file will eventually implement the game of Breakout.
	 */

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	public static final int WIDTH = APPLICATION_WIDTH;
	public static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	public double PADDLE_WIDTH = 60;
	public double PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	public static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	public static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	public static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	public static final int BRICK_SEP = 10;

	/** Width of a brick */
	public static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1)
			* BRICK_SEP)
			/ NBRICKS_PER_ROW;

	/** Height of a brick */
	public static final int BRICK_HEIGHT = 12;

	/** animation delay between the step */
	public static final int DELAY = 10;

	/** Radius of the ball in pixels */
	public double BALL_RADIUS = 5;

	/** Offset of the top brick row from the top */
	public static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	public static final int NTURNS = 3;

	/** Start value for yvel */
	public double yVel = 3.0;
	
	/** Start value for yvel */
	public double xVel;

	/** Middle of the world on x-axis */
	public static final int xCoord = WIDTH / 2;

	/** Counts how many rows should be filled with one color */
	public static int colorPerRow;

	/** Defines the number of the color(used for switch) */
	public int color = 1;

	/** checks when to switch the color */
	public int nextCol;

	/** Value that speed increases on */
	public static int speedIncrease = 5;

	/** number of bricfks left*/
	public int bricksLeft = NBRICK_ROWS * NBRICKS_PER_ROW;
	
	/** value that controls when to finish the game*/
	public static final int FINISHGAME = NBRICKS_PER_ROW * NBRICK_ROWS;

	
	public int raxyvalnuk = 0;

	/** controls user's score*/
	public int score = 0;
	/** controls how many attempts have user used already*/
	public int attempt = 0;
	
	/** label that shows the score */
	String scoreLabel = "Score: " + score;
	/** three objects that draw hearts in the top right corner*/
	GObject heart1;
	GObject heart2;
	GObject heart3;
	
	/**background image*/
	GImage background;

	/**audio clips that are used in the game*/
	AudioClip hit = MediaTools.loadAudioClip("hit.au");
	AudioClip winBoi = MediaTools.loadAudioClip("yeahboi.au");
	AudioClip win = MediaTools.loadAudioClip("win.au");
	AudioClip lose = MediaTools.loadAudioClip("lose.au");
	AudioClip mylife = MediaTools.loadAudioClip("mylife.au");
	AudioClip why = MediaTools.loadAudioClip("why.au");

	/** implements random*/
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		this.setSize(WIDTH, HEIGHT);
		background = new GImage("background.jpg");
		add(background);
		xVel = rgen.nextDouble(1.0, 3.0); //x-Speed that are set by random
		if (rgen.nextBoolean(0.5)) {
			xVel = -xVel;
		}
		drawField();
		paddle();
		setup();
		mainGame();
	}

	public void mainGame() {
		drawBall();
		waitForClick(); // game won't start until you click the mouse button 
		firstBall();
		pause(DELAY); // delay
		while (true) {
			if (attempt != 3) //if the user hasn't used all the attempts keep playing
				moveBall();
			bonusMove();
			checkForCollisionWithBricks();
			collideWithBonus();
			collisionWithPaddle();
			if (ball.getY() >= HEIGHT - PADDLE_HEIGHT - PADDLE_Y_OFFSET) { //if the ball reached the bottom of the world
				attempt++;
				switch (attempt) {
				case 1:
					remove(heart1);
					remove(ball);
					yVel = 4;
					if (raxyvalnuk != FINISHGAME && attempt != 3) {
						pause(1100);
						mainGame();
					}
				case 2:
					remove(heart2);
					remove(ball);
					yVel = 4;
					if (raxyvalnuk != FINISHGAME && attempt != 3) {
						pause(1100);
						mainGame();
					}
				case 3:
					removeAll();
					remove(ball);
					break;
				}
			}
			if (raxyvalnuk == FINISHGAME) { //if user won the game
				removeAll();
				winBoi.play();
				GImage los = new GImage("winn.gif");
				los.scale(2, 1.5);
				add(los);
				pause(4000);
				System.exit(0);
				return;
			}
			if (attempt == 3) { //if user lost
				why.play();
				GImage losee = new GImage("why.gif");
				losee.scale(2.4, 2.9);
				add(losee);
				pause(9000);
				System.exit(0);
				return;
			}
		}
	}

	// checking the collision with the paddle(перевіряється чи різниця
	// координати центра ракетки і центра м"ячика по іксу менша від половини
	// довжини ракетки, і водночас чи координата м"ячика по ігрику більша верхню
	// точку ракетки по ігрику і ще одна умава, якщо вони
	// правдиві, то м"ячик змінює напрям руху на протилежний і відбивається від
	// ракетки
	public void collisionWithPaddle() {
		if (Math.abs((paddle.getX() + PADDLE_WIDTH / 2)
				- (ball.getX() + BALL_RADIUS * 2)) <= PADDLE_WIDTH / 2
				+ BALL_RADIUS * 2
				&& ball.getY() >= HEIGHT - PADDLE_HEIGHT - PADDLE_Y_OFFSET
						- BALL_RADIUS * 2
				&& ball.getY() < HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT
						- BALL_RADIUS * 2 + 4) {
			yVel = -yVel;
		}
	}

	/** creates 3-life hearts */
	public void setup() {
		GLine linee = new GLine(0, 30, WIDTH, 30);
		linee.setColor(Color.BLACK);
		add(linee);
		drawCounter();
		heart3 = heart0(APPLICATION_WIDTH - 23);
		add(heart3);
		heart2 = (heart0((APPLICATION_WIDTH - (23 * 2))));
		add(heart2);
		heart1 = (heart0((APPLICATION_WIDTH - (23 * 3))));
		add(heart1);

	}

	/**
	 * starts the game with moving the ball
	 */
	public void firstBall() {
		while (ball.getY() < getHeight() - BALL_RADIUS * 2 - 5) {
			ball.move(0, yVel);
			pause(DELAY);
			if (Math.abs((paddle.getX() + PADDLE_WIDTH / 2)
					- (ball.getX() + BALL_RADIUS * 2)) <= PADDLE_WIDTH / 2
					+ BALL_RADIUS * 2
					&& ball.getY() >= HEIGHT - PADDLE_HEIGHT - PADDLE_Y_OFFSET
							- BALL_RADIUS * 2
					&& ball.getY() < HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT
							- BALL_RADIUS * 2 + 4) {
				yVel = -yVel;
				break;
			}
		}
	}

	/** Lets the paddle move in  range from the beggining of the screen till its end */
	public void mouseMoved(MouseEvent pad) {
		if ((pad.getX() < getWidth() - PADDLE_WIDTH / 2)
				&& (pad.getX() > PADDLE_WIDTH / 2)) {
			paddle.setLocation(pad.getX() - PADDLE_WIDTH / 2, HEIGHT
					- PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		}
	}

	/** creates a ball in the middle of the world*/
	public void drawBall() {
		ball = new GOval(WIDTH / 2 - BALL_RADIUS, HEIGHT / 2 - BALL_RADIUS,
				BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setColor(Color.MAGENTA);
		add(ball);
	}
	/**image of the paddle*/
	GImage paddle;
	/**shape of the ball*/
	GOval ball;

	/** creates a paddle */
	public void paddle() {
		paddle = new GImage("paddle1.png");
		add(paddle, WIDTH / 2 - paddle.getWidth() / 2,
				HEIGHT - paddle.getHeight() - PADDLE_Y_OFFSET);
		PADDLE_WIDTH = paddle.getWidth();
		PADDLE_HEIGHT = paddle.getHeight();
		addMouseListeners();
	}

	/** checking for the collision with the world */
	public void checkForCollision() {
		// if the ball crosses the top of the world then its y-velocity changes
		if (ball.getY() > getHeight() - BALL_RADIUS * 2 - 5 || ball.getY() < 34) {
			yVel = -yVel;
			ball.move(xVel, yVel);
		}
		//if the ball crosses right/left side of the world then its x-velocity changes 
		if (ball.getX() > getWidth() - BALL_RADIUS * 2 - 5
				|| ball.getX() < 0 + 5) {
			xVel = -xVel;
			ball.move(xVel, yVel);
		}
	}

	/** movings of the ball */
	public void moveBall() {
		ball.move(xVel, yVel);
		collisionWithPaddle();
		checkForCollision();
		collisionWithPaddle();
		pause(DELAY);
	}

	/** increases balls speed every 5 removed blocks*/
	public void speedInc() {
		if ((NBRICK_ROWS * NBRICKS_PER_ROW) - bricksLeft >= speedIncrease) {
			if (yVel < 0) {
				yVel -= 0.5;
			} else {
				yVel += 0.5;
			}
			speedIncrease *= 2;
		}
	}

	/**checks for collision with bricks and remove it if it is needed*/
	public void checkForCollisionWithBricks() {
		if (getElementAt(ball.getX(), ball.getY()) != null
				&& getElementAt(ball.getX(), ball.getY()) != paddle
				&& getElementAt(ball.getX(), ball.getY()) != background
				&& getElementAt(ball.getX(), ball.getY()) != ball
				&& getElementAt(ball.getX(), ball.getY()) != good
				&& getElementAt(ball.getX(), ball.getY()) != bad
				&& getElementAt(ball.getX(), ball.getY()) != standart
				&& getElementAt(ball.getX(), ball.getY()) != speed1
				&& getElementAt(ball.getX(), ball.getY()) != speed2
				&& getElementAt(ball.getX(), ball.getY()) != speed3
				&& getElementAt(ball.getX(), ball.getY()) != coin) {
			hit.play();
			bricksLeft -= 1;
			speedInc();
			remove(getElementAt(ball.getX(), ball.getY()));
			bonus();
			optimization();
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != paddle
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != background
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != ball
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != good
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != bad
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != standart
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != speed1
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != speed2
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != speed3
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != coin) {
			hit.play();
			bricksLeft -= 1;
			speedInc();
			remove(getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()));
			bonus();
			optimization();
		} else if (getElementAt(ball.getX(), ball.getY() + 2 * ball.getHeight()
				/ 2) != null
				&& getElementAt(ball.getX(), ball.getY() + 2 * ball.getHeight()
						/ 2) != paddle
				&& getElementAt(ball.getX(), ball.getY() + 2 * ball.getHeight()
						/ 2) != background
				&& getElementAt(ball.getX(), ball.getY() + 2 * ball.getHeight()
						/ 2) != ball
				&& getElementAt(ball.getX(), ball.getY() + 2 * ball.getHeight()
						/ 2) != good
				&& getElementAt(ball.getX(), ball.getY() + 2 * ball.getHeight()
						/ 2) != bad
				&& getElementAt(ball.getX(), ball.getY() + 2 * ball.getHeight()
						/ 2) != standart
				&& getElementAt(ball.getX(), ball.getY() + 2 * ball.getHeight()
						/ 2) != speed1
				&& getElementAt(ball.getX(), ball.getY() + 2 * ball.getHeight()
						/ 2) != speed2
				&& getElementAt(ball.getX(), ball.getY() + 2 * ball.getHeight()
						/ 2) != speed3
				&& getElementAt(ball.getX(), ball.getY() + 2 * ball.getHeight()
						/ 2) != coin) {
			hit.play();
			bricksLeft -= 1;
			speedInc();
			remove(getElementAt(ball.getX(), ball.getY() + 2 * ball.getHeight()
					/ 2));
			bonus();
			optimization();
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
				* BALL_RADIUS) != null
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
						* BALL_RADIUS) != paddle
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
						* BALL_RADIUS) != background
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
						* BALL_RADIUS) != ball
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
						* BALL_RADIUS) != good
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
						* BALL_RADIUS) != bad
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
						* BALL_RADIUS) != standart
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
						* BALL_RADIUS) != speed1
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
						* BALL_RADIUS) != speed2
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
						* BALL_RADIUS) != speed3
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
						* BALL_RADIUS) != coin) {
			hit.play();
			bricksLeft -= 1;
			speedInc();
			remove(getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
					* BALL_RADIUS));
			bonus();
			optimization();
		}
	}

	/** counts the number of bricks removed and changes balls y-velocity after the collision*/
	public void optimization() {
		raxyvalnuk++;
		yVel = -yVel;
		drawCounter();
	}

	/** countes users score*/
	public void drawCounter() {
		if (getElementAt(1, 10) != null && getElementAt(1, 10) != background) {
			remove(getElementAt(1, 10));
		}
		GLabel counter = new GLabel(scoreLabel, 1, 24);
		counter.setFont("TimesNewRoman-30");
		add(counter);
		score++;
		scoreLabel = "Score: " + score;
	}

	/** draws all bricks and gives them different colors*/
	public void drawField() {

		nextCol = 0;
		colorPerRow = NBRICK_ROWS / 5;
		for (int clmn = 0; clmn < NBRICK_ROWS; clmn++) {
			for (int row = 0; row < NBRICKS_PER_ROW; row++) {
				double startX = xCoord - (NBRICKS_PER_ROW * BRICK_WIDTH) / 2
						- ((NBRICKS_PER_ROW - 1) * BRICK_SEP) / 2 + row
						* BRICK_WIDTH + row * BRICK_SEP;
				double startY = BRICK_Y_OFFSET + clmn * BRICK_HEIGHT + clmn
						* BRICK_SEP;
				// calculates start y-coordinate to draw a field of blocks
				GRect brick = new GRect(startX, startY, BRICK_WIDTH,
						BRICK_HEIGHT);
				pause(17);
				add(brick);
				brick.setFilled(true);
				switch (color) {
				case 1:
					brick.setFillColor(Color.RED);
					brick.setColor(Color.YELLOW);
					break;
				case 2:
					brick.setFillColor(Color.ORANGE);
					brick.setColor(Color.YELLOW);
					break;
				case 3:
					brick.setFillColor(Color.YELLOW);
					brick.setColor(Color.YELLOW);
					break;
				case 4:
					brick.setFillColor(Color.GREEN);
					brick.setColor(Color.YELLOW);
					break;
				case 5:
					brick.setFillColor(Color.CYAN);
					brick.setColor(Color.YELLOW);
					break;
				}
			}
			nextCol++; // changes the color

			if (colorPerRow == nextCol) { // if all needed rows are filled with
											// one color, the color is being
											// changed
				nextCol = 0;
				color++;
			}
			if (color == 6) { // if the last color is chosen - switch to the
								// first one
				color = 1;
			}

		}

	}
	/**loads an image of the heart*/
	public GImage heart0(int xCoord) {
		GImage life0 = new GImage("heart.png", xCoord, 0);
		return (life0);
	}
	/** gives a bonus to the player*/
	public void bonusMove() {
		if (good != null)
			good.move(0, 4);
		if (bad != null)
			bad.move(0, 4);
		if (standart != null)
			standart.move(0, 4);
		if (speed1 != null)
			speed1.move(0, 4);
		if (speed2 != null)
			speed2.move(0, 4);
		if (speed3 != null)
			speed3.move(0, 4);
		if (coin != null)
			coin.move(0, 4);
	}
	
	/** checks for collision with bonuses*/
	public void collideWithBonus() {
		if (good != null) {
			if (getElementAt(good.getX() + good.getWidth() / 2,
					good.getHeight() + good.getY()) == paddle) {
				remove(paddle);
				remove(good);
				paddle = new GImage("paddle3.png");
				paddle.scale(1, 0.7);
				add(paddle, good.getX() + good.getWidth() / 2, good.getHeight()
						+ good.getY());
				good = null;
				PADDLE_WIDTH = paddle.getWidth();
				PADDLE_HEIGHT = paddle.getHeight();
			}
		}
		if (bad != null) {
			if (getElementAt(bad.getX() + bad.getWidth() / 2, bad.getHeight()
					+ bad.getY()) == paddle) {
				remove(paddle);
				remove(bad);

				paddle = new GImage("paddle2.png");
				paddle.scale(1.5, 1);
				add(paddle, bad.getX() + bad.getWidth() / 2, bad.getHeight()
						+ bad.getY());
				bad = null;
				PADDLE_WIDTH = paddle.getWidth();
				PADDLE_HEIGHT = paddle.getHeight();
			}
		}
		if (standart != null) {
			if (getElementAt(standart.getX() + standart.getWidth() / 2,
					standart.getHeight() + standart.getY()) == paddle) {
				remove(paddle);
				remove(standart);

				paddle = new GImage("paddle1.png");
				add(paddle, standart.getX() + standart.getWidth() / 2,
						standart.getHeight() + standart.getY());
				standart = null;
				PADDLE_WIDTH = paddle.getWidth();
				PADDLE_HEIGHT = paddle.getHeight();
			}
		}
		if (speed1 != null) {
			if (getElementAt(speed1.getX() + speed1.getWidth() / 2,
					speed1.getHeight() + speed1.getY()) == paddle) {
				remove(speed1);
				speed1 = null;
				if (yVel > 0)
					yVel = yVel + 2;
				else
					yVel = yVel - 2;
			}
		}
		if (speed2 != null) {
			if (getElementAt(speed2.getX() + speed2.getWidth() / 2,
					speed2.getHeight() + speed2.getY()) == paddle) {
				remove(speed2);
				speed2 = null;
				if (yVel > 0)
					yVel = yVel - 2;
				else
					yVel = yVel + 2;
			}
		}
		if (speed3 != null) {
			if (getElementAt(speed3.getX() + speed3.getWidth() / 2,
					speed3.getHeight() + speed3.getY()) == paddle) {
				remove(speed3);
				speed3 = null;
				if (yVel > 0)
					yVel = 3;
				else
					yVel = -3;
			}
		}
		if (coin != null) {
			if (getElementAt(coin.getX() + coin.getWidth() / 2,
					coin.getHeight() + coin.getY()) == paddle) {
				remove(coin);
				coin = null;
				if (getElementAt(1, 10) != null
						&& getElementAt(1, 10) != background) {
					remove(getElementAt(1, 10));
				}
				GLabel counter = new GLabel(scoreLabel, 1, 24);
				counter.setFont("TimesNewRoman-30");
				add(counter);
				score += 5;
				scoreLabel = "Score: " + score;
			}
		}
	}

	boolean pullToUp;
	int rand = 4;
	int rand1 = 1;
	GImage good;
	GImage bad;
	GImage standart;
	GImage speed1;
	GImage speed2;
	GImage speed3;
	GImage coin;

	/** initializes all bonuses*/
	public void bonus() {
		rand = rgen.nextInt(1, 7);
		rand1 = rgen.nextInt(1, 2);
		if (rand == 1 && rand1 == 1 && good == null) {
			good = new GImage("good.png", ball.getX(), ball.getY());
			add(good);
		}
		if (rand == 2 && rand1 == 1 && bad == null) {
			bad = new GImage("bad.png", ball.getX(), ball.getY());
			add(bad);
		}
		if (rand == 3 && rand1 == 1 && standart == null) {
			standart = new GImage("standart.png", ball.getX(), ball.getY());
			add(standart);
		}
		if (rand == 4 && rand1 == 1 && speed1 == null) {
			speed1 = new GImage("speed+.png", ball.getX(), ball.getY());
			add(speed1);
		}
		if (rand == 5 && rand1 == 1 && speed2 == null) {
			speed2 = new GImage("speed-.png", ball.getX(), ball.getY());
			add(speed2);
		}
		if (rand == 6 && rand1 == 1 && speed3 == null) {
			speed3 = new GImage("speed=.png", ball.getX(), ball.getY());
			add(speed3);
		}
		if (rand == 7 && rand1 == 1 && coin == null) {
			coin = new GImage("oko.png", ball.getX(), ball.getY());
			add(coin);
		}
	}

	GRect button;

}