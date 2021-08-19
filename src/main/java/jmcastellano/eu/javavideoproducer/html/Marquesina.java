/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.javavideoproducer.html;

import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JLabel;

/**
 *
 * @author rpgdragon
 */
public class Marquesina extends JLabel {
    
    public static final int MARQUEE_SPEED_DIV = 10;
    public static final int REPAINT_WITHIN_MS = 5;

    /**
     * 
     */
    private static final long serialVersionUID = -7737312573505856484L;

    /**
     * 
     */
    public Marquesina() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param image
     * @param horizontalAlignment
     */
    public Marquesina(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param image
     */
    public Marquesina(Icon image) {
        super(image);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param text
     * @param icon
     * @param horizontalAlignment
     */
    public Marquesina(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param text
     * @param horizontalAlignment
     */
    public Marquesina(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param text
     */
    public Marquesina(String text) {
        super(text);
    }



    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
       g.translate((int) (getWidth() - (System.currentTimeMillis() / MARQUEE_SPEED_DIV) % (getWidth() * 2)), 0);  
        super.paintComponent(g);
        repaint(REPAINT_WITHIN_MS);
    }
}
