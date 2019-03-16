/**
 * This is a "Drop!" game for the final project.
 * 
 * Programmer: Qifan Yang, Ruobing(Icy) Chen
 * Date: 12/3/2018
 */

import java.io.*;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class Jump implements ActionListener, MouseListener, KeyListener
{
    public static Jump jump;
    
    public final int WIDTH = 1600, HEIGHT = 800;
    
    public Renderer renderer;
    
    public Trajectory traj;
    
    public Rectangle player;
    
    public int ticks, yMotion, time, totalSpace, score, hForce, hForceLength, vForce, vForceLength, life, highestScore, count = 0, index = 0, gravityMode = 1;
    
    public boolean started, gameOver, jumped = false, landed = true, mousePressed, intersect = false;
    
    public ArrayList<Rectangle> columns;
    
    public ArrayList<Integer> spaces;
    
    public Random random;
    
    public Timer timer;
    
    public Jump()
    {
        JFrame jframe = new JFrame();
        timer = new Timer(20, this);
        
        renderer = new Renderer();
        random=new Random();
        traj = new Trajectory();
        
        jframe.setSize(WIDTH, HEIGHT);
        jframe.add(renderer);
        jframe.setVisible(true);
        jframe.setResizable(false);
        jframe.addMouseListener(this);
        jframe.addKeyListener(this);//set up of JFrame
        jframe.setTitle("Drop!");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exit when click the close botton
        
        player = new Rectangle(180, HEIGHT - 120 - 300 - 20, 20, 20); ///////cannot use columns.get(0).y because it happens before adding the first column//////
        columns = new ArrayList<Rectangle>();
        spaces = new ArrayList<Integer>();
        
        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true); //add 8 columns at the beginning
        life = 3; //players have three lives
              
        timer.start();
    }
    
    public void addColumn(boolean start)
    {
        int width = 100;
        int height = 50 + random.nextInt(300);
        int space = 150 + random.nextInt(700);
        
        if(start && columns.size()==0)
        {
            columns.add(new Rectangle(100, HEIGHT - 300 - 120, width, 300)); //add the first column at a specific position
        }
        else if(start)
        {
            columns.add(new Rectangle(columns.get(columns.size() - 1).x + space, HEIGHT - height - 120, width, height));
            spaces.add(space); //save the space value into the space arraylist
        }
    }
    
    public void paintColumn(Graphics g, Rectangle column)
    {
        g.setColor(Color.gray);
        g.fillRect(column.x, column.y, column.width, column.height);
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(started && !gameOver)
        {
        	if(ticks % 2 == 0 && yMotion < 30)
            {
                yMotion += 2;
            } //"gravity"
            player.y += yMotion; //change the player position 
        	
            if(player.y + yMotion >= HEIGHT - 120)
            {
                player.y = HEIGHT - 120 - player.height;
                gameOver = true;
                life--; 
                //restart = false;
            }
            
            if(mousePressed && count == 0)
            {
                hForce += 4; //show the magnitude of hForce       
            }
            else if(mousePressed && count == 1)
            {
            	vForce += 4;
            }
            
            hForceLength = hForce % 780;
            if(hForceLength >= 390)
            {
                hForceLength = 780 - hForceLength;
            }
            
            vForceLength = vForce % 780;
            if(vForceLength >= 390)
            {
                vForceLength = 780 - vForceLength;
            }
	        
            if(player.y >= (HEIGHT - 120 - columns.get(0).y) && ((player.x + 20) > columns.get(0).x && player.x < (columns.get(0).x + 100)))
            {
                player.y = columns.get(0).y - 20;
            } // make sure play can stand on the columns
            
            for(int i=0; i < columns.size(); i++)
            {
                if(columns.get(i).intersects(player) && (player.y) <= (columns.get(i).y) && !landed)
                {
                    landed = true;
                    hForce = 0;
                    vForce = 0;
                    index = 0;
                    player.x = 180;
                    for(int k = 0; k< i; k++)
                    {
                        score++;
                        totalSpace += spaces.get(0);
                        columns.remove(0); 
                        spaces.remove(0);
                        addColumn(true); //make the game infinity
                    }
                    
                    for(int l=0; l< columns.size(); l++)
                    {
                        columns.get(l).x = columns.get(l).x - totalSpace;
                    }
                    totalSpace = 0; //initialize
                }
                else if(columns.get(i).intersects(player) && (player.y) >= (columns.get(i).y) && !landed)
                {
                	intersect = true;
                	player.x = columns.get(i).x - 20;
                }
                }
            }
            
            if(jumped)
            {
                jumped = false;
            }
        
        if(!intersect && !landed && !gameOver)
        {
        	if(gravityMode == 1)
        	{
        		index++;
                player.x = (int) traj.getX(index); //3.58974 is the ratio of distance to hForce magnitude (1400/390)
                player.y = (int) traj.getY(index);
        	}
        	else if(gravityMode == 2)
        	{
        		player.x += hForceLength * 15 / 200;
        	}
        }
        
        renderer.repaint();
    }
    
    public void repaint(Graphics g)
    {
        g.setColor(Color.cyan);
        g.fillRect(0, 0, WIDTH, HEIGHT); //background
        
        g.setColor(Color.orange);
        g.fillRect(0, HEIGHT-120, WIDTH, 100); //ground
        
        g.setColor(Color.green.darker());
        g.fillRect(0, HEIGHT-120, WIDTH, 20); //grass
        
        g.setColor(Color.red);
        g.fillRect(player.x, player.y, player.width, player.height); //player
        
        g.setColor(Color.white);
        g.fillRect(WIDTH / 2 - 200, HEIGHT-90, 400, 50); //hForce
        g.fillRect(10, HEIGHT / 2 - 200, 50, 400);
        
        g.setColor(Color.red);
        g.fillRect(WIDTH / 2 - 195, HEIGHT-85, hForceLength, 40); //Magnitude of hForce
        g.fillRect(15, HEIGHT / 2 + 195 - vForceLength, 40, vForceLength);
        
        if(life == 3)
        {
            g.setColor(Color.red);
            g.fillRect(1560, 20, 20, 20);
            g.fillRect(1530, 20, 20, 20);
            g.fillRect(1500, 20, 20, 20);
        }
        
        if(life == 2)
        {
            g.setColor(Color.red);
            g.fillRect(1560, 20, 20, 20);
            g.fillRect(1530, 20, 20, 20);
            
            g.setColor(Color.gray);
            g.fillRect(1500, 20, 20, 20);
        }
        
        if(life == 1)
        {
            g.setColor(Color.red);
            g.fillRect(1560, 20, 20, 20);
            
            g.setColor(Color.gray);
            g.fillRect(1530, 20, 20, 20);
            g.fillRect(1500, 20, 20, 20);
        }
        
        if(life == 0)
        {
            g.setColor(Color.gray);
            g.fillRect(1560, 20, 20, 20);
            g.fillRect(1530, 20, 20, 20);
            g.fillRect(1500, 20, 20, 20);
        }
        
        for(Rectangle column : columns)
        {
            paintColumn(g, column);
        } //paint columns
         
        if(!started)
        {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", 1, 100));
            g.drawString("Press the mouse", 100, HEIGHT / 2-50);
            g.drawString("or hold any key to start!", 100, HEIGHT / 2+50);
        }
        
        if(gameOver && life >= 1)
        {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", 1, 100));
            g.drawString("Click to jump again.", 75, HEIGHT / 2-50);
        }
        
        
        if(gameOver && life == 0)
        {
            if(score >= highestScore)
            {
                highestScore = score;
            }
            g.setColor(Color.white);
            g.setFont(new Font("Arial", 1, 100));
            g.drawString("Game Over! Your Score: " + score, 75, HEIGHT / 2-50);

            g.setColor(Color.white);
            g.setFont(new Font("Arial", 1, 40));
            g.drawString("Your Highest Score: " + highestScore, 75, HEIGHT / 2);
            g.drawString("This game is programmed by Qifan Yang & Ruobing(Icy) Chen.", 75, HEIGHT / 2 + 50);
            
            g.setColor(Color.yellow);
            g.setFont(new Font("Arial", 1, 40));
            g.drawString("Click to restart the game.", 75, HEIGHT / 2 + 125);
        }
        
        if(!gameOver && started)
        {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", 1, 100));
            g.drawString(String.valueOf(score), WIDTH/2-25, 100); //draw the score on the top
        }
        
        if(count == 0)
        {
        	 g.setColor(Color.red);
             g.setFont(new Font("Impact", 1, 40));
             g.drawString("Horizontal Force", WIDTH / 2 - 500, HEIGHT - 50);
             
             g.setColor(Color.yellow);
             g.setFont(new Font("Impact", 1, 40));
             g.drawString("Vertical Force", 10, 180);
        }
        
        if(count == 1)
        {
        	 g.setColor(Color.yellow);
             g.setFont(new Font("Impact", 1, 40));
             g.drawString("Horizontal Force", WIDTH / 2 - 500, HEIGHT - 50);
             
             g.setColor(Color.red);
             g.setFont(new Font("Impact", 1, 40));
             g.drawString("Vertical Force", 10, 180);
        }
        
        g.setColor(Color.red);
        g.setFont(new Font("Impact", 1, 30));
        g.drawString("LIVES", 1420, 40);
    }
    
    public static void main(String[] args)
    {
        jump = new Jump();
    }
    
    public void pressed()
    {
    	started = true;     
        if(gameOver)
        {
            mousePressed = false; //when restart the game, this step won't trigger the hForce notation
        } //to restart the game
        
        if(landed)
        {
        	//hForce = 0;
        	mousePressed = true;
        }
    }
    
    public void released()
    {
    	if(landed && count == 0)
    	{
    		count++;
    		mousePressed = false;
    	}
    	else if(landed)
    	{
	    	jumped = true;
	        landed = false;
	        mousePressed = false;
	        intersect = false;
	        count = 0;
	        traj.newTraj(hForceLength, vForceLength, player.y);
	        if(gravityMode == 2)
	        {
	        	yMotion = -(vForceLength / 6);
	        }
        }
        
        if(gameOver && life > 0)
        {
            gameOver = false;
            jumped = false;
            landed = true;
            intersect = false;
            hForce = 0;
            vForce = 0;
            index = 0;
            
            player.x = 180;
            player.y = columns.get(0).y - 20; //Initialize the position of player
        }
        
        if(gameOver && life == 0)
        {
            gameOver = false;
            jumped = false; //make sure there is no error when restart the game (won't drop from the top)
            landed = true;
            intersect = false;
            score = 0; //clean the score from the last game
            
            columns.clear();
            spaces.clear(); //clean the game data from last game
            
            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true); //reload columns
            
            life = 3; //reset the lives
            hForce = 0;
            vForce = 0;
            index = 0;
            
            player.x = 180;
            player.y = columns.get(0).y - 20; //Initialize the position of player
        } //to restart the game
    }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
        
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
    	pressed();
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        released();
    }
    
    @Override
    public void mouseEntered(MouseEvent e)
    {
        
    }
    
    @Override
    public void mouseExited(MouseEvent e)
    {
        
    }
    
    public void keyPressed(KeyEvent e)
    {
    	if(e.getKeyCode() == KeyEvent.VK_1 || e.getKeyCode() == KeyEvent.VK_2)
    	{
    		
    	}
    	else
    	{
    		pressed();
    	}
    }
    
    public void keyReleased(KeyEvent e)
    {
    	if(e.getKeyCode() == KeyEvent.VK_1)
    	{
    		gravityMode = 1;
    	}
    	else if(e.getKeyCode() == KeyEvent.VK_2)
    	{
    		gravityMode = 2;
    	}
    	else
    	{
        	released();
    	}
    }
    
    public void keyTyped(KeyEvent e)
    {
        
    }
    
    public void gravity1()
    {
    	
    }
    
    public void gravity2()
    {
    	
    }
}
