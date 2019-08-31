package com.elevenpaths.googleindexretriever;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.elevenpaths.googleindexretriever.process.KeyWordsProcess;
import com.elevenpaths.googleindexretriever.process.MakeShotProcess;
import com.elevenpaths.googleindexretriever.process.MakeShotSpamProcess;
import com.elevenpaths.googleindexretriever.process.SpamProcess;

import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;

// TODO: Auto-generated Javadoc
/**
 * The Class Control.
 */
@SuppressWarnings("restriction")
public class Control {

	/** The Constant ZERO. */
	private static final int ZERO = 0;

	/** The Constant EMPTY. */
	private static final String EMPTY = "   ";

	/** The gs. */
	private final GoogleSearch gs;// Model

	/** The navigator. */
	private final Navigator navigator;

	/** The tab pane. */
	private final TabPane tabPane;

	/** The query value. */
	private final Label queryValue;

	/** The time. */
	private final Label time;

	/** The pb. */
	private final ProgressBar pb;

	/** The result. */
	private final ListView<String> listResult;

	/** The result queue. */
	private final BlockingQueue<String> resultQueue; // comunication between threads

	/** The query queue. */
	private final BlockingQueue<String> queryQueue; // comunication between threads

	/** The elapsed queue. */
	private final BlockingQueue<String> elapsedQueue; // comunication between threads

	/** The stop queue. */
	private final BlockingQueue<String> stopQueue; // comunication between threads

	/** The bgkeywords process. */
	private KeyWordsProcess bgkeywordsProcess;

	/** The bgspam process. */
	private SpamProcess bgspamProcess;

	/** The make shot process. */
	private MakeShotProcess makeShotProcess;

	/** The make shot spam proccess. */
	private MakeShotSpamProcess makeShotSpamProccess;

	/**
	 * Instantiates a new control.
	 *
	 * @param navigator the navigator
	 * @param tabPane the tab pane
	 * @param listResult the list result
	 * @param queryValue the query value
	 * @param time the time
	 * @param pb the pb
	 */
	public Control(final Navigator navigator, final TabPane tabPane, final ListView<String> listResult,
			final Label queryValue, final Label time, final ProgressBar pb) {

		gs = new GoogleSearch(navigator, tabPane);

		this.listResult = listResult;
		this.tabPane = tabPane;
		this.navigator = navigator;
		this.queryValue = queryValue;
		this.time = time;
		this.pb = pb;

		resultQueue = new ArrayBlockingQueue<String>(1000);

		queryQueue = new ArrayBlockingQueue<String>(1000);

		elapsedQueue = new ArrayBlockingQueue<String>(1000);

		stopQueue = new ArrayBlockingQueue<String>(1);

		final LongProperty lastUpdate = new SimpleLongProperty();

		final long minUpdateInterval = ZERO; // nanoseconds. Set to higher number to slow output.

		final AnimationTimer timer = new AnimationTimer() {

			@Override
			public void handle(final long now) {
				if (now - lastUpdate.get() > minUpdateInterval) {
					final String res = resultQueue.poll();

					final String query = queryQueue.poll();

					final String elapsedTime = elapsedQueue.poll();

					final String stop = stopQueue.poll();

					if (stop != null) {
						realStop();
					}

					if (elapsedTime != null) {
						time.setText(elapsedTime);

					}

					if (query != null) {
						queryValue.setText(query);

					}

					if (res != null) {
						listResult.getItems().add(res);

					}
					lastUpdate.set(now);
				}
			}

		};

		timer.start();

	}

	/**
	 * Make query.
	 *
	 * @param query the query
	 */
	public void makeQuery(final String query) {

		if (!gs.isLoadedKeywords()) {
			loadKeywords();
		}

		if (!query.trim().isEmpty()) {
			bgkeywordsProcess = new KeyWordsProcess(this, gs, query);
			bgkeywordsProcess.start();
		}
	}

	/**
	 * Make spam query.
	 *
	 * @param query the query
	 */
	public void makeSpamQuery(final String query) {

		if (!gs.isLoadedSpamKeywords()) {
			loadSpamKeywords();
		}
		if (!query.trim().isEmpty()) {
			bgspamProcess = new SpamProcess(this, gs, query);
			bgspamProcess.start();
		}

	}

