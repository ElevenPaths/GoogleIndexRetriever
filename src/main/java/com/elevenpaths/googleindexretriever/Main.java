/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 *
 */

package com.elevenpaths.googleindexretriever;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

import com.elevenpaths.googleindexretriever.exceptions.CaptchaException;
import com.elevenpaths.googleindexretriever.exceptions.EmptyQueryException;
import com.elevenpaths.googleindexretriever.exceptions.ManyResultsException;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Control class
 *
 */
public class Main {
    // private Window win = new Window();;

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

                    //UIManager.put("Button.background", Color.RED);
                    UIManager.put("Label.disabledForeground", new Color(108, 112, 0));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
                Control con = new Control();
                con.run();
            }
        });
    }

}

class Control {
    private GoogleSearch gs;// Model
    private Window w;// View
    private static KeyWordsProcess bgkeywordsProcess;
    private static SpamProcess bgspamProcess;
    private boolean useKeywords;
    private boolean useSpamKeywords;

    public Control() {
        this.gs = new GoogleSearch();
        this.w = new Window(this);
        this.useKeywords = true;
        this.useSpamKeywords = true;

    }

    public void run() {
        w.createAndShowGUI();
        try {
            gs.loadKeywords();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("strings").getString("keywords.notfound"), "", JOptionPane.INFORMATION_MESSAGE);
            gs.loadDefaultKeywords();
        } catch (IOException e) {
        }
        try {
            gs.loadSpamKeywords();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("strings").getString("keywords.notfound"), "", JOptionPane.INFORMATION_MESSAGE);
            gs.loadDefaultSpamKeywords();
        } catch (IOException e) {
        }

    }



    public void launchKeywords(boolean spam) {
        KeywordsDialog ksd = null;
        try {
            if(spam)
            {
                ksd = new KeywordsDialog(null, true,"Spam Keywords", gs.getSpamKeywords(), useSpamKeywords);
            } else {
                ksd = new KeywordsDialog(null, true,"Retriever Keywords", gs.getKeywords(), useKeywords);
            }
            ksd.setVisible(true);
            if (ksd.isSave()) {
                if(spam) {
                    gs.setSpamKeywords(ksd.getKeywordsList());
                    gs.saveSpamKeywords();
                    useSpamKeywords = ksd.getKeywordsCheckBox();

                } else {
                    gs.setKeywords(ksd.getKeywordsList());
                    gs.saveKeywords();
                    useKeywords = ksd.getKeywordsCheckBox();
                }
                JOptionPane.showMessageDialog( null, ResourceBundle.getBundle("strings").getString("saveSuccess"), "", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    public void makeQuery(String query) {
        bgkeywordsProcess = new KeyWordsProcess(this, gs, query, gs.getKeywords(), useKeywords);
        bgkeywordsProcess.start();

    }

    public void makeSpamQuery(String query) {
        bgspamProcess = new SpamProcess(this, gs, query, gs.getSpamKeywords() , useSpamKeywords);
        bgspamProcess.start();

    }

    public void makeShot(String query) throws EmptyQueryException,
            ManyResultsException, CaptchaException {
        String result = "";
        do {
            try {
                gs.setQuery(query);
                result = gs.getResults();
                addList("", "", result);//found word search
            } catch (CaptchaException e) {
                gs.setTokenCookie(runCaptchaDialog(e.getMessage()));
            } catch (UnsupportedEncodingException e){

            }
        } while (result.isEmpty());
    }

    public void makeShotSpam(String query) throws EmptyQueryException, ManyResultsException, CaptchaException {
        int i = 0;
        do {
            try {
                gs.setQuery(query);
                Elements results = gs.getResultSpam();

                //Successful request

                //String time = c.calcHMS((int) TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));
                //setFindElepased("Elapsed: " + time);
                for(Element e : results) {
                    addListFind("", "" , e.text());
                }

                i = results.size();
            } catch (CaptchaException e) {
                gs.setTokenCookie(runCaptchaDialog(e.getMessage()));
            }
            catch (UnsupportedEncodingException e){

            }
        } while (i == 0);
    }

    public void stop() {
        bgkeywordsProcess.stop();
        w.disableStop();
        w.enableStart();
        w.searchWords();
    }

    public void stopSpam() {
        bgspamProcess.stop();
        w.disableSpamStop();
        w.enableSpamStart();
        w.searchSpamWords();
    }

    public void searchKeywords() {
        w.searchKeywords();
    }

    public void setPathProgressBar(String path) {
        w.setProgressBar(path);

    }
    public void setPathFindProgressBar(String path) {
        w.setFindProgressBar(path);

    }

    public void export(ArrayList<ArrayList <String>> data, String nameFile) throws IOException {
        gs.export(data, w.getTextField(), nameFile);
    }

    public void launchHelp() throws MalformedURLException, IOException,
            URISyntaxException {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop()
                : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            desktop.browse(new URL(ResourceBundle.getBundle("strings")
                    .getString("helpURL")).toURI());
        }
    }

    public String runCaptchaDialog(String message) throws CaptchaException {
        String newCookie = "";
        if (gs.getIdCaptcha().isEmpty()) {
            // Something go wrong
            throw new CaptchaException();
        }
        CaptchaDialog jd = new CaptchaDialog(null, true);
        jd.setVisible(true);
        if (jd.getCaptchaString().isEmpty()) {
            throw new CaptchaException();
        } else {
            newCookie = gs.responseCaptcha(jd.getCaptchaString());
        }

        return newCookie;

    }

    public String calcHMS(int timeInSeconds) {
        int hours, minutes, seconds;
        hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds - (hours * 3600);
        minutes = timeInSeconds / 60;
        timeInSeconds = timeInSeconds - (minutes * 60);
        seconds = timeInSeconds;
        DecimalFormat formatter = new DecimalFormat("00");
        // String aFormatted = formatter.format(a);
        return formatter.format(hours) + ":" + formatter.format(minutes) + ":"
                + formatter.format(seconds);
    }

    public void addList(String time, String word, String result) {
        if (!w.isInList(result) && !result.isEmpty()) {
            w.insertInList(time, word, result);
        }
    }
    public void addListFind(String time, String word, String result) {
        if (!w.isInListFind(result) && !result.isEmpty()) {
            w.insertInListFind(time, word, result);
        }
    }

    public void setElepased(String time) {
        w.setLblNewLabel(time);
    }
    public void setFindElepased(String time) {
        w.setFindLblNewLabel(time);
    }
}

//Find Spam panel

class SpamProcess implements Runnable {
    private Thread t;
    private boolean stop = false;
    private long startTime;

    private static Control c;
    private GoogleSearch gs;
    private String query;
    private ArrayList<String> spamKeywordsList;

    public SpamProcess(Control c, GoogleSearch gs, String query, ArrayList<String> keywords, boolean useKeywords) {
        this.c = c;
        this.gs = gs;
        this.query = query;
        this.startTime = System.nanoTime();
        this.spamKeywordsList = keywords;

    }

    public void run() {
        try {
            ArrayDeque<String> wordsSpamQueue = new ArrayDeque<String>();
            wordsSpamQueue.addAll(gs.getSpamKeywords());
            String wordQueue = "";
            if( wordsSpamQueue != null){
                do {
                    try{
                        wordQueue = wordsSpamQueue.pop();

                        c.setPathFindProgressBar(query + " \"" + wordQueue + "\"");

                        gs.setQuery(query + " \"" + wordQueue + "\"");
                        Elements result = gs.getResultSpam();

                        //Successful request

                        String time = c.calcHMS((int) TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));
                        c.setFindElepased("Elapsed: " + time);
                        for(Element e : result) {
                            c.addListFind(time, wordQueue , e.text());
                        }
                    }catch (CaptchaException e) {

                        wordsSpamQueue.addFirst(wordQueue);//Insert again to process
                        gs.setTokenCookie(c.runCaptchaDialog(e.getMessage()));
                    }
                    catch (UnsupportedEncodingException e){

                    }


                }while(!stop && !wordsSpamQueue.isEmpty());


            }

            c.stopSpam();
            //gs.setQuery(query);
            //Elements result = gs.getResultSpam();


            //System.out.println("------------------------------");
            //System.out.println(result);



        } catch (EmptyQueryException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
            c.stopSpam();

        } catch (ManyResultsException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
            c.stopSpam();

        } catch (CaptchaException e) {
            // disableStop();
            // enableStart();
            JOptionPane.showMessageDialog(null, e.getMessage(), "",
                    JOptionPane.INFORMATION_MESSAGE);
            c.stopSpam();
        }
    }
    public void start() {
        // System.out.println("Starting " + threadName );
        if (t == null) {
            t = new Thread(this, "spam");
            t.start();
        }
    }

    public void stop() {
        stop = true;

    }
}


//Retriever panel Process

class KeyWordsProcess implements Runnable {
    private Thread t;
    private boolean stop = false;
    private static Control c;
    private ArrayDeque<String> wordsFoundQueue = new ArrayDeque<String>();
    private Set<String> wordsProcessed = new HashSet<String>();
    private ArrayList<String> keywordsList;
    private String cookie = "";
    private long startTime;

    private String query;
    private GoogleSearch gs;
    private String result = "";
    private String wordQueue = "";
    private boolean firstQuery = true;
    private boolean useKeywordsProcess;

    public KeyWordsProcess(Control c, GoogleSearch gs, String query, ArrayList<String> keywords, boolean useKeywords) {
        this.c = c;
        this.gs = gs;
        this.query = query;
        this.useKeywordsProcess = useKeywords;
        this.startTime = System.nanoTime();
        this.keywordsList = keywords;
    }

    public void run() {
        try {

            //First
            do {
                try{
                    c.setPathProgressBar(query);

                    gs.setQuery(query);
                    result = gs.getResults();

                    //Successful request
                    wordsProcessed.add(wordQueue);
                    String time = c.calcHMS((int) TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));
                    c.setElepased("Elapsed: " + time);
                    c.addList(time, wordQueue , result);



                    result = result.replaceAll("\\.\\.\\.|\\.|\\:|\\,", " ");



                    String[] parts = result.split(" ");

                    for (String a : parts) {

                        if (!wordsProcessed.contains(a) && !wordsFoundQueue.contains(a) && !a.isEmpty()) {
                            wordsFoundQueue.addFirst(a);
                        }
                    }
                    firstQuery = false;//First query successful
                } catch (CaptchaException e) {
                    gs.setTokenCookie(c.runCaptchaDialog(e.getMessage()));
                }
                catch (UnsupportedEncodingException e){

                }
            }while(firstQuery);

            do {
                try {

                    wordQueue = wordsFoundQueue.pop();

                    c.setPathProgressBar(query + " \"" + wordQueue + "\"");

                    gs.setQuery(query + " \"" + wordQueue + "\"");
                    result = gs.getResults();

                    //Successful request
                    wordsProcessed.add(wordQueue);
                    String time = c.calcHMS((int) TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));
                    c.setElepased("Elapsed: " + time);
                    c.addList(time, wordQueue , result);


                    result = result.replaceAll("\\.\\.\\.|\\.|\\:|\\,", " ");


                    String[] parts = result.split(" ");

                    for (String a : parts) {

                        if (!wordsProcessed.contains(a) && !wordsFoundQueue.contains(a) && !a.isEmpty()) {
                            wordsFoundQueue.addFirst(a);
                        }
                    }



                } catch (CaptchaException e) {

                    wordsFoundQueue.addFirst(wordQueue);//Insert again to process
                    gs.setTokenCookie(c.runCaptchaDialog(e.getMessage()));
                }
                catch (UnsupportedEncodingException e){

                }

            } while (!stop && !wordsFoundQueue.isEmpty());

            //Starts KeyWords
            if(useKeywordsProcess) {
                wordsFoundQueue.addAll(keywordsList);
                c.searchKeywords();

                do {
                    try {

                        wordQueue = wordsFoundQueue.pop();

                        c.setPathProgressBar(query + " \"" + wordQueue + "\"");

                        gs.setQuery(query + " \"" + wordQueue + "\"");
                        result = gs.getResults();

                        //Successful request
                        wordsProcessed.add(wordQueue);
                        String time = c.calcHMS((int) TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));
                        c.setElepased("Elapsed: " + time);
                        c.addList(time, wordQueue , result);


                        result = result.replaceAll("\\.\\.\\.|\\.|\\:|\\,", " ");


                        String[] parts = result.split(" ");

                        for (String a : parts) {

                            if (!wordsProcessed.contains(a) && !wordsFoundQueue.contains(a) && !a.isEmpty()) {
                                wordsFoundQueue.addFirst(a);
                            }
                        }



                    } catch (CaptchaException e) {

                        wordsFoundQueue.addFirst(wordQueue);//Insert again to process
                        gs.setTokenCookie(c.runCaptchaDialog(e.getMessage()));
                    }
                    catch (UnsupportedEncodingException e){

                    }

                } while (!stop && !wordsFoundQueue.isEmpty());


            }

            c.stop();
        } catch (EmptyQueryException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "",  JOptionPane.INFORMATION_MESSAGE);
            c.stop();
        } catch (ManyResultsException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
            c.stop();

        } catch (CaptchaException e) {
            // disableStop();
            // enableStart();
            JOptionPane.showMessageDialog(null, e.getMessage(), "",
                    JOptionPane.INFORMATION_MESSAGE);
            c.stop();
        }

    }

    public void start() {
        // System.out.println("Starting " + threadName );
        if (t == null) {
            t = new Thread(this, "keywords");
            t.start();
        }
    }

    public void stop() {
        stop = true;

    }



}
