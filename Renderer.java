 


/**
 * This is a renderer for the game.
 * 
 * Programmer: Qifan Yang, Ruobing(Icy) Chen
 * Date: 12/3/2018
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class Renderer extends JPanel
{
    private static final long serialversionUID=1L; //add a generl serial version UID
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        Jump.jump.repaint(g);
    }
}
