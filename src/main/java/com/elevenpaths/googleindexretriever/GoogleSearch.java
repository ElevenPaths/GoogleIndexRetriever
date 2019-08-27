package com.elevenpaths.googleindexretriever;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import com.elevenpaths.googleindexretriever.exceptions.EmptyQueryException;
import org.jsoup.Jsoup;
//import org.w3c.dom.Document;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.control.TabPane;
import org.jsoup.helper.W3CDom;

/**
 * The Class GoogleSearch.
 */
public class GoogleSearch {

	/** The Constant CAPTCHA. */
	private static final String CAPTCHA = "https://www.google.com/sorry/index";

	/** The Constant NAVIGATOR_TAB. */
	private static final int NAVIGATOR_TAB = 2;

	/** The navigator. */
	private final Navigator navigator;

	/** The tab pane. */
	private final TabPane tabPane;

	/** The obs. */
	private Observer obs;

	/** The query. */
	private String query = "";

	/** The w3cDom. */
	private final W3CDom w3cDom;

	/** The Constant buzzWordsDefault. */
	private static final String[] buzzWordsDefault = { "time", "person", "year", "way", "day", "thing", "man", "world",
			"life", "hand", "part", "child", "eye", "woman", "place", "work", "week", "case", "point", "government",
			"company", "number", "group", "problem", "fact", "have", "say", "get", "make", "know", "take", "see",
			"come", "think", "look", "want", "give", "use", "find", "tell", "ask", "work", "seem", "feel", "try",
			"leave", "call", "good", "new", "first", "last", "long", "great", "little", "own", "other", "old", "right",
			"big", "high", "different", "small", "large", "next", "early", "young", "important", "few", "public", "bad",
			"same", "able", "for", "with", "from", "about", "into", "over", "after", "beneath", "under", "above", "the",
			"and", "that", "not", "you", "this", "but", "his", "they", "her", "she", "will", "one", "all", "would",
			"there", "their" };

	/** The Constant buzzWordsSpamDefault. */
	private static final String[] buzzWordsSpamDefault = { "cialis", "orgasms", "viagra", "shipping", "milf", "valium",
			"pharmacy", "xanax", "increase", "vicodin", "orgasm", "online", "disclaimer", "rolex", "required", "remove",
			"prescription", "hydrocodone", "guaranteed", "cheap", "adobe", "ambien", "free", "price", "discount" };

	/** The keywords. */
	private ArrayList<String> keywords;

	/** The keywords spam. */
	private ArrayList<String> keywordsSpam;

	/**
	 * Instantiates a new google search.
	 *
	 * @param navigator the navigator
	 * @param tabPane the tab pane
	 */
	public GoogleSearch(final Navigator navigator, final TabPane tabPane) {

		this.navigator = navigator;
		this.tabPane = tabPane;
		keywords = new ArrayList<String>();
		keywordsSpam = new ArrayList<String>();
		w3cDom = new W3CDom();
	}

	/**
	 * Instantiates a new google search.
	 *
	 * @param query the query
	 * @param navigator the navigator
	 * @param tabPane the tab pane
	 */
	public GoogleSearch(final String query, final Navigator navigator, final TabPane tabPane) {
		this.navigator = navigator;
		this.tabPane = tabPane;
		this.query = query;

		keywords = new ArrayList<String>();
		keywordsSpam = new ArrayList<String>();
		w3cDom = new W3CDom();
	}

	/**
	 * Strip XSS.
	 *
	 * @param value the value
	 * @return the string
	 */
	public String stripXSS(String value) {
		if (value == null) {
			return null;
		}

		// Avoid null characters
		value = value.replaceAll("\0", "");

		// Clean out HTML
		value = Jsoup.clean(value, Whitelist.none());

		return value;
	}