	/**
	 * Make shot.
	 *
	 * @param query the query
	 */
	public void makeShot(final String query) {
		if (!query.trim().isEmpty()) {
			makeShotProcess = new MakeShotProcess(this, gs, query);
			makeShotProcess.start();
		}
	}

	/**
	 * Make shot spam.
	 *
	 * @param query the query
	 */
	public void makeShotSpam(final String query) {
		if (!query.trim().isEmpty()) {
			makeShotSpamProccess = new MakeShotSpamProcess(this, gs, query);
			makeShotSpamProccess.start();
		}

	}

	/**
	 * Stop.
	 */
	public void stop() {

		stopQueue.add("Stop");

	}

	/**
	 * Real stop.
	 */
	private void realStop() {

		if (bgkeywordsProcess != null && bgkeywordsProcess.getThread().isAlive()) {
			bgkeywordsProcess.stop();
		}

		if (makeShotProcess != null && makeShotProcess.getThread().isAlive()) {
			makeShotProcess.stop();
		}

		if (bgspamProcess != null && bgspamProcess.getThread().isAlive()) {
			bgspamProcess.stop();
		}

		if (makeShotSpamProccess != null && makeShotSpamProccess.getThread().isAlive()) {
			makeShotSpamProccess.stop();
		}

		pb.setProgress(100);

	}

	/**
	 * Sets the path progress bar.
	 *
	 * @param path the new path progress bar
	 */
	public void setPathProgressBar(final String path) {
		queryQueue.add(path);
	}

	/**
	 * Calc HMS.
	 *
	 * @param timeInSeconds the time in seconds
	 * @return the string
	 */
	public String calcHMS(int timeInSeconds) {
		int hours, minutes, seconds;
		hours = timeInSeconds / 3600;
		timeInSeconds = timeInSeconds - (hours * 3600);
		minutes = timeInSeconds / 60;
		timeInSeconds = timeInSeconds - (minutes * 60);
		seconds = timeInSeconds;
		final DecimalFormat formatter = new DecimalFormat("00");
		// String aFormatted = formatter.format(a);
		return formatter.format(hours) + ":" + formatter.format(minutes) + ":" + formatter.format(seconds);
	}

	/**
	 * Adds the list.
	 *
	 * @param time the time
	 * @param word the word
	 * @param result the result
	 */
	public void addList(final String time, String word, final String result) {

		System.out.println("time:" + time + " word:" + word + " result:" + result);

		if (result != null && !result.isEmpty() && !checkStringInList(result)) {

			if (word == null || word == "") {
				word = EMPTY;
			}

			final String value = time + "---" + word + "---" + result;

			resultQueue.add(value);
		}

	}

	/**
	 * Sets the elepased.
	 *
	 * @param time the new elepased
	 */
	public void setElepased(final String time) {
		elapsedQueue.add(time);
	}

	/**
	 * Check string in list.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	private boolean checkStringInList(final String value) {

		for (final String itemValue : listResult.getItems()) {

			final String[] itemValues = itemValue.split("---");

			if (value.contains(itemValues[2])) {
				return true;
			}

		}

		return false;
	}

	/**
	 * Export.
	 *
	 * @param data the data
	 * @param dork the dork
	 * @param nameFile the name file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void export(final ArrayList<String> data, final String dork, final String nameFile) throws IOException {

		final File file = new File(nameFile);

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		final FileWriter fw = new FileWriter(file.getAbsoluteFile());
		final BufferedWriter bw = new BufferedWriter(fw);
		bw.write(
				"<style type=\"text/css\"> table.sample { border-width: 1px;  border-spacing: 2px;  border-style: outset;   border-color: black; border-collapse: separate; background-color: white; } table.sample th {   border-width: 1px;  padding: 5px; border-style: inset; border-color: green;  background-color: white; } table.sample td { border-width: 1px;  padding: 5px;  border-style: inset;   border-color: green;    background-color: white;    }</style>");
		bw.newLine();
		bw.write("<h1>#Dork: <font color=blue>" + dork + "</font><br>#Total: <font color=blue>" + data.size()
				+ "</font></h1>");
		bw.write(
				"<table class=sample><tr><td><font color=blue>Words</font></td><td><font color=blue>Sentences</font></td></tr>");// Start
																																	// table
		for (final String ds : data) {
			final String[] list = ds.split("---");

			bw.write("<tr>");

			for (int i = 1; i < list.length; i++) {
				final String value = list[i];
				if (!value.isEmpty()) {
					bw.write("<td>" + escapeHtml4(value) + "</td>");
					bw.newLine();
				}
			}

			bw.write("</tr>");
		}

		bw.write("</table>");
		bw.close();
		fw.close();

	}

	/**
	 * Load keywords.
	 *
	 * @return the array list
	 */
	private ArrayList<String> loadKeywords() {
		final ArrayList<String> keywords = new ArrayList<String>();

		try {
			final FileReader file = new FileReader("keywords.txt");
			final BufferedReader reader = new BufferedReader(file);
			String line;

			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("#")) {
					keywords.add(line);
				}
			}

