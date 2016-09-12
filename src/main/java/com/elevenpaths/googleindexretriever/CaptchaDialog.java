/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 *
 */

package com.elevenpaths.googleindexretriever;

import java.awt.Frame;
import java.awt.Window;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ResourceBundle;

import javax.swing.JTextField;
import javax.swing.JButton;

/**
 * CaptchaDialog is a class to launch insert the captcha
 *
 *
 * @author Juan Manuel Tirado
 * @since 1.0
 */
public class CaptchaDialog extends JDialog {
    private JTextField textField;
    private ResourceBundle rb;
    private String captchaString = "";

    public CaptchaDialog(Frame parent, boolean modal) {
        super(parent, modal);

        this.rb = ResourceBundle.getBundle("strings");


        this.setSize(159, 101);
        this.setResizable(false);
        this.setTitle(rb.getString("titleCaptcha"));
        this.setLocationRelativeTo(parent);



        try{
            BufferedImage image = ImageIO.read(new File("Captcha.png"));

            JLabel imageCaptcha = new JLabel(new ImageIcon(image));
            getContentPane().add(imageCaptcha, BorderLayout.NORTH);

            //TextField
            textField = new JTextField();
            textField.addKeyListener(new EnterKeyPressed());
            getContentPane().add(textField, BorderLayout.CENTER);
            textField.setColumns(10);


            //Button
            JButton sendButton = new JButton(rb.getString("send"));
            getContentPane().add(sendButton, BorderLayout.SOUTH);
            sendButton.addActionListener(new SendButtonListener());

        }catch ( MalformedURLException e){

        }catch (IOException e){

        }

        this.pack();
    }

    class SendButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            sendCaptchaResponse();
        }

    }
    class EnterKeyPressed implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void keyPressed(KeyEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void keyReleased(KeyEvent e) {
            // TODO Auto-generated method stub
            if(e.getKeyCode() == KeyEvent.VK_ENTER){
                sendCaptchaResponse();
            }

        }

    }

    private void sendCaptchaResponse() {
        captchaString = textField.getText();
        this.close();
    }
    public void close(){
        Window window = SwingUtilities.getWindowAncestor(this);
        window.dispose(); // System.exit(0); would work as well

    }

    public String getCaptchaString() {
        return captchaString;
    }

    public void setCaptchaString(String captchaString) {
        this.captchaString = captchaString;
    }




}