	/**
	 * Gets the data.
	 *
	 * @param query the query
	 * @return the data
	 * @throws EmptyQueryException the empty query exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	@SuppressWarnings("restriction")
	private void getData(final String query) throws EmptyQueryException, UnsupportedEncodingException {

		if (this.query.isEmpty() || this.query == null) {
			throw new EmptyQueryException();
		}

		final String request = "https://www.google.com/search?q=" + URLEncoder.encode(stripXSS(query), "UTF-8");

		navigator.setUrl(request);

		navigator.getWebEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {

			@Override
			public void changed(final ObservableValue<? extends Worker.State> observable, final Worker.State oldValue,
					final Worker.State newValue) {

				if (newValue != Worker.State.SUCCEEDED) {
					return;
				}

				// check captcha
				if (navigator.getUrl().contains(CAPTCHA)) {
					tabPane.getSelectionModel().select(NAVIGATOR_TAB);
					return;
				}

				final org.w3c.dom.Document doc = navigator.getWebEngine().getDocument();

				Document doc2 = Jsoup.parse(w3cDom.asString(doc));

				final Whitelist wl = new Whitelist().basic();
				wl.addAttributes("span", "class");
				final Cleaner clean = new Cleaner(wl);
				doc2 = clean.clean(doc2);

				if (obs instanceof KeyWordsProcess || obs instanceof MakeShotProcess) {
					setResult(doc2);
				} else if (obs instanceof SpamProcess || obs instanceof MakeShotSpamProcess) {
					setResultSpam(doc2);
				}

			}
		});

	}

	/**
	 * Sets the result.
	 *
	 * @param doc the new result
	 */
	public void setResult(final Document doc) {

		final Elements data = doc.select(".st");

		if (data.size() == 0) {

			obs.setResult("");
			obs.setSemaphore(true);

		} else if (data.size() > 1) {
			/*
			 * final Alert alert = new Alert(AlertType.ERROR); alert.setTitle("Information Dialog");
			 * alert.setHeaderText(null); alert.setContentText("more than one result"); alert.showAndWait(); obs.stop();
			 */
			obs.setSemaphore(true);

			if (obs instanceof MakeShotProcess) {
				obs.setResult(data.text());
			} else {
				obs.setResult(data.get(0).text());

			}

		} else { // data.size() == 1

			obs.setResult(data.get(0).text());
			obs.setSemaphore(true);
		}

		// return data.text();

	}

	/**
	 * Sets the result spam.
	 *
	 * @param doc the new result spam
	 */
	public void setResultSpam(final Document doc) {

		final Elements elements = doc.select(".st");
		obs.setElements(elements);
		obs.setSemaphore(true);
	}

	/**
	 * Sets the query.
	 *
	 * @param query the query
	 * @param observer the observer
	 * @throws EmptyQueryException the empty query exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	public void setQuery(final String query, final Observer observer)
			throws EmptyQueryException, UnsupportedEncodingException {
		this.query = query;
		obs = observer;

		getData(query);

	}

	/**
	 * Load default keywords.
	 */
	public void loadDefaultKeywords() {
		keywords = new ArrayList<String>(Arrays.asList(buzzWordsDefault));
	}

	/**
	 * Load default spam keywords.
	 */
	public void loadDefaultSpamKeywords() {
		keywordsSpam = new ArrayList<String>(Arrays.asList(buzzWordsSpamDefault));
	}

	/**
	 * Gets the keywords.
	 *
	 * @return the keywords
	 */
	public ArrayList<String> getKeywords() {
		return keywords;
	}

	/**
	 * Sets the keywords.
	 *
	 * @param keyw the new keywords
	 */
	public void setKeywords(final ArrayList<String> keyw) {
		keywords = keyw;
	}

	/**
	 * Gets the spam keywords.
	 *
	 * @return the spam keywords
	 */
	public ArrayList<String> getSpamKeywords() {
		return keywordsSpam;
	}

	/**
	 * Sets the spam keywords.
	 *
	 * @param keyw the new spam keywords
	 */
	public void setSpamKeywords(final ArrayList<String> keyw) {
		keywordsSpam = keyw;
	}

}