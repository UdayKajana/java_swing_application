package com.jeditor;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;


public class RoundEdgedButton extends JButton {
    public RoundEdgedButton(String label) {
        super(label);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(Color.gray);
        } else {
            g.setColor(getBackground());
        }

        // Draw a round-edged shape for the button
        int width = getWidth();
        int height = getHeight();
        int diameter = Math.min(width, height);
        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, width, height, diameter, diameter);
        ((Graphics2D) g).fill(roundedRectangle);

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        int width = getWidth();
        int height = getHeight();
        int diameter = Math.min(width, height);
        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, width, height, diameter, diameter);
        ((Graphics2D) g).draw(roundedRectangle);
    }
}