package com.elevenpaths.googleindexretriever;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.control.Tooltip;

/**
 * The Class Navigator.
 */
public class Navigator extends HBox {

	/** The page url. */
	private final TextField pageUrl;

	/** The go button. */
	private final Button goButton;

	/** The web engine. */
	private final WebEngine webEngine;

	/**
	 * Instantiates a new navigator.
	 *
	 * @param webView the web view
	 * @param homePageUrl the home page url
	 * @param goToHomePage the go to home page
	 */
	public Navigator(final WebView webView, final String homePageUrl, final boolean goToHomePage) {
		// Set Spacing
		this.setSpacing(4);

		// Set the Style-properties of the Navigation Bar
		this.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
				+ "-fx-border-insets: 2;" + "-fx-border-radius: 2;" + "-fx-border-color: gray;");

		// Create the WebEngine
		webEngine = webView.getEngine();
		// webEngine.setUserAgent("Mozilla/5.0 (Windows NT 6.3; WOW64; rv:36.0) Gecko/20100101 Firefox/48.0");
		webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:68.0) Gecko/20100101 Firefox/68.0");

		pageUrl = new TextField();

		// Create the Buttons
		goButton = new Button("Go");
		final Tooltip goTooltip = new Tooltip("Use http or https in the URLs");
		goButton.setTooltip(goTooltip);

		// Let the TextField grow horizontallly
		HBox.setHgrow(pageUrl, Priority.ALWAYS);

		// Add an ActionListener to navigate to the entered URL
		pageUrl.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				webEngine.load(pageUrl.getText());
			}
		});

		// Update the stage title when a new web page title is available
		webEngine.locationProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldvalue,
					final String newvalue) {
				// Set the Title of the Stage
				pageUrl.setText(newvalue);
			}
		});

		// Add an ActionListener for the Go Button
		goButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				webEngine.load(pageUrl.getText());
			}
		});

		this.getChildren().addAll(pageUrl, goButton);

		if (goToHomePage) {
			// Load the URL
			webEngine.load(homePageUrl);
		}
	}

	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(final String url) {
		pageUrl.setText(url);
		goButton.fire();
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return pageUrl.getText();
	}

	/**
	 * Gets the web engine.
	 *
	 * @return the web engine
	 */
	public WebEngine getWebEngine() {
		return webEngine;
	}

}
