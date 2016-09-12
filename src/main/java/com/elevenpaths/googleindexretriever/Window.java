/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 *
 */

package com.elevenpaths.googleindexretriever;

import com.elevenpaths.googleindexretriever.exceptions.CaptchaException;
import com.elevenpaths.googleindexretriever.exceptions.EmptyQueryException;
import com.elevenpaths.googleindexretriever.exceptions.ManyResultsException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;


import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;

/**
 * Generate the main view.
 *
 * @author Juan Manuel Tirado
 * @since 1.0
 */
public class Window extends JPanel {
    private static Control c;
    private ResourceBundle rb;

    //Design
    private Font defaultFont = new Font("Arial", Font.PLAIN, 12);

    //tabs
    private JTabbedPane tabbedPane = null;

    //retriever panel
    private DefaultListModel<String> listModel;
    private DefaultListModel<String> listLeftModel;
    private JList<String> list;
    private JList<String> leftList;
    private JTextField textField;
    private JButton btnStart;
    private JButton btnStop;
    private JButton btnShot;
    private JButton btnClear;
    private JProgressBar progressBar;
    private JLabel elapsedLabel;

    //menu
    private JMenuItem helpItem;
    private JMenuItem aboutItem;
    private JMenuBar menuBar;
    private JMenuItem retrieveResult;
    private JMenuItem spamResult;

    //find panel
    private DefaultListModel<String> listModelFind;
    private DefaultListModel<String> listLeftModelFind;
    private JList<String> listFind;
    private JList<String> leftListFind;
    private JTextField textFieldFind;
    private JButton btnStartFind;
    private JButton btnStopFind;
    private JButton btnShotFind;
    private JButton btnClearFind;
    private JProgressBar progressBarFind;
    private JLabel elapsedLabelFind;





