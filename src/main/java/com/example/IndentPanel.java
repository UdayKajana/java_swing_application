package com.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.example.ComponentProvider.BACKGROUND;

public class IndentPanel {
    private static int colorSeq = 0;
    Dimension dimension;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    LinkedHashMap<Integer, JPanel> propertyPanels = new LinkedHashMap<>();
    private int order;
    private JPanel iPanel;
    private ArrayList components = new ArrayList();
    private int seq;

    public IndentPanel(IndentPanel parent, int indent) {
        this.order = 0;
        int indentGap = ((int) screenSize.getWidth() / 50);
        iPanel = new JPanel();
        iPanel.setLayout(new BoxLayout(iPanel, BoxLayout.Y_AXIS));
        iPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, (indentGap * indent), 2, 0),
                BorderFactory.createLineBorder(ComponentProvider.colorPalette.get(((colorSeq++) % ComponentProvider.colorPalette.size())),1)));
        iPanel.setAlignmentX(1);
        iPanel.setBackground(BACKGROUND);
        Component[] components1 = iPanel.getComponents();
        iPanel.add(Box.createHorizontalGlue());
        iPanel.setName("IndentPanel" + seq++);
        iPanel.setVisible(true);
        refresh();
    }

    public IndentPanel(JPanel parent, int indent) {
        this.order = 0;
        int indentGap = ((int) screenSize.getWidth() / 50);
        iPanel = new JPanel();
        iPanel.setLayout(new BoxLayout(iPanel, BoxLayout.Y_AXIS));
        iPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, (indentGap * indent), 2, 0),
                BorderFactory.createLineBorder(ComponentProvider.colorPalette.get(((colorSeq++) % ComponentProvider.colorPalette.size())))));
        iPanel.setAlignmentX(1);
        iPanel.setBackground(BACKGROUND);
        iPanel.add(Box.createHorizontalGlue());
        iPanel.setName("IndentPanel" + seq++);
        iPanel.setVisible(true);
        parent.add(iPanel);
        refresh();
    }

    public JPanel getIndentPanel() {
        return iPanel;
    }

    public void addPropertyPanel(PropertyPanel propPanel) {
        iPanel.add(propPanel.getPropertyPanel());
        refresh();
        components.add(propPanel);
    }

    void refresh() {
        iPanel.revalidate();
        iPanel.repaint();
        iPanel.setVisible(true);
    }

    StringBuilder getContents() {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<components.size();i++) {
            Object content = components.get(i);
            if (content instanceof IndentPanel) {
                sb.append("{\n").append(((IndentPanel) content).getContents());
            } else {
                sb.append(content.toString()).append(i==components.size()-1?"\n":",\n");
            }
        }
        return sb.append("}\n");
    }

    public void addIndentPanel(IndentPanel Panel) {
        iPanel.add(Panel.getIndentPanel());
        components.add(Panel);
    }
}
