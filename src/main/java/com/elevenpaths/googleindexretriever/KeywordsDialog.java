/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 *
 */

package com.elevenpaths.googleindexretriever;

import javax.swing.JDialog;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.SpringLayout;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.ScrollPaneConstants;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.border.EmptyBorder;

/**
 * Window to manage the keywords.
 *
 * @author Juan Manuel Tirado
 * @since 1.0
 */
public class KeywordsDialog extends JDialog {
    private ResourceBundle rb;
    private DefaultListModel<String> listModel;
    private JList<String> list;
    private boolean save;
    private JLabel totalLabel;
    private JCheckBox keywordsCheckBox;

    public KeywordsDialog(Frame parent, boolean modal, String title, ArrayList<String> keywords, boolean keywordsSelect) {
        super(parent, modal);

        this.rb = ResourceBundle.getBundle("strings");
        save = false;
        setResizable(false);
        setTitle(title);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(380, 300));
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        listModel = new DefaultListModel<String>();
        for (String k : keywords) {
            listModel.addElement(k);
        }

        JPanel panel_2 = new JPanel();
        panel.add(panel_2);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(260, 290));
        panel_2.add(scrollPane);
        scrollPane.setSize(50, 50);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        list = new JList<String>(listModel);
        list.setSize(50, 50);
        list.setBorder(new EmptyBorder(5,7,0,5));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(new listDoubleClick());
        scrollPane.setViewportView(list);

        JPanel panel_1 = new JPanel();
        panel.add(panel_1);
        SpringLayout sl_panel_1 = new SpringLayout();
        panel_1.setLayout(sl_panel_1);

        JButton btnSave = new JButton(rb.getString("keywords.save"));
        sl_panel_1.putConstraint(SpringLayout.WEST, btnSave, 10,
                SpringLayout.WEST, panel_1);
        sl_panel_1.putConstraint(SpringLayout.SOUTH, btnSave, -10,
                SpringLayout.SOUTH, panel_1);
        btnSave.addActionListener(new BtnSaveListener());
        panel_1.add(btnSave);

        JButton btnDelete = new JButton(rb.getString("keywords.delete"));
        sl_panel_1.putConstraint(SpringLayout.EAST, btnSave, 0,
                SpringLayout.EAST, btnDelete);
        sl_panel_1.putConstraint(SpringLayout.WEST, btnDelete, 10,
                SpringLayout.WEST, panel_1);
        sl_panel_1.putConstraint(SpringLayout.EAST, btnDelete, -26,
                SpringLayout.EAST, panel_1);
        btnDelete.addActionListener(new BtnDeleteListener());
        panel_1.add(btnDelete);

        JButton btnAdd = new JButton(rb.getString("keywords.add"));
        sl_panel_1.putConstraint(SpringLayout.NORTH, btnDelete, 8,
                SpringLayout.SOUTH, btnAdd);
        sl_panel_1.putConstraint(SpringLayout.EAST, btnAdd, 0,
                SpringLayout.EAST, btnDelete);
        sl_panel_1.putConstraint(SpringLayout.WEST, btnAdd, 10,
                SpringLayout.WEST, panel_1);
        sl_panel_1.putConstraint(SpringLayout.SOUTH, btnAdd, -267,
                SpringLayout.SOUTH, panel_1);
        btnAdd.addActionListener(new BtnAddListener());
        panel_1.add(btnAdd);

        totalLabel = new JLabel(rb.getString("total") + keywords.size() );

        sl_panel_1.putConstraint(SpringLayout.WEST, totalLabel, 10, SpringLayout.WEST, panel_1);
        sl_panel_1.putConstraint(SpringLayout.SOUTH, totalLabel, -6, SpringLayout.NORTH, btnSave);
        panel_1.add(totalLabel);

        keywordsCheckBox = new JCheckBox(rb.getString("keywords.use"));
        keywordsCheckBox.setSelected(keywordsSelect);
        sl_panel_1.putConstraint(SpringLayout.NORTH, keywordsCheckBox, 6, SpringLayout.SOUTH, btnDelete);
        sl_panel_1.putConstraint(SpringLayout.EAST, keywordsCheckBox, -2, SpringLayout.EAST, panel_1);
        panel_1.add(keywordsCheckBox);

        pack();
    }


    class listDoubleClick extends MouseAdapter {
        public void mouseClicked(MouseEvent evt) {

            if (evt.getClickCount() == 2) {
                int index = list.locationToIndex(evt.getPoint());
                editList(listModel.getElementAt(index), index);

            }
        }
    }

    class BtnAddListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            editList("", -1);

        }

    }

    class BtnDeleteListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int i = list.getSelectedIndex();
            if (i != -1) {
                listModel.remove(i);
                refreshTotal();
            }

        }

    }


    public boolean getKeywordsCheckBox() {
        return keywordsCheckBox.isSelected();
    }

    public void setKeywordsCheckBox(boolean s) {
        keywordsCheckBox.setSelected(s);
    }


    public void refreshTotal() {
        totalLabel.setText(rb.getString("total") + listModel.size());
    }

    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this);
        window.dispose(); // System.exit(0); would work as well
    }

    class BtnSaveListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            save = true;
            close();
        }

    }

    private void editList(String item, int index) {
        // System.out.println("item: " + item + " index: " + index);
        String s = (String) JOptionPane.showInputDialog(null,
                rb.getString("keywords.message"),
                rb.getString("keywords.title"), JOptionPane.PLAIN_MESSAGE,
                null, null, item);
        if (s != null) {
            if (index == -1) {
                listModel.addElement(s);
            } else {
                listModel.set(index, s);
            }
            refreshTotal();
        }

    }

    public ArrayList<String> getKeywordsList() {
        ArrayList<String> keywords = new ArrayList<String>();
        for (int i = 0; i < listModel.getSize(); i++) {
            keywords.add(listModel.get(i));
        }

        return keywords;
    }

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }
}