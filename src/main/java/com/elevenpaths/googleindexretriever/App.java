package com.elevenpaths.googleindexretriever;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class App.
 */
@SuppressWarnings("restriction")
public class App extends Application {

	/** The bundle. */
	public static ResourceBundle bundle;

	/** The Constant RETRIEVER_EXPORT_FILE. */
	private static final String RETRIEVER_EXPORT_FILE = "export.html";

	/** The Constant SPAM_EXPORT_FILE. */
	private static final String SPAM_EXPORT_FILE = "spamExport.html";

	/** The Constant google url. */
	private static final String GOOGLE_URL = "http://www.google.com";

	/** The navigator. */
	private Navigator navigator;

	/** The tab pane. */
	private TabPane tabPane;

	/** The retriever control. */
	private Control retrieverControl;

	/** The spam control. */
	private Control spamControl;

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

		// Locale
		final Locale locale = new Locale("en", "UK");
		// Locale locale = new Locale("es", "ES");
		// final Locale locale = Locale.getDefault(); --> Automatic detection of Locale

		// Bundle
		bundle = ResourceBundle.getBundle("strings", locale);

		// App Icon
		primaryStage.getIcons().add(new Image("img/xr.png"));

		// App tittle
		primaryStage.setTitle(App.bundle.getString("app.title"));

		// disable resize
		primaryStage.setResizable(false);

		// Root panel
		final VBox root = new VBox();

		// menu
		final AppMenuBar appMenuBar = new AppMenuBar(this);
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

		final Tab tabRetriever = new Tab(App.bundle.getString("app.tab.retriever"));
		final Tab tabSpam = new Tab(App.bundle.getString("app.tab.spam"));
		final Tab tabNavigator = new Tab(App.bundle.getString("app.tab.navigator"));

		tabPane.getTabs().add(tabRetriever);

		tabPane.getTabs().add(tabSpam);

		tabPane.getTabs().add(tabNavigator);

		// No move
		tabNavigator.setContent(createNavigator());

		// retriever body
		final AppRetrieverBody retrieverBody = new AppRetrieverBody(navigator, tabPane, RETRIEVER_EXPORT_FILE, false);
		setRetrieverControl(retrieverBody.getControl());
		final VBox vboxRetriever = new VBox(retrieverBody);
		tabRetriever.setContent(vboxRetriever);

		// spam retriever body
		final AppRetrieverBody spamBody = new AppRetrieverBody(navigator, tabPane, SPAM_EXPORT_FILE, true);
		setSpamControl(spamBody.getControl());
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
		final String homePageUrl = GOOGLE_URL;

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

	/**
	 * Gets the retriever control.
	 *
	 * @return the retriever control
	 */
	protected Control getRetrieverControl() {
		return retrieverControl;
	}

	/**
	 * Sets the retriever control.
	 *
	 * @param retrieverControl the new retriever control
	 */
	protected void setRetrieverControl(final Control retrieverControl) {
		this.retrieverControl = retrieverControl;
	}

	/**
	 * Gets the spam control.
	 *
	 * @return the spam control
	 */
	protected Control getSpamControl() {
		return spamControl;
	}

	/**
	 * Sets the spam control.
	 *
	 * @param spamControl the new spam control
	 */
	protected void setSpamControl(final Control spamControl) {
		this.spamControl = spamControl;
	}

	/**
	 * Sets the locale.
	 *
	 * @param locale the new locale
	 */
	public static void setLocale(final Locale locale) {
		bundle = ResourceBundle.getBundle("strings", locale);
	}

}
