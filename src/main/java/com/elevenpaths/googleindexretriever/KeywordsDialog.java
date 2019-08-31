package com.elevenpaths.googleindexretriever;

import javafx.stage.Window;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

// TODO: Auto-generated Javadoc
/**
 * The Class KeywordsDialog.
 */
@SuppressWarnings("restriction")
public class KeywordsDialog extends Dialog<String> {

	/** The Constant WITHOUT_SELECTION. */
	public static final Integer WITHOUT_SELECTION = Integer.valueOf(-1);

	/** The keywords. */
	private ArrayList<String> keywords;

	/** The use keywords check. */
	private boolean useKeywordsCheck;

	/** The save. */
	private boolean save;

	/** The spam window. */
	private final boolean spamWindow;

	/** The selected index. */
	private Integer selectedIndex;

	/** The that. */
	private final KeywordsDialog that;

	/**
	 * Instantiates a new keywords dialog.
	 *
	 * @param title the title
	 * @param keywords the keywords
	 * @param useKeywordsCheck the use keywords check
	 * @param spamWindow the spam window
	 */
	public KeywordsDialog(final String title, final ArrayList<String> keywords, final boolean useKeywordsCheck,
			final boolean spamWindow) {
		super();
		this.keywords = keywords;
		this.useKeywordsCheck = useKeywordsCheck;
		this.spamWindow = spamWindow;
		save = false;
		that = this;
		selectedIndex = WITHOUT_SELECTION; // selected element

		// title
		setTitle(title);

		// icon
		final Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("img/xr.png"));

		// close button
		final Window window = this.getDialogPane().getScene().getWindow();
		window.setOnCloseRequest(event -> window.hide());

		// add content
		getDialogPane().setContent(buildBody());

	}

	/**
	 * Builds the body.
	 *
	 * @return the grid pane
	 */
	private GridPane buildBody() {

		// Creating a GridPane container
		final GridPane root = new GridPane();
		root.setPadding(new Insets(10, 10, 0, 10));
		root.setVgap(5);
		root.setHgap(5);

		// Create the ListView for the keys
		final ListView<String> keysListView = new ListView<String>();
		// Set the Orientation of the ListView
		keysListView.setOrientation(Orientation.VERTICAL);
		// Set the Size of the ListView
		keysListView.setPrefSize(380, 300);

		GridPane.setConstraints(keysListView, 0, 0, 4, 44);
		root.getChildren().add(keysListView);

		// add keys to ListView
		keysListView.getItems().addAll(keywords);

		// Defining the add row
		final TextField newWord = new TextField();
		newWord.setPromptText("Add new words...");
		newWord.setPrefWidth(140);

		final Button add = new Button("Add");
		final Tooltip addTooltip = new Tooltip("Add keyword");
		add.setTooltip(addTooltip);

		final HBox addRow = new HBox(newWord, add);
		addRow.setSpacing(5);

		GridPane.setConstraints(addRow, 5, 0);
		root.getChildren().add(addRow);

		// Defining the delete button
		final Button delete = new Button("Delete");
		final Tooltip deleteTooltip = new Tooltip("Delete keyword");
		delete.setTooltip(deleteTooltip);
		GridPane.setConstraints(delete, 5, 1);
		root.getChildren().add(delete);

		// Defining the useKeywords checkbox
		final CheckBox useKeywords = new CheckBox("Use Keywords");
		if (!spamWindow) {
			useKeywords.setSelected(useKeywordsCheck);
			GridPane.setConstraints(useKeywords, 5, 2);
			root.getChildren().add(useKeywords);
		}

		// Defining the total row
		final Label total = new Label("Total:");
		final Label totalValue = new Label(String.valueOf(keywords.size()));
		final HBox totalRow = new HBox(total, totalValue);
		totalRow.setSpacing(5);
		GridPane.setConstraints(totalRow, 5, 42);
		root.getChildren().add(totalRow);

		// Defining the delete button
		final Button saveButton = new Button("Save");
		final Tooltip saveTooltip = new Tooltip("Save data");
		saveButton.setTooltip(saveTooltip);
		GridPane.setConstraints(saveButton, 5, 43);
		root.getChildren().add(saveButton);

		// List View Mouse click action Listener
		keysListView.setOnMouseClicked((event) -> {
			selectedIndex = Integer.valueOf(keysListView.getSelectionModel().getSelectedIndex());

		});

		// Add an ActionListener for add Button
		add.setOnAction((event) -> {
			final String value = newWord.getText().trim();
			if (!value.isEmpty()) {
				keywords.add(value);
				keysListView.getItems().add(value);
			}
			totalValue.setText(String.valueOf(keywords.size()));
			newWord.setText("");

		});

		// Add an ActionListener for delete Button
		delete.setOnAction((event) -> {
			if (selectedIndex.intValue() != WITHOUT_SELECTION.intValue()) {
				final String value = keysListView.getItems().get(selectedIndex);
				final int index = keywords.indexOf(value);
				keywords.remove(index);
				keysListView.getItems().remove(selectedIndex.intValue());
				selectedIndex = WITHOUT_SELECTION;
				totalValue.setText(String.valueOf(keywords.size()));

			}
		});

		// checkbox change listener
		useKeywords.setOnAction((event) -> {
			useKeywordsCheck = useKeywords.isSelected();
		});

		// Add an ActionListener for add Button
		saveButton.setOnAction((event) -> {
			save = true;
			that.setResult("End");
			that.close();

		});

		return root;
	}

	/**
	 * Checks if is save.
	 *
	 * @return true, if is save
	 */
	public boolean isSave() {
		return save;
	}

	/**
	 * Sets the save.
	 *
	 * @param save the new save
	 */
	public void setSave(final boolean save) {
		this.save = save;
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
	 * @param keywords the new keywords
	 */
	public void setKeywords(final ArrayList<String> keywords) {
		this.keywords = keywords;
	}

	/**
	 * Checks if is use keywords check.
	 *
	 * @return true, if is use keywords check
	 */
	public boolean isUseKeywordsCheck() {
		return useKeywordsCheck;
	}

	/**
	 * Sets the use keywords check.
	 *
	 * @param useKeywordsCheck the new use keywords check
	 */
	public void setUseKeywordsCheck(final boolean useKeywordsCheck) {
		this.useKeywordsCheck = useKeywordsCheck;
	}

}
