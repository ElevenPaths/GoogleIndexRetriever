package com.elevenpaths.googleindexretriever;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.ListView;
import javafx.geometry.Orientation;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * The Class App.
 */
public class App extends Application {

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
	@SuppressWarnings("restriction")
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
		root.getChildren().add(createMenu());

		// body
		root.getChildren().add(createBody());

		final Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	/**
	 * Creates the menu.
	 *
	 * @return the menu bar
	 */
	private MenuBar createMenu() {

		final MenuBar menuBar = new MenuBar();

		// menu File
		final Menu menuFile = new Menu("File");
		final MenuItem kewwords = new MenuItem("Spam Keywords");
		final MenuItem spamkeywords = new MenuItem("Retriever Keywords");
		menuFile.getItems().addAll(kewwords, spamkeywords);

		// menu Help
		final Menu menuHelp = new Menu("Help");
		final MenuItem help = new MenuItem("GIR Help");
		final MenuItem about = new MenuItem("About");
		menuHelp.getItems().addAll(help, about);

		help.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {

				final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
				if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
					try {
						desktop.browse(new URL("https://www.elevenpaths.com/labs/tools/index.html").toURI());
					} catch (final MalformedURLException e1) {

						final Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Information Dialog");
						alert.setHeaderText(null);
						alert.setContentText(e1.getMessage());
						alert.showAndWait();

						e1.printStackTrace();
					} catch (final IOException e1) {

						final Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Information Dialog");
						alert.setHeaderText(null);
						alert.setContentText(e1.getMessage());
						alert.showAndWait();

						e1.printStackTrace();
					} catch (final URISyntaxException e1) {

						final Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Information Dialog");
						alert.setHeaderText(null);
						alert.setContentText(e1.getMessage());
						alert.showAndWait();

						e1.printStackTrace();
					}
				}

			}
		});

		menuBar.getMenus().addAll(menuFile, menuHelp);

		about.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {

				final Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("About");
				alert.setHeaderText(null);
				alert.setContentText("Google Index Retriever(c) ElevenPaths 2019. Version: 2.0");
				alert.showAndWait();

			}
		});

		return menuBar;

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

		final VBox vboxRetriever = new VBox(retrieverBody());
		tabRetriever.setContent(vboxRetriever);

		final VBox vboxSpam = new VBox(spamBody());
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

	/**
	 * Retriever body.
	 *
	 * @return the grid pane
	 */
	private GridPane retrieverBody() {

		// Creating a GridPane container
		final GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(5);
		grid.setHgap(5);

		// Defining the Name text field
		final TextField search = new TextField();
		search.setPromptText("Search...");
		// name.setPrefColumnCount(10);
		search.setMinWidth(850);
		search.getText();
		GridPane.setConstraints(search, 0, 0);
		grid.getChildren().add(search);

		// Defining the start button
		final Button start = new Button("Start");
		GridPane.setConstraints(start, 1, 0);
		grid.getChildren().add(start);

		final Button oneShot = new Button("One Shot");
		GridPane.setConstraints(oneShot, 2, 0);
		grid.getChildren().add(oneShot);

		final Button stop = new Button("Stop");
		GridPane.setConstraints(stop, 3, 0);
		grid.getChildren().add(stop);

		// Create the ListView for the seasons
		final ListView<String> retrieverResult = new ListView<String>();
		// Set the Orientation of the ListView
		retrieverResult.setOrientation(Orientation.VERTICAL);
		// Set the Size of the ListView
		retrieverResult.setPrefSize(700, 500);

		GridPane.setConstraints(retrieverResult, 0, 1, 4, 1);
		grid.getChildren().add(retrieverResult);

		// scroll bar
		final ProgressBar pb = new ProgressBar(100);
		pb.setPrefSize(250, 30);

		final Label query = new Label("Query:");
		// query.setPrefSize(100, 30);

		final Label queryValue = new Label();
		queryValue.setPrefSize(720, 30);

		final GridPane gridScrollRow = new GridPane();
		// gridScrollRow.setPadding(new Insets(10, 10, 0, 0));
		// gridScrollRow.setVgap(5);
		gridScrollRow.setHgap(5);

		GridPane.setConstraints(query, 0, 0, 1, 1);
		gridScrollRow.getChildren().add(query);

		GridPane.setConstraints(queryValue, 1, 0, 1, 1);
		gridScrollRow.getChildren().add(queryValue);

		GridPane.setConstraints(pb, 2, 0, 2, 1);
		gridScrollRow.getChildren().add(pb);

		GridPane.setConstraints(gridScrollRow, 0, 2, 4, 1);
		grid.getChildren().add(gridScrollRow);

		final GridPane gridElapsedlRow = new GridPane();
		// gridElapsedlRow.setPadding(new Insets(0, 10, 10, 0));
		// gridElapsedlRow.setVgap(5);
		gridElapsedlRow.setHgap(5);

		final Label elapsed = new Label("Elapsed:");
		// elapsed.setPrefSize(50, 20);

		final Label time = new Label("00:00:00");
		time.setPrefSize(712, 20);

		GridPane.setConstraints(elapsed, 0, 0);
		gridElapsedlRow.getChildren().add(elapsed);

		GridPane.setConstraints(time, 1, 0);
		gridElapsedlRow.getChildren().add(time);

		// Defining the clean button
		final Button clean = new Button("Clean");
		final Tooltip cleanTooltip = new Tooltip("Clean results");
		clean.setTooltip(cleanTooltip);
		GridPane.setConstraints(clean, 2, 0);
		gridElapsedlRow.getChildren().add(clean);

		// Defining the export button
		final Button export = new Button("Export");
		final Tooltip exportTooltip = new Tooltip("Export results");
		export.setTooltip(exportTooltip);

		GridPane.setConstraints(export, 3, 0);
		gridElapsedlRow.getChildren().add(export);

		GridPane.setConstraints(gridElapsedlRow, 0, 3, 4, 1);
		grid.getChildren().add(gridElapsedlRow);

		final Control control = new Control(navigator, tabPane, retrieverResult, queryValue, time, pb);

		// Add an ActionListener for start Button
		start.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				pb.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
				control.makeQuery(search.getText());
			}
		});

		// Add an ActionListener for oneShot Button
		oneShot.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				pb.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
				control.makeShot(search.getText());
			}
		});

		// Add an ActionListener for stop Button
		stop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				pb.setProgress(100);
				control.stop();
				time.setText("00:00:00");
				queryValue.setText(" ");
			}
		});

		// Add an ActionListener for clean Button
		clean.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				retrieverResult.getItems().clear();
			}
		});

		// Add an ActionListener for export Button
		export.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				final ObservableList<String> values = retrieverResult.getItems();

				if (retrieverResult.getItems().size() > 0) {
					final ArrayList<String> rows = new ArrayList<String>();

					for (final String element : values) {
						rows.add(element);
					}

					try {
						control.export(rows, search.getText(), "export.html");
					} catch (final IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

						final Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Information Dialog");
						alert.setHeaderText(null);
						alert.setContentText("Error building file");
						alert.showAndWait();
					}

					final Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Information Dialog");
					alert.setHeaderText(null);
					alert.setContentText("Export success");
					alert.showAndWait();

				} else {
					final Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Information Dialog");
					alert.setHeaderText(null);
					alert.setContentText("No data to export");
					alert.showAndWait();

				}

			}
		});

		return grid;

	}

	/**
	 * Spam body.
	 *
	 * @return the grid pane
	 */
	private GridPane spamBody() {

		// Creating a GridPane container
		final GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(5);
		grid.setHgap(5);

		// Defining the Name text field
		final TextField search = new TextField();
		search.setPromptText("Search...");
		// name.setPrefColumnCount(10);
		search.setMinWidth(850);
		search.getText();
		GridPane.setConstraints(search, 0, 0);
		grid.getChildren().add(search);

		// Defining the start button
		final Button start = new Button("Start");
		GridPane.setConstraints(start, 1, 0);
		grid.getChildren().add(start);

		final Button oneShot = new Button("One Shot");
		GridPane.setConstraints(oneShot, 2, 0);
		grid.getChildren().add(oneShot);

		final Button stop = new Button("Stop");
		GridPane.setConstraints(stop, 3, 0);
		grid.getChildren().add(stop);

		// Create the ListView for the seasons
		final ListView<String> retrieverResult = new ListView<String>();
		// Set the Orientation of the ListView
		retrieverResult.setOrientation(Orientation.VERTICAL);
		// Set the Size of the ListView
		retrieverResult.setPrefSize(700, 500);

		GridPane.setConstraints(retrieverResult, 0, 1, 4, 1);
		grid.getChildren().add(retrieverResult);

		// scroll bar
		final ProgressBar pb = new ProgressBar(100);
		pb.setPrefSize(250, 30);

		final Label query = new Label("Query:");
		// query.setPrefSize(100, 30);

		final Label queryValue = new Label();
		queryValue.setPrefSize(720, 30);

		final GridPane gridScrollRow = new GridPane();
		// gridScrollRow.setPadding(new Insets(10, 10, 0, 0));
		// gridScrollRow.setVgap(5);
		gridScrollRow.setHgap(5);

		GridPane.setConstraints(query, 0, 0, 1, 1);
		gridScrollRow.getChildren().add(query);

		GridPane.setConstraints(queryValue, 1, 0, 1, 1);
		gridScrollRow.getChildren().add(queryValue);

		GridPane.setConstraints(pb, 2, 0, 2, 1);
		gridScrollRow.getChildren().add(pb);

		GridPane.setConstraints(gridScrollRow, 0, 2, 4, 1);
		grid.getChildren().add(gridScrollRow);

		final GridPane gridElapsedlRow = new GridPane();
		// gridElapsedlRow.setPadding(new Insets(0, 10, 10, 0));
		// gridElapsedlRow.setVgap(5);
		gridElapsedlRow.setHgap(5);

		final Label elapsed = new Label("Elapsed:");
		// elapsed.setPrefSize(50, 20);

		final Label time = new Label("00:00:00");
		time.setPrefSize(712, 20);

		GridPane.setConstraints(elapsed, 0, 0);
		gridElapsedlRow.getChildren().add(elapsed);

		GridPane.setConstraints(time, 1, 0);
		gridElapsedlRow.getChildren().add(time);

		// Defining the clean button
		final Button clean = new Button("Clean");
		final Tooltip cleanTooltip = new Tooltip("Clean results");
		clean.setTooltip(cleanTooltip);

		GridPane.setConstraints(clean, 2, 0);
		gridElapsedlRow.getChildren().add(clean);

		// Defining the export button
		final Button export = new Button("Export");
		final Tooltip exportTooltip = new Tooltip("Export results");
		export.setTooltip(exportTooltip);

		GridPane.setConstraints(export, 3, 0);
		gridElapsedlRow.getChildren().add(export);

		GridPane.setConstraints(gridElapsedlRow, 0, 3, 4, 1);
		grid.getChildren().add(gridElapsedlRow);

		final Control control = new Control(navigator, tabPane, retrieverResult, queryValue, time, pb);

		// Add an ActionListener for start Button
		start.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				pb.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
				control.makeSpamQuery(search.getText());
			}
		});

		// Add an ActionListener for oneShot Button
		oneShot.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				pb.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
				control.makeShotSpam(search.getText());
			}
		});

		// Add an ActionListener for stop Button
		stop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				pb.setProgress(100);
				control.stop();
				time.setText("00:00:00");
				queryValue.setText(" ");
			}
		});

		// Add an ActionListener for clean Button
		clean.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				retrieverResult.getItems().clear();
			}
		});

		// Add an ActionListener for export Button
		export.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				final ObservableList<String> values = retrieverResult.getItems();

				if (retrieverResult.getItems().size() > 0) {
					final ArrayList<String> rows = new ArrayList<String>();

					for (final String element : values) {
						rows.add(element);
					}

					try {
						control.export(rows, search.getText(), "spamExport.html");
					} catch (final IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

						final Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Information Dialog");
						alert.setHeaderText(null);
						alert.setContentText("Error building file");
						alert.showAndWait();
					}

					final Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Information Dialog");
					alert.setHeaderText(null);
					alert.setContentText("Export success");
					alert.showAndWait();

				} else {
					final Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Information Dialog");
					alert.setHeaderText(null);
					alert.setContentText("No data to export");
					alert.showAndWait();

				}

			}
		});

		return grid;

	}

}