    /**
     * Contructor
     *
     * @param c
     *            Control of the view
     */
    public Window(Control c) {
        this.c = c;
        this.rb = ResourceBundle.getBundle("strings");
        this.menuBar = createMenu();
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(defaultFont);
        //tabbedPane.setBounds(0,0,200,200);
        final JLabel retrieverLabel = new JLabel(" Retriever ", new ImageIcon(ClassLoader.getSystemResource( "img/icn_retrieve_on.png" )), JLabel.CENTER);
        retrieverLabel.setPreferredSize(new Dimension(80, 30));
        final JLabel spamLabel = new JLabel("  Find SPAM ", new ImageIcon(ClassLoader.getSystemResource( "img/icn_findspam.png" )), JLabel.CENTER);
        spamLabel.setPreferredSize(new Dimension(85, 30));

        tabbedPane.addTab("Retriever",retrieverPanel());
        tabbedPane.addTab("Find SPAM", findSpamPanel());


        //Add the tabbed pane to this panel.
        add(tabbedPane);

        // Set tabs with dimensions
        tabbedPane.setTabComponentAt(0, retrieverLabel);  // tab index, jLabel
        tabbedPane.setTabComponentAt(1, spamLabel);

        // Change Icon
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(tabbedPane.getSelectedIndex() == 0){
                    retrieverLabel.setIcon(new ImageIcon(ClassLoader.getSystemResource( "img/icn_retrieve_on.png" )));
                    spamLabel.setIcon(new ImageIcon(ClassLoader.getSystemResource( "img/icn_findspam.png" )));

                }
                if(tabbedPane.getSelectedIndex() == 1){
                    retrieverLabel.setIcon(new ImageIcon(ClassLoader.getSystemResource( "img/icn_retrieve.png" )));
                    spamLabel.setIcon(new ImageIcon(ClassLoader.getSystemResource( "img/icn_findspam_on.png" )));
                }
            }
        });

    }

    public void setDefaultButton(JButton btn){

        //btn.setBackground(Color.WHITE);
        btn.setFont(defaultFont);
        //btn.setBorder(btn.getBorder());
        //ButtonUI tmp = btn.getUI();
        btn.setBackground(new Color(222, 223, 183));


        //customBorder.paintBorder(btn,customColor ,btn.getX(),btn.getY(),btn.getWidth(),btn.getHeight());
        Border customBorder = new LineBorder(new Color(174, 176, 0));

        //Border customBorder = btn.getBorder();
        //Insets customInsets = customBorder.getBorderInsets(btn);
        btn.setBorder(BorderFactory.createCompoundBorder(customBorder,BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        //btn.setMargin(customInsets);

        //Border ll = new LineBorder()
        //btn.set
        //btn.setBorder(customBorder);

        // these next two lines do the magic..
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
    }

    //FIND SPAM
    public JPanel findSpamPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);


        textFieldFind = new JTextField();
        textFieldFind.setFont(new Font("Tahoma", Font.PLAIN, 14));
        //textFieldFind.setText(rb.getString("f.sampleTL"));
        textFieldFind.setColumns(10);

        btnStartFind = new JButton(rb.getString("start"));
        btnStartFind.addActionListener(new BtnStartFindListener());

        btnStopFind = new JButton(rb.getString("stop"));
        btnStopFind.setEnabled(false);
        btnStopFind.addActionListener(new BtnStopFindListener());

        btnShotFind = new JButton(rb.getString("shot"));
        btnShotFind.addActionListener(new BtnShotFindListener());

        btnClearFind = new JButton(rb.getString("clear"));
        btnClearFind.addActionListener(new BtnClearFindListener());




        /////////// List
        listModelFind = new DefaultListModel<String>();

        listFind = new JList<String>(listModelFind);
        listFind.setFont(new Font("Arial", Font.PLAIN, 14));
        // list.setCellRenderer(new MyCellRenderer());
        listFind.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listFind.setSelectedIndex(0);

        listLeftModelFind = new DefaultListModel<String>();
        leftListFind = new JList<String>(listLeftModelFind);
        leftListFind.setEnabled(false);
        leftListFind.setFixedCellWidth(140);
        leftListFind.setFont(new Font("Arial", Font.PLAIN, 14));


        //Margins
        listFind.setBorder(new EmptyBorder(10,10, 10, 0));
        leftListFind.setBorder(new EmptyBorder(10,10, 10, 0));
        textFieldFind.setMargin(new Insets(0,10,0,0));


        listFind.setForeground(new Color(133, 133, 133));

        JScrollPane listScrollPane = new JScrollPane(listFind);
        listScrollPane.setRowHeaderView(leftListFind);

        progressBarFind = new JProgressBar();
        progressBarFind.setStringPainted(true);
        progressBarFind.setString(rb.getString("progressbar.ready"));


        elapsedLabelFind = new JLabel(rb.getString("elapsed.start"));

        //Set default design
        setDefaultButton(btnStartFind);
        setDefaultButton(btnStopFind);
        setDefaultButton(btnShotFind);
        setDefaultButton(btnClearFind);
        elapsedLabelFind.setFont(defaultFont);

        GroupLayout groupLayout = new GroupLayout(panel);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(progressBarFind, GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
                                        .addComponent(textFieldFind, GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
                                        .addComponent(listScrollPane, GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(btnClearFind)
                                                .addPreferredGap(ComponentPlacement.RELATED, 429, Short.MAX_VALUE)
                                                .addComponent(elapsedLabelFind)
                                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                                .addComponent(btnStopFind)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(btnShotFind)
                                                .addGap(4)
                                                .addComponent(btnStartFind)))
                                .addContainerGap())
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(textFieldFind, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(listScrollPane, GroupLayout.DEFAULT_SIZE, 470, 470)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(progressBarFind, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(btnClearFind)
                                        .addComponent(btnStartFind)
                                        .addComponent(btnShotFind)
                                        .addComponent(btnStopFind)
                                        .addComponent(elapsedLabelFind))
                                .addContainerGap())
        );

        panel.setLayout(groupLayout);

        return panel;

    }

    //Retriever
    public JPanel retrieverPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);


        textField = new JTextField();
        textField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        //textField.setText(rb.getString("sampleTL"));
        textField.setColumns(10);


        btnStart = new JButton(rb.getString("start"));
        btnStart.addActionListener(new BtnStartListener());

        btnStop = new JButton(rb.getString("stop"));
        btnStop.setEnabled(false);
        btnStop.addActionListener(new BtnStopListener());

        btnShot = new JButton(rb.getString("shot"));
        btnShot.addActionListener(new BtnShotListener());

        btnClear = new JButton(rb.getString("clear"));
        btnClear.addActionListener(new BtnClearListener());



        /////////// List
        listModel = new DefaultListModel<String>();


        list = new JList<String>(listModel);
        list.setFont(new Font("Arial", Font.PLAIN, 14));
        // list.setCellRenderer(new MyCellRenderer());

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);

        listLeftModel = new DefaultListModel<String>();

        leftList = new JList<String>(listLeftModel);
        leftList.setEnabled(false);

        //leftList.setPrototypeCellValue("00:00:00 - XXXXXXX"); //put size of the cell
        leftList.setFixedCellWidth(140);
        leftList.setFont(new Font("Arial", Font.PLAIN, 14));

        //UIManager.put("JList.disabledForeGround", Color.RED);
        //UIManager.put("Label.disabledText", Color.RED);
        //leftList.setForeground(new Color(174, 176, 0));
        list.setForeground(new Color(133, 133, 133));
        //leftList.add(new JLabel("<html><font color=red>RED</font> - <font color=navy>Navy</font></html>"));


        //Margins
        list.setBorder(new EmptyBorder(10,10, 10, 0));
        leftList.setBorder(new EmptyBorder(10,10, 10, 0));
        //textField.setBorder(new EmptyBorder(0,10, 0, 0));
        textField.setMargin(new Insets(0,10,0,0));


        JScrollPane listScrollPane = new JScrollPane(list);
        listScrollPane.setRowHeaderView(leftList);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString(rb.getString("progressbar.ready"));
        //System.out.println("color : " + progressBar.getForeground() );


        elapsedLabel = new JLabel(rb.getString("elapsed.start"));

        //Set default design
        setDefaultButton(btnStart);
        setDefaultButton(btnStop);
        setDefaultButton(btnShot);
        setDefaultButton(btnClear);
        elapsedLabel.setFont(defaultFont);

        GroupLayout groupLayout = new GroupLayout(panel);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
                                        .addComponent(textField, GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
                                        .addComponent(listScrollPane, GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(btnClear)
                                                .addPreferredGap(ComponentPlacement.RELATED, 429, Short.MAX_VALUE)
                                                .addComponent(elapsedLabel)
                                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                                .addComponent(btnStop)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(btnShot)
                                                .addGap(4)
                                                .addComponent(btnStart)))
                                .addContainerGap())
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(listScrollPane, GroupLayout.DEFAULT_SIZE, 470, 470)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(btnClear)
                                        .addComponent(btnStart)
                                        .addComponent(btnShot)
                                        .addComponent(btnStop)
                                        .addComponent(elapsedLabel))
                                .addContainerGap())
        );

        panel.setLayout(groupLayout);

        return panel;

        //DefaultTableModel  model = new DefaultTableModel();
        //Object[][] data = {{"v1", "v2"}};

        // ImageIcon ii = new
        // ImageIcon(this.getClass().getResource("/images/loader.gif"));
        // imageLabel.setIcon(ii);

        //this.setLayout(groupLayout);
        // this.setSize(800, 550);
        /*
         * if(listModel.size() == 0){ listScrollPane.setVisible(false); }
         */
    }


