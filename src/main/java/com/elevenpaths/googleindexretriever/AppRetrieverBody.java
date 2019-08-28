package com.elevenpaths.googleindexretriever;

import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;

/**
 * The Class AppRetrieverBody.
 */
@SuppressWarnings("restriction")
public class AppRetrieverBody extends GridPane {

	/** The navigator. */
	private final Navigator navigator;

	/** The tab pane. */
	private final TabPane tabPane;

	/** The name file to export. */
	private final String nameFileToExport;

	/** The spam. */
	private final boolean spam;

	/**
	 * Instantiates a new app retriever body.
	 *
	 * @param navigator the navigator
	 * @param tabPane the tab pane
	 * @param nameFileToExport the name file to export
	 * @param spam the spam
	 */
	public AppRetrieverBody(final Navigator navigator, final TabPane tabPane, final String nameFileToExport,
			final boolean spam) {
		super();
		this.navigator = navigator;
		this.tabPane = tabPane;
		this.nameFileToExport = nameFileToExport;
		this.spam = spam;
		this.buildBody();
	}

	/**
	 * Builds the body.
	 */
	private void buildBody() {

		this.setPadding(new Insets(10, 10, 10, 10));
		this.setVgap(5);
		this.setHgap(5);

		// Defining the Name text field
		final TextField search = new TextField();
		search.setPromptText("Search...");
		// name.setPrefColumnCount(10);
		search.setMinWidth(850);
		search.getText();
		GridPane.setConstraints(search, 0, 0);
		this.getChildren().add(search);

		// Defining the start button
		final Button start = new Button("Start");
		GridPane.setConstraints(start, 1, 0);
		this.getChildren().add(start);

		final Button oneShot = new Button("One Shot");
		GridPane.setConstraints(oneShot, 2, 0);
		this.getChildren().add(oneShot);

		final Button stop = new Button("Stop");
		GridPane.setConstraints(stop, 3, 0);
		this.getChildren().add(stop);

		// Create the ListView for the seasons
		final ListView<String> retrieverResult = new ListView<String>();
		// Set the Orientation of the ListView
		retrieverResult.setOrientation(Orientation.VERTICAL);
		// Set the Size of the ListView
		retrieverResult.setPrefSize(700, 500);

		GridPane.setConstraints(retrieverResult, 0, 1, 4, 1);
		this.getChildren().add(retrieverResult);

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
		this.getChildren().add(gridScrollRow);

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
		this.getChildren().add(gridElapsedlRow);

		final Control control = new Control(navigator, tabPane, retrieverResult, queryValue, time, pb);

		// Add an ActionListener for start Button
		start.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				pb.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
				if (spam) {
					control.makeSpamQuery(search.getText());
				} else {
					control.makeQuery(search.getText());
				}

			}
		});

		// Add an ActionListener for oneShot Button
		oneShot.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				pb.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
				if (spam) {
					control.makeShotSpam(search.getText());
				} else {
					control.makeShot(search.getText());
				}

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
						control.export(rows, search.getText(), nameFileToExport);
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

	}
}
