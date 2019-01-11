package FlappyBirdApp;
	
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class FlappyBird implements ActionListener, MouseListener, KeyListener {

	public static FlappyBird flappyBird; 
	public final int WIDTH = 800, HEIGHT = 800;
	public Renderer renderer; 
	public Rectangle bird;
	public ArrayList<Rectangle> walls;
	public ArrayList<Image> clouds; 
	public int ticks, yMotion, score;
	public boolean gameOver, started;
	public Random rand;
	
	public FlappyBird() {
		JFrame jframe = new JFrame();
		Timer timer = new Timer(20, this);

		renderer = new Renderer();
		rand = new Random();
		
		jframe.add(renderer);
		jframe.setTitle("Flappy Bird");
		jframe.setSize(WIDTH, HEIGHT);
		jframe.addMouseListener(this);
		jframe.addKeyListener(this);
		jframe.setResizable(false);
		jframe.setVisible(true);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
		walls = new ArrayList<Rectangle>();
		
		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);

		timer.start();
	}
	
	
	// A function that addColumn() to the game. 
	public void addColumn(boolean start) {
		int space = 300;
		int width = 100;
		int height = 50 + rand.nextInt(300);

		if (start) {
			walls.add(new Rectangle(WIDTH + width + walls.size() * 300, HEIGHT - height - 120, width, height));
			walls.add(new Rectangle(WIDTH + width + (walls.size() - 1) * 300, 0, width, HEIGHT - height - space));
		}
		else {
			walls.add(new Rectangle(walls.get(walls.size() - 1).x + 600, HEIGHT - height - 120, width, height));
			walls.add(new Rectangle(walls.get(walls.size() - 1).x, 0, width, HEIGHT - height - space));
		}
	}

	public void paintColumn(Graphics g, Rectangle column) {
		Color columnColor = new Color(255, 204, 0);
		g.setColor(columnColor);
		g.fillRect(column.x, column.y, column.width, column.height);
	}

	public void jump() {
		
		if (gameOver) {
			bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
			walls.clear();
			yMotion = 0;
			score = 0;

			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);
			
			gameOver = false;
		}

		if (!started) {
			started = true;
			
		} else if (!gameOver) {
			if (yMotion > 0) {
				yMotion = 0;
			}
			
			yMotion -= 10;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int speed = 10;
		ticks++;

		if (started) {
			for (int i = 0; i < walls.size(); i++) {
				Rectangle column = walls.get(i);
				column.x -= speed;
			}

			if (ticks % 2 == 0 && yMotion < 15) {
				yMotion += 2;
			}

			for (int i = 0; i < walls.size(); i++) {
				Rectangle column = walls.get(i);

				if (column.x + column.width < 0) {
					walls.remove(column);

					if (column.y == 0) {
						addColumn(false);
					}
				}
			}

			bird.y += yMotion;

			for (Rectangle column : walls) {
				if (column.y == 0 
						&& bird.x + bird.width / 2 > column.x + column.width / 2 - 10 
						&& bird.x + bird.width / 2 < column.x + column.width / 2 + 10) {
					score++;
				}

				if (column.intersects(bird)) {	
					gameOver = true;
					
					// Bird stays where it falls 
					if (bird.x <= column.x) { 
						bird.x = column.x - bird.width;	
					} else {
						if (column.y != 0) {
							bird.y = column.y - bird.height;
						} else if (bird.y < column.height) {
							bird.y = column.height;
						}
					}
				}
			}

			if (bird.y > HEIGHT - 120 || bird.y < 0) {
				gameOver = true;
			}

			if (bird.y + yMotion >= HEIGHT - 120) {
				bird.y = HEIGHT - 120 - bird.height;
				gameOver = true;
			}
		}
		renderer.repaint(); 
	}
	
	/** Paint + decorate */ 
	public void repaint(Graphics g) {
		Color background = new Color(85, 192, 214);
		g.setColor(background);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		Color chocolate = new Color(134, 89, 45);
		g.setColor(chocolate); 
		g.fillRect(0, HEIGHT - 120, WIDTH, 120);

		Color green = new Color(0, 128, 0);
		g.setColor(green);
		g.fillRect(0, HEIGHT - 120, WIDTH, 30);
	     
		Color birdColor = new Color(204, 51, 0);
		g.setColor(birdColor);
		g.fillRect(bird.x, bird.y, bird.width, bird.height);

		for (Rectangle column : walls) {
			paintColumn(g, column);
		}
		
		Color text = new Color(204, 51, 0);
		g.setColor(text);
		g.setFont(new Font("Arial", 1, 100));

		if (gameOver) {
			g.drawString("Game Over!", 100, HEIGHT / 2 - 50);
		}
	
		g.setColor(Color.white);
		g.setFont(new Font("Arial", 1, 50));
		
		if (gameOver) {
			g.drawString("Total Score: " + String.valueOf(score), 100, HEIGHT / 2 + 50);
		}	
		
		g.setColor(Color.white);
		g.setFont(new Font("Arial", 1, 100));
		
		if (!started) {
			g.drawString("Click to start!", 75, HEIGHT / 2 - 50);
		}
		
		if (!gameOver && started) {
			g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
		}

		
	}

	public static void main(String[] args) {
		flappyBird = new FlappyBird();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		jump();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			jump();
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {}

}
