package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.Editor.objectNode;

public class ComponentProvider {

    static ArrayList<Color> colorPalette = new ArrayList<>();
    static Color KEY = new Color(156, 220, 254);
    static Color BACKGROUND = new Color(30, 30, 30);
    static Color VALUE = new Color(206, 145, 120);
    static int seq = 0;
    static float indentGap = 0;
    static JPanel PANEL_FILE = null, PANEL_JSON = null/*, PANEL_ERROR = null*/;
    Map<JTextField, String> details = new LinkedHashMap<JTextField, String>();
    int colorSeq = 0;
    JPanel homePanel = null;

    public ComponentProvider() {
        colorPalette.add(new Color(156, 39, 176));      // Purple
        colorPalette.add(new Color(255, 193, 7));       // Amber
        colorPalette.add(new Color(76, 175, 80));       // Green
        colorPalette.add(new Color(0, 188, 212));       // Cyan
        colorPalette.add(new Color(233, 30, 99));       // Pink
        colorPalette.add(new Color(96, 125, 139));      // Blue Gray
        colorPalette.add(new Color(255, 152, 0));       // Deep Orange
        colorPalette.add(new Color(0, 150, 136));      // Tea
        colorPalette.add(new Color(103, 58, 183));     // Deep Purple
        colorPalette.add(new Color(255, 160, 0));      // Orange
        colorPalette.add(new Color(121, 85, 72));      // Brown
        colorPalette.add(new Color(255, 202, 40));     // Yellow
        colorPalette.add(new Color(244, 67, 54));      //Red
    }

