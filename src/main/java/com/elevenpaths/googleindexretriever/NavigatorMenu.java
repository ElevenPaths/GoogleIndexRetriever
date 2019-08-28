package com.elevenpaths.googleindexretriever;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.web.WebView;

/**
 * The Class NavigatorMenu.
 */
@SuppressWarnings("restriction")
public class NavigatorMenu extends MenuButton {

	/**
	 * Instantiates a new navigator menu.
	 *
	 * @param webView the web view
	 */
	public NavigatorMenu(final WebView webView) {
		// Set the Text of the WebMenu
		this.setText("Options");

		// Set the Style-properties of the Navigation Bar
		// this.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
		// + "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: blue;");

		// Create the Menu Items
		final CheckMenuItem ctxMenu = new CheckMenuItem("Enable Context Menu");
		ctxMenu.setSelected(true);

		final MenuItem normalFontMenu = new MenuItem("Normal");
		final MenuItem biggerFontMenu = new MenuItem("10% Bigger");
		final MenuItem smallerFontMenu = new MenuItem("10% Smaller");

		final MenuItem normalZoomMenu = new MenuItem("Normal");
		final MenuItem biggerZoomMenu = new MenuItem("10% Bigger");
		final MenuItem smallerZoomMenu = new MenuItem("10% Smaller");

		// Create the RadioMenuItems
		final RadioMenuItem grayMenu = new RadioMenuItem("GRAY");
		grayMenu.setSelected(true);
		final RadioMenuItem lcdMenu = new RadioMenuItem("LCD");

		// Create the Menus
		final Menu scalingMenu = new Menu("Font Scale");
		scalingMenu.textProperty().bind(new SimpleStringProperty("Font Scale ")
				.concat(webView.fontScaleProperty().multiply(100.0)).concat("%"));

		final Menu smoothingMenu = new Menu("Font Smoothing");

		final Menu zoomMenu = new Menu("Zoom");
		zoomMenu.textProperty()
				.bind(new SimpleStringProperty("Zoom ").concat(webView.zoomProperty().multiply(100.0)).concat("%"));

		// Add the Items to the corresponding Menu
		scalingMenu.getItems().addAll(normalFontMenu, biggerFontMenu, smallerFontMenu);
		smoothingMenu.getItems().addAll(grayMenu, lcdMenu);
		zoomMenu.getItems().addAll(normalZoomMenu, biggerZoomMenu, smallerZoomMenu);

		// Create the ToggleGroup
		new ToggleGroup().getToggles().addAll(lcdMenu, grayMenu);

		// Define the Event Handler
		normalFontMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				webView.setFontScale(1.0);
			}
		});

		biggerFontMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				webView.setFontScale(webView.getFontScale() + 0.10);
			}
		});

		smallerFontMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				webView.setFontScale(webView.getFontScale() - 0.10);
			}
		});

		grayMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				webView.setFontSmoothingType(FontSmoothingType.GRAY);
			}
		});

		lcdMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				webView.setFontSmoothingType(FontSmoothingType.LCD);
			}
		});

		normalZoomMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				webView.setZoom(1.0);
			}
		});

		biggerZoomMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				webView.setZoom(webView.getZoom() + 0.10);
			}
		});

		smallerZoomMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				webView.setZoom(webView.getZoom() - 0.10);
			}
		});

		webView.contextMenuEnabledProperty().bind(ctxMenu.selectedProperty());

		// Enabled JavaScript option
		final CheckMenuItem scriptMenu = new CheckMenuItem("Enable JavaScript");
		scriptMenu.setSelected(true);
		webView.getEngine().javaScriptEnabledProperty().bind(scriptMenu.selectedProperty());

		// Add Menus to the WebMenu
		this.getItems().addAll(ctxMenu, scalingMenu, smoothingMenu, zoomMenu, new SeparatorMenuItem(), scriptMenu);
	}
}