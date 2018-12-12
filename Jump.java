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


public class Jump implements ActionListener, MouseListener
{
    public static Jump jump;
    
    public final int WIDTH = 1600, HEIGHT = 800;
    
    public Renderer renderer;
    
    public Rectangle player;
    
    public int ticks, yMotion, time, totalSpace, score, force, forceLength, life, highestScore;
    
    public boolean started, gameOver, jumped, landed, mousePressed;
    
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
        
        jframe.setSize(WIDTH, HEIGHT);
        jframe.add(renderer);
        jframe.setVisible(true);
        jframe.setResizable(false);
        jframe.addMouseListener(this); //set up of JFrame
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
            if(ticks % 2 ==0 && yMotion < 15)
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
            
            if(mousePressed)
            {
                force += 4; //show the magnitude of force
            }
            
            forceLength = force % 780;
            if(forceLength >= 390)
            {
                forceLength = 780 - forceLength;
            }
            
            //time = (int) (forceLength * 3.64); //pass the force magnitude to the distance
            
            if(player.y >= (HEIGHT - 120 - columns.get(0).y) && ((player.x + 20) > columns.get(0).x && player.x < (columns.get(0).x + 100)))
            {
                player.y = columns.get(0).y - 20;
            } // make sure play can stand on the columns
            
            for(int i=0; i < columns.size(); i++)
            {
                
                if(columns.get(i).intersects(player) && (player.y) >= (columns.get(i).y) && !landed)
                {
                    landed = true;
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
            }
            
            if(jumped)
            {
                jumped = false;
                player.x = (int) (180 + (forceLength * 3.58974)); //3.58974 is the ratio of distance to force magnitude (1400/390)
                player.y = 100;
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
        g.fillRect(WIDTH / 2 - 200, HEIGHT-90, 400, 50); //force
        
        g.setColor(Color.red);
        g.fillRect(WIDTH / 2 - 195, HEIGHT-85, forceLength, 40); //magintude of force
        
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
            g.drawString("Press the mouse to start!", 100, HEIGHT / 2-50);
        }
        
        if(gameOver && life > 1)
        {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", 1, 100));
            g.drawString("Click to jump again.", 75, HEIGHT / 2-50);
        }
        
        if(gameOver && life == 1)
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
        
        g.setColor(Color.red);
        g.setFont(new Font("Impact", 1, 40));
        g.drawString("FORCE", WIDTH / 2 - 320, HEIGHT - 50);
        
        g.setColor(Color.red);
        g.setFont(new Font("Impact", 1, 30));
        g.drawString("Life", 1440, 40);
    }
    
    public static void main(String[] args)
    {
        jump = new Jump();
    }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
        
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
        started = true;
        mousePressed = true;
        force = 0;
        if(gameOver)
        {
            mousePressed = false; //when restart the game, this step won't trigger the force notation
        } //to restart the game
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        jumped = true;
        landed = false;
        mousePressed = false;
        
        if(gameOver && life > 0)
        {
            gameOver = false;
            jumped = false;
            
            player.x = 180;
            player.y = columns.get(0).y - 20; //initiallize the position of player
        }
        
        if(gameOver && life == 0)
        {
            gameOver = false;
            jumped = false; //make sure there is no error when restart the game (won't drop from the top)
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
            
            player.x = 180;
            player.y = columns.get(0).y - 20; //initiallize the position of player
        } //to restart the game
    }
    
    @Override
    public void mouseEntered(MouseEvent e)
    {
        
    }
    
    @Override
    public void mouseExited(MouseEvent e)
    {
        
    }
    
}
