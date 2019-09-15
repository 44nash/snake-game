import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JOptionPane;

public class PortalCanvas extends Canvas implements Runnable, KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3065698597616690756L;
	private final int BOX_HEIGHT = 24;
	private final int BOX_WIDTH = 24;
	private final int GRID_WIDTH = 19;
	private final int GRID_HEIGHT = 19;
	private int speed = 100;

	private LinkedList<Point> snake;
	private Point fruit, fruit2;
	//private Point portal,portal2,portal3, portal6;
	private int direction = Direction.NO_DIRECTION;

	private Thread runThread;
	private int score = 0;
	private String highscore = "";
	private boolean isInMenu = true;
	private Image menuImage = null;
	private boolean EndGame = false;
	private boolean won = false;
	private int count = 0;
	private boolean HelpScreen = false;
	
	
	public void paint(Graphics g) {
		if (runThread == null) {
			this.setPreferredSize(new Dimension(640, 515));
			this.addKeyListener(this);
			runThread = new Thread(this);
			runThread.start();
		}
		if(isInMenu){
			DrawMenu(g);
		}else if(EndGame){
			DrawEndGame(g);
		}
		else if(HelpScreen){
			DrawHelp(g);
		}
		else{
			if(snake == null){
				snake = new LinkedList<Point>();
				DefaultSnake();
				PlaceFruit();
//				PlacePortal();
//				PlacePortal2();
//				PlacePortal3();
//				PlacePortal6();
			}
			if(highscore == ""){
				highscore = this.HighScore();
				System.out.println(highscore);
			}
//			DrawPortal(g);
//			DrawPortal2(g);
//			DrawPortal3(g);
//			DrawPortal6(g);
			DrawFruit(g);
			DrawGrid(g);
			DrawSnake(g);
			Score(g);
			Level(g);
		}
		}
	public void DrawEndGame(Graphics g){
		BufferedImage endGameImage = new BufferedImage(this.getPreferredSize().width,this.getPreferredSize().height,BufferedImage.TYPE_INT_ARGB);
		Graphics endGameGraphics = endGameImage.getGraphics();
		endGameGraphics.setColor(Color.BLACK);
		if(won)
			endGameGraphics.drawString("You Have Beat SnakePortal. Congratulations!", this.getPreferredSize().width/2, this.getPreferredSize().height/2);
		else if (!won && score == Integer.parseInt(highscore.split(":")[1]))
			endGameGraphics.drawString("You Have A New Highcore But You Still Lose!!", this.getPreferredSize().width/2, this.getPreferredSize().height/2);
		else if(count==5)
			endGameGraphics.drawString("You Ran Over Too Many Portals. I'm Calling The Mercy Rule!", (this.getPreferredSize().width/2)-125, this.getPreferredSize().height/2);
		else
			endGameGraphics.drawString("YOU SUCK! LOSER!!", this.getPreferredSize().width/2, this.getPreferredSize().height/2);
		endGameGraphics.drawString("Your Score: "+score, this.getPreferredSize().width/2, (this.getPreferredSize().height/2) + 20);
		endGameGraphics.drawString("Press \"SPACE\" to start a new game!", this.getPreferredSize().width/2, (this.getPreferredSize().height/2) + 40);
		g.drawImage(endGameImage, 0, 0, this);
	}
	public void DrawHelp(Graphics g){
		BufferedImage HelpImage = new BufferedImage(this.getPreferredSize().width,this.getPreferredSize().height,BufferedImage.TYPE_INT_ARGB);
		Graphics HelpGraphics = HelpImage.getGraphics();
		HelpGraphics.setColor(Color.BLACK);
		HelpGraphics.drawString("Use Arrow Keys To Move. Avoid The Portals And Collect The Apples. Press \"SPACE\" To Go Back.", GRID_WIDTH / 2, GRID_HEIGHT / 2);
		g.drawImage(HelpImage, 0, 0, this);
	}
	public void DrawMenu(Graphics g){
		if(this.menuImage == null){
		try{
			URL imagePath = PortalCanvas.class.getResource("icon.jpg");
			menuImage = Toolkit.getDefaultToolkit().getImage(imagePath);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
		g.drawImage(menuImage, 0,0,640,515,this);
	}
	public void update(Graphics g){
		Graphics offScreenGraphics; //draws off screen
		BufferedImage offscreen = null;
		Dimension d = this.getSize();
		
		offscreen = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		offScreenGraphics = offscreen.getGraphics();
		offScreenGraphics.setColor(this.getBackground());
		offScreenGraphics.fillRect(0, 0, d.width, d.height);
		offScreenGraphics.setColor(this.getForeground());
		paint(offScreenGraphics);
		
		//flip
		g.drawImage(offscreen,0,0,this);
	}
	public void Snake(){
		snake.add(new Point(GRID_WIDTH / 2, GRID_HEIGHT / 2));
		snake.add(new Point(GRID_WIDTH / 2, (GRID_HEIGHT / 2) + 1));
		snake.add(new Point(GRID_WIDTH / 2, (GRID_HEIGHT / 2) + 2));
	}

	public void DefaultSnake() {
		score = 0;
		count = 0;
		snake.clear();
		Snake();
		direction = Direction.NO_DIRECTION;
	}
	public void LevelUp(){
		if(score > 50){
			speed = 90;
		}else if(score > 100){
			speed = 70;
		}else if(score > 150){
			speed = 50;
		}else if(score > 250){
			speed = 40;
		}else if(score > 350){
			speed = 30;
		}
	}
	public void Level(Graphics g){
		if(direction == Direction.NO_DIRECTION){
			g.drawString("Press \"H\" To Learn How To Play",GRID_WIDTH / 2, (GRID_HEIGHT / 2) + 4);
		}
		if(score >= 50){
			g.drawString("LEVEL 2!", 0,BOX_HEIGHT * GRID_HEIGHT + 45 );
		}else if(score >= 100){
			g.drawString("       LEVEL 3!", 0,BOX_HEIGHT * GRID_HEIGHT + 45 );
		}else if(score >= 150){
			g.drawString("              LEVEL 4!", 0,BOX_HEIGHT * GRID_HEIGHT + 45 );
		}else if(score >= 250){
			g.drawString("                     LEVEL 5!", 0,BOX_HEIGHT * GRID_HEIGHT + 45 );
		}else if(score >= 350){
			g.drawString("                            LEVEL....DOESN'T MATTER, YOU CAN'T WIN!", 0,BOX_HEIGHT * GRID_HEIGHT + 45 );
	}
	}
	
	
	public void Move() {
		if(direction == Direction.NO_DIRECTION){
			return;
		}
		Point head = snake.peekFirst();
		Point newPoint = head;
		switch (direction) {
		case Direction.NORTH:
			newPoint = new Point(head.x, head.y - 1);
			break;
		case Direction.SOUTH:
			newPoint = new Point(head.x, head.y + 1);
			break;
		case Direction.EAST:
			newPoint = new Point(head.x + 1, head.y);
			break;
		case Direction.WEST:
			newPoint = new Point(head.x - 1, head.y);
			break;
		}
		if(this.direction != Direction.NO_DIRECTION)
			snake.remove(snake.peekLast());
//		if(newPoint.equals(portal)){
//			score -= 20;
//			count += 1;
//			switch (direction) {
//			case Direction.NORTH:
//				newPoint = new Point(head.x, head.y - 1);
//				break;
//			case Direction.SOUTH:
//				newPoint = new Point(head.x, head.y + 1);
//				break;
//			case Direction.EAST:
//				newPoint = new Point(head.x + 1, head.y);
//				break;
//			case Direction.WEST:
//				newPoint = new Point(head.x - 1, head.y);
//				break;
//			}
//			PlacePortal();
//			PlacePortal2();
//			PlaceFruit();
//		}
//		if(newPoint.equals(portal2)){
//			score -= 10;
//			count += 1;
//			switch (direction) {
//			case Direction.NORTH:
//				newPoint = new Point(head.x, head.y - 1);
//				break;
//			case Direction.SOUTH:
//				newPoint = new Point(head.x, head.y + 1);
//				break;
//			case Direction.EAST:
//				newPoint = new Point(head.x + 1, head.y);
//				break;
//			case Direction.WEST:
//				newPoint = new Point(head.x - 1, head.y);
//				break;
//			}
//			PlacePortal();
//			PlacePortal2();
//			PlacePortal3();
//			PlaceFruit();
//		}
//		if(newPoint.equals(portal3)){
//			snake.remove(snake.peekLast());
//			switch (direction) {
//			case Direction.NORTH:
//				newPoint = new Point(head.x, head.y - 1);
//				break;
//			case Direction.SOUTH:
//				newPoint = new Point(head.x, head.y + 1);
//				break;
//			case Direction.EAST:
//				newPoint = new Point(head.x + 1, head.y);
//				break;
//			case Direction.WEST:
//				newPoint = new Point(head.x - 1, head.y);
//				break;
//			}
//			PlacePortal();
//			PlacePortal2();
//			PlacePortal3();
//		}

		if (newPoint.equals(fruit)) {
			// Snake hits fruit
			score += 10;
			LevelUp();
			Point addPoint = (Point) newPoint.clone();
//			PlacePortal();
//			PlacePortal2();
//			PlacePortal3();
			switch (direction) {
			case Direction.NORTH:
				newPoint = new Point(head.x, head.y - 1);
				break;
			case Direction.SOUTH:
				newPoint = new Point(head.x, head.y + 1);
				break;
			case Direction.EAST:
				newPoint = new Point(head.x + 1, head.y);
				break;
			case Direction.WEST:
				newPoint = new Point(head.x - 1, head.y);
				break;
			}
			snake.push(addPoint);
			PlaceFruit();
//			PlacePortal();
//			PlacePortal2();
//			PlacePortal3();
		} else if (newPoint.x < 0 || newPoint.x > (GRID_WIDTH - 1)) {
			// Out Of Bounds, reset game
			CheckScore();
			won = false;
			EndGame = true;
			return;
		} else if (newPoint.y < 0 || newPoint.y > (GRID_HEIGHT - 1)) {
			// Out Of Bounds, reset game
			CheckScore();
			won = false;
			EndGame = true;
			return;
		} else if (snake.contains(newPoint)) {
			// Ran into Ourselves, reset game
			if(direction != Direction.NO_DIRECTION){
				CheckScore();
				won = false;
				EndGame = true;
				return;
			}
		}else if(snake.size() == (GRID_WIDTH * GRID_HEIGHT)){
			CheckScore();
			won = true;
			EndGame = true;
			return;
		}else if(count==5){
			CheckScore();
			won = false;
			EndGame = true;
			return;
		}
		// still playing
		snake.push(newPoint);
	}

	public void Score(Graphics g) {
		g.drawString("Score: " + score, 0, BOX_HEIGHT * GRID_HEIGHT + 15);
		g.drawString("Highscore: "+ highscore, 0,BOX_HEIGHT * GRID_HEIGHT + 30 );
	}
	public void CheckScore(){
		System.out.println(highscore);
		if(score >= Integer.parseInt(highscore.split(":")[1])){
			String name = JOptionPane.showInputDialog("Congratulations! New Highscore! What is your name?");
			highscore = name + ":" + score;
			
			File scoreFile = new File("highscore.txt");
			if(!scoreFile.exists()){
				try {
					scoreFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			FileWriter writeFile = null;
			BufferedWriter writer = null;
			try{
				writeFile = new FileWriter(scoreFile);
				writer = new BufferedWriter(writeFile);
				writer.write(this.highscore);
			}
			catch(Exception e){
				
			}
			finally{
				try{
					if(writer != null){
						writer.close();
					}
				}
					catch(Exception e){}
				}
			}
		}
	

	public void DrawGrid(Graphics g) {
		// Outside Rectangle
		g.drawRect(0, 0, GRID_WIDTH * BOX_WIDTH, GRID_HEIGHT * BOX_HEIGHT);
		// Vertical Lines
		for (int x = BOX_WIDTH; x < GRID_WIDTH * BOX_WIDTH; x += BOX_WIDTH) {
			g.drawLine(x, 0, x, BOX_HEIGHT * GRID_HEIGHT);
		}
		// Horizontal Lines
		for (int y = BOX_HEIGHT; y < GRID_HEIGHT * BOX_HEIGHT; y += BOX_HEIGHT) {
			g.drawLine(0, y, GRID_WIDTH * BOX_WIDTH, y);
		}
	}

	public void DrawSnake(Graphics g) {
		g.setColor(Color.CYAN);
		for (Point p : snake) {
			g.fillRect(p.x * BOX_WIDTH, p.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
		}
		g.setColor(Color.BLACK);
	}

	public void DrawFruit(Graphics g) {
		g.setColor(Color.RED);
		g.fillOval(fruit.x * BOX_WIDTH, fruit.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
		g.setColor(Color.BLACK);
	}

//	public void DrawPortal(Graphics g){
//		g.setColor(Color.BLACK);
//		g.fillOval(portal.x * BOX_WIDTH, portal.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
//		g.setColor(Color.BLUE);
//	}
//	public void DrawPortal6(Graphics g){
//		g.setColor(Color.CYAN);
//		g.fillOval(portal.x * BOX_WIDTH, portal.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
//		g.setColor(Color.BLUE);
//	}
//	public void DrawPortal2(Graphics g){
//		g.setColor(Color.BLUE);
//		g.fillOval(portal2.x * BOX_WIDTH, portal2.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
//		g.setColor(Color.BLUE);
//	}
//	public void DrawPortal3(Graphics g){
//		g.setColor(Color.GREEN);
//		g.fillOval(portal3.x * BOX_WIDTH, portal3.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
//		g.setColor(Color.BLUE);
//	}
//	public void PlacePortal(){
//		Random rand = new Random();
//		int randomX = rand.nextInt(GRID_WIDTH - 1);
//		int randomY = rand.nextInt(GRID_HEIGHT - 1);
//		Point randomPoint = new Point(randomX, randomY);
//		while (snake.contains(randomPoint)) {
//			randomX = rand.nextInt(GRID_WIDTH - 1);
//			randomY = rand.nextInt(GRID_HEIGHT - 1);
//			randomPoint = new Point(randomX, randomY);
//		}
//		portal = randomPoint;
//	}
//	public void PlacePortal2(){
//		Random rand2 = new Random();
//		int randomW = rand2.nextInt(GRID_WIDTH - 1);
//		int randomZ = rand2.nextInt(GRID_HEIGHT - 1);
//		Point randomPoint2 = new Point(randomW, randomZ);
//		while (snake.contains(randomPoint2)) {
//			randomW = rand2.nextInt(GRID_WIDTH - 1);
//			randomZ = rand2.nextInt(GRID_HEIGHT - 1);
//			randomPoint2 = new Point(randomW, randomZ);
//		}
//		portal2 = randomPoint2;
//	}
//	public void PlacePortal6(){
//		Random rand6 = new Random();
//		int randomW = rand6.nextInt(GRID_WIDTH - 1);
//		int randomZ = rand6.nextInt(GRID_HEIGHT - 1);
//		Point randomPoint2 = new Point(randomW, randomZ);
//		while (snake.contains(randomPoint2)) {
//			randomW = rand6.nextInt(GRID_WIDTH - 1);
//			randomZ = rand6.nextInt(GRID_HEIGHT - 1);
//			randomPoint2 = new Point(randomW, randomZ);
//		}
//		portal6 = randomPoint2;
//	}
//	public void PlacePortal3(){
//		Random rand3 = new Random();
//		int randomA = rand3.nextInt(GRID_WIDTH - 1);
//		int randomB = rand3.nextInt(GRID_HEIGHT - 1);
//		Point randomPoint3 = new Point(randomA, randomB);
//		while (snake.contains(randomPoint3)) {
//			randomA = rand3.nextInt(GRID_WIDTH - 1);
//			randomB = rand3.nextInt(GRID_HEIGHT - 1);
//			randomPoint3 = new Point(randomA, randomB);
//		}
//		portal3 = randomPoint3;
//	}

	public void PlaceFruit() {
		Random rand = new Random();
		int randomX = rand.nextInt(GRID_WIDTH - 1);
		int randomY = rand.nextInt(GRID_HEIGHT - 1);
		Point randomPoint = new Point(randomX, randomY);
		while (snake.contains(randomPoint)) {
			randomX = rand.nextInt(GRID_WIDTH - 1);
			randomY = rand.nextInt(GRID_HEIGHT - 1);
			randomPoint = new Point(randomX, randomY);
		}
		fruit = randomPoint;
	}

	@Override
	public void run() {
		while (true) {
			// runs forever
			repaint();
			if(!isInMenu && !EndGame){
				Move();
			}
			

			try {
				Thread.currentThread();
				Thread.sleep(speed);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			if (direction != Direction.SOUTH)
				direction = Direction.NORTH;
			break;
		case KeyEvent.VK_DOWN:
			if (direction != Direction.NORTH)
				direction = Direction.SOUTH;
			break;
		case KeyEvent.VK_RIGHT:
			if (direction != Direction.WEST)
				direction = Direction.EAST;
			break;
		case KeyEvent.VK_LEFT:
			if (direction != Direction.EAST)
				direction = Direction.WEST;
			break;
		case KeyEvent.VK_ENTER:
			if(isInMenu){
				isInMenu = false;
				repaint();
			}
			break;
		case KeyEvent.VK_P:
			isInMenu = true;
			break;
		case KeyEvent.VK_SPACE:
			if(EndGame || HelpScreen){
				EndGame = false;
				HelpScreen = false;
				won = false;
				DefaultSnake();
				repaint();
			}
			break;
		case KeyEvent.VK_H:
			HelpScreen = true;
			break;
		}
	}
	
	public String HighScore(){
		FileReader readFile = null;
		BufferedReader reader = null;
		try{
			readFile = new FileReader("highscore.txt");
			reader = new BufferedReader(readFile);
			return reader.readLine();
		}
		catch(Exception e){
			return "N/A:0";
		}
		finally{
			try {
				if(reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent arg0) {


	}
}

