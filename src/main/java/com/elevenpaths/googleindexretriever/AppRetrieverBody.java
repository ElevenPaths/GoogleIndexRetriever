package com.elevenpaths.googleindexretriever;

import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.ObservableList;
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

// TODO: Auto-generated Javadoc
/**
 * The Class AppRetrieverBody.
 */
@SuppressWarnings("restriction")
public class AppRetrieverBody extends GridPane {

	/** The Constant PROGRESS_BAR_FULL. */
	public static final int PROGRESS_BAR_FULL = 100;

	/** The Constant EMPTY. */
	public static final String EMPTY = " ";

	/** The navigator. */
	private final Navigator navigator;

	/** The tab pane. */
	private final TabPane tabPane;

	/** The name file to export. */
	private final String nameFileToExport;

	/** The control. */
	private Control control;

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
		search.setPromptText(App.bundle.getString("app.body.search"));
		search.setMinWidth(850);
		search.getText();
		GridPane.setConstraints(search, 0, 0);
		this.getChildren().add(search);

		// Defining the start button
		final Button start = new Button(App.bundle.getString("app.body.start"));
		final Tooltip startTooltip = new Tooltip(App.bundle.getString("app.body.start.tooltip"));
		start.setTooltip(startTooltip);
		GridPane.setConstraints(start, 1, 0);
		this.getChildren().add(start);

		final Button oneShot = new Button(App.bundle.getString("app.body.oneShot"));
		final Tooltip oneShotTooltip = new Tooltip(App.bundle.getString("app.body.oneShot.tooltip"));
		oneShot.setTooltip(oneShotTooltip);
		GridPane.setConstraints(oneShot, 2, 0);
		this.getChildren().add(oneShot);

		final Button stop = new Button(App.bundle.getString("app.body.stop"));
		final Tooltip stopTooltip = new Tooltip(App.bundle.getString("app.body.stop.tooltip"));
		stop.setTooltip(stopTooltip);
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

		final Label query = new Label(App.bundle.getString("app.body.query"));
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

		final Label elapsed = new Label(App.bundle.getString("app.body.elapsed"));
		// elapsed.setPrefSize(50, 20);

		final Label time = new Label(App.bundle.getString("app.body.elapsed.time"));
		time.setPrefSize(712, 20);

		GridPane.setConstraints(elapsed, 0, 0);
		gridElapsedlRow.getChildren().add(elapsed);

		GridPane.setConstraints(time, 1, 0);
		gridElapsedlRow.getChildren().add(time);

		// Defining the clean button
		final Button clean = new Button(App.bundle.getString("app.body.clean"));
		final Tooltip cleanTooltip = new Tooltip(App.bundle.getString("app.body.clean.tooltip"));
		clean.setTooltip(cleanTooltip);
		GridPane.setConstraints(clean, 2, 0);
		gridElapsedlRow.getChildren().add(clean);

		// Defining the export button
		final Button export = new Button(App.bundle.getString("app.body.export"));
		final Tooltip exportTooltip = new Tooltip(App.bundle.getString("app.body.export.tooltip"));
		export.setTooltip(exportTooltip);

		GridPane.setConstraints(export, 3, 0);
		gridElapsedlRow.getChildren().add(export);

		GridPane.setConstraints(gridElapsedlRow, 0, 3, 4, 1);
		this.getChildren().add(gridElapsedlRow);

		control = new Control(navigator, tabPane, retrieverResult, queryValue, time, pb);

		// Add an ActionListener for start Button
		start.setOnAction((event) -> {
			final String queryVal = search.getText();

			if (!queryVal.trim().isEmpty()) {
				pb.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
				if (spam) {
					control.makeSpamQuery(queryVal);
				} else {
					control.makeQuery(queryVal);
				}
			}

		});

		// Add an ActionListener for oneShot Button
		oneShot.setOnAction((event) -> {
			final String queryVal = search.getText();

			if (!queryVal.trim().isEmpty()) {
				pb.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
				if (spam) {
					control.makeShotSpam(queryVal);
				} else {
					control.makeShot(queryVal);
				}
			}

		});

		// Add an ActionListener for stop Button
		stop.setOnAction((event) -> {
			pb.setProgress(PROGRESS_BAR_FULL);
			control.stop();
			time.setText(App.bundle.getString("app.body.elapsed.time"));
			queryValue.setText(EMPTY);

		});

		// Add an ActionListener for clean Button
		clean.setOnAction((event) -> {
			retrieverResult.getItems().clear();

		});

		// Add an ActionListener for export Button
		export.setOnAction((event) -> {
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
					alert.setTitle(App.bundle.getString("alert.title"));
					alert.setHeaderText(null);
					alert.setContentText(App.bundle.getString("alert.export.error"));
					alert.showAndWait();
				}

				final Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle(App.bundle.getString("alert.title"));
				alert.setHeaderText(null);
				alert.setContentText(App.bundle.getString("alert.export.success"));
				alert.showAndWait();

			} else {
				final Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle(App.bundle.getString("alert.export.no"));
				alert.setHeaderText(null);
				alert.setContentText("alert.export.noData");
				alert.showAndWait();

			}

		});

	}

	/**
	 * Gets the control.
	 *
	 * @return the control
	 */
	public Control getControl() {
		return control;
	}

	/**
	 * Sets the control.
	 *
	 * @param control the new control
	 */
	public void setControl(final Control control) {
		this.control = control;
	}

}
