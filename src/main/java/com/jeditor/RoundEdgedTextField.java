package com.jeditor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.RoundRectangle2D;

public class RoundEdgedTextField extends JTextField {

    private String placeholder;

    public RoundEdgedTextField(String placeholder) {
        this.placeholder = placeholder;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!isOpaque() && getBorder() instanceof EmptyBorder) {
            Graphics2D g2 = (Graphics2D) g.create();

            int width = getWidth();
            int height = getHeight();
            int diameter = Math.min(width, height);

            if (isFocusOwner()) {
                g2.setColor(new Color(100, 100, 100));
            } else {
                g2.setColor(new Color(150, 150, 150));
            }

            RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, width, height, diameter, diameter);
            g2.fill(roundedRectangle);
            super.paintComponent(g2);
            g2.dispose();
        } else {
            super.paintComponent(g);
        }

        if (getText().isEmpty() && !isFocusOwner()) {
            FontMetrics fm = g.getFontMetrics();
            g.setColor(getDisabledTextColor());
            int x = getInsets().left;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(placeholder, x, y);
        }
    }
}