    public JPanel getHomePanel() {
        if (homePanel == null) {
            JFrame homeFrame = new JFrame("Json Editor");
            homeFrame.setLayout(new BoxLayout(homeFrame.getContentPane(), BoxLayout.Y_AXIS));
            homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            homeFrame.setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());
            homePanel = getIndentPanel(1);
            indentGap = (float) screenSize.getWidth() / 50;
            homePanel.setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());
            homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
            homePanel.setBorder(new LineBorder(colorPalette.get(((colorSeq++) % colorPalette.size())), 0));
            //homePanel.setName("PAN_HOME");
            JScrollPane homeScrollPane = getScrollablePane(homePanel);
            homeFrame.add(homeScrollPane);
            homeFrame.setVisible(true);
        }
        return homePanel;
    }

    public JPanel getIndentPanel(int indent) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, (int) (indentGap * indent), 2, 0),
                BorderFactory.createLineBorder(colorPalette.get(((colorSeq++) % colorPalette.size())))));
        panel.setAlignmentX(1);
        panel.setBackground(ComponentProvider.BACKGROUND);
        panel.add(Box.createHorizontalGlue());
        //panel.setName("PAN_INDENT" + seq++);
        panel.setVisible(true);
        return panel;
    }

    public JPanel getPropertyPanel(int index, JsonNode jsonNode, String key, String value, boolean isALabel) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(ComponentProvider.BACKGROUND);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        panel.add(Box.createHorizontalGlue());
        panel.setVisible(true);
        panel.setBorder(null);
        if (!isALabel) {
            panel.add(new JLabel(""));
        }
        JTextField keyField = getKVTextField(index, jsonNode, key, value, true, true);
        JTextField dotField = getKVTextField(index, jsonNode, key, " : ", false, false);
        JTextField valueField = getKVTextField(index, jsonNode, key, value, false, !isALabel);
        keyField.putClientProperty("valueTF",valueField);
        valueField.putClientProperty("keyTF",keyField);
        keyField.putClientProperty("jsonNode",jsonNode);
        valueField.putClientProperty("jsonNode", jsonNode);
        panel.add(keyField);
        panel.add(dotField);
        panel.add(valueField);
        return panel;
    }

    public JPanel getFilePanel() {
        if (ComponentProvider.PANEL_FILE == null) {
            PANEL_FILE = new JPanel();
            PANEL_FILE.setBackground(ComponentProvider.BACKGROUND);
            PANEL_FILE.setLayout(new BoxLayout(PANEL_FILE, BoxLayout.X_AXIS));
            PANEL_FILE.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            PANEL_FILE.setBorder(new EmptyBorder(20, 20, 20, 20));
            //PANEL_FILE.setName("PAN_FILE");
            PANEL_FILE.setVisible(true);
        }
        return PANEL_FILE;
    }

    public JPanel getJsonPanel() {
        if (ComponentProvider.PANEL_JSON == null) {
            PANEL_JSON = getIndentPanel(1);
            //PANEL_JSON.setName("PAN_JSON");
        }
        return PANEL_JSON;
    }

    public JTextField getKVTextField(int index, JsonNode jsonNode, String key, String value, boolean isKey, boolean editable) {
        JTextField textField = new JTextField();
        textField.setEditable(editable);
        if(isKey) textField.setText(key);
        else textField.setText(value);
        textField.setAlignmentX(1);
        textField.setBorder(null);
        textField.setFont(new Font("Verdana", editable ? Font.ITALIC : Font.BOLD, 16));
        textField.setBackground(ComponentProvider.BACKGROUND);
        textField.setForeground(isKey ? ComponentProvider.KEY : ComponentProvider.VALUE);
        if (editable) {
            textField.putClientProperty("key", key);
            textField.putClientProperty("value", value);
            UndoManager undoManager = new UndoManager();
            Action undoAction = new AbstractAction("Undo") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (undoManager.canUndo()) {
                        undoManager.undo();
                    }
                }
            };
            Action redoAction = new AbstractAction("Redo") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (undoManager.canRedo()) {
                        undoManager.redo();
                    }
                }
            };
            textField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "undo");
            textField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "redo");
            textField.getActionMap().put("undo", undoAction);
            textField.getActionMap().put("redo", redoAction);
            textField.getDocument().addUndoableEditListener(e -> {
                if (e.getEdit() instanceof AbstractDocument.DefaultDocumentEvent) {
                    undoManager.addEdit(e.getEdit());
                }
            });
            textField.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateJsonObject();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateJsonObject();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    // Plain text components do not fire these events
                }

                private void updateJsonObject() {
                    if(jsonNode.isObject()){
                        ObjectNode objectNode1 = (ObjectNode) jsonNode;
                        String presentKey = (key.length() >= 2 && key.startsWith("\"") && key.endsWith("\"")) ? key.substring(1, key.length() - 1) : key;
                        JsonNode child = objectNode1.get(presentKey);
                        if (isKey) {
                            objectNode1.remove(presentKey);
                            String updatedKey = (textField.getText().length() >= 2 && textField.getText().startsWith("\"") && textField.getText().endsWith("\"")) ? textField.getText().substring(1, textField.getText().length() - 1) : textField.getText();
                            textField.putClientProperty("key",updatedKey);
                            ObjectNode keyValueNode = JsonNodeFactory.instance.objectNode();
                            keyValueNode.set(updatedKey, child);
                            System.out.println("index = " + index);
                        } else {
                            String v = textField.getText();
                            v = (v.length() >= 2 && v.startsWith("\"") && v.endsWith("\"")) ? v.substring(1, v.length() - 1) : v;
                            textField.putClientProperty("value",v);
                            JsonNode jsonNode = TextNode.valueOf(v);
                            objectNode1.set(presentKey, jsonNode);
                            System.out.println("index = " + index);
                        }
                    }
                }
            });
            textField.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    textField.requestFocus();
                    String text = textField.getText();
                    int start = Math.min(1, text.length()), end = Math.max(0, text.length() - 1);
                    if (textField.getText().charAt(0) == '"' && textField.getText().charAt(textField.getText().length() - 1) == '"') {
                        textField.setSelectionStart(start);
                        textField.setSelectionEnd(end);
                        textField.moveCaretPosition(end);
                    } else {
                        textField.selectAll();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    textField.removeAll();
                }
            });
        }
        textField.setVisible(true);
        return textField;
    }

    public RoundEdgedButton getButton(String text, Color bgColor, Color fgColor) {
        RoundEdgedButton roundEdgedButton = new RoundEdgedButton(text);
        roundEdgedButton.setBackground(bgColor);
        roundEdgedButton.setForeground(fgColor);
        //roundEdgedButton.setName(text);
        roundEdgedButton.setVisible(true);
        return roundEdgedButton;
    }

    public RoundEdgedTextField getTextField(String text) {
        RoundEdgedTextField roundEdgedTextField = new RoundEdgedTextField(text);
        roundEdgedTextField.setBackground(new Color(54, 69, 79));
        roundEdgedTextField.setForeground(Color.BLUE);
        roundEdgedTextField.setFont(new Font("Verdana", Font.BOLD, 16));
        //roundEdgedTextField.setBorder(new LineBorder(new Color(54, 69, 79), 2));
        //roundEdgedTextField.setName("TF_" + text.replace(" ", "_"));
        roundEdgedTextField.setVisible(true);
        return roundEdgedTextField;
    }

    public JScrollPane getScrollablePane(JPanel panel) {
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
//        verticalScrollBar.addAdjustmentListener(new CustomScrollSpeedAdjustmentListener());
        //scrollPane.setName(("PANS_" + seq++).replace(" ", "_"));
        return scrollPane;
    }

}