			reader.close();
			file.close();

			gs.setKeywords(keywords);
		} catch (final FileNotFoundException e) {
			final Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Keywords not found.Default keywords in use");
			alert.showAndWait();

			gs.loadDefaultKeywords();

		} catch (final IOException e) {
			final Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Error reading Keywords file");
			alert.showAndWait();
		}

		return keywords;
	}

	/**
	 * Load spam key words.
	 *
	 */
	private void loadSpamKeywords() {
		final ArrayList<String> keywordsSpam = new ArrayList<String>();

		try {
			final FileReader file = new FileReader("spamKeywords.txt");
			final BufferedReader reader = new BufferedReader(file);
			String line;

			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("#")) {
					keywordsSpam.add(line);
				}
			}

			reader.close();
			file.close();
			gs.setSpamKeywords(keywordsSpam);
		} catch (final FileNotFoundException e) {
			final Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Spam Keywords not found. Default spam keywords in use");
			alert.showAndWait();

			gs.loadDefaultSpamKeywords();
		} catch (final IOException e) {
			final Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Error reading Spam Keywords file");
			alert.showAndWait();
			e.printStackTrace();
		}

	}

	/**
	 * Save keywords.
	 *
	 */
	public void saveKeywords() {
		final ArrayList<String> keywords = gs.getKeywords();
		try {
			final File file = new File("keywords.txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			final FileWriter fw = new FileWriter(file.getAbsoluteFile());
			final BufferedWriter bw = new BufferedWriter(fw);
			for (final String k : keywords) {
				bw.write(k);
				bw.newLine();
			}
			bw.close();
			fw.close();
		} catch (final IOException e) {

			final Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Error saving Keywords into a file");
			alert.showAndWait();

			e.printStackTrace();
		}
	}

	/**
	 * Save spam keywords.
	 */
	public void saveSpamKeywords() {

		final ArrayList<String> keywordsSpam = gs.getSpamKeywords();
		try {
			final File file = new File("spamKeywords.txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			final FileWriter fw = new FileWriter(file.getAbsoluteFile());
			final BufferedWriter bw = new BufferedWriter(fw);
			for (final String k : keywordsSpam) {
				bw.write(k);
				bw.newLine();
			}
			bw.close();
			fw.close();
		} catch (final IOException e) {

			final Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Error saving Spam Keywords into a file");
			alert.showAndWait();

			e.printStackTrace();
		}
	}

	/**
	 * Launch keywords.
	 *
	 * @param spam the spam
	 */
	public void launchKeywords(final boolean spam) {
		KeywordsDialog ksd = null;

		if (spam) {
			if (!gs.isLoadedSpamKeywords()) {
				loadSpamKeywords();
			}
			ksd = new KeywordsDialog("Spam Keywords", gs.getSpamKeywords(), gs.isUseSpamKeywords(), spam);
		} else {

			if (!gs.isLoadedKeywords()) {
				loadKeywords();
			}
			ksd = new KeywordsDialog("Keywords", gs.getKeywords(), gs.isUseKeywords(), spam);
		}
		ksd.showAndWait();

		if (ksd.isSave()) {
			if (spam) {
				gs.setSpamKeywords(ksd.getKeywords());
				saveSpamKeywords();
				gs.setUseSpamKeywords(ksd.isUseKeywordsCheck());

			} else {
				gs.setKeywords(ksd.getKeywords());
				saveKeywords();
				gs.setUseKeywords(ksd.isUseKeywordsCheck());
			}

			final Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Save success");
			alert.showAndWait();

		}

	}

}