//    /** Returns an ImageIcon, or null if the path was invalid. */
//    protected static ImageIcon createImageIcon(String path) {
//        java.net.URL imgURL = Window.class.getResource(path);
//        if (imgURL != null) {
//            return new ImageIcon(imgURL);
//        } else {
//            System.err.println("Couldn't find file: " + path);
//            return null;
//        }
//    }

    public void searchWords() {
        progressBar.setForeground(new Color(163,184,204));
    }
    public void searchSpamWords() {
        progressBarFind.setForeground(new Color(163,184,204));
    }

    public void searchKeywords() {
        progressBar.setForeground(Color.LIGHT_GRAY);
    }

    public JLabel getLblNewLabel() {
        return elapsedLabel;
    }

    public void setLblNewLabel(String lblNewLabel) {
        this.elapsedLabel.setText(lblNewLabel);;
    }
    public void setFindLblNewLabel(String lblNewLabel) {
        this.elapsedLabelFind.setText(lblNewLabel);;
    }

    public void disableStop() {
        btnStop.setEnabled(false);
    }
    public void disableSpamStop() {
        btnStopFind.setEnabled(false);
    }

    public void enableStop() {
        btnStop.setEnabled(true);
    }
    public void enableSpamStart() {
        btnStartFind.setEnabled(true);
        btnShotFind.setEnabled(true);
        spamResult.setEnabled(true);
        stopFindProgressBar();
    }
    public void disableSpamStart() {
        btnStartFind.setEnabled(false);
        btnShotFind.setEnabled(false);
        spamResult.setEnabled(false);
        startFindProgressBar();

    }

    public void enableStart() {
        btnStart.setEnabled(true);
        btnShot.setEnabled(true);
        retrieveResult.setEnabled(true);
        stopProgressBar();
    }

    public void disableStart() {
        btnStart.setEnabled(false);
        btnShot.setEnabled(false);
        retrieveResult.setEnabled(false);
        startProgressBar();
    }

    //ProgressBar

    public void startProgressBar() {
        progressBar.setIndeterminate(true);
    }

    public void stopProgressBar() {
        progressBar.setIndeterminate(false);
        progressBar.setString(rb.getString("progressbar.done"));
    }

    public void setProgressBar(String message) {
        progressBar.setString(message);
    }



    public void clearAll() {
        listModel.removeAllElements();
        listLeftModel.removeAllElements();
        elapsedLabel.setText(rb.getString("elapsed.start"));
    }
    public void clearSpamAll() {
        listModelFind.removeAllElements();
        listLeftModelFind.removeAllElements();
        elapsedLabelFind.setText(rb.getString("elapsed.start"));
    }



    public void startFindProgressBar() {
        progressBarFind.setIndeterminate(true);
    }

    public void stopFindProgressBar() {
        progressBarFind.setIndeterminate(false);
        progressBarFind.setString(rb.getString("progressbar.done"));
    }


    public void setFindProgressBar(String message) {
        progressBarFind.setString(message);
    }



    // Listeners
    class BtnStartListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            enableStop();
            disableStart();
            clearAll();
            c.makeQuery(textField.getText());
        }

    }

    class BtnClearListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            clearAll();

        }

    }

    class BtnStopListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            disableStop();
            enableStart();
            c.stop();

        }

    }



    class BtnShotListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            enableStop();
            disableStart();
            try {
                c.makeShot(textField.getText());
                disableStop();
                enableStart();

            } catch (EmptyQueryException ex) {
                disableStop();
                enableStart();
                JOptionPane.showMessageDialog(null, ex.getMessage(), "",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (ManyResultsException ex) {
                disableStop();
                enableStart();
                JOptionPane.showMessageDialog(null, ex.getMessage(), "",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (CaptchaException ex) {
                disableStop();
                enableStart();
                JOptionPane.showMessageDialog(null, ex.getMessage(), "",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        }

    }

    class BtnExportListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            saveFile(false);

        }

    }
    class BtnSpamExportListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            saveFile(true);

        }

    }

    public void saveFile(boolean spam) {

        ArrayList<ArrayList<String>> dataToExport = new ArrayList<ArrayList<String>> ();
        try {
            if(spam) {
                if (listModelFind.getSize() > 0) {
                    for (int i = 0; i < listModelFind.getSize(); i++) {
                        ArrayList<String> row = new ArrayList<String> ();
                        row.add(listLeftModelFind.getElementAt(i));
                        row.add(listModelFind.getElementAt(i));

                        dataToExport.add(row);
                    }
                    c.export(dataToExport, "spamExport.html");
                    JOptionPane.showMessageDialog(null,  new JLabel(rb.getString("exportSuccess"), JLabel.CENTER), "", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, rb.getString("noExport"),
                            "", JOptionPane.INFORMATION_MESSAGE);

                }

            }
            else {
                if (listModel.getSize() > 0) {
                    for (int i = 0; i < listModel.getSize(); i++) {
                        ArrayList<String> row = new ArrayList<String> ();
                        row.add(listLeftModel.getElementAt(i));
                        row.add(listModel.getElementAt(i));

                        dataToExport.add(row);
                    }
                    c.export(dataToExport, "export.html");
                    JOptionPane.showMessageDialog(null,  new JLabel(rb.getString("exportSuccess"), JLabel.CENTER), "", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, rb.getString("noExport"),
                            "", JOptionPane.INFORMATION_MESSAGE);

                }

            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "",
                    JOptionPane.INFORMATION_MESSAGE);
        }


    }

    class BtnHelpMenu implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                c.launchHelp();
            } catch (MalformedURLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (URISyntaxException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        }

    }

    class BtnAboutMenu implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null,
                    rb.getString("menu.aboutMessage"),
                    rb.getString("menu.aboutTitle"),
                    JOptionPane.INFORMATION_MESSAGE);
        }

    }

    class BtnKeywordsMenu implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            c.launchKeywords(false);

        }

    }
    class BtnSpamKeywordsMenu implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            c.launchKeywords(true);

        }

    }

    class BtnExitMenu implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);

        }

    }


    /// FIND SPAM
    class BtnStartFindListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            btnStopFind.setEnabled(true);
            disableSpamStart();
            //TODO: FIX CLEAR
            //clearAll();
            c.makeSpamQuery(textFieldFind.getText());
        }

    }
    class BtnStopFindListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            disableSpamStop();
            enableSpamStart();
            c.stopSpam();

        }

    }
    class BtnShotFindListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO: fix this to put in find mode
            btnStopFind.setEnabled(true);
            disableSpamStart();
            try {
                c.makeShotSpam(textFieldFind.getText());
                disableSpamStop();
                enableSpamStart();

            } catch (EmptyQueryException ex) {
                disableSpamStop();
                enableSpamStart();
                JOptionPane.showMessageDialog(null, ex.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
            } catch (ManyResultsException ex) {
                disableSpamStop();
                enableSpamStart();
                JOptionPane.showMessageDialog(null, ex.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
            } catch (CaptchaException ex) {
                disableSpamStop();
                enableSpamStart();
                JOptionPane.showMessageDialog(null, ex.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
            }

        }
    }
    class BtnClearFindListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            clearSpamAll();

        }

    }

    public String getTextField() {
        String dork = "";
        if(listModel.getSize() > 0){
            dork = textField.getText();
        }
        else if(listModelFind.getSize() > 0 ){
            dork = textFieldFind.getText();
        }
        else{
            dork = rb.getString("notFoundDork");
        }
        return dork;
    }

    public void insertInList(String time , String word, String text) {
        listLeftModel.addElement(time + " - " + word);
        listModel.addElement(text);

        //System.out.println("insertado" + text);

    }
    public void insertInListFind(String time , String word, String text) {
        listLeftModelFind.addElement(time + " - " + word);
        listModelFind.addElement(text);

        //System.out.println("insertado" + text);

    }

    public boolean isInList(String text) {
        return listModel.contains(text);
    }
    public boolean isInListFind(String text) {
        return listModelFind.contains(text);
    }

    public void createAndShowGUI() {
        // Create and set up the window.
        JFrame frame = new JFrame("Google Index Retriever");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon(ClassLoader.getSystemResource( "img/xr.png" )).getImage() );
        frame.setJMenuBar(menuBar);
        frame.setResizable(false);
        frame.getContentPane().add( this , BorderLayout.CENTER); // add this view in the frame
        frame.setLocationRelativeTo(null); // show in center
        frame.pack();
        frame.setVisible(true);
    }

    public JMenuBar createMenu() {
        JMenuBar menuBarC = new JMenuBar();

        JMenu fileMenu = new JMenu(rb.getString("menu.file"));


        JMenu exportMenu = new JMenu(rb.getString("menu.export"));

        retrieveResult = new JMenuItem(rb.getString("menu.retrieveResults"));
        retrieveResult.addActionListener(new BtnExportListener());
        spamResult = new JMenuItem(rb.getString("menu.spamResults"));
        spamResult.addActionListener(new BtnSpamExportListener());

        //Edit keywords
        JMenu editKeywordsMenu = new JMenu(rb.getString("menu.editKeywords"));

        JMenuItem spamKeywords = new JMenuItem(rb.getString("menu.spamKeywords"));
        spamKeywords.addActionListener(new BtnSpamKeywordsMenu());
        JMenuItem retrieveKeywords = new JMenuItem(rb.getString("menu.retrieveKeywords"));
        retrieveKeywords.addActionListener(new BtnKeywordsMenu());


        JMenuItem exitItem = new JMenuItem(rb.getString("menu.exit"));
        exitItem.addActionListener(new BtnExitMenu());

        //Submenu
        exportMenu.add(retrieveResult);
        exportMenu.add(spamResult);

        editKeywordsMenu.add(retrieveKeywords);
        editKeywordsMenu.add(spamKeywords);


        //Menu
        fileMenu.add(editKeywordsMenu);
        fileMenu.addSeparator();
        fileMenu.add(exportMenu);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBarC.add(fileMenu);


        JMenu helpMenu = new JMenu(rb.getString("menu.helpM"));
        //keyWordsItem.addActionListener(new BtnKeyWordsMenu());
        helpItem = new JMenuItem(rb.getString("menu.help"));
        helpItem.addActionListener(new BtnHelpMenu());
        aboutItem = new JMenuItem(rb.getString("menu.about"));
        aboutItem.addActionListener(new BtnAboutMenu());

        helpMenu.add(helpItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);


        menuBarC.add(Box.createHorizontalGlue());
        menuBarC.add(helpMenu);

        return menuBarC;

    }
}