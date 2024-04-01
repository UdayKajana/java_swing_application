package com.jeditor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import static com.jeditor.ComponentProvider.BACKGROUND;

public class PropertyPanel {
    boolean isALable = false;
    JTextField Key, Dot, Value;
    private JPanel pPanel;

    public PropertyPanel(IndentPanel indentPanel, int index, JsonNode jsonNode, String key, String value, boolean isALabel) {
        pPanel = new JPanel();
        pPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        pPanel.setBackground(BACKGROUND);
        pPanel.add(Box.createHorizontalGlue());
        pPanel.setVisible(true);
        pPanel.setBorder(null);
        if (!isALabel) {
            this.isALable = isALabel;
            pPanel.add(new JLabel(""));
        }
        Key = getKVTextField(index, jsonNode, key, value, true, true);
        Dot = getKVTextField(index, jsonNode, key, " : ", false, false);
        Value = getKVTextField(index, jsonNode, key, value, false, !isALabel);
        Key.putClientProperty("valueTF", Value);
        Value.putClientProperty("keyTF", Key);
        Key.putClientProperty("jsonNode", jsonNode);
        Value.putClientProperty("jsonNode", jsonNode);
        pPanel.add(Key);
        pPanel.add(Dot);
        pPanel.add(Value);
        pPanel.setSize(new Dimension(Key.getPreferredSize().width + Value.getPreferredSize().width, Key.getPreferredSize().height+5));
        refresh();
        indentPanel.addPropertyPanel(this);
        indentPanel.refresh();
    }

    public JTextField getKVTextField(int index, JsonNode jsonNode, String key, String value, boolean isKey, boolean editable) {
        JTextField textField = new JTextField();
        textField.setEditable(editable);
        textField.setName("kdv");
        if (isKey) textField.setText(key);
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
                    if (jsonNode.isObject()) {
                        ObjectNode objectNode1 = (ObjectNode) jsonNode;
                        String presentKey = (key.length() >= 2 && key.startsWith("\"") && key.endsWith("\"")) ? key.substring(1, key.length() - 1) : key;
                        JsonNode child = objectNode1.get(presentKey);
                        if (isKey) {
                            objectNode1.remove(presentKey);
                            String updatedKey = (textField.getText().length() >= 2 && textField.getText().startsWith("\"") && textField.getText().endsWith("\"")) ? textField.getText().substring(1, textField.getText().length() - 1) : textField.getText();
                            textField.putClientProperty("key", updatedKey);
                            ObjectNode keyValueNode = JsonNodeFactory.instance.objectNode();
                            keyValueNode.set(updatedKey, child);
                            System.out.println("index = " + index);
                        } else {
                            String v = textField.getText();
                            v = (v.length() >= 2 && v.startsWith("\"") && v.endsWith("\"")) ? v.substring(1, v.length() - 1) : v;
                            textField.putClientProperty("value", v);
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

    private void refresh() {
        pPanel.revalidate();
        pPanel.repaint();
        pPanel.setVisible(true);
    }

    public String toString() {
        return (Key.getText() + " : " + Value.getText())+ (Value.isEditable()?",\n":"\n");
    }
    public JPanel getPropertyPanel() {
        return pPanel;
    }

}
