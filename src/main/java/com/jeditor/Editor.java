package com.jeditor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Editor {
    static JPanel home;
    static JsonNode jsonNode = null;
    static ObjectNode objectNode = null;
    IndentPanel indentLevel1 = null;
    int index = 0;
    StringBuilder json = new StringBuilder();
    int count = 0;
    ArrayList indentBlocks = new ArrayList();
    ComponentProvider componentProvider = new ComponentProvider();

    public Editor() {
        home = componentProvider.getHomePanel();
        refresh(home);
        home.setVisible(true);
    }

    public void generateFilePanel() {
        JPanel filePanel = componentProvider.getFilePanel();
        RoundEdgedButton btnUpload = componentProvider.getButton(" UPLOAD ", Color.BLUE, Color.WHITE);
        RoundEdgedButton btnSave = componentProvider.getButton(" SAVE ", Color.BLUE, Color.WHITE);
        RoundEdgedTextField tfFileName = componentProvider.getTextField("your filename will be here...");
        filePanel.add(btnUpload);
        filePanel.add(btnSave);
        filePanel.add(tfFileName);
        home.add(filePanel);
        refresh(home);

        btnUpload.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON files", "json");
            fileChooser.setFileFilter(filter);
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile.getName().endsWith(".json")) {
                    tfFileName.setText(selectedFile.getName());
                    generateJsonPanel(selectedFile.getAbsolutePath());

                } else {
                    JOptionPane.showMessageDialog(null, "Please select a JSON file.");
                }
            }
        });

        btnSave.addActionListener(e -> {
            printStatus(indentLevel1);
        });
    }

    public void generateJsonPanel(String absolutePath) {
        JPanel jsonPanel = componentProvider.getJsonPanel();
        refresh(jsonPanel);
        home.add(jsonPanel);
        refresh(home);
        StringBuilder fileBuilder = new StringBuilder();
        String line;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(absolutePath));
            while ((line = bufferedReader.readLine()) != null) {
                fileBuilder.append(line);
            }
            bufferedReader.close();
            jsonNode = new ObjectMapper().readTree(fileBuilder.toString());
            objectNode = (ObjectNode) jsonNode;
            indentLevel1 = new IndentPanel(jsonPanel, 1);
            jsonPanel.add(indentLevel1.getIndentPanel());
            refresh(jsonPanel);
            refresh(indentLevel1.getIndentPanel());
            constructGUI(indentLevel1, jsonNode, 1);
            jsonPanel.add(indentLevel1.getIndentPanel());
            refresh(jsonPanel);

        } catch (JsonProcessingException e) {
            System.out.println("JsonProcessingException occurred" + e);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        refresh(jsonPanel);
    }

    private void refresh(JPanel panel) {
        panel.revalidate();
        panel.repaint();
        panel.setVisible(true);
    }

    public void constructGUI(IndentPanel indentPanel, JsonNode jsonNode, int indent) {
        if (jsonNode.isObject()) {
            jsonNode.fields().forEachRemaining(entry -> {
                count++;
                JsonNode value = entry.getValue();
                if (value.isObject() || value.isArray()) {
                    PropertyPanel propertyPanel = new PropertyPanel(indentPanel, index++, jsonNode, '"' + entry.getKey() + '"', "", true);
                    IndentPanel indentPanel1 = new IndentPanel(indentPanel, indent);
                    indentPanel.addIndentPanel(indentPanel1);
                    indentBlocks.add(indentPanel1);
                    constructGUI(indentPanel1, value, indent + 1);
                } else {
                    PropertyPanel propertyPanel1 = new PropertyPanel(indentPanel, 0, jsonNode, '"' + entry.getKey() + '"', String.valueOf(value), false);
                    JPanel panel = propertyPanel1.getPropertyPanel();
                    refresh(panel);
                    indentPanel.refresh();
                }
            });
        } else if (jsonNode.isArray()) {
            for (JsonNode element : jsonNode) {
                if (element.isObject() || element.isArray()) {
                    constructGUI(indentPanel, element, indent);
                } else {
                    System.out.println(" ".repeat(indent) + element);
                    count++;
                }
            }
        }
    }

    public void buildJSON(JPanel panel, boolean addBrace, int indent) {
        Component[] components = panel.getComponents();
        //System.out.println("Parent =" + panel.getName());
        for (Component c : components) {
            if (!(c instanceof JPanel)) {
                //System.out.println("child =" + panel.getName());
            } else {
                buildJSON(((JPanel) c), false, indent + 1);
            }
        }

//        for (int i=0;i<components.length;i++) {
//            if (components[i] instanceof JPanel) {
//                if (components[i].getName().startsWith("PAN_PROP")) {
//                    json.append(getLine((JPanel) components[i],addBrace, i!=components.length-1,indent));
//                    addBrace = false;
//                } else if (components[i].getName().startsWith("PAN_INDENT")) {
//                    buildJSON(
//                            (JPanel) components[i],
//                            consistsMultipleIndentPans((JPanel) components[i]),
//                            indent + 1);
//                } else {
//                    System.out.println("END");
//                }
//            }
//        }
//        json.append("},\n");
    }

    private StringBuilder getLine(JPanel panel, boolean addBrace, boolean addComma, int indent) {
        count = 0;
        Component[] components = panel.getComponents();
        StringBuilder line = new StringBuilder();
        for (Component component : components) {
            if (component instanceof JTextField) {
                count++;
                line.append(((JTextField) component).getText()).append(" ");
            }
        }
        if (addBrace) line.append("[\n");
        if (count == 2) line.append("{\n");
        else line.insert(line.length() - 1, addComma ? ",\n" : "\n");
        return new StringBuilder(" ".repeat(indent)).append(line);
    }

    private boolean consistsMultipleIndentPans(JPanel panel) {
        int count = 0;
        Component[] components = panel.getComponents();
        for (Component component : components) {
            count = component instanceof JPanel && component.getName().startsWith("PAN_INDENT") ? count + 1 : count;
        }
        return count >= 1;
    }

    private void printStatus(IndentPanel indentPanel) {
        StringBuilder sb = new StringBuilder("{");
        sb.append(indentLevel1.getContents());
        System.out.println(sb.toString());
    }
}

