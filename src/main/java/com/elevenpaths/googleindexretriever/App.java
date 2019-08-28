package com.elevenpaths.googleindexretriever;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * The Class App.
 */
@SuppressWarnings("restriction")
public class App extends Application {

	private static final String RETRIEVER_EXPORT_FILE = "export.html";
	private static final String SPAM_EXPORT_FILE = "spamExport.html";

	/** The navigator. */
	private Navigator navigator;

	/** The tab pane. */
	private TabPane tabPane;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(final String[] args) {
		Application.launch(args);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(final Stage primaryStage) throws Exception {

		// LOOK & FEEL
		// setUserAgentStylesheet(STYLESHEET_CASPIAN); default in java 7
		// setUserAgentStylesheet(STYLESHEET_MODENA); default in java 8

		// App Icon
		primaryStage.getIcons().add(new Image("img/xr.png"));

		// App tittle
		primaryStage.setTitle("Google Index Retreiver");

		// Root panel
		final VBox root = new VBox();

		// menu
		final AppMenuBar appMenuBar = new AppMenuBar();
		root.getChildren().add(appMenuBar);

		// body
		root.getChildren().add(createBody());

		final Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	/**
	 * Creates the body.
	 *
	 * @return the tab pane
	 */
	private TabPane createBody() {

		tabPane = new TabPane();

		// disable close button
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

		final Tab tabRetriever = new Tab("Retriever");
		final Tab tabSpam = new Tab("Find Spam");
		final Tab tabNavigator = new Tab("Navigator");

		tabPane.getTabs().add(tabRetriever);

		tabPane.getTabs().add(tabSpam);

		tabPane.getTabs().add(tabNavigator);

		// No move
		tabNavigator.setContent(createNavigator());

		final AppRetrieverBody retrieverBody = new AppRetrieverBody(navigator, tabPane, RETRIEVER_EXPORT_FILE, false);
		final VBox vboxRetriever = new VBox(retrieverBody);
		tabRetriever.setContent(vboxRetriever);

		final AppRetrieverBody spamBody = new AppRetrieverBody(navigator, tabPane, SPAM_EXPORT_FILE, true);
		final VBox vboxSpam = new VBox(spamBody);
		tabSpam.setContent(vboxSpam);

		return tabPane;

	}

	/**
	 * Creates the navigator.
	 *
	 * @return the v box
	 */
	private VBox createNavigator() {

		// Create the WebView
		final WebView webView = new WebView();

		// Load the Google web page
		final String homePageUrl = "http://www.google.com";

		// Create the WebMenu
		final MenuButton menu = new NavigatorMenu(webView);

		// Create the Navigator
		navigator = new Navigator(webView, homePageUrl, true);
		// Add the children to the Navigation
		navigator.getChildren().add(menu);

		// Create the VBox
		final VBox rootNavigator = new VBox(navigator, webView);

		// Set the Style-properties of the VBox
		// rootNavigator.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
		// + "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: blue;");

		return rootNavigator;

	}

}
