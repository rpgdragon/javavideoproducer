/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.javavideoproducer.html;

import java.awt.Graphics;
import javax.swing.JLabel;
import java.awt.Color;

/**
 *
 * @author rpgdragon
 */
public class Marquesina extends JLabel {
    
   /**
	 * 
	 */
	private static final long serialVersionUID = -7531412382260951998L;
public static int LEFT_TO_RIGHT = 1;  
   public static int RIGHT_TO_LEFT = 2;    
   private final int Option;  
   private final int Speed;

   public Marquesina(String text, int Option, int Speed) {  
     this.Option = Option;  
     this.Speed = Speed;  
     super.setText(text);
   }
  
   @Override  
   protected void paintComponent(Graphics g) {
     g.setColor(Color.white);
     g.clearRect(0, 0, getWidth(), getHeight());
     g.setColor(Color.black);
     if (Option == LEFT_TO_RIGHT) {  
       g.translate((int) ((System.currentTimeMillis() / Speed) % (getWidth() * 2) - getWidth()), 0);  
     } else if (Option == RIGHT_TO_LEFT) {  
       g.translate((int) (getWidth() - (System.currentTimeMillis() / Speed) % (getWidth() * 2)), 0);  
     }
     super.paintComponent(g);  
     repaint(5);  
   } 
}
